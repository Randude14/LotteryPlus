package com.randude14.lotteryplus.lottery;

public interface Timer {
	
	void load(LotteryProperties properties);
	
	void save(LotteryProperties properties);
	
	void reset(LotteryProperties properties);
	
	void onTick();
	
	void setTime(long time);
	
	long getTime();
	
	String format();
	
	void setRunning(boolean running);
	
	boolean isRunning();
	
	boolean isOver();

}
