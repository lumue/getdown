package io.github.lumue.getdown.application;

import io.github.lumue.getdown.downloader.DownloadProgressListener;

import java.io.Serializable;
import java.util.UUID;

public interface DownloadJob {

	public interface DownloadJobHandle {
		public static DownloadJobHandle create(String key) {
			return new DownloadJobHandleImpl(key);
		}
	}

	static class DownloadJobHandleImpl implements DownloadJobHandle, Serializable {
		
		private static final long serialVersionUID = -7582907691952041979L;
		private final String key;

		DownloadJobHandleImpl() {
			this(UUID.randomUUID().toString());
		}

		public DownloadJobHandleImpl(String key) {
			this.key = key;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DownloadJobHandleImpl other = (DownloadJobHandleImpl) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return key;
		}

	}

	static class DownloadJobImpl implements DownloadJob, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6228176038070877982L;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((handle == null) ? 0 : handle.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DownloadJobImpl other = (DownloadJobImpl) obj;
			if (handle == null) {
				if (other.handle != null)
					return false;
			} else if (!handle.equals(other.handle))
				return false;
			return true;
		}

		private final DownloadJobHandle handle = new DownloadJobHandleImpl();
		private final DownloadProgressListener progressListener;
		private final String outputFilename;
		private final String url;

		DownloadJobImpl(String url, String outputFilename, DownloadProgressListener progressListener) {
			super();
			this.progressListener = progressListener;
			this.outputFilename = outputFilename;
			this.url = url;
		}

		@Override
		public DownloadJobHandle getHandle() {
			return handle;
		}

		@Override
		public DownloadProgressListener getProgressListener() {
			return progressListener;
		}

		@Override
		public String getOutputFilename() {
			return outputFilename;
		}

		@Override
		public String getUrl() {
			return url;
		}

	}

	public static class DownloadJobBuilder {
		private DownloadProgressListener progressListener=new DownloadProgressListener();
		private String outputFilename;
		private String url;

		public DownloadJobBuilder withProgressListener(DownloadProgressListener progressListener) {
			this.progressListener = progressListener;
			return this;
		}

		public DownloadJobBuilder withOutputFilename(String outputFilename) {
			this.outputFilename = outputFilename;
			return this;
		}

		public DownloadJobBuilder withUrl(String url) {
			this.url = url;
			return this;
		}

		DownloadJob build() {
			return new DownloadJobImpl(url, outputFilename, progressListener);
		}
	}

	DownloadJobHandle getHandle();

	DownloadProgressListener getProgressListener();

	String getOutputFilename();

	String getUrl();

}
