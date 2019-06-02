package com.randude14.lotteryplus.lottery;

import com.randude14.lotteryplus.util.ChatUtils;

/*
 * Represents a timer that never ends. Normally used with lotteries
 * that have limited tickets or the server admins plan to end
 * the lottery themselves
 */
public class InfiniteTimer extends LotteryTimer {
	
	protected InfiniteTimer() {
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

	public boolean isOver() {
		return false;
	}

}
