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

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.randude14.lotteryplus.ChatUtils;
import com.randude14.lotteryplus.ClaimManager;
import com.randude14.lotteryplus.Logger;
import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.LotteryPlus;
import com.randude14.lotteryplus.PluginSupport;
import com.randude14.lotteryplus.WinnersManager;
import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.lottery.permission.*;
import com.randude14.lotteryplus.lottery.reward.*;
import com.randude14.lotteryplus.util.Time;
import com.randude14.lotteryplus.util.Utils;
import com.randude14.register.economy.*;

public class Lottery implements Runnable {
	private static final String FORMAT_REWARD = "<reward>";
	private static final String FORMAT_TIME = "<time>";
	private static final String FORMAT_NAME = "<name>";
	private static final String FORMAT_WINNER = "<winner>";
	private static final String FORMAT_TICKET_COST = "<ticketcost>";
	private static final String FORMAT_TICKET_TAX = "<ticket_tax>";
	private static final String FORMAT_POT_TAX = "<pot_tax>";
	private final Map<String, Long> cooldowns;
	private final List<Permission> perms;
	private final List<Reward> rewards;
	private final List<Sign> signs;
	private List<String> aliases;
	private List<String> worlds;
	private List<String> towny;
	private final String lotteryName;
	private LotteryProperties properties;
	private BukkitTask drawTask;
	private Timer timer;
	private Economy econ;
	private boolean success;

	public Lottery(String name) {
		this.cooldowns = Collections.synchronizedMap(new HashMap<String, Long>());
		this.perms = new ArrayList<Permission>();
		this.rewards = new ArrayList<Reward>();
		this.signs = new ArrayList<Sign>();
		this.lotteryName = name;
		success = false;
		this.perms.add(new WorldPermission(this));
		if(PluginSupport.TOWNY.isInstalled()) {
			this.perms.add(new TownyPermission(this));
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
	
	private void printWarningTimes() {
		String line = properties.getString(Config.DEFAULT_WARNING_TIMES);
		if(line != null && !line.isEmpty()) {
			for(String timeStr : line.split("\\s+")) {
				int len = timeStr.length();
				if(len == 0) {
					continue;
				}
				try {
					long time = Long.parseLong(timeStr.substring(0, len-1));
					String timeMess = time + " ";
					char c = Character.toLowerCase(timeStr.charAt(len-1));
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
					default:
						//no need to do anything with time, already in seconds
						timeMess += ChatUtils.getRawName("lottery.time.seconds");
						break;
					}
					if(timer.getTime() == time) {
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
	
	private void updateSigns(boolean over) {
		final String line1 = ChatUtils.replaceColorCodes(Config.getString(Config.SIGN_TAG));
		final String line2, line3, line4;
		if(over) {
			line2 = ChatUtils.replaceColorCodes(format(Config.getString(Config.OVER_SIGN_LINE_TWO)));
			line3 = ChatUtils.replaceColorCodes(format(Config.getString(Config.OVER_SIGN_LINE_THREE)));
			line4 = ChatUtils.replaceColorCodes(format(Config.getString(Config.OVER_SIGN_LINE_FOUR)));
		} else {
			if(isDrawing()) {
				line2 = ChatUtils.replaceColorCodes(format(Config.getString(Config.DRAWING_SIGN_LINE_TWO)));
				line3 = ChatUtils.replaceColorCodes(format(Config.getString(Config.DRAWING_SIGN_LINE_THREE)));
				line4 = ChatUtils.replaceColorCodes(format(Config.getString(Config.DRAWING_SIGN_LINE_FOUR)));
			} else {
				line2 = ChatUtils.replaceColorCodes(format(Config.getString(Config.UPDATE_SIGN_LINE_TWO)));
				line3 = ChatUtils.replaceColorCodes(format(Config.getString(Config.UPDATE_SIGN_LINE_THREE)));
				line4 = ChatUtils.replaceColorCodes(format(Config.getString(Config.UPDATE_SIGN_LINE_FOUR)));
			}
		}
		LotteryPlus.scheduleSyncDelayedTask(new Runnable() {
			public void run() {
				for(Sign sign : signs) {
					sign.setLine(0, line1);
					sign.setLine(1, line2);
					sign.setLine(2, line3);
					sign.setLine(3, line4);
					sign.update(true);
				}
			}
		}, 0);
	}
	
	private void updateCooldowns() {
		synchronized(cooldowns) {
			Iterator<Map.Entry<String, Long>> it = cooldowns.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<String, Long> entry = it.next();
				long cooldown = entry.getValue();
				if(cooldown-- <= 0)
					it.remove();
				else
					entry.setValue(cooldown);
			}
		}
	}

	public String format(String mess) {
		String winner = properties.getString("winner", "");
		return mess
				.replace(FORMAT_TIME, timer.format())
				.replace(FORMAT_REWARD, formatReward())
				.replace(FORMAT_NAME, lotteryName)
				.replace(FORMAT_WINNER, (!winner.isEmpty()) ? winner : ChatUtils.getRawName("lottery.error.nowinner"))
				.replace(FORMAT_TICKET_COST, econ.format(properties.getDouble(Config.DEFAULT_TICKET_COST)))
				.replace(FORMAT_TICKET_TAX, String.format("%,.2f", properties.getDouble(Config.DEFAULT_TICKET_TAX)))
				.replace(FORMAT_POT_TAX, String.format("%,.2f", properties.getDouble(Config.DEFAULT_POT_TAX)));
	}

	private String formatReward() {
		if (properties.getBoolean(Config.DEFAULT_USE_POT))
			return econ.format(properties.getDouble(Config.DEFAULT_POT));
		int num = 0;
		for (int cntr = 0; cntr < rewards.size(); cntr++) {
			if (rewards instanceof ItemReward)
				num++;
		}
		return ChatUtils.getRawName("lottery.reward.items", "<number>", num);
	}

	public synchronized boolean addToPot(CommandSender sender, double add) {
		if (!properties.getBoolean(Config.DEFAULT_USE_POT)) {
			ChatUtils.send(sender, "lottery.error.nopot", "<lottery>", lotteryName);
			return false;
		}
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(!econ.hasAccount(player)) {
				ChatUtils.send(player, "lottery.error.noaccount");
				return false;
			}
			if(!econ.hasEnough(player, add)) {
				ChatUtils.send(player, "lottery.error.notenough", "<money>", econ.format(add));
				return false;
			}
			econ.withdraw(player, add);
		}
		double pot = properties.getDouble(Config.DEFAULT_POT);
		properties.set(Config.DEFAULT_POT, pot + add);
		ChatUtils.send(sender, "plugin.command.atp.mess", "<money>", econ.format(add), "<lottery>", lotteryName);
		LotteryManager.saveLottery(lotteryName);
		return true;
	}

	public void setProperties(CommandSender sender, LotteryProperties properties) throws InvalidLotteryException {
		setProperties(sender, properties, false);
	}

	public void setProperties(CommandSender sender, LotteryProperties properties, boolean force) throws InvalidLotteryException {
		try {
			// CHECK FOR NEGATIVE properties
			double time = properties.getDouble(Config.DEFAULT_TIME);
			double pot = properties.getDouble(Config.DEFAULT_POT);
			double ticketCost = properties.getDouble(Config.DEFAULT_TICKET_COST);
			Validate.isTrue(time >= 0, ChatUtils.getRawName("lottery.error.negative.time", "<time>", time));
			Validate.isTrue(pot >= 0.0, ChatUtils.getRawName("lottery.error.negative.pot", "<pot>", pot));
			Validate.isTrue(ticketCost >= 0.0, ChatUtils.getRawName("lottery.error.negative.ticket-cost", "<ticket_cost>", ticketCost));

			if (force) {
				rewards.clear();
			} else {
				transfer(this.properties, properties);
				signs.clear();
				int cntr = 1;
				while(properties.contains("sign" + cntr)) {
					String str = properties.remove("sign" + cntr).toString();
					Location loc = Utils.parseToLocation(str);
					if(loc != null) {
						Block block = loc.getBlock();
						if(LotteryPlus.isSign(block)) {
							Sign sign = (Sign) block.getState();
							signs.add(sign);
						} else {
							Logger.info("lottery.error.sign.load", "<loc>", str);
							properties.remove("sign" + cntr);
						}
					} else {
						Logger.info("lottery.error.loc.load", "<line>", str);
						properties.remove("sign" + cntr);
					}
					cntr++;
				}
			}

			if(this.properties != null) {
				properties.set("winner", this.properties.getString("winner", ""));
			}
			this.properties = properties;
			
			// ECONOMY
			if(properties.contains("econ")) {
				econ = (Economy) properties.get("econ");
			} else {
				econ = null;
				if(properties.getBoolean(Config.DEFAULT_USE_VAULT)) {
					if(PluginSupport.VAULT.isInstalled()) {
						econ = new VaultEconomy();
					}
				} else {
					String materialID = properties.getString(Config.DEFAULT_MATERIAL);
					String name = properties.getString(Config.DEFAULT_MATERIAL_NAME);
					econ = new MaterialEconomy(materialID, name);
				}
			}
			
			if(econ == null) {
				throw new NullPointerException(ChatUtils.getRawName("lottery.exception.economy.load", "<lottery>", lotteryName));
			}

			// LOAD ITEM REWARDS
			String itemRewards = properties.getString(Config.DEFAULT_ITEM_REWARDS);
			if (!(itemRewards == null || itemRewards.equals(""))) {
				for(ItemStack item : Utils.getItemStacks(itemRewards)) {
					rewards.add(new ItemReward(item));
				}
			}

			// LOAD TIME
			if(properties.getBoolean(Config.DEFAULT_USE_TIMER)) {
				this.timer = new LotteryTimer();
			} else {
				this.timer = new BlankTimer();
			}
			timer.setRunning(true);
			timer.load(properties);
			
			// WORLDS
			worlds = properties.getStringList(Config.DEFAULT_WORLDS);
			
			// TOWNY
			towny = properties.getStringList(Config.DEFAULT_TOWNY);
			
			// ALIASES
			aliases = properties.getStringList(Config.DEFAULT_ALIASES);
			
			cooldowns.clear();
			if(properties.contains("item-only")) {
				properties.set(Config.DEFAULT_USE_POT, !properties.getBoolean("item-only"));
				properties.remove("item-only");
			}
		} catch (Exception ex) {
			throw new InvalidLotteryException(ChatUtils.getRawName("lottery.exception.lottery.load", "<lottery>", lotteryName), ex);
		}
	}

	private void transfer(LotteryProperties oldproperties, LotteryProperties newproperties) {
		if (oldproperties != null) {
			if(!success) {
				if (!oldproperties.getBoolean(Config.DEFAULT_CLEAR_POT)) {
					double pot = newproperties.getDouble(Config.DEFAULT_POT);
					newproperties.set(Config.DEFAULT_POT, pot + oldproperties.getDouble(Config.DEFAULT_POT));
				}
				if (oldproperties.getBoolean(Config.DEFAULT_CLEAR_REWARDS)) {
					rewards.clear();
				}
				if(oldproperties.getBoolean(Config.DEFAULT_KEEP_TICKETS)) {
					for(String player : getPlayers()) {
						newproperties.set("players." + player, oldproperties.getInt("players." + player, 0));
					}
				}
			}
		} else{
			rewards.clear();
		}
	}
	
	public void broadcast(String code, Object... args) {
		ChatUtils.broadcast(getPlayersToBroadcast(), code, args);
	}
	
	public void broadcastRaw(String code, Object... args) {
		ChatUtils.broadcastRaw(getPlayersToBroadcast(), code, args);
	}
	
	private List<Player> getPlayersToBroadcast() {
		List<Player> players = new ArrayList<Player>();
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(hasAccess(p)) {
				players.add(p);
			}
		}
		return players;
	}
	
	public boolean checkAccess(CommandSender sender) {
		for(Permission perm : perms) {
			if(!perm.checkAccess(sender)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean hasAccess(CommandSender sender) {
		for(Permission perm : perms) {
			if(!perm.hasAccess(sender)) {
				return false;
			}
		}
		return true;
	}

	public Map<String, Object> save() {
		timer.save(properties);
		properties.remove("drawing");
		properties.set("econ", econ);
		Map<String, Object> map = new HashMap<String, Object>();
		map.putAll(properties.getValues());
		int cntr = 1;
		for(Sign sign : signs) {
			map.put("sign" + cntr++, Utils.parseLocation(sign.getLocation()));
		}
		return map;
	}
	
	public boolean registerSign(CommandSender sender, Sign sign) {
		if(!checkAccess(sender)) {
			return false;
		}
		if(hasRegisteredSign(sign)) return false;
		signs.add(sign);
		updateSigns();
		return true;
	}
	
	public boolean hasRegisteredSign(Block block) {
		for(Sign s : signs) {
			if(LotteryPlus.locsInBounds(block.getLocation(), s.getLocation())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRegisteredSign(Sign sign) {
		for(Sign s : signs) {
			if(LotteryPlus.locsInBounds(sign.getLocation(), s.getLocation())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean unregisterSign(Sign sign) {
		for(int cntr = 0;cntr < signs.size();cntr++) {
			Sign s = signs.get(cntr);
			if(LotteryPlus.locsInBounds(sign.getLocation(), s.getLocation())) {
				signs.remove(cntr);
				return true;
			}
		}
		return false;
	}
	
	public void onVote(String player) {
		int reward = properties.getInt(Config.DEFAULT_VOTIFIER_REWARD);
		if(reward > 0) {
			String prevWinner = properties.getString("winner", "");
			if(prevWinner.equalsIgnoreCase(player) && !properties.getBoolean(Config.DEFAULT_WIN_AGAIN)) {
				return;
			}
			int ticketLimit = properties.getInt(Config.DEFAULT_TICKET_LIMIT);
			int num = getTicketsBought(player);
			if (ticketLimit > 0) {
				int total = getTotalTicketsBought();
				if (total >= ticketLimit) {
					return;
				}
				if (reward + num > ticketLimit) {
					return;
				}
			}
			addTickets(player, reward);
			broadcast("lottery.vote.reward.mess", "<player>", player, "<number>", reward, "<lottery>", lotteryName);
			LotteryManager.saveLottery(lotteryName);
		}
	}
	
	private boolean canBuy(Player player, int tickets) {
		if(!properties.getBoolean(Config.DEFAULT_BUY_TICKETS)) {
			ChatUtils.sendRaw(player, "lottery.error.tickets.disabled", "<lottery>", lotteryName);
			return false;
		}
		String prevWinner = properties.getString("winner", "");
		if(prevWinner.equalsIgnoreCase(player.getName()) && !properties.getBoolean(Config.DEFAULT_WIN_AGAIN)) {
			ChatUtils.send(player, "lottery.error.already-won", "<lottery>", lotteryName);
			return false;
		}
		if(!checkAccess(player)) {
			return false;
		}
		if (isDrawing() && !Config.getBoolean(Config.BUY_DURING_DRAW)) {
			ChatUtils.sendRaw(player, "lottery.error.drawing");
			return false;
		}
		int ticketLimit = properties.getInt(Config.DEFAULT_TICKET_LIMIT);
		if (ticketLimit > 0 && getTotalTicketsBought() >= ticketLimit) {
			ChatUtils.sendRaw(player, "lottery.error.tickets.soldout");
			return false;
		}
		String name = player.getName();
		int maxTickets = properties.getInt(Config.DEFAULT_MAX_TICKETS);
		int maxPlayers = properties.getInt(Config.DEFAULT_MAX_PLAYERS);
		int playersEntered = getPlayersEntered();
		int num = getTicketsBought(name);
		if (maxTickets > 0) {
			if (num >= maxTickets) {
				ChatUtils.sendRaw(player, "lottery.error.tickets.anymore");
				return false;
			} else if (num + tickets > maxTickets) {
				ChatUtils.sendRaw(player, "lottery.error.tickets.toomuch");
				return false;
			}
		}
		if(maxPlayers > 0) {
			if (playersEntered >= maxPlayers) {
				ChatUtils.sendRaw(player, "lottery.error.players.nomore");
				return false;
			}
		}
		if(tickets <= 0) {
			ChatUtils.sendRaw(player, "lottery.error.tickets.negative");
			return false;
		}
		synchronized(cooldowns) {
			if(cooldowns.containsKey(name)) {
				ChatUtils.sendRaw(player, "lottery.error.cooldown", "<time>", cooldowns.get(name));
				return false;
			}
		}
		return true;
	}

	public synchronized boolean buyTickets(Player player, int tickets) {
		String name = player.getName();
		if (!canBuy(player, tickets)) {
			return false;
		}
		if (!econ.hasAccount(name)) {
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
		if (!econ.hasEnough(name, total)) {
			ChatUtils.sendRaw(player, "lottery.error.tickets.notenough");
			return false;
		}
		econ.withdraw(name, total);
		if(taxAccount != null && econ.hasAccount(taxAccount)) {
			double left = econ.deposit(taxAccount, taxes);
			if(left > 0) {
				List<Reward> list = new ArrayList<Reward>();
				list.add(new PotReward(econ, left));
				ClaimManager.addClaim(taxAccount, lotteryName, list);
			}
		}
		addTickets(name, tickets);
		ChatUtils.sendRaw(player, "lottery.tickets.mess", "<tickets>", tickets, "<lottery>", lotteryName);
		if (properties.getBoolean(Config.DEFAULT_USE_POT)) {
			ChatUtils.sendRaw(player, "lottery.pot.mess", "<money>", econ.format(d), "<lottery>", lotteryName);
			properties.set(Config.DEFAULT_POT,
					properties.getDouble(Config.DEFAULT_POT) + d);
		}
		long cooldown = properties.getLong(Config.DEFAULT_COOLDOWN);
		long warmup = properties.getLong(Config.DEFAULT_WARMUP);
		long time = timer.getTime() - cooldown + warmup;
		timer.setTime(time);
		long delay = Config.getLong(Config.BUY_DELAY);
		if(delay >= 0) {
			synchronized(cooldowns) {
				cooldowns.put(name, delay);
			}
		}
		updateSigns();
		LotteryManager.saveLottery(lotteryName);
		return true;
	}

	public synchronized boolean rewardPlayer(CommandSender rewarder, String player, int tickets) {
		int ticketLimit = properties.getInt(Config.DEFAULT_TICKET_LIMIT);
		String prevWinner = properties.getString("winner", "");
		if(prevWinner.equalsIgnoreCase(player) && !properties.getBoolean(Config.DEFAULT_WIN_AGAIN)) {
			ChatUtils.send(rewarder, "lottery.error.already-won.reward", "<lottery>", lotteryName, "<player>", player);
			return false;
		}
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
		properties.set("players." + player, num + tickets);
		Player p = Bukkit.getPlayer(player);
		if (p != null) {
			ChatUtils.send(p, "plugin.command.reward.player.mess", "<tickets>", tickets, "<lottery>", lotteryName);
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
			ChatUtils.sendRaw(sender, "lottery.info.tickets.bought", "<number>", getTicketsBought(sender.getName()));
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

	public List<String> getPlayers() {
		List<String> players = new ArrayList<String>();
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

	public int getTicketsBought(String name) {
		return properties.getInt("players." + name, 0);
	}
	
	public void addTickets(String name, int add) {
		int tickets = properties.getInt("players." + name, 0);
		properties.set("players." + name, tickets + add);
	}
	
	public void subTickets(String name, int remove) {
		int tickets = properties.getInt("players." + name, 0);
		properties.set("players." + name, tickets - remove);
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
				resetData();
				properties.set("drawing", false);
				return;
			}
			
			// pick winner
			List<String> players = getPlayers();
			String winner = Lottery.pickRandomPlayer(players, properties.getInt(Config.DEFAULT_TICKET_LIMIT));
			if (winner == null) {
				broadcast("lottery.error.drawing.nowinner");
				properties.set("drawing", false);
				success = false;
				LotteryManager.reloadLottery(lotteryName, false);
				return;
			}
			
			// broadcast winner		
			broadcast("lottery.drawing.winner.mess", "<winner>", winner);
			
			// add pot reward
			if (properties.getBoolean(Config.DEFAULT_USE_POT)) {
				double pot = properties.getDouble(Config.DEFAULT_POT);
				double potTax = properties.getDouble(Config.DEFAULT_POT_TAX);
				double winnings = pot - (pot * (potTax / 100));
				rewards.add(0, new PotReward(econ, winnings));
			}
			
			// log winner
			StringBuilder logWinner = new StringBuilder(lotteryName + ": " + winner);
			for (Reward reward : rewards) {
				logWinner.append(", ");
				logWinner.append("[" + reward.getInfo() + "]");
			}			
			WinnersManager.logWinner(logWinner.toString());
			
			// reward winner if online
			Player pWinner = Bukkit.getPlayer(winner);
			boolean rewarded = false;
			if (pWinner != null) {
				rewarded = Lottery.handleRewards(pWinner, lotteryName, rewards);
			}
			if(!rewarded) {
				ClaimManager.addClaim(winner, lotteryName, rewards);
			}
			
			// set up for next drawing
			properties.set("winner", winner);
			properties.set("drawing", false);
			clearPlayers();
			success = true;
			if (properties.getBoolean(Config.DEFAULT_REPEAT)) {
				LotteryManager.reloadLottery(lotteryName);
			} else {
				LotteryManager.unloadLottery(lotteryName);
				updateSigns(true);
			}
		} catch (Exception ex) {
			Logger.info("lottery.exception.drawing", "<lottery>", lotteryName);
			properties.set("drawing", false);
			success = false;
			ex.printStackTrace();
		}
	}

	private static String pickRandomPlayer(List<String> players, int ticketLimit) {
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

	public static boolean handleRewards(Player player, String lottery, List<Reward> list) {
		Iterator<Reward> rewards = list.iterator();
		while(rewards.hasNext()) {
			Reward reward = rewards.next();
			if(reward.rewardPlayer(player, lottery)) {
				rewards.remove();
			}
		}
		if(!list.isEmpty()) {
			ChatUtils.send(player, "lottery.reward.leftover");
			return false;
		}
		return true;
	}

	private void resetData() {
		timer.reset(properties);
		properties.set(Config.DEFAULT_TICKET_COST, properties.getDouble(Config.DEFAULT_TICKET_COST) + properties.getDouble(Config.DEFAULT_RESET_ADD_TICKET_COST));
		properties.set(Config.DEFAULT_POT, properties.getDouble(Config.DEFAULT_POT) + properties.getDouble(Config.DEFAULT_RESET_ADD_POT));
		properties.set(Config.DEFAULT_COOLDOWN, properties.getLong(Config.DEFAULT_COOLDOWN) + properties.getLong(Config.DEFAULT_RESET_ADD_COOLDOWN));
		properties.set(Config.DEFAULT_WARMUP, properties.getLong(Config.DEFAULT_WARMUP) + properties.getLong(Config.DEFAULT_RESET_ADD_WARMUP));
		properties.set(Config.DEFAULT_MAX_TICKETS, properties.getInt(Config.DEFAULT_MAX_TICKETS) + properties.getInt(Config.DEFAULT_RESET_ADD_MAX_TICKETS));
		properties.set(Config.DEFAULT_MAX_PLAYERS, properties.getInt(Config.DEFAULT_MAX_PLAYERS) + properties.getInt(Config.DEFAULT_RESET_ADD_MAX_PLAYERS));
		properties.set(Config.DEFAULT_MIN_PLAYERS, properties.getInt(Config.DEFAULT_MIN_PLAYERS) + properties.getInt(Config.DEFAULT_RESET_ADD_MIN_PLAYERS));
		properties.set(Config.DEFAULT_TICKET_TAX, properties.getDouble(Config.DEFAULT_TICKET_TAX) + properties.getDouble(Config.DEFAULT_RESET_ADD_TICKET_TAX));
		properties.set(Config.DEFAULT_POT_TAX, properties.getDouble(Config.DEFAULT_POT_TAX) + properties.getDouble(Config.DEFAULT_RESET_ADD_POT_TAX));
		String read = properties.getString(Config.DEFAULT_RESET_ADD_ITEM_REWARDS);
		if(read != null && !read.isEmpty()) {
			for(ItemStack item : Utils.getItemStacks(read)) {
				rewards.add(new ItemReward(item));
			}
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
