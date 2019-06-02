package com.randude14.lotteryplus.lottery;

import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.util.Time;

/*
 * Represents the most basic finite timer used by lotteries
 */
public class LotteryTimer {
	private boolean running;
	private long time;
	private long reset;

	/*
	 * Blank constructor. Values are set using load(LotteryProperties properties)
	 */
	protected LotteryTimer() {
	}
	
	/*
	 * Loads the values for the timer from the lottery properties
	 * 
	 * @param properties - properties to load from
	 */
	public void load(LotteryProperties properties) {
		long defaultTime = convertToSeconds(properties.getDouble(Config.DEFAULT_TIME));
		
		if (properties.contains("save-time") && properties.contains("reset-time")) {
			this.time = properties.getLong("save-time", defaultTime);
			this.reset = properties.getLong("reset-time", defaultTime);
			
		} else {
			long t = defaultTime;
			this.time = this.reset = t;
		}
	}
	
	/*
	 * Saves the timer using the lottery properties
	 * 
	 * @param properties - properties to save to
	 */
	public void save(LotteryProperties properties) {
		properties.set("save-time", time);
		properties.set("reset-time", reset);
	}

	/*
	 * Reset this timer using the lottery properties
	 * 
	 * @param properties - properties to reset from
	 */
	public void reset(LotteryProperties properties) {
		long t = convertToSeconds(properties.getDouble(Config.DEFAULT_RESET_ADD_TIME));
		this.reset += t;
		this.time = reset;
	}
	
	/*
	 * Converts a time in hours to seconds
	 * 
	 * @param timeInHours - time to convert
	 * @return - time in seconds
	 */
	private long convertToSeconds(double timeInHours) {
		return (long) Math.floor(timeInHours * (double)Time.HOUR.getTime());
	}

	/*
	 * @param time - time to set to
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/*
	 * @return - the time in seconds
	 */
	public long getTime() {
		return time;
	}

	/*
	 * @param flag - set whether the timer is running
	 */
	public void setRunning(boolean flag) {
		this.running = flag;
	}

	/*
	 * @return - whether the timer is running
	 */
	public boolean isRunning() {
		return running;
	}

	/*
	 * Called by the lottery every second
	 */
	public void onTick() {
		if(!running && !isOver()) {
			running = true;
		}

		if (running) {
			time--;
		}
	}

	/*
	 * @return - whether timer is over
	 */
	public boolean isOver() {
		return time < 1;
	}

	/*
	 * @return - a string containing the lottery's time left from seconds to weeks
	 */
	public String format() {
		
		long sec = (time) % 60;
		long min = (time / Time.MINUTE.getTime()) % 60;
		long hours = (time / Time.HOUR.getTime()) % 24;
		long days = (time / Time.DAY.getTime()) % 7;
		long weeks = (time / Time.WEEK.getTime()) % 52;
		
		String display = String.format("%02d:%02d:%02d:%02d:%02d", weeks, days,
				hours, min, sec);
		return display;
	}
}
