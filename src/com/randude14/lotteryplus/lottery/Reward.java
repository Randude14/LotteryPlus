package com.randude14.lotteryplus.lottery;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public interface Reward extends ConfigurationSerializable {
	
	void rewardPlayer(Player player);
	
	String getInfo();

}
