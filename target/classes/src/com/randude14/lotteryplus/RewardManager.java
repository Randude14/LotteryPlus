package com.randude14.lotteryplus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.configuration.CustomYaml;
import com.randude14.lotteryplus.lottery.LotteryClaim;
import com.randude14.lotteryplus.lottery.reward.Reward;
import com.randude14.lotteryplus.util.ChatUtils;

public class RewardManager {
	private final Map<String, List<LotteryClaim>> claims = new HashMap<String, List<LotteryClaim>>();
	private final CustomYaml claimsConfig;
	
	public RewardManager() {
		claimsConfig = new CustomYaml("claims.yml");
	}
	
	private void saveRewardClaims() {
		FileConfiguration config = claimsConfig.getConfig();
		config.createSection("claims"); // clean config
		for(String player : claims.keySet()) {
			int cntr = 0;
			for(LotteryClaim claim : claims.get(player)) {
				config.set("claims." + player + ".reward" + ++cntr, claim);
			}
		}
		claimsConfig.saveConfig();
	}
	
	public void loadRewardClaims() {
		FileConfiguration config = claimsConfig.getConfig();
		ConfigurationSection section = config.getConfigurationSection("claims");
		if(section == null)
			section = config.createSection("claims");
		for(String player : section.getKeys(false)) {
			List<LotteryClaim> playerClaims = new ArrayList<LotteryClaim>();
			ConfigurationSection playerSection = section.getConfigurationSection(player);
			if(playerSection == null)
				continue;
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

	public void addRewardClaim(String name, String lottery, List<Reward> rewards) {
		if (!claims.containsKey(name))
			claims.put(name, new ArrayList<LotteryClaim>());
		LotteryClaim claim = new LotteryClaim(lottery, rewards);
		claims.get(name).add(claim);
		this.saveRewardClaims();
	}
	
	public boolean rewardPlayer(Player player, String lottery, List<Reward> rewards) {
		this.givePlayerRewards(player, lottery, rewards);
		if(!rewards.isEmpty()) {
			this.addRewardClaim(player.getName(), lottery, rewards);
			return false;
		}
		return true;
	}
	
	public void checkForPlayerClaims(Player player) {
		List<LotteryClaim> playerClaims = claims.get(player.getName());
		if(playerClaims != null && !playerClaims.isEmpty()) {
			for(int cntr = 0;cntr < playerClaims.size();cntr++) {
				LotteryClaim claim = playerClaims.get(cntr);
				String lotteryName = claim.getLotteryName();
				List<Reward> rewards = claim.getRewards();
				this.givePlayerRewards(player, lotteryName, rewards);
				if(rewards.isEmpty()) {
					playerClaims.remove(cntr);
					cntr--;
				}
			}
			if(playerClaims.isEmpty())
				claims.remove(player.getName());
			this.saveRewardClaims();
		} else {
			ChatUtils.send(player, "lottery.error.claim.none");
		}
	}
	
	public void notifyOfRewardClaims(Player player) {
		List<LotteryClaim> playerClaims = claims.get(player.getName());
		if(playerClaims != null && !playerClaims.isEmpty()) {
			ChatUtils.send(player, "lottery.claim.notify");
		}
	}
	
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
