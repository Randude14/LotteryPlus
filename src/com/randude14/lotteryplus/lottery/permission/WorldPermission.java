package com.randude14.lotteryplus.lottery.permission;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.lottery.Lottery;

public class WorldPermission extends Permission {
	private final Lottery lottery;
	
	public WorldPermission(final Lottery lottery) {
		this.lottery = lottery;
	}

	public boolean hasAccess(CommandSender sender) {
		
		// check if user is a player
		if(sender instanceof Player) {
			List<String> worlds = lottery.getWorlds();
			
			if(worlds.isEmpty()) 
				return true;
			
			String worldToCheck = ((Player) sender).getWorld().getName();
			
			// check if player world is inside the current world
			for(String world : worlds) {
				if(worldToCheck.equalsIgnoreCase(world)) {
					return true;
				}
			}
			
			return false;
		}
		
		// we assume the sender is the console itself
		return true;
	}

	protected String getErrorMessage() {
		return "lottery.error.world.access";
	}
}
