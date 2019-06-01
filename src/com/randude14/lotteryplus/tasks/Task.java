package com.randude14.lotteryplus.tasks;

import org.bukkit.scheduler.BukkitTask;

/*
 * Represents the base for a task. Keeps track of the bukkit task 
 * scheduled so that it may be cancelled later
 */
public abstract class Task implements Runnable {
	private BukkitTask task;
	
	public Task() {
		
	}
	
	/*
	 * Reschedules a task by canceling the current one
	 * and calling another one
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
	 * @return - whether this task should be scheduled or not
	 */
	protected abstract boolean shouldScheduleTask();
	
	/*
	 * @return - the scheduled task
	 */
	protected abstract BukkitTask scheduleTask();

}
