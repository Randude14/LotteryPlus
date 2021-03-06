package com.randude14.lotteryplus.command;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.util.ChatUtils;

public class UnloadCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		boolean delete = false;
		if(args.length >= 2) delete = new Boolean(args[1]).booleanValue();
		LotteryManager.unloadLottery(sender, args[0], delete);
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 0) {
			return LotteryManager.getLotteryNames();
		} else if (args.length == 1) {
			return LotteryManager.getLotteryNames(args[0]);
		} else if (args.length == 2) {
			List<String> options = Arrays.asList(new String[] {"true", "false"});
			return options;
		}
		
		return null;
	}

	public CommandAccess getAccess() {
		return CommandAccess.CONSOLE;
	}
	
    public Perm getPermission() {
		return Perm.UNLOAD;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.UNLOAD, "plugin.command.unload", cmd);
	}

	public void listCommands(CommandSender sender, Set<String> list) {
		list.add("plugin.command.unload");
	}
	
	public int minValues() {
		return 1;
	}
}
