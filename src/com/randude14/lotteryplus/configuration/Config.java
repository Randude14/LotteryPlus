package com.randude14.lotteryplus.configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;

import com.randude14.lotteryplus.LotteryPlus;

/*
 * This class has multiple static methods that pull the config using the multiple defined
 * property objects below. Each is tied to a specific data type and has default values
 * so that the methods will never return null. @see config.yml
 */
public class Config {
	private static final LotteryPlus plugin = LotteryPlus.getInstance();
	
	// Config properties
	public static final Property<Long> SAVE_DELAY = new Property<Long>("properties.save-delay", 15L);
	public static final Property<Long> UPDATE_DELAY = new Property<Long>("properties.update-delay", 60L);
	public static final Property<Long> REMINDER_MESSAGE_TIME = new Property<Long>("properties.reminder-message-delay", 10L);
	public static final Property<Long> DRAW_DELAY = new Property<Long>("properties.draw-delay", 3L);
	public static final Property<Long> BUY_DELAY = new Property<Long>("properties.buy-delay", 30L);
	public static final Property<Boolean> FORCE_SAVE_ENABLE = new Property<Boolean>("properties.save-enable", true);
	public static final Property<Boolean> UPDATE_CHECK_ENABLE = new Property<Boolean>("properties.update-enable", false);
	public static final Property<Boolean> REMINDER_MESSAGE_ENABLE = new Property<Boolean>("properties.reminder-message-enable", true);
	public static final Property<Boolean> DROP_REWARD = new Property<Boolean>("properties.drop-reward", true);
	public static final Property<Boolean> SHOULD_DROP = new Property<Boolean>("properties.should-drop", true);
	public static final Property<Boolean> BUY_ENABLE = new Property<Boolean>("properties.buy-enable", true);
	public static final Property<Boolean> OPEN_INVENTORY = new Property<Boolean>("properties.open-inv", false);
	public static final Property<Boolean> SHOULD_LOG = new Property<Boolean>("properties.should-log", false);
	public static final Property<Boolean> BUY_DURING_DRAW = new Property<Boolean>("properties.buy-during-draw", false);
	public static final Property<String> MAIN_LOTTERIES = new Property<String>("properties.main-lotteries", "");
	public static final Property<String> MONEY_FORMAT = new Property<String>("properties.money-format", "$<money>");
	public static final Property<String> LINE_SEPARATOR = new Property<String>("properties.line-separator", "<newline>");
	public static final Property<String> CHAT_PREFIX = new Property<String>("properties.chat-prefix", "&e[LotteryPlus] - ");
	public static final Property<String> SIGN_TAG = new Property<String>("properties.sign-tag", "&a[Lottery+]");
	public static final Property<String> DEFAULT_FILTER = new Property<String>("properties.default-filter", "");
	public static final Property<Boolean> DEFAULT_OP = new Property<Boolean>("properties.default-op", true);
	
	// Sign format properties
	public static final Property<String> UPDATE_SIGN_LINE_TWO = new Property<String>("sign-format.Update.line-2", "<name>");
	public static final Property<String> UPDATE_SIGN_LINE_THREE = new Property<String>("sign-format.Update.line-3", "<time>");
	public static final Property<String> UPDATE_SIGN_LINE_FOUR = new Property<String>("sign-format.Update.line-4", "<reward>");
	public static final Property<String> DRAWING_SIGN_LINE_TWO = new Property<String>("sign-format.Drawing.line-2", "<name>");
	public static final Property<String> DRAWING_SIGN_LINE_THREE = new Property<String>("sign-format.Drawing.line-3", "Drawing...");
	public static final Property<String> DRAWING_SIGN_LINE_FOUR = new Property<String>("sign-format.Drawing.line-4", "<reward>");
	public static final Property<String> OVER_SIGN_LINE_TWO = new Property<String>("sign-format.Over.line-2", "<name>");
	public static final Property<String> OVER_SIGN_LINE_THREE = new Property<String>("sign-format.Over.line-3", "Over");
	public static final Property<String> OVER_SIGN_LINE_FOUR = new Property<String>("sign-format.Over.line-4", "<winner>");
	
	// Lottery defaults. Also @see com.randude14.lottery.LotteryProperties for further use
	public static final Property<String> DEFAULT_ITEM_REWARDS = new Property<String>("defaults.item-rewards", "").setDescription("config.description.item-rewards");
	public static final Property<String> DEFAULT_WARNING_TIMES = new Property<String>("defaults.warning-times", "").setDescription("config.description.warning-times");
	public static final Property<String> DEFAULT_MATERIAL_NAME = new Property<String>("defaults.material-name", "Gold Ingot").setDescription("config.description.material-name");
	public static final Property<String> DEFAULT_RESET_ADD_ITEM_REWARDS = new Property<String>("defaults.reset-add-item-rewards", "").setDescription("config.description.reset-add-item-rewards");
	public static final Property<String> DEFAULT_TAX_ACCOUNT = new Property<String>("defaults.tax-account", "").setDescription("config.description.tax-account");
	public static final Property<String> DEFAULT_WORLDS = new Property<String>("defaults.worlds", "").setDescription("config.description.worlds");
	public static final Property<String> DEFAULT_TOWNY = new Property<String>("defaults.towny", "").setDescription("config.description.towny");
	public static final Property<String> DEFAULT_ALIASES = new Property<String>("defaults.aliases", "").setDescription("config.description.aliases");
	public static final Property<String> DEFAULT_MATERIAL = new Property<String>("defaults.material", Material.GOLD_INGOT.name()).setDescription("config.description.material-id");
	public static final Property<Boolean> DEFAULT_REPEAT = new Property<Boolean>("defaults.repeat", true).setDescription("config.description.repeat");
	public static final Property<Boolean> DEFAULT_CLEAR_POT = new Property<Boolean>("defaults.clear-pot", false).setDescription("config.description.clear-pot");
	public static final Property<Boolean> DEFAULT_CLEAR_REWARDS = new Property<Boolean>("defaults.clear-rewards", false).setDescription("config.description.clear-rewards");
	public static final Property<Boolean> DEFAULT_USE_VAULT = new Property<Boolean>("defaults.use-vault", true).setDescription("config.description.use-vault");
	public static final Property<Boolean> DEFAULT_KEEP_TICKETS = new Property<Boolean>("defaults.keep-tickets", true).setDescription("config.description.keep-tickets");
	public static final Property<Boolean> DEFAULT_USE_TIMER = new Property<Boolean>("defaults.use-timer", true).setDescription("config.description.use-timer");
	public static final Property<Boolean> DEFAULT_USE_POT = new Property<Boolean>("defaults.use-pot", true).setDescription("config.description.use-pot");
	public static final Property<Boolean> DEFAULT_BUY_TICKETS = new Property<Boolean>("defaults.buy-tickets", true).setDescription("config.description.buy-tickets");
	public static final Property<Boolean> DEFAULT_WIN_AGAIN = new Property<Boolean>("defaults.win-again", true).setDescription("config.description.win-again");
	public static final Property<Double> DEFAULT_POT = new Property<Double>("defaults.pot", 1000.0).setDescription("config.description.pot");
	public static final Property<Double> DEFAULT_TICKET_COST = new Property<Double>("defaults.ticket-cost", 10.0).setDescription("config.description.ticket-cost");
	public static final Property<Double> DEFAULT_TICKET_TAX = new Property<Double>("defaults.ticket-tax", 0.0).setDescription("config.description.ticket-tax");
	public static final Property<Double> DEFAULT_POT_TAX = new Property<Double>("defaults.pot-tax", 0.0).setDescription("config.description.pot-tax");
	public static final Property<Double> DEFAULT_RESET_ADD_TICKET_COST = new Property<Double>("defaults.reset-add-ticket-cost", 0.0).setDescription("config.description.reset-add-ticket-tax");
	public static final Property<Double> DEFAULT_RESET_ADD_POT = new Property<Double>("defaults.reset-add-pot", 0.0).setDescription("config.description.reset-add-pot");
	public static final Property<Double> DEFAULT_TIME = new Property<Double>("defaults.time", 72.0).setDescription("config.description.time");
	public static final Property<Double> DEFAULT_RESET_ADD_TIME = new Property<Double>("defaults.reset-add-time", 0.0).setDescription("config.description.reset-add-time");
	public static final Property<Integer> DEFAULT_MAX_TICKETS = new Property<Integer>("defaults.max-tickets", -1).setDescription("config.description.max-tickets");
	public static final Property<Integer> DEFAULT_MIN_PLAYERS = new Property<Integer>("defaults.min-players", 2).setDescription("config.description.min-players");
	public static final Property<Integer> DEFAULT_MAX_PLAYERS = new Property<Integer>("defaults.max-players", 10).setDescription("config.description.max-players");
	public static final Property<Integer> DEFAULT_TICKET_LIMIT = new Property<Integer>("defaults.ticket-limit", 0).setDescription("config.description.ticket-limit");
	public static final Property<Integer> DEFAULT_RESET_ADD_MAX_TICKETS = new Property<Integer>("defaults.reset-add-max-tickets", 0).setDescription("config.description.reset-add-max-tickets");
	public static final Property<Integer> DEFAULT_RESET_ADD_MIN_PLAYERS = new Property<Integer>("defaults.reset-add-min-players", 0).setDescription("config.description.reset-add-min-players");
	public static final Property<Integer> DEFAULT_RESET_ADD_MAX_PLAYERS = new Property<Integer>("defaults.reset-add-max-players", 0).setDescription("config.description.reset-add-max-players");
	public static final Property<Integer> DEFAULT_VOTIFIER_REWARD = new Property<Integer>("defaults.votifier-reward", 0).setDescription("config.description.votifier-reward");
	public static final Property<Long> DEFAULT_COOLDOWN = new Property<Long>("defaults.cooldown", 0L).setDescription("config.description.cooldown");
	public static final Property<Long> DEFAULT_WARMUP = new Property<Long>("defaults.warmup", 0L).setDescription("config.description.warmup");
	
	// An array of the properties the lotteries uses
	// @see com.randude14.lotteryplus.gui.LotteryCreator for usage
	public static final Property<?>[] lotteryDefaults = {DEFAULT_ITEM_REWARDS, 
		                        DEFAULT_WARNING_TIMES, 
		                        DEFAULT_MATERIAL_NAME, 
		                        DEFAULT_RESET_ADD_ITEM_REWARDS, 
		                        DEFAULT_TAX_ACCOUNT, 
		                        DEFAULT_WORLDS, 
		                        DEFAULT_TOWNY, 
		                        DEFAULT_ALIASES, 
		                        DEFAULT_REPEAT, 
		                        DEFAULT_CLEAR_POT, 
		                        DEFAULT_CLEAR_REWARDS, 
		                        DEFAULT_USE_VAULT, 
		                        DEFAULT_KEEP_TICKETS,  
		                        DEFAULT_USE_TIMER, 
		                        DEFAULT_USE_POT, 
		                        DEFAULT_BUY_TICKETS, 
		                        DEFAULT_POT, 
		                        DEFAULT_TICKET_COST, 
		                        DEFAULT_TICKET_TAX, 
		                        DEFAULT_POT_TAX,
		                        DEFAULT_RESET_ADD_TICKET_COST, 
		                        DEFAULT_RESET_ADD_POT, 
		                        DEFAULT_TIME,
		                        DEFAULT_RESET_ADD_TIME,
		                        DEFAULT_MAX_TICKETS, 
		                        DEFAULT_MIN_PLAYERS, 
		                        DEFAULT_MAX_PLAYERS, 
		                        DEFAULT_TICKET_LIMIT, 
		                        DEFAULT_MATERIAL, 
		                        DEFAULT_RESET_ADD_MAX_TICKETS, 
		                        DEFAULT_RESET_ADD_MIN_PLAYERS, 
		                        DEFAULT_RESET_ADD_MAX_PLAYERS, 
		                        DEFAULT_VOTIFIER_REWARD, 
		                        DEFAULT_COOLDOWN, 
		                        DEFAULT_WARMUP};
	
	static {
		Arrays.sort(Config.lotteryDefaults); // sort lottery defaults
	}
	
	/*
	 * @param property - long property to grab from config
	 * @return - the long value at property
	 */
	public static long getLong(Property<Long> property) {
		return plugin.getConfig().getLong(property.getPath(), property.getDefaultValue());
	}
	
	public static int getInt(Property<Integer> property) {
		return plugin.getConfig().getInt(property.getPath(), property.getDefaultValue());
	}
	
	public static double getDouble(Property<Double> property) {
		return plugin.getConfig().getDouble(property.getPath(), property.getDefaultValue());
	}
	
	public static boolean getBoolean(Property<Boolean> property) {
		return plugin.getConfig().getBoolean(property.getPath(), property.getDefaultValue());
	}
	
	public static String getString(Property<String> property) {
		return plugin.getConfig().getString(property.getPath(), property.getDefaultValue());
	}
	
	/*
	 * Takes the value at a string property and finds the list
	 * by splitting the string by its spaces
	 * 
	 * @param property - string property to find list from
	 * @return - the string list at the property
	 */
	public static List<String> getStringList(Property<String> property) {
		String value = getString(property);
		
		// if value is empty, return empty list
		if(value.equals("")) 
			return Collections.emptyList();
		
		// split string and convert array to a list
		return Arrays.asList(value.split("\\s+"));
	}
}
