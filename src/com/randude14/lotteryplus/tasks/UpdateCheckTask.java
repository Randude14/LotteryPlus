package com.randude14.lotteryplus.tasks;

import org.bukkit.scheduler.BukkitTask;

import com.randude14.lotteryplus.LotteryPlus;
import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.util.Time;

public class UpdateCheckTask extends Task {
	private static final String currentVersion = LotteryPlus.getVersion();
	
	public void run() {
		if(LotteryPlus.isThereNewUpdate(currentVersion)) {
			LotteryPlus.updateCheck(currentVersion);
		}
	}
	
	public BukkitTask scheduleTask() {
		long delay = Config.getLong(Config.UPDATE_DELAY) * Time.MINUTE.getBukkitTime();
		return LotteryPlus.scheduleSyncRepeatingTask(this, delay, delay);
	}

	protected boolean shouldScheduleTask() {
		return Config.getBoolean(Config.UPDATE_CHECK_ENABLE);
	}
}
