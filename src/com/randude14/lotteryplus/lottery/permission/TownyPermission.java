package com.randude14.lotteryplus.lottery.permission;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.randude14.lotteryplus.lottery.Lottery;

public class TownyPermission extends Permission {
	private final Lottery lottery;
	
	public TownyPermission(final Lottery lottery) {
		this.lottery = lottery;
	}

	public boolean hasAccess(CommandSender sender) {
		if(sender instanceof Player) {
			List<String> towny = lottery.getTowny();
			if(towny.isEmpty()) return true;
			String residentTown = null;
			try {
				residentTown = TownyUniverse.getDataSource().getResident(sender.getName()).getTown().getName();
			} catch (NotRegisteredException e) {
			}
			if(residentTown == null) return false;
			for(String town : towny) {
				if(residentTown.equalsIgnoreCase(town)) 
					return true;
			}
			return false;
		}
		return true;
	}

	protected String getErrorMessage() {
		return "lottery.error.towny.access";
	}
}
