package com.randude14.lotteryplus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.configuration.CustomYaml;
import com.randude14.lotteryplus.lottery.LotteryClaim;
import com.randude14.lotteryplus.lottery.reward.Reward;
import com.randude14.lotteryplus.util.ChatUtils;
import com.randude14.lotteryplus.util.Utils;

/*
 * This class keeps track of the rewards given to the winners of the lotteries
 * Uses a HashMap with player names as the key and a LotteryClaim list as their values
 */
public class RewardManager {
	private final Map<String, List<LotteryClaim>> claims = new HashMap<String, List<LotteryClaim>>();
	private final CustomYaml claimsConfig; // file that contains the saved rewards
	
	public RewardManager() {
		claimsConfig = new CustomYaml("claims.yml");
	}
	
	/*
	 * Internal method called to save the rewards
	 */
	private void saveRewardClaims() {
		FileConfiguration config = claimsConfig.getConfig();
		config.createSection("claims"); // clean config
		
		for(String player : claims.keySet()) {
			int cntr = 0;
			for(LotteryClaim claim : claims.get(player)) {
				config.set("claims." + player + ".reward" + (++cntr), claim);
			}
		}
		
		claimsConfig.saveConfig();
	}
	
	/*
	 * Called at plugin load time. Loads the saved rewards
	 */
	public void loadRewardClaims() {
		FileConfiguration config = claimsConfig.getConfig();
		ConfigurationSection section = config.getConfigurationSection("claims");
		
		if(section == null) // create section if it does not exist
			section = config.createSection("claims");
		
		// go through each saved player
		for(String player : section.getKeys(false)) {
			ConfigurationSection playerSection = section.getConfigurationSection(player);
			
			if(playerSection == null)
				continue;
			
			List<LotteryClaim> playerClaims = new ArrayList<LotteryClaim>();
			
		    // attempt to get the rewards and add them to the list
			for(String claimPath : playerSection.getKeys(false)) {
				
				try {
					playerClaims.add((LotteryClaim) playerSection.get(claimPath));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
			claims.put(player, playerClaims);
		}
	}

	/*
	 * Called when a player is offline during the drawing
	 */
	public void addRewardClaim(OfflinePlayer player, String lottery, List<Reward> rewards) {
		String name = Utils.getUniqueName(player);
		
		if (!claims.containsKey(name))
			claims.put(name, new ArrayList<LotteryClaim>());
		
		// create the reward and then save
		LotteryClaim claim = new LotteryClaim(lottery, rewards);
		claims.get(name).add(claim);
		this.saveRewardClaims();
	}
	
	/*
	 * Called when a player could not receive the rewards
	 * 
	 * @param player - player to reward
	 * @param lottery - name of the lottery the reward came from
	 * @Param rewards - the list of unclaimed rewards
	 * 
	 * @return - return whether the list of rewards was emptied
	 */
	public boolean rewardPlayer(Player player, String lottery, List<Reward> rewards) {
		this.givePlayerRewards(player, lottery, rewards);
		if(!rewards.isEmpty()) {
			this.addRewardClaim(player, lottery, rewards);
			return false;
		}
		return true;
	}
	
	/*
	 * Checks if a player has any unclaimed rewards and if they do, reward them
	 * 
	 * @param player - player to check
	 */
	public void checkForPlayerClaims(Player player) {
		
		// new system also stores the player's unique id
		List<LotteryClaim> playerClaims = claims.get(Utils.getUniqueName(player));
		
		// check for old storage of claims that saved only by player name
		List<LotteryClaim> oldClaims = claims.get(player.getName());
		
		if(!oldClaims.isEmpty())
			playerClaims.addAll(oldClaims);
		
		
		if(playerClaims != null && !playerClaims.isEmpty()) {
			
			// go through each reward claim
			for(int cntr = 0;cntr < playerClaims.size();cntr++) {
				LotteryClaim claim = playerClaims.get(cntr);
				String lotteryName = claim.getLotteryName();
				List<Reward> rewards = claim.getRewards();
				
				// give player the rewards
				this.givePlayerRewards(player, lotteryName, rewards);
				
				// if all rewards were given successfully, decrement cntr
				if(rewards.isEmpty()) {
					playerClaims.remove(cntr);
					cntr--;
				}
			}
			
			// update saved reward claims
			if(playerClaims.isEmpty())
				claims.remove(player.getName());
			this.saveRewardClaims();
			
		} else {
			ChatUtils.send(player, "lottery.error.claim.none");
		}
		
		
	}
	
	/*
	 * Called when a player joins the server and informs them if they have
	 * any unrewarded claims
	 * 
	 * @param player - player to check
	 */
	public void notifyOfRewardClaims(Player player) {
		List<LotteryClaim> playerClaims = claims.get(player.getName());
		
		if(playerClaims != null && !playerClaims.isEmpty()) {
			ChatUtils.send(player, "lottery.claim.notify");
		}
	}
	
	/*
	 * Internal method called to give players their unclaimed rewards
	 * 
	 * @param player - player to give the rewards to
	 * @param lottery - name of the lottery the rewards were tied to
	 * @param rewards - the rewards to give
	 */
	private void givePlayerRewards(Player player, String lottery, List<Reward> rewards) {
		Iterator<Reward> it = rewards.iterator();
		
		while(it.hasNext()) {
			Reward reward = it.next();
			String info = reward.getInfo();
			ChatUtils.send(player, "lottery.reward.giving", "<reward>", info);
			
			if(reward.rewardPlayer(player, lottery)) {
				it.remove();
				ChatUtils.send(player, "lottery.reward.rewarded", "<reward>", info);
			} else {
				ChatUtils.send(player, "lottery.reward.leftover", "<reward>", info, "<updatedreward>", reward.getInfo());
			}
		}
	}
}
