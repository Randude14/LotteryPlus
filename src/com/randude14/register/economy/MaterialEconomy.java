 package com.randude14.register.economy;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.randude14.lotteryplus.ChatUtils;

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
			int total = 0;
			for(ItemStack stack : p.getInventory().getContents()) {
				if(stack == null || stack.getItemMeta().hasDisplayName() || stack.getType() != material) {
					continue;
				}
				total += stack.getAmount();
			}
			return total >= amount;
		} else {
			return false;
		}
	}

	public double deposit(String player, double d) {
		int amount = (int)Math.floor(d);
		Player p = Bukkit.getPlayer(player);
		if(p != null) {
			Collection<ItemStack> col = p.getInventory().addItem(new EconomyItemStack(material, amount)).values();
			amount = 0;
			for(ItemStack stack : col)
				amount += stack.getAmount();
		}
		return 0;
	}

	public void withdraw(String player, double d) {
		if(!hasEnough(player, d)) {
			return;
		}
		int amount = (int)Math.floor(d);
		Player p = Bukkit.getPlayer(player);
		if(p != null) {
			p.getInventory().removeItem(new EconomyItemStack(material, amount));
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
