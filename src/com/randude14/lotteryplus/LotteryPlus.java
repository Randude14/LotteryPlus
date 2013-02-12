package com.randude14.lotteryplus;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.randude14.lotteryplus.command.*;
import com.randude14.lotteryplus.configuration.CustomYaml;
import com.randude14.lotteryplus.listeners.*;
import com.randude14.lotteryplus.tasks.*;
import com.randude14.lotteryplus.util.TimeConstants;
import com.randude14.register.permission.BukkitPermission;
import com.randude14.register.permission.Permission;
import com.randude14.register.permission.VaultPermission;

public class LotteryPlus extends JavaPlugin implements TimeConstants {
	private static LotteryPlus instance = null;
	private static final List<Task> tasks = new ArrayList<Task>();
	private static Permission perm;
	private File configFile;

	public void onEnable() {
		instance = this;
		File dataFolder = getDataFolder();
		dataFolder.mkdirs();
		configFile = new File(dataFolder, "config.yml");
		ChatUtils.reload();

		if (!configFile.exists()) {
			Logger.info("logger.config.defaults");
			saveDefaultConfig();
		}
		
		tasks.add(new ReminderMessageTask());
		tasks.add(new SaveTask());
		tasks.add(new UpdateCheckTask());
		ClaimManager.loadClaims();
		WinnersManager.loadWinners();
		int numLotteries = LotteryManager.loadLotteries();
		Logger.info("logger.lottery.num", "<number>", numLotteries);
		loadRegistry();
		callTasks();
		saveExtras();
		registerListeners();
		Command atpCommand = new AddToPotCommand();
		CommandManager cm = new CommandManager()
		    .registerCommand("buy", new BuyCommand())
		    .registerCommand("draw", new DrawCommand())
		    .registerCommand("info", new InfoCommand())
		    .registerCommand("claim", new ClaimCommand())
		    .registerCommand("create", new CreateCommand())
		    .registerCommand("reload", new ReloadCommand())
		    .registerCommand("reloadall", new ReloadAllCommand())
		    .registerCommand("load", new LoadCommand())
		    .registerCommand("list", new ListCommand())
		    .registerCommand("unload", new UnloadCommand())
		    .registerCommand("addtopot", atpCommand)
		    .registerCommand("atp", atpCommand)
		    .registerCommand("winners", new WinnersCommand())
		    .registerCommand("reward", new RewardCommand())
		    .registerCommand("save", new SaveCommand())
		    .registerCommand("config", new ConfigCommand())
		    .registerCommand("version", new VersionCommand())
		    .registerCommand("update", new UpdateCommand());
		this.getCommand("lottery").setExecutor(cm);
		this.getCommand("l").setExecutor(cm);
		LotteryPlus.scheduleSyncDelayedTask(new Runnable() {
			public void run() {
				scheduleAsyncRepeatingTask(new LotteryManager.TimerTask(), 20L, 20L);
			}
		}, 0L);
		Logger.info("logger.enabled");
	}
	
	public void onDisable() {
		Logger.info("logger.disabled");
		getServer().getScheduler().cancelTasks(this);
		LotteryManager.saveLotteries();
		WinnersLogger.close();
		instance = null;
		perm = null;
	}
	
	private static void loadRegistry() {
		if(!PluginSupport.VAULT.isInstalled()) {
			Logger.info("logger.vault.notfound");
			perm = new BukkitPermission();
		} else {
			perm = new VaultPermission();
		}
	}

	private void saveExtras() {
		CustomYaml enchants = new CustomYaml("enchantments.yml", false);
		FileConfiguration enchantsConfig = enchants.getConfig();
		for (Enchantment enchant : Enchantment.values()) {
			enchantsConfig.set(
					"enchantments." + enchant.getName(),
					String.format("%d-%d", enchant.getStartLevel(),
							enchant.getMaxLevel()));
		}
		enchants.saveConfig();
		CustomYaml items = new CustomYaml("items.yml", false);
		FileConfiguration itemsConfig = items.getConfig();
		for (Material mat : Material.values()) {
			itemsConfig.set("items." + mat.name(), mat.getId());
		}
		items.saveConfig();
		CustomYaml colors = new CustomYaml("colors.yml", false);
		FileConfiguration colorsConfig = colors.getConfig();
		for (ChatColor color : ChatColor.values()) {
			colorsConfig.set("colors." + color.name(),
					Character.toString(color.getChar()));
		}
		colors.saveConfig();
	}
	
	private static void callTasks() {
		for(Task task : tasks) {
			task.scheduleTask();
		}
	}

	public static void reload() {
		instance.reloadConfig();
		ChatUtils.reload();
		callTasks();
		loadRegistry();
	}
	
	public static void disable() {
		instance.getServer().getPluginManager().disablePlugin(instance);
	}

	private void registerListeners() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new SignProtectorListener(), this);
		pm.registerEvents(new PlayerListener(), this);
		if(PluginSupport.VOTIFIER.isInstalled()) {
			pm.registerEvents(new VotifierListener(), this);
		}
	}
	
	public static boolean locsInBounds(Location loc1, Location loc2) {
		return loc1.getBlockX() == loc2.getBlockX()
				&& loc1.getBlockY() == loc2.getBlockY()
				&& loc1.getBlockZ() == loc2.getBlockZ();
	}

	// uses binary search
	public static OfflinePlayer getOfflinePlayer(String name) {
		OfflinePlayer[] players = instance.getServer().getOfflinePlayers();
		int left = 0;
		int right = players.length - 1;
		while (left <= right) {
			int mid = (left + right) / 2;
			int result = players[mid].getName().compareToIgnoreCase(name);
			if (result == 0)
				return players[mid];
			else if (result < 0)
				left = mid + 1;
			else
				right = mid - 1;
		}

		// if it doesn't exist, then have the server
		// create the object instead of returning null
		return instance.getServer().getOfflinePlayer(name);
	}

	public static boolean checkPermission(CommandSender sender,
			Perm permission) {
		if (!hasPermission(sender, permission)) {
			ChatUtils.send(sender, "plugin.error.permission");
			return false;
		}
		return true;
	}

	public static boolean hasPermission(CommandSender sender,
			Perm permission) {
		return perm.hasPermission(sender, permission);
	}
	
	public static boolean isThereNewUpdate(String currentVersion) {
		String latestVersion = currentVersion;
		try {
			URL url = new URL("http://dev.bukkit.org/server-mods/lotteryplus/files.rss");
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.parse(url.openConnection().getInputStream());
			doc.getDocumentElement().normalize();
			NodeList nodes = doc.getElementsByTagName("item");
			Node firstNode = nodes.item(0);
			if (firstNode.getNodeType() == 1) {
				Element firstElement = (Element) firstNode;
				NodeList firstElementTagName = firstElement
						.getElementsByTagName("title");
				Element firstNameElement = (Element) firstElementTagName
						.item(0);
				NodeList firstNodes = firstNameElement.getChildNodes();
				latestVersion = firstNodes.item(0).getNodeValue();
			}
		} catch (Exception ex) {
			latestVersion = currentVersion;
		}
		return !latestVersion.endsWith(currentVersion);
	}
	
	public static void updateCheck(String currentVersion) {
		updateCheck(Bukkit.getConsoleSender(), currentVersion);
	}
	
	public static void updateCheck(CommandSender sender, String currentVersion) {
		String latestVersion = currentVersion;
		try {
			URL url = new URL("http://dev.bukkit.org/server-mods/lotteryplus/files.rss");
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.parse(url.openConnection().getInputStream());
			doc.getDocumentElement().normalize();
			NodeList nodes = doc.getElementsByTagName("item");
			Node firstNode = nodes.item(0);
			if (firstNode.getNodeType() == 1) {
				Element firstElement = (Element) firstNode;
				NodeList firstElementTagName = firstElement
						.getElementsByTagName("title");
				Element firstNameElement = (Element) firstElementTagName
						.item(0);
				NodeList firstNodes = firstNameElement.getChildNodes();
				latestVersion = firstNodes.item(0).getNodeValue();
			}
		} catch (Exception ex) {
			latestVersion = currentVersion;
		}
		if(!latestVersion.endsWith(currentVersion)) {
			ChatUtils.send(sender, "plugin.update-available", "<current_version>", currentVersion, "<new_version>", latestVersion);
		} else {
			ChatUtils.send(sender, "plugin.error.no-update");
		}
	}
	
	public static int scheduleAsyncRepeatingTask(Runnable runnable,
			long initialDelay, long reatingDelay) {
		return instance
				.getServer()
				.getScheduler().runTaskTimerAsynchronously(instance, runnable, initialDelay, reatingDelay).getTaskId();
	}

	public static int scheduleAsyncDelayedTask(Runnable runnable, long delay) {
		return instance.getServer().getScheduler().runTaskLaterAsynchronously(instance, runnable, delay).getTaskId();
	}

	public static int scheduleSyncRepeatingTask(Runnable runnable,
			long initialDelay, long reatingDelay) {
		return instance
				.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(instance, runnable, initialDelay,
						reatingDelay);
	}

	public static int scheduleSyncDelayedTask(Runnable runnable, long delay) {
		return instance.getServer().getScheduler()
				.scheduleSyncDelayedTask(instance, runnable, delay);
	}
	
	public static void cancelTask(int taskId) {
		instance.getServer().getScheduler().cancelTask(taskId);
	}
	
	public static String getVersion() {
		return instance.getDescription().getVersion();
	}

	public static boolean isSign(Block block) {
		return block.getState() instanceof Sign;
	}

	public static boolean isSign(Location loc) {
		return isSign(loc.getBlock());
	}
	
	public static Permission getPermission() {
		return perm;
	}
	
	public static final LotteryPlus getInstance() {
		return instance;
	}
}
