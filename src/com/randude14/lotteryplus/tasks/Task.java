package com.randude14.lotteryplus.tasks;

import org.bukkit.scheduler.BukkitTask;

/*
 * represents a task
 */
public abstract class Task implements Runnable {
	private BukkitTask task;
	
	public Task() {
		
	}
	
	/*
	 * reschedule/schedule this task
	 */
	public void reschedule() {
		if(task != null) {
			task.cancel();
		}
		if(shouldScheduleTask()) {
			task = scheduleTask();
		}
	}
	
	/*
	 * @return if this task should be scheduled
	 */
	protected abstract boolean shouldScheduleTask();
	
	/*
	 * schedules the task
	 * @return the @BukkitTask that was scheduled
	 */
	protected abstract BukkitTask scheduleTask();

}
