package com.randude14.lotteryplus.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.randude14.lotteryplus.Logger;
import com.randude14.lotteryplus.LotteryPlus;
import com.randude14.lotteryplus.configuration.Config;

public class Utils {
	
	/*
	 * Sleep for a certain amount of delay
	 * @param delay - time to delay
	 */
	public static void sleep(long delay) {
		try {
			Thread.sleep(delay);
		} catch (Exception ex) {
			Logger.info("logger.exception.sleep");
		}
	}
	
	/*
	 * Check if an object is a number
	 * @param value - value to check
	 * @return - whether the value is a number
	 */
	public static boolean isNumber(Object value) {
		
		if(value == null) {
			return false;
		}
		
		String string = value.toString();
		
		// check all chars are between 0 and 9
		return string.chars().allMatch( (int num) -> (num >= '0' && num <= '9') );
	}
	
	/*
	 * Convert a location to a string
	 * @param loc - location to convert
	 * @return - string containing the location info
	 */
	public static String parseLocation(Location loc) {
		return String.format("%s %d %d %d", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	/*
	 * Convert a string to a location
	 * @param str - string to convert
	 * @return - the location taken from the string
	 */
	public static Location parseToLocation(String str) {
		String[] lines = str.split("\\s");
		String worldName = "";
		
		// finds the world name
		// adds spaces between the names if the length > 4
		for(int cntr = 0;cntr < lines.length-3;cntr++) {
			
			if(lines[cntr].equals("")) {
				worldName += " ";
				
			} else {
				worldName += lines[cntr];
			}
			
			if(cntr != lines.length-4)
				worldName += " ";
		}
		
		World world = Bukkit.getWorld(worldName);
		
		// return null if the world no longer exists
		if(world == null) 
			return null;
		
		int x = Integer.parseInt(lines[lines.length-3]);
		int y = Integer.parseInt(lines[lines.length-2]);
		int z = Integer.parseInt(lines[lines.length-1]);
		return new Location(world, x, y, z);
	}
	
	/*
	 * Determine if two locations point to the same area
	 * @param loc1 - location of the first point
	 * @param loc2 - location of the second point
	 * @return - whether the two points are in the same area
	 */
	public static boolean locsInBounds(Location loc1, Location loc2) {
		return loc1.getBlockX() == loc2.getBlockX()
				&& loc1.getBlockY() == loc2.getBlockY()
				&& loc1.getBlockZ() == loc2.getBlockZ();
	}
	
	public static boolean isUUID(String tag) {
		return tag.split("-").length == 5;
	}
	
	/*
	 * Gets rid of the UUID in a unique identifier
	 * @param player - the unique identifier
	 * @return - the name from the unique identifier
	 */
	public static String getPlayerName(String tag) {
		
		if(Utils.isUUID(tag)) {
			try {
				UUID id = UUID.fromString(tag);
				OfflinePlayer player = Bukkit.getOfflinePlayer(id);
				return player.getName();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		return tag;
	}
	
	/*
	 * Given an offline player and a name, find if they are the same
	 * @param player - offline player to check
	 * @param name - the name to check
	 */
	public static boolean isSamePlayer(OfflinePlayer player, String name) {
		name = Utils.getPlayerName(name);
		
		 return player.getName().equalsIgnoreCase(name);
	}
	
	private static final Comparator<OfflinePlayer> offlineplayerComp = 
			(OfflinePlayer p1, OfflinePlayer p2) -> p1.getName().compareToIgnoreCase(p2.getName());
	
	/*
	 * Given a name, return the offline player
	 * @param name - the name to look for, can be a unique identifier
	 */
	public static OfflinePlayer getOfflinePlayer(String tag) {
		
		if(tag == null || tag.isEmpty()) {
			return null;
		}
		
		// if there is a colon, grab the UUID
		if(Utils.isUUID(tag)) {
			UUID id = UUID.fromString(tag);
			OfflinePlayer player = Bukkit.getOfflinePlayer(id);
			return player;
		}
		
		Stream<OfflinePlayer> stream = Arrays.stream( LotteryPlus.getBukkitServer().getOfflinePlayers() );
		// remove players that do not have names
		// can't identify players if they do have a name
		OfflinePlayer[] players = stream.filter( (player) -> player.getName() != null)
				                        .toArray(OfflinePlayer[]::new);
		
		Arrays.sort(players, offlineplayerComp);
		
		// use binary search
		int left = 0;
		int right = players.length - 1;
		
		while (left <= right) {
			int mid = (left + right) / 2;
			int result = players[mid].getName().compareTo(tag);
			
			if (result == 0)
				return players[mid];
			else if (result < 0)
				left = mid + 1;
			else
				right = mid - 1;
		}

		return null;
	}
	
	/*
	 * Return the player with a name
	 * @param playerName - player to search for
	 * @return - the player, if they are online
	 */
	public static Player getBukkitPlayer(String playerName) {
		OfflinePlayer player = Utils.getOfflinePlayer(playerName);
		if(player.isOnline())
			return player.getPlayer();
		else 
			return null;
	}
	
	/*
	 * Searches for and returns a list of players associated with match. If it is
	 * a UUID, searches by id, otherwise searches by player names
	 * @param match - search criteria to use
	 * @return - list of players associated with match
	 */
	public static List<OfflinePlayer> matchForPlayers(String match) {
		Stream<OfflinePlayer> players = Arrays.stream(Bukkit.getOfflinePlayers());
		
		// search using UUID
		if (Utils.isUUID(match)) {
			UUID id = UUID.fromString(match);
			players.filter( player -> player.getName() != null && player.getUniqueId().equals(id) );
			
		// search finding players that start with match
		} else {
			String name = match.toLowerCase(); // create another variable to avoid compiler errors with final
			players.filter( (player) -> (player.getName().toLowerCase().startsWith(name)));
		}
		
		
		return players.collect(Collectors.toList());
	}
	
	public static String format(double amount) {
		return Config.getString(Config.MONEY_FORMAT).replace("<money>", String.format("%,.2f", amount));
	}
	
	public static List<ItemStack> getItemStacks(String line) {
		List<ItemStack> rewards = new ArrayList<ItemStack>();
		listItemStacks(rewards, line);
		return rewards;
	}
	
	/*
	 * Loads an item stack containing in-game items. 
	 * Instructions on how it reads the line is an insert from the config below.
	 * 
	 * @param line - line to read from
	 * @return - the item stack
	 * 
	 * /////// IMPORTED FROM CONFIG ///////////
	 * item rewards that are added to the lottery
     * defined examples: 'white_wool*64' - stack of white wool
     * format -> 'material*amount'
     * more examples: 'Ink sack:0 Ink sack*1'
     *
     * for enchantments, create your item like described above,
     * add '^', the enchantment, add ':', then the level, like so:
     * 'Diamond_Pickaxe^durablility:3' -> this creates a diamond pickaxe with a durability enchantment with a level of 3
	 */
	private static ItemStack loadItemStack(String line) {
		
		if(line == null || line.isEmpty()) {
			return null;
		}
		
		try {
			int stackSize = 1;
			int sizeIndex = line.indexOf('*');
			int hyphonIndex = line.indexOf('^');
			
			// keep track of the material index. assume the name goes to the end
			int materialIndex = line.length();
			
			// get size
			if (sizeIndex >= 0) {
				
				// if there is an enchantment
				if (hyphonIndex >= 0) {
					stackSize = Integer.parseInt( line.substring(sizeIndex+1, hyphonIndex) );
					
				} else {
					stackSize = Integer.parseInt( line.substring(sizeIndex+1) );
				}
				
				// size is defined so we scale back the index to its size
				materialIndex = sizeIndex;
				
			} else if (hyphonIndex >= 0) {
				
				// size was not defined but enchantments are so we scale to the first hyphon
				materialIndex = hyphonIndex;
			}
			
			Material material = Material.matchMaterial( line.substring(0, materialIndex) );
			
			// if we could not find the material of the item, return null
			if(material == null)
				return null;
			
			ItemStack result = new ItemStack(material, stackSize);
			String[] enchants = line.split("\\^");
			
			// is size > 1, we assume there are enchantments
			if (enchants.length > 1) {
				
				for (int cntr = 1;cntr < enchants.length;cntr++) {
					String enchantLine = enchants[cntr];
					int levelIndex = enchantLine.indexOf(':');
					
					// if no level given
					if (levelIndex < 0) {
						Enchantment enchant = Enchantment.getByName(enchantLine);
						
						// return null. 
						if(enchant == null) 
							return null;
						
						result.addEnchantment(enchant, enchant.getStartLevel());
						
				    // level given
					} else {
						Enchantment enchant = Enchantment.getByName(  enchantLine.substring(0, levelIndex) );
						
						if(enchant == null) 
							return null;
						
						int level = Integer.parseInt(enchantLine.substring(levelIndex + 1));
						result.addEnchantment(enchant, level);
					}
				}
			}
			
			return result;
			
		} catch (Exception ex) {
			
			ex.printStackTrace();
		}
		
		return null;
	}
	
	private static void listItemStacks(List<ItemStack> rewards, String line) {
		for (String str : line.split("\\s+")) {
			try {
				ItemStack item = loadItemStack(str);
				if(item != null)
					rewards.add(item);
				else
					Logger.info("logger.exception.itemstack", "<line>", line);
			} catch (Exception ex) {
				Logger.info("logger.exception.itemstack", "<line>", line);
			}
		}
	}
}
