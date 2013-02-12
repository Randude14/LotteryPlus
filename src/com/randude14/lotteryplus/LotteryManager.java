package com.randude14.lotteryplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.configuration.CustomYaml;
import com.randude14.lotteryplus.lottery.InvalidLotteryException;
import com.randude14.lotteryplus.lottery.Lottery;
import com.randude14.lotteryplus.lottery.LotteryOptions;

public class LotteryManager {
	private static final CustomYaml lotteriesConfig = new CustomYaml("lotteries.yml");
	private static final Map<String, Lottery> lotteries = new LinkedHashMap<String, Lottery>();

	public static boolean createLotterySection(CommandSender sender,
			String lotteryName) {
		if (lotteries.containsKey(lotteryName.toLowerCase())) {
			ChatUtils.send(sender, "lottery.error.exists", "<lottery>", lotteryName);
			return false;
		}
		ConfigurationSection lotteriesSection = getOrCreateLotteriesSection();
		for (String key : lotteriesSection.getKeys(false)) {
			if (key.equalsIgnoreCase(lotteryName)) {
				ChatUtils.send(sender, "lottery.error.section.exists", "<lottery>", lotteryName);
				return false;
			}
		}
		ConfigurationSection section = lotteriesSection
				.createSection(lotteryName);
		writeDefaults(section);
		lotteriesConfig.saveConfig();
		ChatUtils.send(sender, "lottery.section.created", "<lottery>", lotteryName);
		return true;
	}

	public static boolean loadLottery(CommandSender sender, String find) {
		Lottery l;
		l = lotteries.get(find.toLowerCase());
		if (l != null) {
			ChatUtils.send(sender, "lottery.error.exists", "<lottery>", l.getName());
			return false;
		}
		lotteriesConfig.reloadConfig();
		ConfigurationSection section = getOrCreateLotteriesSection();
		for (String sectionName : section.getKeys(false)) {
			if (sectionName.equalsIgnoreCase(find)) {
				ConfigurationSection lotteriesSection = section
						.getConfigurationSection(sectionName);
				Lottery lottery = new Lottery(sectionName);
				Map<String, Object> values = lotteriesSection.getValues(true);
				try {
					lottery.setOptions(sender, new LotteryOptions(values));
				} catch (Exception ex) {
					Logger.info("lottery.exception.lottery.load", "<lottery>", lottery.getName());
					ex.printStackTrace();
					continue;
				}
				ChatUtils.send(sender, "lottery.section.loaded", "<lottery>", lottery.getName());
				lotteries.put(sectionName.toLowerCase(), lottery);
				return true;
			}
		}
		ChatUtils.send(sender, "lottery.notfound", "<lottery>", find);
		return false;
	}

	public static boolean unloadLottery(String find) {
		return unloadLottery(Bukkit.getConsoleSender(), find, false);
	}

	public static boolean unloadLottery(CommandSender sender, String find) {
		return unloadLottery(sender, find, false);
	}

	public static boolean unloadLottery(CommandSender sender, String find,
			boolean delete) {
		if (!lotteries.containsKey(find.toLowerCase())) {
			ChatUtils.send(sender, "lottery.notfound", "<lottery>", find);
			return false;
		}
		Lottery lottery;
		lottery = lotteries.remove(find.toLowerCase());
		ConfigurationSection savesSection = lotteriesConfig.getConfig().getConfigurationSection("saves");
		if (savesSection != null) {
			deleteSection(savesSection, find);
		}
		if (delete) {
			ConfigurationSection section = getOrCreateLotteriesSection();
			deleteSection(section, find);
			ChatUtils.send(sender, "lottery.unloaded-removed", "<lottery>", lottery.getName());
		} else {
			ChatUtils.send(sender, "lottery.unloaded", "<lottery>", lottery.getName());
		}
		lotteriesConfig.saveConfig();
		return true;
	}

	private static void deleteSection(ConfigurationSection section, String find) {
		for (String key : section.getKeys(false)) {
			if (key.equalsIgnoreCase(find)) {
				section.set(key, null);
			}
		}
	}

	public static List<Lottery> getLotteries() {
		return new ArrayList<Lottery>(lotteries.values());
	}

	public static List<Lottery> getLotteries(CommandSender sender) {
		List<Lottery> list = new ArrayList<Lottery>(lotteries.values());
		for (int cntr = 0; cntr < list.size(); cntr++) {
			if (!list.get(cntr).hasAccess(sender)) {
				list.remove(cntr);
			}
		}
		return list;
	}

	public static Lottery getLottery(String string) {
		Lottery lottery = lotteries.get(string.toLowerCase());
		if (lottery != null)
			return lottery;
		for (Lottery l : lotteries.values()) {
			for (String alias : l.getAliases()) {
				if (alias.equalsIgnoreCase(string)) {
					return l;
				}
			}
		}
		return null;
	}

	public static Lottery getLottery(CommandSender sender, String string) {
		Lottery lottery = getLottery(string);
		if (lottery != null && !lottery.hasAccess(sender)) {
			return null;
		} else {
			return lottery;
		}
	}

	public static boolean reloadLottery(String lotteryName) {
		return reloadLottery(Bukkit.getConsoleSender(), lotteryName, true);
	}

	public static boolean reloadLottery(CommandSender sender,
			String lotteryName, boolean force) {
		Lottery lottery;
		lottery = lotteries.get(lotteryName.toLowerCase());
		if (lottery == null) {
			ChatUtils.send(sender, "lottery.error.notfound", "<lottery>", lotteryName);
			return false;
		}
		if (lottery.isDrawing()) {
			ChatUtils.send(sender, "lottery.error.drawing", "<lottery>", lottery.getName());
			return false;
		}
		lotteriesConfig.reloadConfig();
		ConfigurationSection section = getOrCreateLotteriesSection();
		for (String sectionName : section.getKeys(false)) {
			if (sectionName.equalsIgnoreCase(lotteryName)) {
				ConfigurationSection lotteriesSection = section
						.getConfigurationSection(sectionName);
				Map<String, Object> values = lotteriesSection.getValues(true);
				try {
					lottery.setOptions(sender, new LotteryOptions(values), force);
				} catch (Exception ex) {
					ChatUtils.send(sender, "lottery.exception.lottery.reload", "<lottery>", lottery.getName());
					ex.printStackTrace();
					lotteries.remove(lotteryName.toLowerCase());
					return false;
				}
				ChatUtils.send(sender, "lottery.reload", "<lottery>", lottery.getName());
				return true;
			}
		}
		return true;
	}

	public static void reloadLotteries(CommandSender sender) {
		for (Lottery lottery : lotteries.values()) {
			reloadLottery(sender, lottery.getName(), true);
		}
	}

	public static int loadLotteries() {
		return loadLotteries(Bukkit.getConsoleSender(), true);
	}

	public static int loadLotteries(CommandSender sender, boolean clear) {
		if (clear) {
			lotteries.clear();
		}
		if (!lotteriesConfig.exists()) {
			lotteriesConfig.saveDefaultConfig();
		}
		lotteriesConfig.reloadConfig();
		ConfigurationSection section = getOrCreateLotteriesSection();
		ConfigurationSection savesSection = lotteriesConfig.getConfig().getConfigurationSection("saves");
		int numLotteries = 0;
		for (String lotteryName : section.getKeys(false)) {
			if (lotteries.containsKey(lotteryName.toLowerCase()))
				continue;
			ConfigurationSection lotteriesSection;
			if (savesSection != null && savesSection.contains(lotteryName)) {
				lotteriesSection = savesSection.getConfigurationSection(lotteryName);
			} else {
				lotteriesSection = section.getConfigurationSection(lotteryName);
			}
			Lottery lottery = new Lottery(lotteryName);
			Map<String, Object> values = lotteriesSection.getValues(true);
			try {
				lottery.setOptions(sender, new LotteryOptions(values));
			} catch (InvalidLotteryException ex) {
				Logger.info("lottery.exception.lottery.load", "<lottery>", lotteryName);
				ex.printStackTrace();
				continue;
			}
			numLotteries++;
			lotteries.put(lotteryName.toLowerCase(), lottery);
		}
		return numLotteries;
	}

	public static void saveLotteries() {
		lotteriesConfig.reloadConfig(); // doesn't erase current options in place for future reloads
		ConfigurationSection savesSection = lotteriesConfig.getConfig().createSection("saves");
		for (Lottery lottery : lotteries.values()) {
			lottery.save();
			savesSection.createSection(lottery.getName(), lottery.getOptions().getValues());
		}
		lotteriesConfig.saveConfig();
	}
	
	public static void saveLottery(String lotteryName) {
		Lottery lottery = getLottery(lotteryName);
		if(lottery != null) {
			lottery.save();
			ConfigurationSection section = lotteriesConfig.getConfig();
			ConfigurationSection savesSection = section.getConfigurationSection("saves");
			if(savesSection == null) savesSection = lotteriesConfig.getConfig().createSection("saves");
			savesSection.createSection(lottery.getName(), lottery.getOptions().getValues());
			lotteriesConfig.saveConfig();
		}
	}
	
	public static void listLotteries(CommandSender sender, int page) {
		listLotteries(sender, page, null);
	}

	public static void listLotteries(CommandSender sender, int page, String filter) {
		List<Lottery> list = getLotteries(sender);
		Collections.sort(list, new LotterySorter(filter));
		int len = list.size();
		int max = (len / 10) + 1;
		if (len % 10 == 0)
			max--;
		if (page > max)
			page = max;
		if (page < 1)
			page = 1;
		ChatUtils.sendRaw(sender, "lottery.list.headliner", "<page>", page, "<max>", max);
		for (int cntr = (page * 10) - 10, stop = cntr + 10; cntr < stop && cntr < len; cntr++) {
			ChatUtils.sendRaw(sender, "lottery.list.token", "<number>", cntr + 1, "<lottery>", list.get(cntr).getName());
		}
	}

	public static boolean isSignRegistered(Sign sign) {
		if (LotteryPlus.isSign(sign.getLocation()))
			return false;
		for (Lottery lottery : lotteries.values()) {
			if (lottery.hasRegisteredSign(sign)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSignRegistered(Block sign) {
		for (Lottery lottery : lotteries.values()) {
			if (lottery.hasRegisteredSign(sign)) {
				return true;
			}
		}
		return false;
	}

	private static ConfigurationSection getOrCreateLotteriesSection() {
		FileConfiguration config = lotteriesConfig.getConfig();
		ConfigurationSection lotteriesSection = config.getConfigurationSection("lotteries");
		return (lotteriesSection != null) ? lotteriesSection : config.createSection("lotteries");
	}

	static class TimerTask implements Runnable {

		public void run() {
			Lottery prevLottery = null;
			for (Lottery lottery : getLotteries()) {
				if (prevLottery != null && prevLottery.isDrawing()) {
					while (prevLottery.isDrawing()) {
						Utils.sleep(10L);
					}
				}
				lottery.onTick();
				prevLottery = lottery;
			}
		}
	}

	private static class LotterySorter implements Comparator<Lottery> {
		private final String sort;
		
		public LotterySorter(String filter) {
			if(filter == null || filter.equals("")) {
				this.sort = Config.getString(Config.DEFAULT_FILTER);
			} else {
				this.sort = filter;
			}
		}

		public int compare(Lottery l1, Lottery l2) {
			if (sort.equalsIgnoreCase("name")) {
				return l1.getName().compareToIgnoreCase(l2.getName());
			} else if (sort.equalsIgnoreCase("time")) {
				long time1 = l1.getTimeLeft();
				long time2 = l2.getTimeLeft();
				if(time1 != time2) 
					return (int) (time2 - time1);
			} else if (sort.equalsIgnoreCase("pot")) {
				double pot1 = l1.getPot();
				double pot2 = l2.getPot();
				if(pot1 != pot2) 
					return (int) (pot2 - pot1);
			} else if(sort.equalsIgnoreCase("config")) {
				return 1;
			}
			return l1.getName().compareToIgnoreCase(l2.getName());
		}
	}

	private static void writeDefaults(ConfigurationSection section) {
		section.set(Config.DEFAULT_TICKET_COST.getName(), Config.getDouble(Config.DEFAULT_TICKET_COST));
		section.set(Config.DEFAULT_POT.getName(), Config.getDouble(Config.DEFAULT_POT));
		section.set(Config.DEFAULT_TIME.getName(), Config.getDouble(Config.DEFAULT_TIME));
		section.set(Config.DEFAULT_MAX_TICKETS.getName(), Config.getInt(Config.DEFAULT_MAX_TICKETS));
		section.set(Config.DEFAULT_MIN_PLAYERS.getName(), Config.getInt(Config.DEFAULT_MIN_PLAYERS));
		section.set(Config.DEFAULT_MAX_PLAYERS.getName(), Config.getInt(Config.DEFAULT_MAX_PLAYERS));
		section.set(Config.DEFAULT_TICKET_TAX.getName(), Config.getDouble(Config.DEFAULT_TICKET_TAX));
		section.set(Config.DEFAULT_POT_TAX.getName(), Config.getDouble(Config.DEFAULT_POT_TAX));
	}
}
