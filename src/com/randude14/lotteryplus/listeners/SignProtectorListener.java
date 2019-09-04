package com.randude14.lotteryplus.listeners;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.util.ChatUtils;
import com.randude14.lotteryplus.util.Utils;

public class SignProtectorListener implements Listener {

	public SignProtectorListener() {

	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if (LotteryManager.isSignRegistered(block)) {
			event.setCancelled(true);
			ChatUtils.send(player, "lottery.error.sign.break");
			return;
		}
		if (!checkBlockBroken(block)) {
			event.setCancelled(true);
			ChatUtils.send(player, "lottery.error.block.break");
			return;
		}
	}
	
	// return true if block can be broken
	private boolean checkBlockBroken(Block broken) {
		return canBreakBlock(broken.getRelative(BlockFace.UP), broken)
		&& canBreakBlock(broken.getRelative(BlockFace.NORTH), broken)
		&& canBreakBlock(broken.getRelative(BlockFace.EAST), broken)
		&& canBreakBlock(broken.getRelative(BlockFace.SOUTH), broken)
		&& canBreakBlock(broken.getRelative(BlockFace.WEST), broken);
	}

	private boolean canBreakBlock(Block check, Block broken) {
		if (!LotteryManager.isSignRegistered(check))
			return true;
		if(check.getBlockData() instanceof WallSign) {
			Directional dir = (Directional) check.getBlockData();
			BlockFace face = dir.getFacing().getOppositeFace();
			Block attached = check.getRelative(face);
			return !Utils.locsInBounds(attached.getLocation(),
					broken.getLocation());
		}
		else if(check.getBlockData() instanceof Sign) {
			Block attached = check.getRelative(BlockFace.DOWN);
			return !Utils.locsInBounds(attached.getLocation(),
					broken.getLocation());
		}
		return true;
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		for (Block block : event.blockList()) {
			if (LotteryManager.isSignRegistered(block) || !checkBlockBroken(block)) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPistonExtend(BlockPistonExtendEvent event) {
		for (Block block : event.getBlocks()) {
			if (LotteryManager.isSignRegistered(block) || !checkBlockBroken(block)) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent event) {
		Block block = event.getBlock();
		if (LotteryManager.isSignRegistered(block) || !checkBlockBroken(block)) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockPhysics(BlockPhysicsEvent event) {
		Block block = event.getBlock();
		if (LotteryManager.isSignRegistered(block) || !checkBlockBroken(block)) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockFade(BlockFadeEvent event) {
		Block block = event.getBlock();
		if (LotteryManager.isSignRegistered(block) || !checkBlockBroken(block)) {
			event.setCancelled(true);
			return;
		}
	}	
}
