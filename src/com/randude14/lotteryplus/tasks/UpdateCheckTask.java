package com.randude14.lotteryplus.tasks;

import com.randude14.lotteryplus.LotteryPlus;
import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.util.Time;

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
		updateId = -1;
		if(!Config.getBoolean(Config.UPDATE_CHECK_ENABLE)) return;
		long delay = Config.getLong(Config.UPDATE_DELAY) * Time.MINUTE.getBukkitTime();
		updateId = LotteryPlus.scheduleSyncRepeatingTask(this, delay, delay);
	}
}
