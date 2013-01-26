package com.randude14.lotteryplus.tasks;

import com.randude14.lotteryplus.util.TimeConstants;

public interface Task extends Runnable, TimeConstants {

	void scheduleTask();
	
}
