package com.randude14.lotteryplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.configuration.CustomYaml;
import com.randude14.lotteryplus.configuration.Property;
import com.randude14.lotteryplus.lottery.InvalidLotteryException;
import com.randude14.lotteryplus.lottery.Lottery;
import com.randude14.lotteryplus.lottery.LotteryProperties;
import com.randude14.lotteryplus.util.ChatUtils;
import com.randude14.lotteryplus.util.Utils;

/*
 * This class has many responsibilities pertaining specifically to the file, 'lotteries.yml'. 
 * It uses a HashMap to sort lotteries by their names in lowercase, allowing the class to
 * quickly find the lottery. Alternatively, it can also list the lotteries using the
 * functions of the Map interface.
 */
public class LotteryManager {
	
	// points to 'lotteries.yml', see @CustomYaml for more about this class
	private static final CustomYaml lotteriesConfig = new CustomYaml("lotteries.yml");
	
	// The HashMap that contains the lotteries. Sorts by their names in lowercase
	private static final Map<String, Lottery> lotteries = new HashMap<String, Lottery>();
	
	// name of the lotteries section of the lotteriesConfig
	private static final String LOTTERIES_SECTION = "lotteries";
	
	// name of the saves section of the lotteriesConfig
	private static final String SAVES_SECTION = "lotteries";
	
	
	
	/*
	 * Creates a lottery section based on the name and values passed
	 * 
	 * @param sender - the command sender that called this function
	 * @param lotteryName - the name of the lottery section to be created
	 * @param values - the values that will set under the lottery section
	 */
	public static void createLotterySection(CommandSender sender, String lotteryName, Map<String, Object> values) {
		
		// create or retrieve the lottery section
		ConfigurationSection lotteriesSection = getOrCreateSection(LOTTERIES_SECTION); 
		
		// double checks if the section already exists 
		for (String key : lotteriesSection.getKeys(false)) {
			if (key.equalsIgnoreCase(lotteryName)) {
				lotteryName = key;
			}
		}
		
		lotteriesSection.createSection(lotteryName, values);
		lotteriesConfig.saveConfig();                        // create and save the section to the config
	}

	/*
	 * Creates a lottery section based on the name passed. Sets values to default based on config.yml
	 * 
	 * @param sender - the command sender that called this function
	 * @param lotteryName - the name of the lottery section to be created
	 * @param values - the values that will set under the lottery section
	 * 
	 * @return - return if the section was created successfully
	 */
	public static boolean createLotterySection(CommandSender sender, String lotteryName) {
		
		// check if a lottery exists before moving on
		Lottery lottery = LotteryManager.getLottery(lotteryName);
		if (lottery != null) {
			ChatUtils.send(sender, "lottery.error.exists", "<lottery>", lotteryName);
			return false;
		}
		
		ConfigurationSection lotteriesSection = getOrCreateSection(LOTTERIES_SECTION); 
		
		// check if the section exists already
		for (String key : lotteriesSection.getKeys(false)) {
			if (key.equalsIgnoreCase(lotteryName)) {
				ChatUtils.send(sender, "lottery.error.section.exists", "<lottery>", lotteryName);
				return false;
			}
		}
		
		
		ConfigurationSection section = lotteriesSection.createSection(lotteryName); // create the section
		writeDefaults(section);                                                     // set values to default
		lotteriesConfig.saveConfig();                                               // save the config
		ChatUtils.send(sender, "lottery.section.created", "<lottery>", lotteryName); // send the user that it was created
		return true;
	}

	/*
	 * Attempts to find an unloaded section and create a lottery based on its values
	 * 
	 * @param sender - executed this command
	 * @param find - the section to find
	 */
	public static boolean loadLottery(CommandSender sender, String find) {
		
		// double check that the lottery isn't loaded
		Lottery l = LotteryManager.getLottery(find);
		if (l != null) {
			ChatUtils.send(sender, "lottery.error.exists", "<lottery>", l.getName());
			return false;
		}
		
		ConfigurationSection section = getOrCreateSection(LOTTERIES_SECTION);
		
		// find section and attempt to load
		for (String sectionName : section.getKeys(false)) {
			
			if (sectionName.equalsIgnoreCase(find)) {
				
				ConfigurationSection lotteriesSection = section.getConfigurationSection(sectionName);
				Lottery lottery = new Lottery(sectionName);
				Map<String, Object> values = lotteriesSection.getValues(true);
				
				// attempt to set the properties of the lottery
				try {
					lottery.setProperties(sender, new LotteryProperties(values));
				} catch (Exception ex) {
					
					// if failure to load, alert user and print the exception.
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

	/*
	 * Attempts to find a lottery and unload it from the server.
	 * 
	 * @param sender - user who executed this function
	 * @param find - lottery to find
	 * @param delete - if set to true, deletes the lottery section
	 */
	public static boolean unloadLottery(CommandSender sender, String find, boolean delete) {
		
		// find lottery
		Lottery lottery = LotteryManager.getLottery(find);
		if (lottery == null) {
			ChatUtils.send(sender, "lottery.notfound", "<lottery>", find);
			return false;
		}
		
		// delete lottery saves
		ConfigurationSection savesSection = getOrCreateSection(SAVES_SECTION);		
		deleteSection(savesSection, lottery.getName());
		
		// iterate and remove values
		Iterator<Lottery> it = lotteries.values().iterator();
		while(it.hasNext()) {
			if(it.next().equals(lottery)) {
				it.remove();
			}
		}
		
		// delete or simply tell the user the lottery has been unloaded
		if (delete) {
			ConfigurationSection section = getOrCreateSection(LOTTERIES_SECTION);
			deleteSection(section, lottery.getName());
			ChatUtils.send(sender, "lottery.unloaded-removed", "<lottery>", lottery.getName());
		} else {
			ChatUtils.send(sender, "lottery.unloaded", "<lottery>", lottery.getName());
		}
		lotteriesConfig.saveConfig();
		return true;
	}
	
	/*
	 * Tries to find a section for the lotteryName and fills the given map with its values
	 * 
	 * @param lotteryName - section to search for
	 * @param putin - map to put in
	 * @return the name of the section
	 */
	public static String putAll(String lotteryName, Map<Property<?>, Object> putin) {
		ConfigurationSection lotteriesSection = getOrCreateSection(LOTTERIES_SECTION);
		
		// go through each section
		for (String key : lotteriesSection.getKeys(false)) {
			
			// find section
			if (key.equalsIgnoreCase(lotteryName)) {
				ConfigurationSection section = lotteriesSection.getConfigurationSection(key);
				Map<String, Object> values = section.getValues(false);
				
				// go through the default values for lotteries
				for(Property<?> prop : Config.lotteryDefaults) {
					String name = prop.getName();
					
					// if section contains this property add it
					if(values.containsKey(name)) {
						putin.put(prop, values.get(name));
					}
					
				}
	
				lotteryName = key;
				break;
			}
		}
		
		return lotteryName;
	}

	/*
	 * Simply returns the currently loaded lotteries
	 */
	public static List<Lottery> getLotteries() {
		return new ArrayList<Lottery>(lotteries.values());
	}

	/*
	 * Returns a list of loaded lotteries but eliminates those this user does not have access to
	 * 
	 * @param sender - user who called this method
	 * @return - the list of lotteries the sender has access to
	 */
	public static List<Lottery> getLotteries(CommandSender sender) {
		List<Lottery> list = new ArrayList<Lottery>(lotteries.values());
		for (int cntr = 0; cntr < list.size(); cntr++) {
			if (!list.get(cntr).hasAccess(sender)) {
				list.remove(cntr);
			}
		}
		return list;
	}

	/*
	 * Attempts to find a lottery
	 * 
	 * @param lotteryName - lottery to search for
	 * @return the lottery if found, if not return null
	 */
	public static Lottery getLottery(String lotteryName) {
		
		Lottery lottery = lotteries.get(lotteryName.toLowerCase());
		
		if (lottery != null)
			return lottery;
		
		for (Lottery l : lotteries.values()) {
			
			for (String alias : l.getAliases()) {
				
				if (alias.equalsIgnoreCase(lotteryName)) {
					return l;
				}
			}
		}
		
		return null;
	}

	/*
	 * Returns a lottery, if found, and checks if user has access to it
	 * 
	 * @param sender - the user who called this function
	 * @param lotteryName - the lottery name to search for
	 * 
	 * @return the lottery found
	 */
	public static Lottery getLottery(CommandSender sender, String lotteryName) {
		Lottery lottery = getLottery(lotteryName);
		if (lottery != null && !lottery.hasAccess(sender)) {
			return null;
		} else {
			return lottery;
		}
	}

	public static boolean reloadLottery(String lotteryName) {
		return reloadLottery(Bukkit.getConsoleSender(), lotteryName, true);
	}
	
	public static boolean reloadLottery(String lotteryName, boolean force) {
		return reloadLottery(Bukkit.getConsoleSender(), lotteryName, force);
	}

	/*
	 * Attempts to reload a lottery, assuming that it is not drawing
	 * 
	 * @param sender - the user who called this function
	 * @param lotteryName - lottery to search for
	 * @param clearRewards - if set to true, clears the rewards
	 */
	public static boolean reloadLottery(CommandSender sender, String lotteryName, boolean clearRewards) {
		Lottery lottery = LotteryManager.getLottery(lotteryName);
		
		if (lottery == null) {
			ChatUtils.send(sender, "lottery.error.notfound", "<lottery>", lotteryName);
			return false;
		}
		
		if (lottery.isDrawing()) {
			ChatUtils.send(sender, "lottery.error.drawing", "<lottery>", lottery.getName());
			return false;
		}
		
		ConfigurationSection lotteriesSection = getOrCreateSection(LOTTERIES_SECTION);
		
		for (String sectionName : lotteriesSection.getKeys(false)) {
			
			if (sectionName.equalsIgnoreCase(lottery.getName())) {
				ConfigurationSection lotterySection = lotteriesSection.getConfigurationSection(sectionName);
				
				 // retrieve new properties
				Map<String, Object> values = lotterySection.getValues(true);
				
				// attempt to apply the new properties to the lottery
				try {
					lottery.setProperties(sender, new LotteryProperties(values), clearRewards);
				} catch (Exception ex) {
					ChatUtils.send(sender, "lottery.exception.lottery.reload", "<lottery>", lottery.getName());
					ex.printStackTrace();
					lotteries.remove(lottery.getName().toLowerCase());
					return false;
				}
				
				// delete lottery saves
				ConfigurationSection savesSection = getOrCreateSection(SAVES_SECTION);
				deleteSection(savesSection, lottery.getName());
				
				ChatUtils.send(sender, "lottery.reload", "<lottery>", lottery.getName());
				return true;
			}
		}
		
		ChatUtils.send(sender, "lottery.error.section.notfound", "<lottery>", lottery.getName());
		return false;
	}

	/*
	 * Reloads all currently running lotteries
	 * 
	 * @param sender - user who called this function
	 */
	public static void reloadLotteries(CommandSender sender) {
		for (Lottery lottery : lotteries.values()) {
			reloadLottery(sender, lottery.getName(), true);
		}
	}

	public static int loadLotteries() {
		return loadLotteries(Bukkit.getConsoleSender(), true);
	}


	/*
	 * This is normally only called at the loading of plugin. Loads all lotteries defined in 'lotteries.yml'
	 * 
	 * @sender - user who called this function
	 * 
	 * @return - returns the number of lotteries loaded
	 */
	public static int loadLotteries(CommandSender sender, boolean clear) {
		
		if (clear) {
			lotteries.clear();
		}
		
		// copy the default lottery file from the Jar to the server plugin directory
		if (!lotteriesConfig.exists()) {
			lotteriesConfig.saveDefaultConfig();
		}
		
		ConfigurationSection section = getOrCreateSection(LOTTERIES_SECTION);
		ConfigurationSection savesSection = getOrCreateSection(SAVES_SECTION);
		int numLotteries = 0; // keep track of lotteries loaded
		
		for (String lotteryName : section.getKeys(false)) {
			
			// skip this section if it's already loaded
			if (lotteries.containsKey(lotteryName.toLowerCase()))
				continue;
			
			ConfigurationSection lotteriesSection;
			
			// grab the saved section if it is loaded, if not read from 'lotteries.yml'
			if (savesSection.contains(lotteryName)) {
				lotteriesSection = savesSection.getConfigurationSection(lotteryName);
			} else {
				lotteriesSection = section.getConfigurationSection(lotteryName);
			}
			
			Lottery lottery = new Lottery(lotteryName);
			Map<String, Object> values = lotteriesSection.getValues(true);
			
			// attempt to set the properties of the lottery
			try {
				lottery.setProperties(sender, new LotteryProperties(values));
			} catch (InvalidLotteryException ex) {
				Logger.info("lottery.exception.lottery.load", "<lottery>", lotteryName);
				ex.printStackTrace();
				continue;
			}
			
			numLotteries++;
			lotteries.put(lotteryName.toLowerCase(), lottery); // add lottery
		}
		return numLotteries;
	}

	/*
	 * Force saves all running lotteries
	 */
	public static void saveLotteries() {
		ConfigurationSection savesSection = getOrCreateSection(SAVES_SECTION);
		
		for (Lottery lottery : lotteries.values()) {
			savesSection.createSection(lottery.getName(), lottery.save());
		}
		lotteriesConfig.saveConfig();
	}
	
	/*
	 * Force saves a lottery, if it exists
	 *  
	 */
	public static void saveLottery(String lotteryName) {
		Lottery lottery = getLottery(lotteryName);
		
		if(lottery != null) {
			ConfigurationSection savesSection = getOrCreateSection(SAVES_SECTION);
			
			savesSection.createSection(lottery.getName(), lottery.save());
			lotteriesConfig.saveConfig();
		}
	}
	
	public static void listLotteries(CommandSender sender, int page) {
		listLotteries(sender, page, null);
	}

	/*
	 * Lists up to lotteries the user can view according to a specific order
	 * 
	 * @param sender - user who called this function
	 * @param page - goes to a specfic page
	 */
	public static void listLotteries(CommandSender sender, int page, String filter) {
		List<Lottery> list = getLotteries(sender);
		
		// sorts the lotteries, see @LotteryManager.LotterySorter
		Collections.sort(list, new LotterySorter(filter));
		
		// figure out the max # of pages and set page
		int len = list.size();
		int max = (len / 10) + 1;
		if (len % 10 == 0)
			max--;
		if (page > max)
			page = max;
		if (page < 1)
			page = 1;
		
		ChatUtils.sendRaw(sender, "lottery.list.headliner", "<page>", page, "<max>", max);
		
		// send user lotteries based on the page and filter
		for (int cntr = (page * 10) - 10, stop = cntr + 10; cntr < stop && cntr < len; cntr++) {
			Lottery lottery = list.get(cntr);
			List<String> aliases = lottery.getAliases();
			
			ChatUtils.sendRaw(sender, "lottery.list.token", "<number>", cntr + 1, "<lottery>", 
					lottery.getName(), "<aliases>", aliases.isEmpty() ? "[none]" : aliases);
		}
	}

	/*
	 * Check to see if a certain location is registered to a lottery
	 * 
	 * @param sign - location to check
	 */
	public static boolean isSignRegistered(Location sign) {
		
		if (LotteryPlus.isSign(sign)) // check if it is a sign
			return false;
		
		for (Lottery lottery : lotteries.values()) {
			
			if (lottery.hasRegisteredSign(sign)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Check if a certain block is registered to a lottery
	 */
	public static boolean isSignRegistered(Block sign) {
		
		for (Lottery lottery : lotteries.values()) {
			
			if (lottery.hasRegisteredSign(sign)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Returns or creates a section defined by sectionName
	 * 
	 * @param sectionName - name of section
	 * If no section exists yet, it creates one
	 */
	private static ConfigurationSection getOrCreateSection(String sectionName) {
		
		if (lotteriesConfig.getConfig().contains(sectionName)) {
			return lotteriesConfig.getConfig().getConfigurationSection(sectionName);
			
		} else {
			return lotteriesConfig.getConfig().createSection(sectionName);
		}
	}
	
	/*
	 * Private function that attempts to find a lottery section and delete its values
	 * 
	 * @param section - section to search through
	 * @param find - lottery section to delete
	 */
	private static void deleteSection(ConfigurationSection section, String find) {
		for (String key : section.getKeys(false)) {
			if (key.equalsIgnoreCase(find)) {
				section.set(key, null);
			}
		}
	}
	
	/*
	 * This class responsible for calling the onTick() method for lotteries every server second
	 */
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

	/*
	 * This class is used in LotteryManager.listLotteries() method
	 * Sorts the lotteries based on a certain filter
	 */
	private static class LotterySorter implements Comparator<Lottery> {
		private final String sort; // dictates the order of the lotteries
		
		public LotterySorter(String filter) {
			if(filter == null || filter.equals("")) {
				this.sort = Config.getString(Config.DEFAULT_FILTER);
			} else {
				this.sort = filter;
			}
		}

		public int compare(Lottery l1, Lottery l2) {
			
			// sort by how much time lotteries have left
			if (sort.equalsIgnoreCase("time")) {
				long time1 = l1.getTimeLeft();
				long time2 = l2.getTimeLeft();
				if(time1 != time2) 
					return (int) (time2 - time1);
				
			// sort by the size of the lottery pots
			} else if (sort.equalsIgnoreCase("pot")) {
				double pot1 = l1.getPot();
				double pot2 = l2.getPot();
				if(pot1 != pot2) 
					return (int) (pot2 - pot1);
				
			// list by the order dictated in the 'lotteries.yml'
			} else if(sort.equalsIgnoreCase("config")) {
				return 1;
			}
			
			// assume we sort by names
			return l1.getName().compareToIgnoreCase(l2.getName());
		}
	}

	/*
	 * Fills a section with the default values defined in the config
	 * 
	 * @param section - the section to write to
	 */
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
