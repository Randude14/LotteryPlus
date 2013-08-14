package com.randude14.lotteryplus.util;

/*
 * Time constraints used in tasks and lottery timers
 */
public enum Time {
	
	/*
	 * represents a minute
	 */
	MINUTE(60),
	
	/*
	 * represents an hour
	 */
	HOUR(MINUTE, 60),
	
	/*
	 * represents a day
	 */
	DAY(HOUR, 24),
	
	/*
	 * represents a week
	 */
	WEEK(DAY, 7),
	
	/*
	 * represents a second in server ticks
	 */
	SERVER_SECOND(20);
	
	private Time(long time) {
		this.time = time;
	}
	
	private Time(Time time, long t) {
		this.time = time.multi(t);
	}

	/*
	 * @return time
	 */
	public long getTime() {
		return time;
	}
	
	/*
	 * @return total time in server ticks
	 */
	public long getBukkitTime() {
		return SERVER_SECOND.multi(time);
	}
	
	/*
	 * @return t * time
	 */
	public long multi(long t) {
		return t * time;
	}
	
	private final long time;
}
