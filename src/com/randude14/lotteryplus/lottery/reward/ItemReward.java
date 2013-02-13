package com.randude14.lotteryplus.lottery.reward;

import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.randude14.lotteryplus.ChatUtils;
import com.randude14.lotteryplus.configuration.Config;

@SerializableAs("ItemReward")
public class ItemReward implements Reward {
	private final ItemStack reward;
	
	public ItemReward(ItemStack item) {
		this.reward = item;
	}

	public boolean rewardPlayer(Player player, String lottery) {
		boolean drop = player.getInventory().addItem(reward).isEmpty();
		if(!drop) {
			if(Config.getBoolean(Config.DROP_REWARD)) {
				dropReward(player);
			} else {
				return false;
			}
		}
		ChatUtils.send(player, "lottery.reward.item", "<amount>", reward.getAmount(), "<item>", reward.getType().name(), "<lottery>", lottery);
		return true;
	}
	
	private void dropReward(Player player) {
		player.getWorld().dropItem(player.getLocation(), reward);
	}
	
	public String getInfo() {
		return ChatUtils.getRawName("lottery.reward.item.info", "<amount>", reward.getAmount(), "<item>", reward.getType().name());
	}
	
	public static ItemReward deserialize(Map<String, Object> map) {
		return new ItemReward(ItemStack.deserialize(map));
	}

	public Map<String, Object> serialize() {
		return reward.serialize();
	}
}
