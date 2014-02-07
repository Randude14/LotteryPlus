package com.randude14.lotteryplus.util;

public enum Time {
	
	MINUTE(60),
	HOUR(MINUTE, 60),
	DAY(HOUR, 24),
	WEEK(DAY, 7),
	SERVER_SECOND(20);
	
	private Time(long time) {
		this.time = time;
	}
	
	private Time(Time t, long time) {
		this.time = t.getTime() * time;
	}

	public long getTime() {
		return time;
	}
	
	public long getBukkitTime() {
		switch(this) {
		    case SERVER_SECOND:
		    	return time;
		    default:
		    	return SERVER_SECOND.getTime() * time;
		}
	}
	
	public long multi(Time t) {
		return t.getTime() * time;
	}
	
	public long multi(long t) {
		return t * time;
	}
	
	private final long time;
}
