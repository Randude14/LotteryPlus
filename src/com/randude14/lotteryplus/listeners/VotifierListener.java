package com.randude14.lotteryplus.listeners;

import org.bukkit.event.Listener;

import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.lottery.Lottery;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VotifierListener implements Listener {
	
	public void onVotifierEvent(VotifierEvent event) {
		Vote vote = event.getVote();
		for(Lottery lottery : LotteryManager.getLotteries()) {
			lottery.onVote(vote.getUsername());
		}
	}
}
