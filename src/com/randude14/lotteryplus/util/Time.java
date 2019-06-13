package com.randude14.lotteryplus.util;

/*
 * Used for time related tasks 
 */
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
	
	/*
	 * @return - returns the time, in seconds, to Minecraft ticks
	 * NOTE: 1 second = 20 server ticks
	 */
	public long getBukkitTime() {
		switch(this) {
		
		    // simply return if it is already a server second
		    case SERVER_SECOND:
		    	return time;
		    default:
		    // convert seconds to server ticks
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
