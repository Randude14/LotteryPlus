package com.randude14.lotteryplus.support;

import org.bukkit.event.Listener;

import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.lottery.Lottery;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

/*
 * Simply listens for events called from the plugin Votifier
 * 
 * @see Votifier - https://dev.bukkit.org/projects/votifier?gameCategorySlug=bukkit-plugins&projectID=32925
 */
public class VotifierListener implements Listener {
	
	/*
	 * Whenever a player votes for their favorite server online, 
	 * votifier calls all of its listeners 
	 */
	public void onVotifierEvent(VotifierEvent event) {
		Vote vote = event.getVote();
		
		// call onVote() on all lotteries
		for(Lottery lottery : LotteryManager.getLotteries()) {
			lottery.onVote(vote.getUsername());
		}
	}
}
