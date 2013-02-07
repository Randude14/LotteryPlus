package com.randude14.register.economy;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.randude14.lotteryplus.ChatUtils;
import com.randude14.lotteryplus.configuration.Config;

@SuppressWarnings("deprecation") // for Player.updateInventory()
public class MaterialEconomy extends Economy {
	private final Material material;
	private final String name;
	
	public MaterialEconomy(int materialID, String name) {
		this.material = Material.getMaterial(materialID);
		if(this.material == null) {
			throw new NullPointerException("Could not find material for id: " + materialID);
		}
		this.name = (name == null) ? material.name() : name;
	}

	public boolean hasEnough(String player, double amount) {
		amount = Math.floor(amount);
		Player p = Bukkit.getPlayer(player);
		if(p != null) {
			return p.getInventory().contains(material, (int)amount);
		} else {
			return false;
		}
	}

	public void deposit(String player, double d) {
		int amount = (int)Math.floor(d);
		Player p = Bukkit.getPlayer(player);
		if(p != null) {
			int max = material.getMaxStackSize();
			if(!Config.getBoolean(Config.SHOULD_DROP)) {
				Collection<ItemStack> stacks = p.getInventory().addItem(new ItemStack(material, amount)).values();
				amount = 0;
				for(ItemStack stack : stacks) {
					amount += stack.getAmount();
				}
				p.updateInventory();
			}
			while(amount > 0) {
				if(amount <= max) {
					p.getWorld().dropItem(p.getLocation(), new ItemStack(material, amount));
					amount = 0;
				} else {
					p.getWorld().dropItem(p.getLocation(), new ItemStack(material, max));
					amount -= max;
				}
			}
		}
	}

	public void withdraw(String player, double d) {
		int amount = (int)Math.floor(d);
		Player p = Bukkit.getPlayer(player);
		if(p != null) {
			p.getInventory().removeItem(new ItemStack(material, amount));
			p.updateInventory();
		}
	}

	public String format(double amount) {
		return ChatUtils.getRawName("lottery.economy.item", "<material>", name, "<amount>", (int)Math.floor(amount));
	}

	public boolean hasAccount(String player) {
		return true;
	}
	
	public int getMaterialID() {
		return material.getId();
	}
}
