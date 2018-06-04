package io.github.lumue.getdown.core.download.downloader;

import io.github.lumue.getdown.core.download.task.DownloadFormat;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.github.lumue.getdown.core.download.task.DownloadFormat.Type;

public class MergeFilesStep extends AbstractStep {
	
	private final List<DownloadFormat> selectedFormats;
	private final String downloadPath;
	private String ouputfilename;
	
	public MergeFilesStep(List<DownloadFormat> selectedFormats,
	                      String downloadPath,
	                      String ouputfilename
	) {
		
		this.selectedFormats = selectedFormats;
		this.downloadPath = downloadPath;
		this.ouputfilename = ouputfilename;
	}
	
	@Override
	public void execute() {
		
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
			
			initProgression(durationInSeconds);
			executor.createJob(builder, progress -> {
				long outputSeconds = TimeUnit.NANOSECONDS.toSeconds(progress.out_time_ns);
				incrementProgression(outputSeconds-getProgression().getValue());
			}).run();

		} catch (Throwable t) {
			throw new RuntimeException("error merging files",t);
		}
	}
}
