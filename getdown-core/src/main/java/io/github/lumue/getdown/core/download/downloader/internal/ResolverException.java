package io.github.lumue.getdown.core.download.downloader.internal;

public class ResolverException extends RuntimeException {

	private static final long serialVersionUID = 360275365230985961L;

	public ResolverException(String message) {
		super(message);
	}

	public ResolverException(Throwable cause) {
		super(cause);
	}

	public ResolverException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResolverException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
