package com.randude14.lotteryplus.tasks;

import org.bukkit.scheduler.BukkitTask;

public abstract class Task implements Runnable {
	private BukkitTask task;
	
	public Task() {
		
	}
	
	public void reschedule() {
		if(task != null) {
			task.cancel();
		}
		if(shouldScheduleTask()) {
			task = scheduleTask();
		}
	}
	
	protected abstract boolean shouldScheduleTask();
	
	protected abstract BukkitTask scheduleTask();

}
