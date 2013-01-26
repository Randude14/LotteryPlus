package com.randude14.lotteryplus.tasks;

import com.randude14.lotteryplus.Plugin;
import com.randude14.lotteryplus.configuration.Config;

public class UpdateCheckTask implements Task {
	private static final String currentVersion = Plugin.getVersion();
	private int updateId;
	
	public void run() {
		if(Plugin.isThereNewUpdate(currentVersion)) {
			Plugin.updateCheck(currentVersion);
		}
	}
	
	public void scheduleTask() {
		Plugin.cancelTask(updateId);
		long delay = Config.getLong(Config.UPDATE_DELAY);
		if(delay <= 0) {
			return;
		}
		delay *=  SERVER_SECOND * MINUTE;
		updateId = Plugin.scheduleSyncRepeatingTask(this, delay, delay);
	}
}
