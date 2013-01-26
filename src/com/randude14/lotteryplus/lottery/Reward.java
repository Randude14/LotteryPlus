package com.randude14.lotteryplus.lottery;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

//represents a reward for a lottery
public interface Reward extends ConfigurationSerializable {
	
	//return whether or not the player
	//was successfully rewarded
	void rewardPlayer(Player player);
	
	//return info about the reward
	String getInfo();

}
