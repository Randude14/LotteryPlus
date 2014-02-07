package com.randude14.lotteryplus.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.randude14.lotteryplus.ChatUtils;
import com.randude14.lotteryplus.ClaimManager;
import com.randude14.lotteryplus.LotteryManager;
import com.randude14.lotteryplus.Perm;
import com.randude14.lotteryplus.LotteryPlus;
import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.lottery.Lottery;

public class PlayerListener implements Listener {
	private final Map<Player, String> buyers = new HashMap<Player, String>();

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		ClaimManager.notifyOfClaims(player);
		String[] mainLotteries = Config.getString(Config.MAIN_LOTTERIES).split("\\s+");
		for (String lotteryName : mainLotteries) {
			Lottery lottery = LotteryManager.getLottery(player, lotteryName);
			if (lottery == null)
				return;
			String mess = ChatUtils.getNameFor("lottery.mess.main");
			mess = lottery.format(mess);
			player.sendMessage(mess.split(Config.getString(Config.LINE_SEPARATOR)));
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		String[] lines = event.getLines();
		if (isLotterySign(lines) && LotteryPlus.isSign(event.getBlock())) {
			Sign sign = (Sign) event.getBlock().getState();
			for (int cntr = 0; cntr < 4; cntr++)
				sign.setLine(cntr, lines[cntr]);
			createSign(player, sign, event);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		if (!LotteryPlus.isSign(block)) {
			return;
		}
		Player player = event.getPlayer();
		Sign sign = (Sign) block.getState();
		String[] lines = sign.getLines();
		if (isLotterySign(lines)) {
			lines[0] = ChatUtils.replaceColorCodes(Config.getString(Config.SIGN_TAG));
			event.setCancelled(true);
			if (player.isSneaking()) {
				if (LotteryPlus.checkPermission(player, Perm.SIGN_REMOVE)) {
					Lottery lottery = LotteryManager.getLottery(player, lines[1]);
					if (lottery != null && lottery.unregisterSign(sign)) {
						ChatUtils.send(player, "lottery.sign.removed", "<lottery>", lottery.getName());
						block.breakNaturally();
					}
				}
			} else {
				for (Lottery lottery : LotteryManager.getLotteries()) {
					if (lottery.hasRegisteredSign(block)) {
						if (!LotteryPlus.checkPermission(player, Perm.SIGN_USE)) {
							return;
						}
						lines[1] = lottery.getName();
						if (buyers.containsKey(player)) {
							String lotteryName = buyers.remove(player);
							if (lotteryName.equalsIgnoreCase(lottery.getName())) {
								ChatUtils.sendRaw(player,
										"lottery.error.trans-cancelled");
								ChatUtils.sendRaw(player, "plugin.headliner");
								return;
							}
						}
						ChatUtils.sendRaw(player, "plugin.headliner");
						String[] messages = getSignMessage(lottery);
						player.sendMessage(messages);
						ChatUtils.sendRaw(player, "lottery.tickets.howmany");
						buyers.put(player, lottery.getName());
						return;
					}
				}
				if (LotteryPlus.checkPermission(player, Perm.SIGN_CREATE)) {
					createSign(player, sign, event);
				}
			}
		}
	}

	// remove buyers when the leave
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		String name = event.getPlayer().getName();
		buyers.remove(name);
	}

	// remove buyers when they get kicked
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event) {
		String name = event.getPlayer().getName();
		buyers.remove(name);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String chat = event.getMessage();

		if (buyers.containsKey(player)) {
			String lotteryName = buyers.remove(player);
			Lottery lottery = LotteryManager.getLottery(player, lotteryName);
			event.setCancelled(true);

			if (lottery != null) {
				int tickets = 0;

				try {
					tickets = Integer.parseInt(chat);
				} catch (Exception ex) {
					ChatUtils.sendRaw(player, "lottery.error.invalid-number");
					ChatUtils.sendRaw(player, "lottery.error.trans-cancelled");
					ChatUtils.sendRaw(player, "plugin.headliner");
					event.setCancelled(true);
					return;
				}

				if (tickets <= 0) {
					ChatUtils.sendRaw(player, "lottery.error.neg-tickets");
					ChatUtils.sendRaw(player, "lottery.error.trans-cancelled");
					ChatUtils.sendRaw(player, "plugin.headliner");
					return;
				}

				if (lottery.buyTickets(player, tickets)) {
					ChatUtils.sendRaw(player, "lottery.trans-completed");
					ChatUtils.sendRaw(player, "plugin.headliner");
					lottery.broadcast("lottery.mess.buy", "<player>", player.getName(), "<tickets>", tickets, "<lottery>", lottery.getName());
					if (lottery.isOver()) {
						lottery.draw();
					}
				} else {
					ChatUtils.sendRaw(player, "lottery.error.trans-cancelled");
					ChatUtils.sendRaw(player, "plugin.headliner");
				}
			}

			else {
				ChatUtils.sendRaw(player, "lottery.error.unknown-removed", "<lottery>", lotteryName);
				ChatUtils.sendRaw(player, "lottery.error.trans-cancelled");
				ChatUtils.sendRaw(player, "plugin.headliner");
			}
		}
	}

	private void createSign(Player player, Sign sign, Cancellable cancel) {
		if (!LotteryPlus.checkPermission(player, Perm.SIGN_CREATE)) {
			return;
		}
		String[] lines = sign.getLines();
		if (lines[1] == null || lines[1].equals("")) {
			ChatUtils.send(player, "lottery.error.sign.specify");
			cancel.setCancelled(true);
			return;
		}
		Lottery lottery = LotteryManager.getLottery(lines[1]);
		if (lottery == null) {
			ChatUtils.send(player, "lottery.error.notfound", "<lottery>", lines[1]);
			cancel.setCancelled(true);
			return;
		}
		lines[0] = ChatUtils.replaceColorCodes(Config.getString(Config.SIGN_TAG));
		lines[1] = lottery.getName();
		boolean success = lottery.registerSign(player, sign);
		if(success) {
			ChatUtils.send(player, "lottery.sign.created", "<lottery>", lottery.getName());
		} else {
			cancel.setCancelled(true);
		}
	}

	private String[] getSignMessage(Lottery lottery) {
		String mess = ChatUtils.getNameFor("lottery.mess.sign");
		mess = lottery.format(mess);
		return mess.split(Config.getString(Config.LINE_SEPARATOR));
	}

	private boolean isLotterySign(String[] lines) {
		String signTag = ChatUtils.cleanColorCodes(Config.getString(Config.SIGN_TAG));
		String line1 = ChatColor.stripColor(lines[0]);
		return signTag.equalsIgnoreCase(line1);
	}
}
