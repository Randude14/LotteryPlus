package com.randude14.lotteryplus.lottery;

public interface Timer {
	
	void load(LotteryOptions options);
	
	void save(LotteryOptions options);
	
	void reset(LotteryOptions options);
	
	void onTick();
	
	void setTime(long time);
	
	long getTime();
	
	String format();
	
	void setRunning(boolean running);
	
	boolean isRunning();
	
	boolean isOver();

}
