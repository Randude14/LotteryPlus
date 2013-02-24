package com.randude14.lotteryplus;

import com.randude14.lotteryplus.configuration.CustomYaml;

public class Stats {
	public static final CustomYaml statsConfig = new CustomYaml("stats.yml");
	
	public static void inc(Stat stat, int inc) {
		if(LotteryPlus.getMetrics().isOptOut()) return;
		int num = statsConfig.getConfig().getInt(stat.name(), 0);
		statsConfig.getConfig().set(stat.name(), num + inc);
		statsConfig.saveConfig();
	}
	
	public static void dec(Stat stat, int dec) {
		if(LotteryPlus.getMetrics().isOptOut()) return;
		int num = statsConfig.getConfig().getInt(stat.name(), 0);
		statsConfig.getConfig().set(stat.name(), num - dec);
		statsConfig.saveConfig();
	}
	
	public static int reset(Stat stat) {
		int num = statsConfig.getConfig().getInt(stat.name(), 0);
		statsConfig.getConfig().set(stat.name(), 0);
		statsConfig.saveConfig();
		return num;
	}

	public enum Stat {
		
		// stat for money spent
		MONEY_SPENT
		
	}
}
