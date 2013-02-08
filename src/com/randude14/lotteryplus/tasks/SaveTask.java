package com.randude14.lotteryplus.tasks;

import com.randude14.lotteryplus.Logger;
import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.LotteryPlus;
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
		LotteryPlus.cancelTask(updateId);
		updateId = -1;
		if(!Config.getBoolean(Config.FORCE_SAVE_ENABLE)) return;
		long delay = Config.getLong(Config.SAVE_DELAY) * SERVER_SECOND * MINUTE;
		updateId = LotteryPlus.scheduleSyncRepeatingTask(this, delay, delay);
	}
}
