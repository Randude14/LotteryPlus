package com.randude14.lotteryplus.lottery;

import java.util.Map;

import org.bukkit.World;
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

	public void rewardPlayer(Player player) {
		if(Config.getBoolean(Config.SHOULD_DROP)) {
			World world = player.getWorld();
			world.dropItem(player.getLocation(), reward);
		} else {
			for(ItemStack item : player.getInventory().addItem(reward).values()) {
				World world = player.getWorld();
				world.dropItem(player.getLocation(), item);
			}
		}
		ChatUtils.send(player, "lottery.reward.item", "<amount>", reward.getAmount(), "<item>", reward.getType().name());
	}
	
	public ItemStack getItem() {
		return reward;
	}
	
	public String getInfo() {
		return ChatUtils.getNameFor("lottery.reward.item.info", "<amount>", reward.getAmount(), "<item>", reward.getType().name());
	}
	
	public static ItemReward deserialize(Map<String, Object> map) {
		return new ItemReward(ItemStack.deserialize(map));
	}

	public Map<String, Object> serialize() {
		return reward.serialize();
	}
}
