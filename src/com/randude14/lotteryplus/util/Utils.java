package com.randude14.lotteryplus.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.randude14.lotteryplus.Logger;
import com.randude14.lotteryplus.LotteryPlus;
import com.randude14.lotteryplus.configuration.Config;

public class Utils {
	
	public static void sleep(long delay) {
		try {
			Thread.sleep(delay);
		} catch (Exception ex) {
			Logger.info("logger.exception.sleep");
		}
	}
	
	public static String parseLocation(Location loc) {
		return String.format("%s %d %d %d", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	public static Location parseToLocation(String str) {
		String[] lines = str.split("\\s");
		String worldName = "";
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
		if(world == null) return null;
		int x = Integer.parseInt(lines[lines.length-3]);
		int y = Integer.parseInt(lines[lines.length-2]);
		int z = Integer.parseInt(lines[lines.length-1]);
		return new Location(world, x, y, z);
	}
	
	public static boolean locsInBounds(Location loc1, Location loc2) {
		return loc1.getBlockX() == loc2.getBlockX()
				&& loc1.getBlockY() == loc2.getBlockY()
				&& loc1.getBlockZ() == loc2.getBlockZ();
	}
	
	public static String getUniqueName(OfflinePlayer player) {
		return player.getName() + LotteryPlus.NAME_SEPARATOR + player.getUniqueId();
	}
	
	public static String stripNameOfId(String player) {
        int colonIndex = player.lastIndexOf(LotteryPlus.NAME_SEPARATOR);
		
		if(colonIndex >= 0) {
			return player.substring(0, colonIndex);
		}
		
		return player;
	}
	
	
	private static final Comparator<OfflinePlayer> offlineplayerComp = 
			(OfflinePlayer p1, OfflinePlayer p2) -> p1.getName().compareToIgnoreCase(p2.getName());
	
	public static boolean isSamePlayer(OfflinePlayer player, String name) {
		int colonIndex = name.lastIndexOf(LotteryPlus.NAME_SEPARATOR);
		
		if(colonIndex >= 0) {
			String id = name.substring(colonIndex+1);
			UUID uuid = UUID.fromString(id);
			OfflinePlayer check = Bukkit.getOfflinePlayer(uuid);
			return player.getUniqueId() == check.getUniqueId();
		}
		
		 return player.getName().equalsIgnoreCase(name);
	}
	
	// uses binary search
	@SuppressWarnings("deprecation")
	public static OfflinePlayer getOfflinePlayer(String name) {
		int colonIndex = name.lastIndexOf(LotteryPlus.NAME_SEPARATOR);
		
		if(colonIndex >= 0) {
			String id = name.substring(colonIndex+1);
			UUID uuid = UUID.fromString(id);
			OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
			if(player != null) {
				return player;
			} else {
				name = name.substring(0, colonIndex);
			}
		}
		
		OfflinePlayer[] players = LotteryPlus.getBukkitServer().getOfflinePlayers();
		
		Arrays.sort(players, offlineplayerComp);
		
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
		OfflinePlayer player = Utils.getOfflinePlayer(playerName);
		if(player.isOnline())
			return player.getPlayer();
		else 
			return null;
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
						Enchantment enchant = Enchantment.getByKey(
								NamespacedKey.minecraft(enchantLine));
						
						// return null. 
						if(enchant == null) 
							return null;
						
						result.addEnchantment(enchant, enchant.getStartLevel());
						
				    // level given
					} else {
						Enchantment enchant = Enchantment.getByKey( 
								NamespacedKey.minecraft( enchantLine.substring(0, levelIndex) ) );
						
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
