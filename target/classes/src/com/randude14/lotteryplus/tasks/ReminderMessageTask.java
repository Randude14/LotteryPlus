package com.randude14.lotteryplus.tasks;

import org.bukkit.scheduler.BukkitTask;

import com.randude14.lotteryplus.LotteryPlus;
import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.util.ChatUtils;
import com.randude14.lotteryplus.util.Time;

public class ReminderMessageTask extends Task {
	
	public void run() {
		ChatUtils.broadcastRaw("lottery.mess.reminder");
	}

	public BukkitTask scheduleTask() {
		long delay = Config.getLong(Config.REMINDER_MESSAGE_TIME) * Time.MINUTE.getBukkitTime();
		return LotteryPlus.scheduleSyncRepeatingTask(this, delay, delay);
	}

	protected boolean shouldScheduleTask() {
		return Config.getBoolean(Config.REMINDER_MESSAGE_ENABLE);
	}
}
