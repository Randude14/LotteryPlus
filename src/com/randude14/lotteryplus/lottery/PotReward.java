package com.randude14.lotteryplus.lottery;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.ChatUtils;
import com.randude14.lotteryplus.register.economy.Economy;

@SerializableAs("PotReward")
public class PotReward implements Reward {
	private final Economy econ;
	private final double pot;
	
	public PotReward(Economy econ, final double pot) {
		this.econ = econ;
		this.pot = pot;
	}
	
	public PotReward(final int materialID, final double pot) {
		this.econ = Economy.valueOf(materialID);
		this.pot = pot;
	}
	
	public void rewardPlayer(Player player) {
		try {
			econ.deposit(player.getName(), pot);
			ChatUtils.send(player, "lottery.reward.pot", "<pot>", econ.format(pot));
		} catch (Exception ex) {
			ChatUtils.send(player, "lottery.exception.reward");
			ex.printStackTrace();
		}
	}
	
	public String getInfo() {
		return ChatUtils.getNameFor("lottery.reward.pot.info", "<pot>", econ.format(pot));
	}
	
	public static PotReward deserialize(Map<String, Object> map) {
		double pot = (Double) map.get("pot");
		int materialID = (map.containsKey("material-id")) ? (Integer) map.get("material-id") : -1;
		return new PotReward(materialID, pot);
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pot", pot);
		map.put("material-id", econ.getMaterialID());
		return map;
	}
}
