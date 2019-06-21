package com.randude14.lotteryplus.command;

import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.util.ChatUtils;

public class SaveCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		LotteryManager.saveLotteries();
		ChatUtils.send(sender, "plugin.command.save.mess");
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}
	
    public Perm getPermission() {
		return Perm.FORCE_SAVE;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.FORCE_SAVE, "plugin.command.save", cmd);
	}

	public void listCommands(CommandSender sender, Set<String> list) {
		list.add("plugin.command.save");
	}
	
	public int minValues() {
		return 0;
	}
}
