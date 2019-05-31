package com.randude14.lotteryplus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.randude14.lotteryplus.configuration.CustomYaml;
import com.randude14.lotteryplus.util.ChatUtils;

/*
 * Uses the WinnerLogger class to log winner records and allows users to see past winners
 */
public class WinnersManager {
	private final CustomYaml winnersConfig = new CustomYaml("winners.yml");
	private final List<String> winners = new ArrayList<String>();
	private final WinnersLogger logger = new WinnersLogger();
	
	/*
	 * Called to log a winner after a successful drawing
	 */
	public void logWinner(String record) {
		logger.info(record);
		winners.add(record);
		updateWinners();
		winnersConfig.getConfig().set("winners", winners);
		winnersConfig.saveConfig();
	}
	
	/*
	 * Called when the plugin is enabled
	 */
	public void onEnable() {
		this.loadWinners();
	}
	
	/*
	 * Called when the plugin is disabled
	 */
	public void onDisable() {
		logger.close();
	}
	
	/*
	 * Send a user a list of the past winners
	 */
	public void listWinners(CommandSender sender) {
		ChatUtils.sendRaw(sender, "plugin.command.list-winners.headliner");
		for(int cntr = 0;cntr < winners.size();cntr++) {
			ChatUtils.send(sender, "plugin.command.list-winners.token", "<number>", (cntr+1) , "<winner>", winners.get(cntr));
		}
	}
	
	/*
	 * Called to update the winners from the file
	 */
	public void loadWinners() {
		winners.clear();
		List<String> list = winnersConfig.getConfig().getStringList("winners");
		if(list != null && !list.isEmpty()) {
			winners.addAll(list);
		}
		updateWinners();
	}
	
	/*
	 * Internal method called to update winners. Only keeps the past 10 recent winners
	 */
	private void updateWinners() {
		while(winners.size() > 10) {
			winners.remove(0);
		}
	}
}
