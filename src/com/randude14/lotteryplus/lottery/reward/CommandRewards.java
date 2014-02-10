package com.randude14.lotteryplus.lottery.reward;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.randude14.lotteryplus.ChatUtils;

public class CommandRewards implements Reward {
	private final List<String> messages;
	
	public CommandRewards(List<String> messages, List<String> commands) {
		if(messages.size() != commands.size() || messages.isEmpty()) {
			throw new IllegalArgumentException("Messages and commands must be of equals size and not empty!");
		}
		this.messages = messages;
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("message", messages);
		return map;
	}

	public boolean rewardPlayer(Player player, String lottery) {
		for(int i = 0;i < messages.size();i++) {
			ChatUtils.send(player, messages.get(i), "<player>", player.getName());
		}
		return true;
	}

	public String getInfo() {
		return null;
	}
	
}
