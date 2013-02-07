package com.randude14.lotteryplus.lottery.reward;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public interface Reward extends ConfigurationSerializable {
	
	boolean rewardPlayer(Player player);
	
	String getInfo();

}
