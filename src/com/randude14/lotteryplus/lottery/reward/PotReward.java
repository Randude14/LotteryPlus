package com.randude14.lotteryplus.lottery.reward;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.util.ChatUtils;
import com.randude14.register.economy.Economy;

@SerializableAs("PotReward")
public class PotReward implements Reward {
	private final Economy econ;
	private double pot;
	
	public PotReward(Economy econ, final double pot) {
		this.econ = econ;
		this.pot = pot;
	}
	
	public boolean rewardPlayer(Player player, String lottery) {
		double left = econ.deposit(player, pot);
		pot = left;
		return left <= 0;
	}
	
	public String getInfo() {
		return ChatUtils.getRawName("lottery.reward.pot.info", "<pot>", econ.format(pot));
	}
	
	public static PotReward deserialize(Map<String, Object> map) {
		double pot = ((Number) map.get("pot")).doubleValue();
		Economy econ = (Economy) map.get("econ");
		return new PotReward(econ, pot);
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pot", pot);
		map.put("econ", econ);
		return map;
	}
}
