package com.randude14.lotteryplus.lottery;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.randude14.lotteryplus.Logger;
import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.LotteryPlus;
import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.lottery.permission.*;
import com.randude14.lotteryplus.lottery.reward.*;
import com.randude14.lotteryplus.support.PluginSupport;
import com.randude14.lotteryplus.util.ChatUtils;
import com.randude14.lotteryplus.util.Time;
import com.randude14.lotteryplus.util.Utils;
import com.randude14.register.economy.*;

/*
 * The main lottery class where the majority of this plugin's magic comes from
 */
public class Lottery implements Runnable {
	
	// used for sign formatting
	private static final String FORMAT_REWARD = "<reward>";
	private static final String FORMAT_TIME = "<time>";
	private static final String FORMAT_NAME = "<name>";
	private static final String FORMAT_WINNER = "<winner>";
	private static final String FORMAT_TICKET_COST = "<ticketcost>";
	private static final String FORMAT_TICKET_TAX = "<ticket_tax>";
	private static final String FORMAT_POT_TAX = "<pot_tax>";
	
	private final String lotteryName;
	
	private LotteryProperties properties;
	
	// keeps users from spam buying tickets
	private final Map<String, Long> cooldowns;
	
	private final List<Permission> perms;  // permissions this 
	private final List<Reward> rewards;    // list of rewards
	private final List<Location> signs;    // signs this lottery outputs to
	
	private List<String> aliases; // aliases this lottery can be called
	private List<String> worlds;  // worlds this lottery is tied to
	private List<String> towny;   // towns this lottery is tied to


	private BukkitTask drawTask;  // task of the drawing
	
	private LotteryTimer timer;     // lottery timer
	
	private Economy econ;         // economy used to buy tickets
	
	private boolean drawSuccess;  // keep track of a successful drawing of a winner

	
	/*
	 * Init variables that stay constant
	 * Actual setting of variables happens at 
	 * @see setProperties(LotteryProperties properties, boolean forceReset)
	 */
	public Lottery(String name) {
		
		// cooldowns must be synchonized so that numbers are not skewed across
		
		this.cooldowns = Collections.synchronizedMap(new HashMap<String, Long>());
		
		this.perms = new ArrayList<Permission>();
		this.rewards = new ArrayList<Reward>();
		this.signs = new ArrayList<Location>();
		this.lotteryName = name;
		this.perms.add(new WorldPermission(this));
		
		// check towny is installed before adding to avoid errors
		if(PluginSupport.TOWNY.isInstalled()) {
			this.perms.add(new TownyPermission(this));
		}
	}
	
	/*
	 * Called after initial creation of lottery. Wraps the values into a LotteryProperties
	 * object to load from and init the values of the class
	 * 
	 * @param values - values to load from
	 * @return - whether the load was successful
	 */
	public boolean loadFrom(Map<String, Object> values) throws InvalidLotteryException {
		
		if(properties != null) {
			return reloadFrom(values, true);
		}
		
		try {
			
			LotteryProperties propertiesToLoad = new LotteryProperties(values);
			
			// load saved economy
			if(propertiesToLoad.contains("econ")) {
				econ = (Economy) propertiesToLoad.get("econ");
			} 
			
			// economy was not loaded
			if(econ == null) {
				throw new NullPointerException("Economy failed to load.");
			}
			
			loadData(propertiesToLoad);
			
			// load signs
			for(int cntr = 1; propertiesToLoad.contains("sign" + cntr); cntr++) {
				String str = propertiesToLoad.remove("sign" + cntr).toString();
				Location loc = Utils.parseToLocation(str);
				
				if(loc != null) {
					Block block = loc.getBlock();
					
					if(LotteryPlus.isSign(block)) {
						signs.add(loc);
						
					} else {
						Logger.info("lottery.error.sign.load", "<loc>", str);
					}
					
				} else {
					Logger.info("lottery.error.loc.load", "<line>", str);
				}
			}
			
			properties = propertiesToLoad;
			
			return true;
			
		} catch (Exception ex) {
			throw new NullPointerException(ChatUtils.getRawName("lottery.exception.economy.load", 
					"<lottery>", lotteryName));
		}

	}
	
	/*
	 * Called to reload the lottery. Wraps the values into a LotteryProperties
	 * object to load from and init the values of the class. Depending on forceReset
	 * and drawSuccess, this function does different tasks. forceReset forces the
	 * lottery to reset despite the state of the lottery. While if drawSuccess and
	 * forceReset are both false, the lottery resets.
	 * 
	 * @param values - values to load from
	 * @return - whether the load was successful
	 */
	public boolean reloadFrom(Map<String, Object> values, boolean forceReset) 
			throws InvalidLotteryException {
		
		if(properties == null) {
			return loadFrom(values);
		}
		
		try {
			
			LotteryProperties propertiesToLoad = new LotteryProperties(values);
			
			econ = null;
			
			// create new data if draw was successful or force resetting
			if (forceReset || drawSuccess) {
				loadData(propertiesToLoad);
				
			// reset if draw was not successful
			} else {
				resetLottery(propertiesToLoad);
			}
			
			// load economy
			if(propertiesToLoad.getBoolean(Config.DEFAULT_USE_VAULT) && 
					PluginSupport.VAULT.isInstalled()) {
				econ = new VaultEconomy();
				
			} else {
				String materialID = propertiesToLoad.getString(Config.DEFAULT_MATERIAL);
				String name = propertiesToLoad.getString(Config.DEFAULT_MATERIAL_NAME);
				econ = new MaterialEconomy(materialID, name);
			}
			
			properties = propertiesToLoad;
			
			return true;
			
		} catch (Exception ex) {
			throw new InvalidLotteryException(ChatUtils.getRawName("lottery.exception.economy.load", 
					"<lottery>", lotteryName), ex);
		}

	}
	
	/*
	 * Internal method for loading data properties from
	 */
	private void loadData(LotteryProperties propertiesToLoad) {
		
		// Check for negative properties
		double time = propertiesToLoad.getDouble(Config.DEFAULT_TIME);
		double pot = propertiesToLoad.getDouble(Config.DEFAULT_POT);
		double ticketCost = propertiesToLoad.getDouble(Config.DEFAULT_TICKET_COST);
		
		if (time < 0) {
			throw new InvalidLotteryException( 
					ChatUtils.getRawName("lottery.error.negative.time", "<time>", time));
		} else if (pot < 0) {
			throw new InvalidLotteryException( 
					ChatUtils.getRawName("lottery.error.negative.pot", "<pot>", pot));
		} else if (ticketCost < 0) {
			throw new InvalidLotteryException(
					ChatUtils.getRawName("lottery.error.negative.ticket-cost", 
							"<ticket_cost>", ticketCost));
		}
		
		// copy over winner
		if(properties != null) {
			propertiesToLoad.set("winner", properties.getString("winner", ""));
		}
		
		// load timer
		if(propertiesToLoad.getBoolean(Config.DEFAULT_USE_TIMER)) {
			this.timer = new LotteryTimer();
		} else {
			this.timer = new InfiniteTimer();
		}
		
		timer.setRunning(true);
		timer.load(propertiesToLoad);
		
		
		// load items
		rewards.clear();
		List<ItemStack> items = Utils.getItemStacks(
				propertiesToLoad.getString(Config.DEFAULT_ITEM_REWARDS));
		
		for(ItemStack item : items) {
			rewards.add(new ItemReward(item));
		}
		
		if (econ == null) {
			
			if(propertiesToLoad.getBoolean(Config.DEFAULT_USE_VAULT) && PluginSupport.VAULT.isInstalled()) {
				econ = new VaultEconomy();
				
			} else {
				String materialID = propertiesToLoad.getString(Config.DEFAULT_MATERIAL);
				String name = propertiesToLoad.getString(Config.DEFAULT_MATERIAL_NAME);
				econ = new MaterialEconomy(materialID, name);
			}
		}
		
		
		
		// WORLDS
		worlds = propertiesToLoad.getStringList(Config.DEFAULT_WORLDS);
		
		// TOWNY
		towny = propertiesToLoad.getStringList(Config.DEFAULT_TOWNY);
		
		// ALIASES
		aliases = propertiesToLoad.getStringList(Config.DEFAULT_ALIASES);
		
		cooldowns.clear(); //clear the cooldowns
		
		if(propertiesToLoad.contains("item-only")) {
			propertiesToLoad.set(Config.DEFAULT_USE_POT, 
					!propertiesToLoad.getBoolean("item-only"));
			propertiesToLoad.remove("item-only");
		}
	}
	
	/*
	 * Internal method called if a drawing failed to find a winner
	 */
	private void resetLottery(LotteryProperties propertiesToLoad) {
		
		if(properties == null || drawSuccess) {
			throw new NullPointerException("This method is only used for restting after a failed drawing.");
		}
		
		// reset and add onto values defined
		timer.reset(properties);
		propertiesToLoad.set(Config.DEFAULT_TICKET_COST, properties.getDouble(
				Config.DEFAULT_TICKET_COST) + properties.getDouble(Config.DEFAULT_RESET_ADD_TICKET_COST));
		propertiesToLoad.set(Config.DEFAULT_MAX_TICKETS, properties.getInt(
				Config.DEFAULT_MAX_TICKETS) + properties.getInt(Config.DEFAULT_RESET_ADD_MAX_TICKETS));
		propertiesToLoad.set(Config.DEFAULT_MAX_PLAYERS, properties.getInt(
				Config.DEFAULT_MAX_PLAYERS) + properties.getInt(Config.DEFAULT_RESET_ADD_MAX_PLAYERS));
		propertiesToLoad.set(Config.DEFAULT_MIN_PLAYERS, properties.getInt(
				Config.DEFAULT_MIN_PLAYERS) + properties.getInt(Config.DEFAULT_RESET_ADD_MIN_PLAYERS));
		
		if (! propertiesToLoad.getBoolean(Config.DEFAULT_KEEP_TICKETS)) {
			clearPlayers();
		}
		
		// clear rewards
		if(propertiesToLoad.getBoolean(Config.DEFAULT_CLEAR_REWARDS)) {
			rewards.clear();
			
		// or add more item rewards
		} else {
			// load reset items
			List<ItemStack> items = Utils.getItemStacks(
					propertiesToLoad.getString(Config.DEFAULT_RESET_ADD_ITEM_REWARDS));
			
			for (ItemStack item : items) {
				rewards.add(new ItemReward(item));
			}
		}
		
		// add on to pot
		if(! propertiesToLoad.getBoolean(Config.DEFAULT_CLEAR_POT)) {
			propertiesToLoad.set(Config.DEFAULT_POT, properties.getDouble(
					Config.DEFAULT_POT) + properties.getDouble(Config.DEFAULT_RESET_ADD_POT));
		}
		
		// keep tickets
		if(properties.getBoolean(Config.DEFAULT_KEEP_TICKETS)) {	
			for(OfflinePlayer player : getPlayers()) {
				int tickets = getTicketsBought(player);
				String name = Utils.getUniqueName(player);
				propertiesToLoad.set("players." + name, tickets);
			}
		}
	}

	public final String getName() {
		return lotteryName;
	}

	public boolean isDrawing() {
		return properties.getBoolean("drawing", false);
	}

	public boolean isRunning() {
		return timer.isRunning();
	}

	public long getTimeLeft() {
		return timer.getTime();
	}
	
	public double getPot() {
		
		// return 0 if this lottery does not have a pot
		if(!properties.getBoolean(Config.DEFAULT_USE_POT))
			return 0;
		
		return properties.getDouble(Config.DEFAULT_POT);
	}
	
	public Economy getEconomy() {
		return econ;
	}
	
	public List<String> getWorlds() {
		return worlds;
	}
	
	public List<String> getTowny() {
		return towny;
	}
	
	public List<String> getAliases() {
		return aliases;
	}

	//called every second
	public synchronized void onTick() {
		timer.onTick();
		if (timer.isOver()) {
			this.draw();
				return;
		}
		printWarningTimes();
		updateCooldowns();
		updateSigns();
	}
	
	/*
	 * Checks the properties and prints out when the time is at certain intervals
	 * 
	 *  ///////////// INSERT FROM CONFIG /////////////////
	 *  
	 * used to alert players about the current time of the lottery
     * how to:
     *  
     * '24h 12h 6h 3h 1h'
     * this will alert the players at 24, 12, 6, 3, and 1 hours
     * 
     * <amount><symbol>
     * symbols:
     * w: weeks
     * d: days
     * h: hours
     * m: minutes
     * s: seconds
     *
     * #look for 'lottery.mess.warning' to 'lang.properties' to edit the message
	 */
	private void printWarningTimes() {
		List<String> warningTimes = properties.getStringList(Config.DEFAULT_WARNING_TIMES);
		
		// if list contains
		if(!warningTimes.isEmpty()) {
			
			for(String timeStr : warningTimes) {
				
				if(timeStr.isEmpty()) // check empty string
					continue;
				
				int len = timeStr.length();
				
				try {
					long time = Long.parseLong( timeStr.substring(0, len-1) );
					String timeMess = time + " ";
					
					// grab character at end
					char c = Character.toLowerCase( timeStr.charAt(len-1) );
					
					// find time in appropriate unit and add to message
					switch(c) {
					
						case 'w':
							time = Time.WEEK.multi(time);
							timeMess += ChatUtils.getRawName("lottery.time.weeks");
					   		break;
					   		
						case 'd':
							time = Time.DAY.multi(time);
							timeMess += ChatUtils.getRawName("lottery.time.days");
					    	break;
					    	
						case 'h':
							time = Time.HOUR.multi(time);
							timeMess += ChatUtils.getRawName("lottery.time.hours");
					    	break;
					    	
						case 'm':
							time = Time.MINUTE.multi(time);
							timeMess += ChatUtils.getRawName("lottery.time.minutes");
					    	break;
					    	
					    	// if no time unit specified, assume seconds
						default:
							//no need to do anything with time, already in seconds
							timeMess += ChatUtils.getRawName("lottery.time.seconds");
							break;
					}
					
					if(timer.getTime() == time) { // if this is a warning time, print out
						broadcastRaw("lottery.mess.warning", "<name>", lotteryName, "<time>", timeMess);
					}
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	public void updateSigns() {
		updateSigns(false);
	}
	
	/*
	 * Update the signs this lottery with information from the lottery depending on its state
	 * 
	 * @param over - whether the lottery is over, indicates that the lottery will not be resetting
	 */
	private void updateSigns(boolean over) {
		final String line1 = ChatUtils.replaceColorCodes(Config.getString(Config.SIGN_TAG));
		final String line2, line3, line4;
		
		if(over) {
			line2 = ChatUtils.replaceColorCodes( format(Config.getString(Config.OVER_SIGN_LINE_TWO)) );
			line3 = ChatUtils.replaceColorCodes( format(Config.getString(Config.OVER_SIGN_LINE_THREE)) );
			line4 = ChatUtils.replaceColorCodes( format(Config.getString(Config.OVER_SIGN_LINE_FOUR)) );
			
		} else if(isDrawing()) {
			line2 = ChatUtils.replaceColorCodes( format(Config.getString(Config.DRAWING_SIGN_LINE_TWO)) );
			line3 = ChatUtils.replaceColorCodes( format(Config.getString(Config.DRAWING_SIGN_LINE_THREE)) );
			line4 = ChatUtils.replaceColorCodes( format(Config.getString(Config.DRAWING_SIGN_LINE_FOUR)) );
			
		} else {
			line2 = ChatUtils.replaceColorCodes( format(Config.getString(Config.UPDATE_SIGN_LINE_TWO)) );
			line3 = ChatUtils.replaceColorCodes( format(Config.getString(Config.UPDATE_SIGN_LINE_THREE)) );
			line4 = ChatUtils.replaceColorCodes( format(Config.getString(Config.UPDATE_SIGN_LINE_FOUR)) );
		}
		
		// schedule a task so sign change can be synced with server
		LotteryPlus.scheduleSyncDelayedTask( () -> {
			
			for(Location loc : signs) {
					
				Block block = loc.getBlock();
				Sign sign = (Sign) block.getState();
					
				sign.setLine(0, line1);
				sign.setLine(1, line2);
				sign.setLine(2, line3);
				sign.setLine(3, line4);
					
				sign.update(true);
			}
				
		}, 0);
	}
	
	/*
	 * Updates the player cooldowns. If there cooldown time is 0, 
	 * remove from list so they can buy tickets again
	 */
	private void updateCooldowns() {
		
		synchronized(cooldowns) {
			
			// use map iterator so we can change the value
			Iterator<Map.Entry<String, Long>> it = cooldowns.entrySet().iterator();
			
			while(it.hasNext()) {
				Map.Entry<String, Long> entry = it.next();
				long cooldown = entry.getValue();
				
				// cooldown time is over
				if(cooldown-- <= 0)
					it.remove();
				else
					entry.setValue(cooldown);
			}
		}
	}

	/*
	 * Given a string, it replaces certain keywords with information from the lottery
	 * Primarily used by the method updateSigns(boolean over)
	 * 
	 * @param mess - the message to edit
	 * @return - the edited message
	 * 
	 *  ///// INSERT FROM CONFIG /////////////
	 * various sign formats used in different states of a lottery
     *   
     * <name> - name of the lottery
     * <ticketcost> - cost of a ticket for the lottery
     * <reward> - reward of the lottery
     * <time> - time until drawing
     * <winner> - most recent winner of the lottery
     * <ticket_tax> - ticket tax of the lottery
     * <pot_tax> - pot tax of the lottery
	 */
	public String format(String mess) {
		String winner = properties.getString("winner", "");
		
		return mess
				.replace(FORMAT_TIME, timer.format())
				.replace(FORMAT_REWARD, formatReward())
				.replace(FORMAT_NAME, lotteryName)
				.replace(FORMAT_WINNER, ( !winner.isEmpty() ) ? winner : ChatUtils.getRawName("lottery.error.nowinner") )
				.replace(FORMAT_TICKET_COST, econ.format(properties.getDouble(Config.DEFAULT_TICKET_COST)))
				.replace(FORMAT_TICKET_TAX, String.format("%,.2f", properties.getDouble(Config.DEFAULT_TICKET_TAX)))
				.replace(FORMAT_POT_TAX, String.format("%,.2f", properties.getDouble(Config.DEFAULT_POT_TAX)));
	}

	/*
	 * Returns a small snippet of the rewards this lottery has
	 * @return - string containing the reward this lottery has
	 */
	private String formatReward() {
		
		// return pot in server's currency
		if (properties.getBoolean(Config.DEFAULT_USE_POT))
			return econ.format(properties.getDouble(Config.DEFAULT_POT));
		
		// return # of item rewards this lottery has
		int num = 0;
		
		for (int cntr = 0; cntr < rewards.size(); cntr++) {
			
			if (rewards instanceof ItemReward)
				num++;
		}
		
		return ChatUtils.getRawName("lottery.reward.items", "<number>", num);
	}

	/*
	 * Allows an admin to add more money to a lottery's pot
	 * 
	 * @param sender - user adding pot to lottery
	 * @param add - amount to add
	 * 
	 * @return - whether the amount was added to the pot
	 */
	public synchronized boolean addToPot(CommandSender sender, double add) {
		
		// check if this lottery even has a pot to add to
		if (!properties.getBoolean(Config.DEFAULT_USE_POT)) {
			ChatUtils.send(sender, "lottery.error.nopot", "<lottery>", lotteryName);
			return false;
		}
		
		if(sender instanceof Player) {
			
			Player player = (Player) sender;
			
			if(!econ.hasAccount(player)) { // check for account
				ChatUtils.send(player, "lottery.error.noaccount");
				return false;
			}
			
			if(!econ.hasEnough(player, add)) { // check if they have enough
				ChatUtils.send(player, "lottery.error.notenough", "<money>", econ.format(add));
				return false;
			}
			
			econ.withdraw(player, add);
		}
		
		// add amount to pot
		double pot = properties.getDouble(Config.DEFAULT_POT);
		properties.set(Config.DEFAULT_POT, pot + add);
		
		ChatUtils.send(sender, "plugin.command.atp.mess", "<money>", econ.format(add), "<lottery>", lotteryName);
		LotteryManager.saveLottery(lotteryName);
		return true;
	}
	
	/*
	 * Broadcast a message to the players that have access to the lottery
	 * @param code - code that points to the message in lang.properties
	 * @param args - tags to replace in the message
	 * 
	 * @see com.randude14.lotteryplus.util.ChatUtils.broadcast(List<Player> players, String code, Object.. args);
	 */
	public void broadcast(String code, Object... args) {
		ChatUtils.broadcast(getPlayersToBroadcast(), code, args);
	}
	
	/*
	 * Broadcast a message to the players that have access to the lottery
	 * @param code - code that points to the message in lang.properties
	 * @param args - tags to replace in the message
	 * 
	 * @see com.randude14.lotteryplus.util.ChatUtils.broadcastRaw(List<Player> players, String code, Object.. args);
	 */
	public void broadcastRaw(String code, Object... args) {
		ChatUtils.broadcastRaw(getPlayersToBroadcast(), code, args);
	}
	
	/*
	 * @return - players that have access to the lottery
	 * @see - access to the lottery does not mean that they have entered
	 */
	private List<Player> getPlayersToBroadcast() {
		List<Player> players = new ArrayList<Player>();
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			
			// add player if they have access
			if(hasAccess(p)) {
				players.add(p);
			}
		}
		
		return players;
	}
	
	/*
	 * Check access of a user and send an error message if they do not
	 * 
	 * @param sender - user to check access for
	 * @return - whether the user has access
	 */
	public boolean checkAccess(CommandSender sender) {
		for(Permission perm : perms) {
			if(!perm.checkAccess(sender)) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Check access of a user
	 * 
	 * @param sender - user to check access for
	 * @return - whether the user has access
	 */
	public boolean hasAccess(CommandSender sender) {
		for(Permission perm : perms) {
			if(!perm.hasAccess(sender)) {
				return false;
			}
		}
		return true;
	}

	/*
	 * @return - the lottery saved in a map
	 */
	public Map<String, Object> save() {
		timer.save(properties);          // save timer
		properties.remove("drawing");    // remove drawing state
		properties.set("econ", econ);    // save economy
		Map<String, Object> map = new HashMap<String, Object>();
		map.putAll(properties.getValues()); // put all other values inside the map
		
		// save signs
		for(int cntr = 0;cntr < signs.size();cntr++) {
			map.put("sign" + (cntr+1), Utils.parseLocation(signs.get(cntr)));
		}
		
		return map;
	}
	
	/*
	 * Register a sign to this lottery
	 * @param sender - user attempting register sign
	 * @param sign - sign attempting to register
	 * @return - whether the sign was registered
	 */
	public boolean registerSign(CommandSender sender, Location sign) {
		
		// check if user has access
		if(!checkAccess(sender)) {
			return false;
		}
		
		// check if sign has been registered with any lottery
		if(hasRegisteredSign(sign.getBlock())) 
			return false;
		
		// add sign and update it
		signs.add(sign);
		updateSigns();
		return true;
	}
	
	/*
	 *  Check if the sign at a location is registered with this lottery
	 *  @param loc - location to check
	 *  @return - if sign at location is registered to this lottery
	 */
	public boolean hasRegisteredSign(Location loc) {
		
		// check is a sign first
		if(!LotteryPlus.isSign(loc))
			return false;
		
		for(Location sign : signs) {
			
			// locations point to the same point
			if(Utils.locsInBounds(loc, sign)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRegisteredSign(Block blockToTest) {
		return hasRegisteredSign(blockToTest.getLocation());
	}
	
	/*
	 * Unregister a sign to this lottery
	 * @param loc - location to unregister
	 * @return - whether sign was unregistered
	 */
	public boolean unregisterSign(Location loc) {
		
		for(int cntr = 0;cntr < signs.size();cntr++) {
			
			Location sign = signs.get(cntr);
			
			// locations are at the same point
			if(Utils.locsInBounds(sign, loc)) {
				signs.remove(cntr);
				return true;
			}
			
		}
		return false;
	}
	
	public boolean unregisterSign(Block sign) {
		return unregisterSign(sign.getLocation());
	}
	
	/*
	 * Called when a user votes for this server and tries to find user with playerName to reward tickets to
	 * @param playerName - user to reward tickets to
	 */
	public void onVote(String playerName) {
		int reward = properties.getInt(Config.DEFAULT_VOTIFIER_REWARD);
		if(reward > 0) {
			String prevWinner = properties.getString("winner", "");
			
			// can't win again
			if(prevWinner.equalsIgnoreCase(playerName) && !properties.getBoolean(Config.DEFAULT_WIN_AGAIN)) {
				return;
			}
			
			OfflinePlayer player = Utils.getOfflinePlayer(playerName);
			
			// could not find player, log to server console that we could not find it
			if(player == null) {
				Logger.info("lottery.error.vote.noaccount", "<player>", playerName);
				return;
			}
			
			int ticketLimit = properties.getInt(Config.DEFAULT_TICKET_LIMIT);
				
			int bought = getTicketsBought(player);
			
			// check if player can have more tickets
			if (ticketLimit > 0) {
				int total = getTotalTicketsBought();
				
				if (total >= ticketLimit) {
					return;
				}
				
				if (reward + bought > ticketLimit) {
					return;
				}
			}
			
			// add tickets to player
			addTickets(player, reward);
			broadcast("lottery.vote.reward.mess", "<player>", player, "<number>", reward, "<lottery>", lotteryName);
			LotteryManager.saveLottery(lotteryName);
		}
	}
	
	/*
	 * Internal method that checks if a player can buy a number of tickets
	 * @param player - player trying to buy tickets
	 * @param tickets- numbner of tickets player is trying to buy
	 * @return - whether player can buy tickets
	 */
	private boolean canBuy(Player player, int tickets) {
		
		// check if tickets can be purchased
		if(!properties.getBoolean(Config.DEFAULT_BUY_TICKETS)) {
			ChatUtils.sendRaw(player, "lottery.error.tickets.disabled", "<lottery>", lotteryName);
			return false;
		}
		
		// check that they haven't entered a negative number
		if(tickets <= 0) {
			ChatUtils.sendRaw(player, "lottery.error.tickets.negative");
			return false;
		}
		
		// check if they were a previous winner
		String prevWinner = properties.getString("winner", "");
		
		if(prevWinner.equalsIgnoreCase(player.getName()) && !properties.getBoolean(Config.DEFAULT_WIN_AGAIN)) {
			ChatUtils.send(player, "lottery.error.already-won", "<lottery>", lotteryName);
			return false;
		}
		
		// check access
		if(!checkAccess(player)) {
			return false;
		}
		
		// check lottery drawing
		if (isDrawing() && !Config.getBoolean(Config.BUY_DURING_DRAW)) {
			ChatUtils.sendRaw(player, "lottery.error.drawing");
			return false;
		}
		
		// check ticket limit
		int ticketLimit = properties.getInt(Config.DEFAULT_TICKET_LIMIT);
		int bought = getTicketsBought(player);
		
		if (ticketLimit > 0 && bought >= ticketLimit) {
			ChatUtils.sendRaw(player, "lottery.error.tickets.soldout");
			return false;
		}
		
		
		String name = player.getName();
		int maxTickets = properties.getInt(Config.DEFAULT_MAX_TICKETS);
		int maxPlayers = properties.getInt(Config.DEFAULT_MAX_PLAYERS);
		int playersEntered = getPlayersEntered();
		
		// check that they haven't gotten over the max
		if (maxTickets > 0) {
			if (bought >= maxTickets) {
				ChatUtils.sendRaw(player, "lottery.error.tickets.anymore");
				return false;
			} else if (bought + tickets > maxTickets) {
				ChatUtils.sendRaw(player, "lottery.error.tickets.toomuch");
				return false;
			}
		}
		
		// check if there can be more players
		if(maxPlayers > 0) {
			if (playersEntered >= maxPlayers) {
				ChatUtils.sendRaw(player, "lottery.error.players.nomore");
				return false;
			}
		}
		
		// check if their cooldown from buying tickets earlier is over
		synchronized(cooldowns) {
			if(cooldowns.containsKey(name)) {
				ChatUtils.sendRaw(player, "lottery.error.cooldown", "<time>", cooldowns.get(name));
				return false;
			}
		}
		return true;
	}

	/*
	 * Used to let a player buy tickets for a lottery
	 * @param player - player buying
	 * @param tickets - number of tickets to buy
	 */
	public synchronized boolean buyTickets(Player player, int tickets) {
		
		// check if they can buy
		if (!canBuy(player, tickets)) {
			return false;
		}
		
		// check for account
		if (!econ.hasAccount(player)) {
			ChatUtils.sendRaw(player, "lottery.error.noaccount");
			return false;
		}
		
		String taxAccount = properties.getString(Config.DEFAULT_TAX_ACCOUNT);
		double ticketCost = properties.getDouble(Config.DEFAULT_TICKET_COST);
		double total = ticketCost * (double) tickets;
		double ticketTax = properties.getDouble(Config.DEFAULT_TICKET_TAX);
		double add = ticketCost - (ticketCost * (ticketTax / 100));
		double d = add * (double) tickets;
		double taxes = total - d;
		
		// check they have enough
		if (!econ.hasEnough(player, total)) {
			ChatUtils.sendRaw(player, "lottery.error.tickets.notenough");
			return false;
		}
		econ.withdraw(player, total);
		
		// tax money to an account
		if(taxAccount != null && econ.hasAccount(taxAccount)) {
			double left = econ.deposit(taxAccount, taxes);
			if(left > 0) {
				List<Reward> list = new ArrayList<Reward>();
				list.add(new PotReward(econ, left));
				OfflinePlayer offlineplayer = Utils.getOfflinePlayer(taxAccount);
				LotteryPlus.getRewardsManager().addRewardClaim(offlineplayer, lotteryName, list);
			}
		}
		
		// add tickets
		addTickets(player, tickets);
		
		// announce player buying tickets
		ChatUtils.sendRaw(player, "lottery.tickets.mess", "<tickets>", tickets, "<lottery>", lotteryName);
		
		// announce the amount of pot in the lottery now
		if (properties.getBoolean(Config.DEFAULT_USE_POT)) {
			ChatUtils.sendRaw(player, "lottery.pot.mess", "<money>", econ.format(d), "<lottery>", lotteryName);
			properties.set(Config.DEFAULT_POT,
					properties.getDouble(Config.DEFAULT_POT) + d);
		}
		
		// add or subtract timer depending on the cooldown or warmup
		long cooldown = properties.getLong(Config.DEFAULT_COOLDOWN);
		long warmup = properties.getLong(Config.DEFAULT_WARMUP);
		long time = timer.getTime() - cooldown + warmup;
		timer.setTime(time);
		
		// add delay so player can't spam ticket buying
		long delay = Config.getLong(Config.BUY_DELAY);
		
		if(delay >= 0) {
			synchronized(cooldowns) {
				cooldowns.put(player.getName(), delay);
			}
		}
		
		updateSigns();
		LotteryManager.saveLottery(lotteryName);
		return true;
	}

	public synchronized boolean rewardPlayer(CommandSender rewarder, String playerName, int tickets) {
		int ticketLimit = properties.getInt(Config.DEFAULT_TICKET_LIMIT);
		String prevWinner = properties.getString("winner", "");
		
		if(prevWinner.equalsIgnoreCase(playerName) && !properties.getBoolean(Config.DEFAULT_WIN_AGAIN)) {
			ChatUtils.send(rewarder, "lottery.error.already-won.reward", "<lottery>", lotteryName, "<player>", playerName);
			return false;
		}
		
		OfflinePlayer player = Utils.getOfflinePlayer(playerName);
		
		int num = getTicketsBought(player);
		if (ticketLimit > 0) {
			int total = getTotalTicketsBought();
			if (total >= ticketLimit) {
				ChatUtils.send(rewarder, "lottery.error.tickets.soldout", "<lottery>", lotteryName);
				return false;
			}
			if (tickets + num > ticketLimit) {
				ChatUtils.send(rewarder, "plugin.command.reward.error.toomany");
				return false;
			}
		}
		
		if (player.isOnline()) {
			ChatUtils.send(player.getPlayer(), "plugin.command.reward.player.mess", "<tickets>", tickets, "<lottery>", lotteryName);
		}
		updateSigns();
		LotteryManager.saveLottery(lotteryName);
		return true;
	}

	public boolean isOver() {
		int ticketLimit = properties.getInt(Config.DEFAULT_TICKET_LIMIT);
		if(ticketLimit <= 0) {
			return false;
		}
		int total = getTotalTicketsBought();
		if(total < ticketLimit) {
			return false;
		}
		int minPlayers = properties.getInt(Config.DEFAULT_MIN_PLAYERS);
		if(minPlayers <= 0) return false;
		int entered = getPlayersEntered();
		return entered >= minPlayers && entered >= 1;
	}

	public void sendInfo(CommandSender sender) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(!checkAccess(player)) {
				return;
			}
		}
		ChatUtils.sendRaw(sender, "lottery.info.time", "<time>", timer.format());
		ChatUtils.sendRaw(sender, "lottery.info.drawing", "<is_drawing>", isDrawing());
		if (properties.getBoolean(Config.DEFAULT_USE_POT)) {
			ChatUtils.sendRaw(sender, "lottery.info.pot", "<pot>", econ.format(properties.getDouble(Config.DEFAULT_POT)));
		}
		for (Reward reward : rewards) {
			ChatUtils.sendRaw(sender, "lottery.info.reward", "<reward>", reward.getInfo());
		}
		ChatUtils.sendRaw(sender, "lottery.info.ticket-cost", "<ticket_cost>", econ.format(properties.getDouble(Config.DEFAULT_TICKET_COST)));
		ChatUtils.sendRaw(sender, "lottery.info.ticket-tax", "<ticket_tax>", String.format("%,.2f", properties.getDouble(Config.DEFAULT_TICKET_TAX)));
		ChatUtils.sendRaw(sender, "lottery.info.pot-tax", "<pot_tax>", String.format("%,.2f", properties.getDouble(Config.DEFAULT_POT_TAX)));
		ChatUtils.sendRaw(sender, "lottery.info.players", "<players>", getPlayersEntered());
		ChatUtils.sendRaw(sender, "lottery.info.tickets.left", "<number>", formatTicketsLeft());
		if (sender instanceof Player)
			ChatUtils.sendRaw(sender, "lottery.info.tickets.bought", "<number>", getTicketsBought((Player) sender));
	}

	private String formatTicketsLeft() {
		int ticketLimit = properties.getInt(Config.DEFAULT_TICKET_LIMIT);
		
		if (ticketLimit <= 0)
			return "no limit";
		
		int left = ticketLimit - getTotalTicketsBought();
		return (left > 0) ? "" + left : "none";
	}

	public int getPlayersEntered() {
		Set<String> players = new HashSet<String>();
		for (String key : properties.keySet()) {
			if (key.startsWith("players.")) {
				int index = key.indexOf('.');
				String player = key.substring(index + 1);
				int num = properties.getInt(key, 0);
				for (int cntr = 0; cntr < num; cntr++) {
					players.add(player);
				}
			}
		}
		return players.size();
	}

	public List<OfflinePlayer> getPlayers() {
		List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
		for (String key : properties.keySet()) {
			if (key.startsWith("players.")) {
				int index = key.indexOf('.');
				String playerName = key.substring(index + 1);
				
				OfflinePlayer player = Utils.getOfflinePlayer(playerName);		
				int num = properties.getInt(key, 0);
				for (int cntr = 0; cntr < num; cntr++) {
					players.add(player);
				}
			}
		}
		return players;
	}
	
	public int getTotalTicketsBought() {
		int total = 0;
		for (String key : properties.keySet()) {
			if (key.startsWith("players.")) {
				int num = properties.getInt(key, 0);
				total += num;
			}
		}
		return total;
	}

	public int getTicketsBought(OfflinePlayer player) {
		String name = Utils.getUniqueName(player);
		
		// search for old save and convert to new save if exists
		if (properties.contains("players." + player.getName())) {
			try {
				int tickets = Integer.parseInt( 
						properties.remove("players." + player.getName()).toString() );
				
				properties.set("players." + Utils.getUniqueName(player), tickets);				
				return tickets;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		
		// search for new save and return if it exists
		} else if (properties.contains("players." + name)) {
			return properties.getInt("players." + Utils.getUniqueName(player), 0);
		}
		
		return 0;
	}
	
	public void addTickets(OfflinePlayer player, int add) {
		String name = Utils.getUniqueName(player);
		
		int tickets = getTicketsBought(player);
		properties.set("players." + name, tickets + add);
	}
	
	public void subTickets(OfflinePlayer player, int remove) {
		String name = Utils.getUniqueName(player);
		
		int tickets = getTicketsBought(player);
		
		if ( (tickets - remove) < 0 ) {
			properties.remove("players." + name);
		} else {
			properties.set("players." + name, tickets - remove);
		}    
	}
	
	public void draw() {
		draw(null);
	}

	// sender: sender that initiated force draw, may be null if drawing was done
	// 'naturally'
	public synchronized void draw(CommandSender sender) {
		
		if (properties.getBoolean("drawing", false)) {
			if (sender != null) {
				ChatUtils.send(sender, "lottery.error.drawing", "<lottery>", lotteryName);
			}
			return;
		}
		
		if (sender == null) {
			broadcast("lottery.drawing.mess", "<lottery>", lotteryName);
		} else {
			broadcast("lottery.drawing.force.mess", "<lottery>", lotteryName, "<player>", sender.getName());
		}
		
		long delay = Config.getLong(Config.DRAW_DELAY);
		drawTask = LotteryPlus.scheduleAsyncDelayedTask(this, Time.SERVER_SECOND.multi(delay));
		timer.setRunning(false);
		properties.set("drawing", true);
		updateSigns();
	}

	public synchronized void cancelDrawing() {
		if(drawTask == null) return;
		drawTask.cancel();
	}

	private void clearPlayers() {
		List<String> keys = new ArrayList<String>(properties.keySet());
		for (int cntr = 0; cntr < keys.size(); cntr++) {
			String key = keys.get(cntr);
			if (key.startsWith("players.")) {
				properties.remove(key);
			}
		}
	}

	public void run() {
		try {
			// clear cooldowns
			synchronized(cooldowns) {
				cooldowns.clear();
			}
			drawTask = null;
			int entered = getPlayersEntered();
			
			// check if there were enough players entered in drawing
			if (entered < properties.getInt(Config.DEFAULT_MIN_PLAYERS) || entered < 1) {
				broadcast("lottery.error.drawing.notenough");
				properties.set("drawing", false);
				LotteryManager.reloadLottery(lotteryName, false);
				return;
			}
			
			// pick winner
			List<OfflinePlayer> players = getPlayers();
			OfflinePlayer winner = Lottery.pickRandomPlayer(players, properties.getInt(Config.DEFAULT_TICKET_LIMIT));
			if (winner == null) {
				broadcast("lottery.error.drawing.nowinner");
				properties.set("drawing", false);
				drawSuccess = false;
				LotteryManager.reloadLottery(lotteryName, false);
				return;
			}
			
			// broadcast winner		
			broadcast("lottery.drawing.winner.mess", "<winner>", winner.getName());
			
			// add pot reward
			if (properties.getBoolean(Config.DEFAULT_USE_POT)) {
				double pot = properties.getDouble(Config.DEFAULT_POT);
				double potTax = properties.getDouble(Config.DEFAULT_POT_TAX);
				double taxes = pot * (potTax / 100);
				double winnings = pot - taxes;
				
				String taxAccount = properties.getString(Config.DEFAULT_TAX_ACCOUNT);
				
				if(taxAccount != null && econ.hasAccount(taxAccount)) {
					double left = econ.deposit(taxAccount, taxes);
					if(left > 0) {
						List<Reward> list = new ArrayList<Reward>();
						list.add(new PotReward(econ, left));
						OfflinePlayer offlineplayer = Utils.getOfflinePlayer(taxAccount);
						LotteryPlus.getRewardsManager().addRewardClaim(offlineplayer, lotteryName, list);
					}
				}
				
				rewards.add(0, new PotReward(econ, winnings));
			}
			
			// log winner
			StringBuilder logWinner = new StringBuilder(lotteryName + ": " + winner);
			
			for (Reward reward : rewards) {
				logWinner.append(", ");
				logWinner.append("[" + reward.getInfo() + "]");
			}			
			
			LotteryPlus.getWinnersManager().logWinner(logWinner.toString());
			
			// reward winner if online
			if(winner.isOnline())
				LotteryPlus.getRewardsManager().rewardPlayer(winner.getPlayer(), lotteryName, rewards);
			
			// set up for next drawing
			properties.set("winner", winner);
			properties.set("drawing", false);
			clearPlayers();
			drawSuccess = true;
			
			if (properties.getBoolean(Config.DEFAULT_REPEAT)) {
				LotteryManager.reloadLottery(lotteryName);
			} else {
				LotteryManager.unloadLottery(lotteryName);
				updateSigns(true);
			}
			
		} catch (Exception ex) {
			Logger.info("lottery.exception.drawing", "<lottery>", lotteryName);
			properties.set("drawing", false);
			ex.printStackTrace();
			drawSuccess = false;
		}
	}

	private static OfflinePlayer pickRandomPlayer(List<OfflinePlayer> players, int ticketLimit) {
		SecureRandom rand = new SecureRandom();
		Collections.shuffle(players, rand);
		 
		if (ticketLimit <= 0) {
			return players.get(rand.nextInt(players.size()));
			
		} else {
			int winningNumber = rand.nextInt(ticketLimit);
			if(winningNumber >= players.size()) 
				return null;
			return players.get(winningNumber);
		}
	}
	
	public boolean equals(Lottery other) {
		return other.getName().equalsIgnoreCase(lotteryName);
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public String toString() {
		return lotteryName;
	}
}
