package com.randude14.lotteryplus.tasks;

import com.randude14.lotteryplus.Logger;
import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Plugin;
import com.randude14.lotteryplus.configuration.Config;

public class SaveTask implements Task {
	private int updateId = -1;

	public void run() {
		boolean flag = Config.getBoolean(Config.SHOULD_LOG);
		if (flag)
			Logger.info("logger.lottery.saving.force");
		LotteryManager.saveLotteries();
		if (flag)
			Logger.info("logger.lottery.save.force");
	}

	public void scheduleTask() {
		long delay = Config.getLong(Config.SAVE_DELAY);
		Plugin.cancelTask(updateId);
		if (delay <= 0) {
			return;
		}
		delay *= SERVER_SECOND * MINUTE;
		updateId = Plugin.scheduleSyncRepeatingTask(this, delay, delay);
	}
}
