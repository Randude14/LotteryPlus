package com.randude14.lotteryplus.command;

import java.util.Set;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.util.ChatUtils;
import com.randude14.lotteryplus.LotteryPlus;

public class VersionCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		ChatUtils.send(sender, "plugin.command.version.mess", "<version>", LotteryPlus.getVersion());
		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}
	
    public Perm getPermission() {
    	return Perm.VERSION;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.DRAW, "plugin.command.version", cmd);
	}

	public void listCommands(CommandSender sender, Set<String> list) {
		list.add("plugin.command.version");
	}
	
	public int minValues() {
		return 0;
	}
}
