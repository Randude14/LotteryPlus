package com.randude14.lotteryplus.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.util.ChatUtils;

public class CommandManager implements TabExecutor {
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

		if(args.length == 0 || args[0].equals("?")) {
			this.getCommands(sender, cmd, 1);
			return true;
		} else {
			try {
				int page = Integer.parseInt(args[0]);
				this.getCommands(sender, cmd, page);
				return true;
			} catch (Exception ex) {
			}
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
	
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command c, String label, String[] args) {
		
		if (args.length == 0) {
			return new ArrayList<String>(commands.keySet());
			
		} else if (args.length == 1) {
			return commands.keySet().stream().filter( command -> {
				
				Command cmd = commands.get(command);
				Perm permission = cmd.getPermission();
				
				if ( (permission == null || permission.hasPermission(sender))
						&& command.startsWith(args[0]) && cmd.getAccess().hasAccess(sender)) {
					return true;
				}
				
				return false;
			} ).collect(Collectors.toList());
		}
		
		Command command = commands.get(args[0]);
		
		if (command != null) {
			Perm permission = command.getPermission();
			
			if (permission == null || permission.hasPermission(sender)) {
				
				// create new array to avoid compiler errors with the variable args
				String[] newArgs = (String[]) ArrayUtils.remove(args, 0);
				
				List<String> list = command.onTabComplete(sender, newArgs);
				return list == null ? Collections.emptyList() : list;
			}
		}
		
		return null;
	}
	
    public void listCommands(CommandSender sender, Set<String> list) {
		for(Command command : commands.values()) {
			CommandAccess access = command.getAccess();
			Perm permission = command.getPermission();
			if(access.hasAccess(sender) && (permission == null || permission.hasPermission(sender))) {
					command.listCommands(sender, list);
			}
		}
	}

    private void getCommands(CommandSender sender, org.bukkit.command.Command cmd, int page) {
		Set<String> list = new TreeSet<String>();
		list.add("plugin.command.main");
		listCommands(sender, list);
		
		int len = list.size();
		int max = (len / 10) + 1;
		if (len % 10 == 0)
			max--;
		if (page > max)
			page = max;
		if (page < 1)
			page = 1;
		int skipTo = (page * 10) - 10;
		
		ChatUtils.sendRaw(sender, "plugin.command.headliner", "<page>", page, "<max>", max);
		Iterator<String> iterator = list.iterator();
		
		// skip to where the page is
		while(iterator.hasNext() && skipTo-- > 0)
			iterator.next();
		
		for (int cntr = 0;cntr < 10 && iterator.hasNext();cntr++) {
			ChatUtils.sendRaw(sender, iterator.next(), "<command>", cmd.getLabel());
		}
	}
}
