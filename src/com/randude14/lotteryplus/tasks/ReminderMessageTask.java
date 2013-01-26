package com.randude14.lotteryplus.tasks;

import com.randude14.lotteryplus.ChatUtils;
import com.randude14.lotteryplus.Plugin;
import com.randude14.lotteryplus.configuration.Config;

public class ReminderMessageTask implements Task {
	private int updateId = -1;
	
	public void run() {
		ChatUtils.broadcastRaw("lottery.mess.reminder");
	}

	public void scheduleTask() {
		long delay = Config.getLong(Config.REMINDER_MESSAGE_TIME);
		Plugin.cancelTask(updateId);
		if(delay <= 0) {
			return;
		}
		delay *= SERVER_SECOND * MINUTE;
		updateId = Plugin.scheduleSyncRepeatingTask(this, delay, delay);
	}
}
