package com.randude14.lotteryplus.lottery;

public class BlankTimer implements Timer {
	private boolean running = false;
	
	public BlankTimer() {
	}

	public void load(LotteryOptions options) {
	}

	public void save(LotteryOptions options) {
	}

	public void reset(LotteryOptions options) {
	}

	public void onTick() {
	}

	public void setTime(long time) {	
	}

	public long getTime() {
		return 0;
	}

	public String format() {
		return null;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return running;
	}

	public boolean isOver() {
		return false;
	}

}
