package com.randude14.lotteryplus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.randude14.lotteryplus.command.*;
import com.randude14.lotteryplus.configuration.CustomYaml;
import com.randude14.lotteryplus.gui.MainFrame;
import com.randude14.lotteryplus.listeners.*;
import com.randude14.lotteryplus.lottery.LotteryClaim;
import com.randude14.lotteryplus.lottery.reward.ItemReward;
import com.randude14.lotteryplus.lottery.reward.PotReward;
import com.randude14.lotteryplus.support.VotifierListener;
import com.randude14.lotteryplus.tasks.*;
import com.randude14.lotteryplus.util.Time;
import com.randude14.lotteryplus.util.Updater;
import com.randude14.register.economy.MaterialEconomy;
import com.randude14.register.economy.VaultEconomy;

public class LotteryPlus extends JavaPlugin {
	private static LotteryPlus instance = null;
	private static Updater updater = null;
	private static final List<Task> tasks = new ArrayList<Task>();
	private static MainFrame mainFrame;
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
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		tasks.add(new ReminderMessageTask());
		tasks.add(new SaveTask());
		tasks.add(new UpdateCheckTask());
		registerConfigurationClasses();
		RewardManager.loadRewardClaims();
		WinnersManager.loadWinners();
		Perm.loadPermissions();
		int numLotteries = LotteryManager.loadLotteries();
		Logger.info("logger.lottery.num", "<number>", numLotteries);
		callTasks();
		saveExtras();
		registerListeners();
		CommandManager cm = new CommandManager()
		    .registerCommand(new BuyCommand(), "buy")
		    .registerCommand(new DrawCommand(), "draw")
		    .registerCommand(new InfoCommand(), "info")
		    .registerCommand(new ClaimCommand(), "claim")
		    .registerCommand(new CreateCommand(), "create")
		    .registerCommand(new ReloadCommand(), "reload")
		    .registerCommand(new ReloadAllCommand(), "reloadall")
		    .registerCommand(new LoadCommand(), "load")
		    .registerCommand(new ListCommand(), "list")
		    .registerCommand(new UnloadCommand(), "unload")
		    .registerCommand(new AddToPotCommand(), "addtopot", "atp")
		    .registerCommand(new WinnersCommand(), "winners")
		    .registerCommand(new RewardCommand(), "reward")
		    .registerCommand(new SaveCommand(), "save")
		    .registerCommand(new ConfigCommand(), "config")
		    .registerCommand(new VersionCommand(), "version")
		    .registerCommand(new UpdateCommand(), "update")
		    .registerCommand(new GuiCreatorCommand(), "guic");
		this.getCommand("lottery").setExecutor(cm);
		LotteryPlus.scheduleSyncDelayedTask(new Runnable() {
			public void run() {
				LotteryPlus.scheduleAsyncRepeatingTask(new LotteryManager.TimerTask(), Time.SERVER_SECOND.getBukkitTime(), Time.SERVER_SECOND.getBukkitTime());
			}
		}, 0L);
		
		updater = new Updater(this, 36229, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
		Logger.info("logger.enabled");
	}
	
	public void onDisable() {
		Logger.info("logger.disabled");
		getServer().getScheduler().cancelTasks(this);
		LotteryManager.saveLotteries();
		WinnersLogger.close();
		instance = null;
	}

	private void saveExtras() {
		CustomYaml enchants = new CustomYaml("enchantments.yml");
		FileConfiguration enchantsConfig = enchants.getConfig();
		for (Enchantment enchant : Enchantment.values()) {
			enchantsConfig.set(
					"enchantments." + enchant.getName(),
					String.format("%d-%d", enchant.getStartLevel(),
							enchant.getMaxLevel()));
		}
		enchants.saveConfig();
		CustomYaml items = new CustomYaml("items.yml");
		FileConfiguration itemsConfig = items.getConfig();
		for (Material mat : Material.values()) {
			itemsConfig.set("items." + mat.name(), "");
		}
		items.saveConfig();
		CustomYaml colors = new CustomYaml("colors.yml");
		FileConfiguration colorsConfig = colors.getConfig();
		for (ChatColor color : ChatColor.values()) {
			colorsConfig.set("colors." + color.name(),
					Character.toString(color.getChar()));
		}
		colors.saveConfig();
	}
	
	private void registerConfigurationClasses() {
		ConfigurationSerialization.registerClass(LotteryClaim.class);
		ConfigurationSerialization.registerClass(ItemReward.class);
		ConfigurationSerialization.registerClass(PotReward.class);
		ConfigurationSerialization.registerClass(MaterialEconomy.class);
		ConfigurationSerialization.registerClass(VaultEconomy.class);
	}
	
	private static void callTasks() {
		for(Task task : tasks) {
			task.reschedule();
		}
	}

	public static void reload() {
		instance.reloadConfig();
		ChatUtils.reload();
		callTasks();
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
	@SuppressWarnings("deprecation")
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
		return Bukkit.getOfflinePlayer(name);
	}
	
	public static Player getBukkitPlayer(String playerName) {
		OfflinePlayer player = LotteryPlus.getOfflinePlayer(playerName);
		if(player.isOnline())
			return player.getPlayer();
		else 
			return null;
	}
	
	public static void updateCheck(CommandSender sender) {
		Updater.UpdateResult result = updater.getResult();
		if(result == Updater.UpdateResult.UPDATE_AVAILABLE) {
			String latestVersion = updater.getLatestName();
			String currentVersion = "v" + LotteryPlus.getVersion();
			ChatUtils.send(sender, "plugin.update-available", "<current_version>", currentVersion, "<new_version>", latestVersion);
		} else {
			ChatUtils.send(sender, "plugin.error.no-update");
		}
	}
	
	public static boolean dispatchCommand(String command) {
		return instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), command);
	}

	public static boolean checkPermission(CommandSender sender,
			Perm permission) {
		if (!hasPermission(sender, permission)) {
			ChatUtils.send(sender, "plugin.error.permission");
			return false;
		}
		return true;
	}

	public static boolean hasPermission(CommandSender sender, Perm perm) {
		if(perm.hasPermission(sender)) {
			return true;
		} else {
			Perm parent = perm.getParent();
			return parent != null ? hasPermission(sender, parent) : false;
		}
	}
	
	public static void openGui() {
		if(mainFrame == null) 
			mainFrame = new MainFrame();
		mainFrame.setVisible(true);
		mainFrame.requestFocus();
	}
	
	public static void openGui(String lotteryName) {
		openGui();
		mainFrame.openCreator(lotteryName);
	}
	
	public static String getVersion() {
		return instance.getDescription().getVersion();
	}
	
	public static BukkitTask scheduleAsyncRepeatingTask(Runnable runnable, long initialDelay, long reatingDelay) {
		return instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, runnable, initialDelay, reatingDelay);
	}

	public static BukkitTask scheduleAsyncDelayedTask(Runnable runnable, long delay) {
		return instance.getServer().getScheduler().runTaskLaterAsynchronously(instance, runnable, delay);
	}

	public static BukkitTask scheduleSyncRepeatingTask(Runnable runnable, long initialDelay, long reatingDelay) {
		return instance.getServer().getScheduler().runTaskTimer(instance, runnable, initialDelay, reatingDelay);
	}

	public static BukkitTask scheduleSyncDelayedTask(Runnable runnable, long delay) {
		return instance.getServer().getScheduler().runTaskLater(instance, runnable, delay);
	}
	
	public static void cancelTask(int taskId) {
		instance.getServer().getScheduler().cancelTask(taskId);
	}
	
	public static boolean isSign(Block block) {
		return block.getState() instanceof Sign;
	}

	public static boolean isSign(Location loc) {
		return isSign(loc.getBlock());
	}
	
	public static final LotteryPlus getInstance() {
		return instance;
	}
}
