package com.randude14.lotteryplus.lottery;

import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.util.TimeConstants;

public class LotteryTimer implements TimeConstants, Timer {
	private boolean running;
	private long time;
	private long reset;

	protected LotteryTimer() {
	}
	
	public void load(LotteryOptions options) {
		if (options.contains("save-time") && options.contains("reset-time")) {
			this.time = options.getLong("save-time", 0L);
			this.reset = options.getLong("reset-time", 0L);
		} else {
			long t = (long) Math.floor(options.getDouble(Config.DEFAULT_TIME) * (double)HOUR);
			this.time = this.reset = t;
		}
	}
	
	public void save(LotteryOptions options) {
		options.set("save-time", time);
		options.set("reset-time", reset);
	}

	public void reset(LotteryOptions options) {
		double time = options.getDouble(Config.DEFAULT_RESET_ADD_TIME);
		long t = (long) Math.floor(time * (double)HOUR);
		reset = t + reset;
		time = reset;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setResetTime(long reset) {
		this.reset = reset;
	}

	public long getTime() {
		return time;
	}

	public void start() {
		setRunning(true);
	}

	public void stop() {
		setRunning(false);
	}

	public void setRunning(boolean flag) {
		this.running = flag;
	}

	public long getResetTime() {
		return reset;
	}

	public boolean isRunning() {
		return running;
	}

	public void onTick() {
		if(!running && !isOver()) {
			running = true;
		}

		if (running) {
			time--;
		}
	}

	public boolean isOver() {
		return time < 1;
	}

	public String format() {
		long sec = (time) % 60;
		long min = (time / MINUTE) % 60;
		long hours = (time / HOUR) % 24;
		long days = (time / DAY) % 7;
		long weeks = (time / WEEK) % 52;
		String display = String.format("%02d:%02d:%02d:%02d:%02d", weeks, days,
				hours, min, sec);
		return display;
	}
}
