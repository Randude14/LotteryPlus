package com.randude14.lotteryplus.lottery;

import com.randude14.lotteryplus.util.ChatUtils;

public class BlankTimer implements Timer {
	private boolean running = false;
	
	protected BlankTimer() {
	}

	public void load(LotteryProperties properties) {
	}

	public void save(LotteryProperties properties) {
	}

	public void reset(LotteryProperties properties) {
	}

	public void onTick() {
	}

	public void setTime(long time) {	
	}

	public long getTime() {
		return -1;
	}

	public String format() {
		return ChatUtils.getRawName("lottery.timer.infinite");
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
