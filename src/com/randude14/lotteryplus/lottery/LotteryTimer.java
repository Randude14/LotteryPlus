package com.randude14.lotteryplus.lottery;

import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.util.Time;

public class LotteryTimer implements Timer {
	private boolean running;
	private long time;
	private long reset;

	protected LotteryTimer() {
	}
	
	public void load(LotteryProperties options) {
		long defaultTime = toTime(options.getDouble(Config.DEFAULT_TIME));
		if (options.contains("save-time") && options.contains("reset-time")) {
			this.time = options.getLong("save-time", defaultTime);
			this.reset = options.getLong("reset-time", defaultTime);
		} else {
			long t = defaultTime;
			this.time = this.reset = t;
		}
	}
	
	public void save(LotteryProperties options) {
		options.set("save-time", time);
		options.set("reset-time", reset);
	}

	public void reset(LotteryProperties options) {
		long t = toTime(Config.getDouble(Config.DEFAULT_TIME));
		this.reset = t + reset;
		this.time = reset;
	}
	
	private long toTime(double time) {
		return (long) Math.floor(time * (double)Time.HOUR.getTime());
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public void setRunning(boolean flag) {
		this.running = flag;
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
		long min = (time / Time.MINUTE.getTime()) % 60;
		long hours = (time / Time.HOUR.getTime()) % 24;
		long days = (time / Time.DAY.getTime()) % 7;
		long weeks = (time / Time.WEEK.getTime()) % 52;
		String display = String.format("%02d:%02d:%02d:%02d:%02d", weeks, days,
				hours, min, sec);
		return display;
	}
}
