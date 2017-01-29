package com.randude14.lotteryplus.lottery.reward;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.ChatUtils;
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
		try {
			double left = econ.deposit(player, pot);
			ChatUtils.send(player, "lottery.reward.pot", "<pot>", econ.format(pot), "<lottery>", lottery);
			pot = left;
			return left <= 0;
		} catch (Exception ex) {
			ChatUtils.send(player, "lottery.exception.reward");
			ex.printStackTrace();
		}
		return false;
	}
	
	public String getInfo() {
		return ChatUtils.getRawName("lottery.reward.pot.info", "<pot>", econ.format(pot));
	}
	
	public static PotReward deserialize(Map<String, Object> map) {
		double pot = ((Number) map.get("pot")).doubleValue();
		Economy econ = null;
		if(map.containsKey("material-id")) {
			int materialID = (map.containsKey("material-id")) ? (Integer) map.get("material-id") : -1;
			econ = Economy.getEconomy(materialID);
		} else {
			econ = (Economy) map.get("econ");
		}
		return new PotReward(econ, pot);
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pot", pot);
		map.put("econ", econ);
		return map;
	}
}
