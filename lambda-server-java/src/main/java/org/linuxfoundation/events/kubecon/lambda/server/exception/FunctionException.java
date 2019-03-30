package org.linuxfoundation.events.kubecon.lambda.server.exception;

public class FunctionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1155858492091101258L;

	public FunctionException() {
		super();
	}

	public FunctionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FunctionException(String message, Throwable cause) {
		super(message, cause);
	}

	public FunctionException(String message) {
		super(message);
	}

	public FunctionException(Throwable cause) {
		super(cause);
	}

}
