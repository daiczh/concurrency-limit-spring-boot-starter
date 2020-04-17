package com.dcz.exception;

/**
 * 自定义业务异常
 * 
 * @author dcz
 *
 */
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1664721644245362288L;

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}
}
