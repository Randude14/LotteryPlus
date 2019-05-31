package com.randude14.lotteryplus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
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
import com.randude14.lotteryplus.support.PluginSupport;
import com.randude14.lotteryplus.support.VotifierListener;
import com.randude14.lotteryplus.tasks.*;
import com.randude14.lotteryplus.util.ChatUtils;
import com.randude14.lotteryplus.util.Time;
import com.randude14.lotteryplus.util.Updater;
import com.randude14.register.economy.MaterialEconomy;
import com.randude14.register.economy.VaultEconomy;

/*
 * The main class that is used at the loading of the server. This plugin allows servers to create, set, and load lotteries
 * with limitless settings that can serve several purposes for the economies of servers. For more information on how to
 * use this plugin, please see the main Bukkit dev page at https://dev.bukkit.org/projects/lotteryplus
 * 
 * @arthur Randall Ferree
 * @version 1.0.1
 */
public class LotteryPlus extends JavaPlugin {
	
	/*
	 * Serves as the instance of the plugin. Can be accessed outside of class via LotteryPlus.getInstance()
	 */
	private static LotteryPlus instance = null;
	
	private Updater updater = null;                         // used to check if there is a new version of this plugin
	private final List<Task> tasks = new ArrayList<Task>(); // keeps track of plugin task threads
	private MainFrame mainFrame;	                        // the gui used to create lotteries
	private WinnersManager wManager;                        // keeps track of winners
	private RewardManager rManager;                         // keeps track of rewards

	/*
	 * Called on server load.
	 */
	public void onEnable() {
		instance = this;
		registerConfigurationClasses();
		ChatUtils.reload();
		
		// grab plugin directory
		File dataFolder = getDataFolder();
		dataFolder.mkdirs();

		// save config file to directory if it doesn't exist
		File configFile = new File(dataFolder, "config.yml");
		if (!configFile.exists()) {
			Logger.info("logger.config.defaults");
			saveDefaultConfig();
		}
		
		// set the look and feel for the mainFrame
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		// add server tasks
		tasks.add(new ReminderMessageTask());
		tasks.add(new SaveTask());
		tasks.add(new UpdateCheckTask());
		
		// init managers and enable them
		rManager = new RewardManager();
		rManager.loadRewardClaims();
		wManager = new WinnersManager();
		wManager.onEnable();
		
		// load lotteries and log to console
		int numLotteries = LotteryManager.loadLotteries();
		Logger.info("logger.lottery.num", "<number>", numLotteries);
		
		// more housekeeping before printing out the plugin is enabled 
		callTasks();
		saveExtras();
		registerListeners();
		Perm.loadPermissions();
		
		// controls the commands of the plugin
		// @see com.randude14.lotteryplus.command.CommandManager()
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
		
		// schedule the task that updates the timers on the lotteries
		LotteryPlus.scheduleSyncDelayedTask(new Runnable() {
			public void run() {
				LotteryPlus.scheduleAsyncRepeatingTask(new LotteryManager.TimerTask(), Time.SERVER_SECOND.getBukkitTime(), Time.SERVER_SECOND.getBukkitTime());
			}
		}, 0L);
		
		// set the updater
		updater = new Updater(this, 36229, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
		
		// print out the plugin is enabled
		Logger.info("logger.enabled");
	}
	
	/*
	 * Called on server closing
	 */
	public void onDisable() {
		Logger.info("logger.disabled");
		wManager.onDisable();
		getServer().getScheduler().cancelTasks(this);
		LotteryManager.saveLotteries();
		instance = null;
	}

	/*
	 * Creates/updates files that contain information dealing with the creation of lotteries
	 */
	private void saveExtras() {
		
		// lists all the enchantments that can be used
		CustomYaml enchants = new CustomYaml("enchantments.yml");
		FileConfiguration enchantsConfig = enchants.getConfig();
		for (Enchantment enchant : Enchantment.values()) {
			enchantsConfig.set("enchantments." + enchant.getName(),
			String.format("%d-%d", enchant.getStartLevel(), enchant.getMaxLevel()));
		}
		enchants.saveConfig();
		
		// lists all the items in game
		CustomYaml items = new CustomYaml("items.yml");
		FileConfiguration itemsConfig = items.getConfig();
		for (Material mat : Material.values()) {
			itemsConfig.set("items." + mat.name(), "");
		}
		items.saveConfig();
		
		// lists all the color codes for plugin messaging
		CustomYaml colors = new CustomYaml("colors.yml");
		FileConfiguration colorsConfig = colors.getConfig();
		for (ChatColor color : ChatColor.values()) {
			colorsConfig.set("colors." + color.name(),
					Character.toString(color.getChar()));
		}
		colors.saveConfig();
	}
	
	/*
	 * Called to register the serializable classes
	 * @see org.bukkit.configuration.serialization.ConfigurationSerialization
	 */
	private void registerConfigurationClasses() {
		ConfigurationSerialization.registerClass(LotteryClaim.class);
		ConfigurationSerialization.registerClass(ItemReward.class);
		ConfigurationSerialization.registerClass(PotReward.class);
		ConfigurationSerialization.registerClass(MaterialEconomy.class);
		ConfigurationSerialization.registerClass(VaultEconomy.class);
	}
	
	/*
	 * Called to reschedule the plugin tasks
	 */
	private static void callTasks() {
		for(Task task : instance.tasks) {
			task.reschedule();
		}
	}

	/*
	 * Force reloads the config main file, lang.properties, and callTasks()
	 */
	public static void reload() {
		instance.reloadConfig();
		ChatUtils.reload();
		callTasks();
	}

	/*
	 * Register plugin listeners. Called at onEnable()
	 */
	private void registerListeners() {
		
		// grab the plugin manager to register listeners
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(new SignProtectorListener(), this);
		pm.registerEvents(new PlayerListener(), this);
		
		// check that Votifier is installed before calling its listener to avoid runtime errors
		if(PluginSupport.VOTIFIER.isInstalled()) {
			pm.registerEvents(new VotifierListener(), this);
		}
	}
	
	/*
	 * @return the current running Server
	 * @see org.bukkit.Server
	 */
	public static Server getBukkitServer() {
		return instance.getServer();
	}
	
	/*
	 * Check to see if there is a new version of this plugin
	 */
	public static void updateCheck(CommandSender sender) {
		
		// check last uploaded file and compare versions
		Updater.UpdateResult result = instance.updater.getResult();
		
		if(result == Updater.UpdateResult.UPDATE_AVAILABLE) {
			String latestVersion = instance.updater.getLatestName();
			String currentVersion = "v" + LotteryPlus.getVersion();
			ChatUtils.send(sender, "plugin.update-available", "<current_version>", currentVersion, "<new_version>", latestVersion);
		
		
		} else {
			ChatUtils.send(sender, "plugin.error.no-update");
		}
	}
	
	/*
	 * Used to dispatch a command and uses the console as the CommandSender
	 * @return - whether the command was successful
	 */
	public static boolean dispatchCommand(String command) {
		return instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), command);
	}
	
	/*
	 * Open the lottery creator gui. If LotteryName is not null, it can open a section
	 * from the lotteries file if it exists
	 * @see com.randude14.lotteryplus.gui.MainFrame
	 */
	public static void openGui(String lotteryName) {
		
		// create main frame if it does exist yet
		if(instance.mainFrame == null) 
			instance.mainFrame = new MainFrame();
		
		// open and set to front of screen
		instance.mainFrame.setVisible(true);
		instance.mainFrame.requestFocus();
		
		// open a new tabe with lotteryName
		if(lotteryName != null)
			instance.mainFrame.openCreator(lotteryName);
	}
	
	/*
	 * Schedule a asynchronous task. Tasks may be asynchronous from the server as long as it is calling any world functions
	 * 
	 * @param runnable - the task to call
	 * @param initialDelay - the delay before the first task call
	 * @param repeatingDelay - the delay called between each call after the initialDelay
	 */
	public static BukkitTask scheduleAsyncRepeatingTask(Runnable runnable, long initialDelay, long repeatingDelay) {
		return instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, runnable, initialDelay, repeatingDelay);
	}

	/*
	 * Schedule a asynchronous task. Tasks may be asynchronous from the server as long as it is calling any world functions
	 * 
	 * @param runnable - the task to call
	 * @param delay - the delay between each task call
	 */
	public static BukkitTask scheduleAsyncDelayedTask(Runnable runnable, long delay) {
		return instance.getServer().getScheduler().runTaskLaterAsynchronously(instance, runnable, delay);
	}

	/*
	 * Schedule a synchronous task. Tasks that deal with world manipulation must be synchronous
	 * 
	 * @param runnable - the task to call
	 * @param initialDelay - the delay before the first task call
	 * @param repeatingDelay - the delay called between each call after the initialDelay
	 */
	public static BukkitTask scheduleSyncRepeatingTask(Runnable runnable, long initialDelay, long reatingDelay) {
		return instance.getServer().getScheduler().runTaskTimer(instance, runnable, initialDelay, reatingDelay);
	}

	/*
	 * Schedule a synchronous task. Tasks that deal with world manipulation must be synchronous
	 * 
	 * @param runnable - the task to call
	 * @param delay - the delay between each task call
	 */
	public static BukkitTask scheduleSyncDelayedTask(Runnable runnable, long delay) {
		return instance.getServer().getScheduler().runTaskLater(instance, runnable, delay);
	}
	
	/*
	 * Cancel a task using its unique id
	 * 
	 * @param taskId - the id of the task
	 */
	public static void cancelTask(int taskId) {
		instance.getServer().getScheduler().cancelTask(taskId);
	}
	
	/*
	 * Check if a block is a sign
	 * 
	 * @param block - block to check
	 * @return - if block is a sign
	 */
	public static boolean isSign(Block block) {
		return block.getState() instanceof Sign;
	}

	/*
	 * Check if the block at a location is a sign
	 * 
	 * @param loc - location to check
	 * @return - if block is a sign
	 */
	public static boolean isSign(Location loc) {
		return isSign(loc.getBlock());
	}
	
	public static String getVersion() {
		return instance.getDescription().getVersion();
	}
	
	public static WinnersManager getWinnersManager() {
		return instance.wManager;
	}

	public static RewardManager getRewardsManager() {
		return instance.rManager;
	}
	
	public static final LotteryPlus getInstance() {
		return instance;
	}
}
