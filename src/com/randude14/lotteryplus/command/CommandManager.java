package com.randude14.lotteryplus.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.util.ChatUtils;

public class CommandManager implements CommandExecutor {
	private final Map<String, Command> commands = new HashMap<String, Command>();

	public CommandManager() {

	}

	public CommandManager registerCommand(Command command, String... labels) {
		for(String label : labels) {
			commands.put(label.toLowerCase(), command);
		}
		return this;
	}

	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		boolean help = false;
		if(args.length == 0 || args[0].equals("?")) {
			this.getCommands(sender, cmd, 1);
			help = true;
		}
		if(!help) {
			try {
				int page = Integer.parseInt(args[0]);
				this.getCommands(sender, cmd, page);
				help = true;
			} catch (Exception ex) {
			}
		}
		if(help) {
			return true;
		}
		Command command = commands.get(args[0].toLowerCase());
		if (command != null) {
			CommandAccess access = command.getAccess();
			Perm permission = command.getPermission();
			if (!access.hasAccess(sender)) {
				ChatUtils.send(sender, "plugin.command.error.access");
			} else if(permission == null || permission.checkPermission(sender)) {
				try {
					args = (String[]) ArrayUtils.remove(args, 0);
					if(command.minValues() <= args.length) {
						return command.execute(sender, cmd, args);
					} else {
						command.getCommands(sender, cmd);
						return true;
					}
				} catch (Exception ex) {
					ChatUtils.send(sender, "plugin.command.exception");
					ex.printStackTrace();
				}
			}
		} else {
			ChatUtils.send(sender, "plugin.command.error.notfound", "<command>", args[0]);
		}
		return false;
	}
	
    public void listCommands(CommandSender sender, List<String> list) {
		for(Command command : commands.values()) {
			CommandAccess access = command.getAccess();
			Perm permission = command.getPermission();
			if(access.hasAccess(sender) && (permission == null || permission.hasPermission(sender))) {
					command.listCommands(sender, list);
			}
		}
	}

    private void getCommands(CommandSender sender, org.bukkit.command.Command cmd, int page) {
		List<String> list = new CommandList();
		list.add("plugin.command.main");
		listCommands(sender, list);
		Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
		
		int len = list.size();
		int max = (len / 10) + 1;
		if (len % 10 == 0)
			max--;
		if (page > max)
			page = max;
		if (page < 1)
			page = 1;
		int cntr = (page * 10) - 10;
		ChatUtils.sendRaw(sender, "plugin.command.headliner", "<page>", page, "<max>", max);
		for (int stop = cntr + 10; cntr < stop && cntr < len; cntr++) {
			ChatUtils.sendRaw(sender, list.get(cntr), "<command>", cmd.getLabel());
		}
	}
    
    @SuppressWarnings("serial")
	private class CommandList extends ArrayList<String> {
    	@Override
    	public boolean add(String str) {
    		if(contains(str)) {
    			return false;
    		}
    		return super.add(str);
    	}
    }
}
