package com.randude14.lotteryplus.lottery.reward;

import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.util.ChatUtils;

/*
 * Stores an item stack as a reward. Uses ItamStack.serialize() and
 * ItemStack.deserialize(Map<String, Object> map) for easy saving
 * and loading
 */
@SerializableAs("ItemReward")
public class ItemReward implements Reward {
	private final ItemStack reward;
	
	public ItemReward(ItemStack item) {
		this.reward = item;
	}
	
	public static ItemReward deserialize(Map<String, Object> map) {
		return new ItemReward(ItemStack.deserialize(map));
	}

	public Map<String, Object> serialize() {
		return reward.serialize();
	}

	public boolean rewardPlayer(Player player, String lottery) {
		
		// checks if the returned map contains any items that were leftover
		Map<Integer, ItemStack> items = player.getInventory().addItem(reward);
		
		if(!items.isEmpty()) {
			
			// if the config defines that we can drop the reward
			if(Config.getBoolean(Config.DROP_REWARD)) {
				dropReward(player, items);
			
			// if not return false so that the reward can be stored
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	/*
	 * Internal method used to drop the items to the world at location of player
	 */
	private void dropReward(Player player, Map<Integer, ItemStack> items) {
		
		for(Integer num : items.keySet()) {
		   player.getWorld().dropItem(player.getLocation(), items.get(num));
		}
	}
	
	public String getInfo() {
		return ChatUtils.getRawName("lottery.reward.item.info", "<amount>", 
				reward.getAmount(), "<item>", reward.getType().name());
	}
}
