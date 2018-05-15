package io.github.lumue.getdown.core.download.downloader.youtubedl;

import io.github.lumue.getdown.core.download.job.Progression;
import io.github.lumue.getdown.core.download.job.ProgressionListener;
import io.github.lumue.getdown.core.download.task.DownloadFormat;
import io.github.lumue.getdown.core.download.task.DownloadTask;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.github.lumue.getdown.core.download.task.DownloadFormat.*;

public class MergeFilesStep implements Runnable {
	
	private final List<DownloadFormat> selectedFormats;
	private final String downloadPath;
	private String ouputfilename;
	private final ProgressionListener progressionListener;
	
	public MergeFilesStep(List<DownloadFormat> selectedFormats,
	                      String downloadPath,
	                      String ouputfilename,
	                      ProgressionListener progressionListener
	                      ) {
		
		this.selectedFormats = selectedFormats;
		this.downloadPath = downloadPath;
		this.ouputfilename = ouputfilename;
		this.progressionListener = progressionListener;
	}
	
	@Override
	public void run() {
		
		FFmpeg ffmpeg;
		FFprobe ffprobe;
		
		DownloadFormat audioFormat=null,videoFormat=null;
		
		for (DownloadFormat f:selectedFormats) {
			if(Type.AUDIO.equals(f.getType()))
				audioFormat=f;
			else if(Type.VIDEO.equals(f.getType()))
				videoFormat=f;
		}
		
		if(audioFormat==null||videoFormat==null)
			throw new RuntimeException("must have audio and video formats as input to merge. downloaded formats:"+selectedFormats.toString());
		
		
		try {
			ffmpeg = new FFmpeg("/usr/local/bin/ffmpeg");
			ffprobe = new FFprobe("/usr/local/bin/ffprobe");
			
			String audioInputfilename= downloadPath + File.separator + audioFormat.getFilename();
			String videoInputfilename= downloadPath + File.separator + videoFormat.getFilename();
			String outputFilename=downloadPath + File.separator + ouputfilename;
			
			FFmpegProbeResult audioProbeResult = ffprobe.probe(audioInputfilename);
			FFmpegProbeResult videoProbeResult = ffprobe.probe(videoInputfilename);
			
			FFmpegBuilder builder = new FFmpegBuilder()
					
					.setInput(videoProbeResult)
					.addInput(audioProbeResult)
					.overrideOutputFiles(true)
					.addOutput(outputFilename)
					.setVideoCodec("copy")     // Video using x264
					.setAudioCodec("copy")
					.setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
					.done();
			
			final long durationInSeconds = Double.valueOf(videoProbeResult.getFormat().duration).longValue();
			
			FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
			
			final Progression progression=new Progression(0,durationInSeconds);
			executor.createJob(builder, progress -> {
				long outputSeconds = TimeUnit.NANOSECONDS.toSeconds(progress.out_time_ns);
				progression.incrementProgress(outputSeconds-progression.getValue());
				progressionListener.onProgress("merging audio and video",progression);
			}).run();

		} catch (Throwable t) {
			throw new RuntimeException("error merging files",t);
		}
	}
}
