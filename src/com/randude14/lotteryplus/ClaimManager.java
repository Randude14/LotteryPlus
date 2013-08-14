package com.randude14.lotteryplus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.randude14.lotteryplus.configuration.CustomYaml;
import com.randude14.lotteryplus.lottery.Lottery;
import com.randude14.lotteryplus.lottery.LotteryClaim;
import com.randude14.lotteryplus.lottery.reward.Reward;

public class ClaimManager {
	
	/*
	 * Saves the claims
	 */
	private static void saveClaims() {
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
	
	/*
	 * Loads the claims
	 */
	public static void loadClaims() {
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
					playerClaims.add(LotteryClaim.class.cast(playerSection.get(claimPath)));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			claims.put(player, playerClaims);
		}
	}

	/*
	 * @param name the name of the player for the claim to add
	 * @param lottery the name of the lottery for the claim to add
	 * @param rewards the list of rewards for the claim to add
	 */
	public static void addClaim(String name, String lottery, List<Reward> rewards) {
		if (!claims.containsKey(name))
			claims.put(name, new ArrayList<LotteryClaim>());
		LotteryClaim claim = new LotteryClaim(lottery, rewards);
		claims.get(name).add(claim);
		saveClaims();
	}
	
	/*
	 * @param player the @Player to rewards claims to if he/she has any
	 */
	public static void rewardClaims(Player player) {
		List<LotteryClaim> playerClaims = claims.get(player.getName());
		if(playerClaims != null && !playerClaims.isEmpty()) {
			for(int cntr = 0;cntr < playerClaims.size();cntr++) {
				LotteryClaim claim = playerClaims.get(cntr);
				if(Lottery.handleRewards(player, claim.getLotteryName(), claim.getRewards())) {
					playerClaims.remove(cntr);
					cntr--;
				}
			}
			saveClaims();
		} else {
			ChatUtils.send(player, "lottery.error.claim.none");
		}
	}
	
	/*
	 * @param player the @Player to notify if he/she has any claims
	 */
	public static void notifyOfClaims(Player player) {
		List<LotteryClaim> playerClaims = claims.get(player.getName());
		if(playerClaims != null && !playerClaims.isEmpty()) {
			ChatUtils.send(player, "lottery.claim.notify");
		}
	} 

	private static final Map<String, List<LotteryClaim>> claims = new HashMap<String, List<LotteryClaim>>(); // keeps track of claims
	private static final CustomYaml claimsConfig;
	
	static {
		claimsConfig = new CustomYaml("claims.yml");
	}
}
