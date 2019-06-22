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
import com.randude14.lotteryplus.util.Utils;

/*
 * The command executor for the plugin. Handles different commands registered to it and has an auto
 * complete tab executor to help users filling out the commands
 */
public class CommandManager implements TabExecutor {
	private final Map<String, Command> commands = new HashMap<String, Command>();

	public CommandManager() {

	}

	/*
	 * Registers a command to this executor.
	 * @command - command to register
	 * @param labels - labels to bind this command to
	 * @return - this class to allow easy registering by daisy chaining.
	 */
	public CommandManager registerCommand(Command command, String... labels) {
		for(String label : labels) {
			commands.put(label.toLowerCase(), command);
		}
		return this;
	}

	/*
	 * Called when a user executes the plugin command. Send information dependent on the number of arguments
	 * and the specific information within them.
	 * @param sender - user that executed the command
	 * @param cmd - bukkit command that contains the label
	 * @param label - label of the command
	 * @param args - arguments typed after the command
	 * @return - whether the executed command was successful
	 */
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {

		// send command information if there are no arguments or typed a question mark
		if(args.length == 0 || args[0].equals("?")) {
			this.getCommands(sender, cmd, 1);
			return true;
		
		// if first argument is a number
		} else if (Utils.isNumber(args[0])) {
			int page = Integer.parseInt(args[0]);
			this.getCommands(sender, cmd, page);
			return true;
		}
		
		Command command = commands.get(args[0].toLowerCase());
		
		// if command was found, execute that command or send information if there is not enough arguments
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
	
	/*
	 * Called when a user is typing in a command. Sends a list of suggested words to fill in to the current argument
	 * they are typing in.
	 * @param sender - user that executed the command
	 * @param cmd - bukkit command that contains the label
	 * @param label - label of the command
	 * @param args - arguments typed after the command
	 * @return - whether the executed command was successful
	 */
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command c, String label, String[] args) {
		
		if (args.length == 0) {
			return new ArrayList<String>(commands.keySet());
			
		// typing out first command, return list of commands they have access to and return the
		// commands that have the label that starts with the first argument
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
	
	/*
	 * Lists out all the commands to the user
	 * @param sender - user to list commands for
	 * @param list - set to add the command codes to
	 */
    private void listCommands(CommandSender sender, Set<String> list) {
		for(Command command : commands.values()) {
			CommandAccess access = command.getAccess();
			Perm permission = command.getPermission();
			
			if(access.hasAccess(sender) && (permission == null || permission.hasPermission(sender))) {
					command.listCommands(sender, list);
			}
			
		}
	}

    /*
     * List up to 10 commands according to the page number
     * @param sender - user to send commands to
     * @param cmd - bukkit commands that contains the label
     * @param page - page number to go to
     */
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
