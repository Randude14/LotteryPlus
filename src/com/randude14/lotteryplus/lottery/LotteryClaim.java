package com.randude14.lotteryplus.lottery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import com.randude14.lotteryplus.lottery.reward.Reward;

@SerializableAs("LotteryClaim")
public class LotteryClaim implements ConfigurationSerializable {
	private List<Reward> rewards;
	private String lotteryName;
	
	public LotteryClaim(Lottery lottery, List<Reward> rewards) {
		this(lottery.getName(), rewards);
	}

	public LotteryClaim(String lottery, List<Reward> rewards) {
		this.rewards = new ArrayList<Reward>(rewards);
		this.lotteryName = lottery;
	}

	public String getLotteryName() {
		return lotteryName;
	}
	
	public List<Reward> getRewards() {
		return rewards;
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> serialMap = new HashMap<String, Object>();
		serialMap.put("lottery-name", lotteryName);
		for(int cntr = 0;cntr < rewards.size();cntr++) {
			serialMap.put("reward" + (cntr+1), rewards.get(cntr));
		}
		return serialMap;
	}
	
	public static LotteryClaim deserialize(Map<String, Object> serialMap) {
		String lotteryName = (String) serialMap.get("lottery-name");
		List<Reward> rewards = new ArrayList<Reward>();
		for(int cntr = 1;serialMap.containsKey("reward" + cntr);cntr++) {
			rewards.add((Reward) serialMap.get("reward" + cntr));
		}
		return new LotteryClaim(lotteryName, rewards);
	}
}
