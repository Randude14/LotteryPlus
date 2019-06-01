package com.randude14.lotteryplus.tasks;

import org.bukkit.scheduler.BukkitTask;

import com.randude14.lotteryplus.Logger;
import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.LotteryPlus;
import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.util.Time;

/*
 * Used to save lotteries every so often
 */
public class SaveTask extends Task {

	public void run() {
		boolean logToScreen = Config.getBoolean(Config.SHOULD_LOG);
		
		if (logToScreen)
			Logger.info("logger.lottery.saving.force");
		
		LotteryManager.saveLotteries();
		
		if (logToScreen)
			Logger.info("logger.lottery.save.force");
	}

	public BukkitTask scheduleTask() {
		long delay = Config.getLong(Config.SAVE_DELAY) * Time.MINUTE.getBukkitTime();
		return LotteryPlus.scheduleSyncRepeatingTask(this, delay, delay);
	}

	protected boolean shouldScheduleTask() {
		return Config.getBoolean(Config.FORCE_SAVE_ENABLE);
	}
}
