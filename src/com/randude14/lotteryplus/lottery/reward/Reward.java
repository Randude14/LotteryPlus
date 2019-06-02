package com.randude14.lotteryplus.lottery.reward;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

/*
 * Represents the bases for a reward used in lotteries
 * 
 * @see org.bukkit.configuration.serialization.ConfigurationSerializable
 * Rewards must have a public static final method
 * named deserialization(Map<String, Object> map)
 * so that they can be saved with their lotteries
 * 
 * @see org.bukkit.configuration.serialization.ConfigurationSerializable
 */
public interface Reward extends ConfigurationSerializable {
	
	boolean rewardPlayer(Player player, String lottery);
	
	String getInfo();

}
