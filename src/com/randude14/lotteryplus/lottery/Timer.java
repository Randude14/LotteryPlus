package com.randude14.lotteryplus.lottery;

public interface Timer {
	
	void load(LotteryProperties options);
	
	void save(LotteryProperties options);
	
	void reset(LotteryProperties options);
	
	void onTick();
	
	void setTime(long time);
	
	long getTime();
	
	String format();
	
	void setRunning(boolean running);
	
	boolean isRunning();
	
	boolean isOver();

}
