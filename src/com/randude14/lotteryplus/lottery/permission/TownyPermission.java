package com.randude14.lotteryplus.lottery.permission;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.randude14.lotteryplus.lottery.Lottery;

/*
 * Hooks into the Towny plugin and checks the towns users are in
 */
public class TownyPermission extends Permission {
	private final Lottery lottery;
	
	public TownyPermission(final Lottery lottery) {
		this.lottery = lottery;
	}

	public boolean hasAccess(CommandSender sender) {
		
		// check if user is a player
		if (sender instanceof Player) {
			
			List<String> towny = lottery.getTowny();
			if(towny.isEmpty()) 
				return true;
			String residentTown = null;
			
			try {
				residentTown = TownyUniverse.getDataSource()
						.getResident(sender.getName()).getTown().getName();
				
			} catch (NotRegisteredException e) {
			}
			
			// if we could not find a town associated with this user
			// assume they are not a citizen of any town
			if (residentTown == null) 
				return false;
			
			// search if the user town matches any in the lottery
			for (String town : towny) {
				if (residentTown.equalsIgnoreCase(town)) 
					return true;
			}
			
			return false;
		}
		
		// we assume the sender is the console it self
		return true;
	}

	protected String getErrorMessage() {
		return "lottery.error.towny.access";
	}
}
