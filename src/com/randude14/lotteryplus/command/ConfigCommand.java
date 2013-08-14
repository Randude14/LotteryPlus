package com.randude14.lotteryplus.command;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.ChatUtils;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.LotteryPlus;

public class ConfigCommand implements Command {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if(!LotteryPlus.checkPermission(sender, Perm.CONFIG_RELOAD)) {
			return false;
		}
		if(!args[0].equals("reload")) {
			getCommands(sender, cmd);
			return true;
		}
		LotteryPlus.reload();
		ChatUtils.send(sender, "plugin.command.config.version", "<version>", LotteryPlus.getVersion());
		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, Perm.CONFIG_RELOAD, "plugin.command.config", cmd);
	}

	public void listCommands(CommandSender sender, List<String> list) {
		if(LotteryPlus.hasPermission(sender, Perm.CONFIG_RELOAD))
			list.add("plugin.command.config");
	}
	
	public int minValues() {
		return 1;
	}
}
