package com.randude14.lotteryplus.lottery;

public class InvalidLotteryException extends RuntimeException {
	private static final long serialVersionUID = -9091643167060189764L;

	public InvalidLotteryException(String message) {
		super(message);
	}
	
	public InvalidLotteryException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidLotteryException(Throwable cause) {
		super(cause);
	}
	
	public InvalidLotteryException() {
		super();
	}

}
