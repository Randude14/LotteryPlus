package com.randude14.lotteryplus.tasks;

import com.randude14.lotteryplus.LotteryPlus;
import com.randude14.lotteryplus.configuration.Config;

public class UpdateCheckTask implements Task {
	private static final String currentVersion = LotteryPlus.getVersion();
	private int updateId;
	
	public void run() {
		if(LotteryPlus.isThereNewUpdate(currentVersion)) {
			LotteryPlus.updateCheck(currentVersion);
		}
	}
	
	public void scheduleTask() {
		LotteryPlus.cancelTask(updateId);
		long delay = Config.getLong(Config.UPDATE_DELAY);
		if(delay <= 0) {
			return;
		}
		delay *=  SERVER_SECOND * MINUTE;
		updateId = LotteryPlus.scheduleSyncRepeatingTask(this, delay, delay);
	}
}
