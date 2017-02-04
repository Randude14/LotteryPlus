package com.randude14.lotteryplus.lottery.reward;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.randude14.lotteryplus.LotteryPlus;
import com.randude14.lotteryplus.util.ChatUtils;

public class CommandReward implements Reward {
	private String name, message, command;
	
	public CommandReward(String name, String message, String command) {
		this.name = name;
		this.message = message;
		this.command = command;
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("message", message);
		map.put("command", command);
		return map;
	}

	public boolean rewardPlayer(Player player, String lottery) {
		ChatUtils.send(player, message, "<lottery>", lottery, "<player>", player.getName());
		command = command.replace("<lottery>", lottery).replace("<player>", player.getName());
		LotteryPlus.dispatchCommand(command);
		return true;
	}

	public String getInfo() {
		return "lottery.reward.command.info";
	}
	
}
