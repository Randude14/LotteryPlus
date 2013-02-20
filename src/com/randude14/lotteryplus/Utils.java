package com.randude14.lotteryplus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.randude14.lotteryplus.configuration.Config;

public class Utils {
	
	public static long loadSeed(String line) {
		if(line == null)
			return new Random().nextLong();
		try {
			return Long.parseLong(line);
		} catch (Exception ex) {	
		}
		return (long) line.hashCode();
	}
	
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
		World world = Bukkit.getWorld(lines[0]);
		if(world == null) return null;
		int x = Integer.parseInt(lines[1]);
		int y = Integer.parseInt(lines[2]);
		int z = Integer.parseInt(lines[3]);
		return new Location(world, x, y, z);
	}
	
	public static String format(double amount) {
		return Config.getString(Config.MONEY_FORMAT).replace("<money>", String.format("%,.2f", amount));
	}
	
	public static List<ItemStack> getItemStacks(String line) {
		List<ItemStack> rewards = new ArrayList<ItemStack>();
		listItemStacks(rewards, line);
		return rewards;
	}
	
	private static ItemStack loadItemStack(String line) {
		if(line == null || line.isEmpty()) {
			return null;
		}
		try {
			short damage = 0;
			int stackSize = 1;
			int colenIndex = line.indexOf(':');
			int sizeIndex = line.indexOf('*');
			int hyphonIndex = line.indexOf('^');
			int itemIndex = line.length();
			if(colenIndex < 0) {
				if(sizeIndex < 0)
					colenIndex = line.length();
				else
					colenIndex = sizeIndex;
			}
			else {
				if(sizeIndex < 0)
					damage = Short.parseShort(line.substring(colenIndex+1));
				else
					damage = Short.parseShort(line.substring(colenIndex+1, sizeIndex));
			}
			for(int cntr = 0;cntr < line.length();cntr++) {
				char c = line.charAt(cntr);
				if(!Character.isDigit(c)) {
					itemIndex = cntr;
					break;
				}
			}
			int itemId = Integer.parseInt(line.substring(0, itemIndex));
			if(sizeIndex >= 0) {
				if(hyphonIndex >= 0)
					stackSize = Integer.parseInt(line.substring(sizeIndex+1, hyphonIndex));
				else
					stackSize = Integer.parseInt(line.substring(sizeIndex+1));
			}
			ItemStack result = new ItemStack(itemId, stackSize, damage);
			String[] enchants = line.split("\\^");
			if(enchants.length > 1) {
				for(int cntr = 1;cntr < enchants.length;cntr++) {
					String enchantLine = enchants[cntr];
					int levelIndex = enchantLine.indexOf(':');
					if(levelIndex < 0) {
						Enchantment enchant = Enchantment.getByName(enchantLine);
						if(enchant == null) return null;
						result.addEnchantment(enchant, enchant.getStartLevel());
					} else {
						Enchantment enchant = Enchantment.getByName(enchantLine.substring(0, levelIndex));
						if(enchant == null) return null;
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
		for(String str : line.split("\\s+")) {
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
