package com.randude14.lotteryplus.register.economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
				PlayerInventory inv = p.getInventory();
				ItemStack[] contents = inv.getContents();
				for(int cntr = 0;cntr < contents.length;cntr++) {
					if(amount <= 0) {
						break;
					}
					if(contents[cntr] == null) {
						if(max >= amount) {
							contents[cntr] = new ItemStack(material, amount);
							amount = 0;
						} else {
							contents[cntr] = new ItemStack(material, max);
							amount -= max;
						}
					} else if(contents[cntr].getType() == material) {
						ItemStack item = contents[cntr];
						int stackSize = item.getAmount();
						if(max >= amount + stackSize) {
							item.setAmount(amount + stackSize);
							amount = 0;
						} else {
							item.setAmount(max);
							amount -= max;
						}
					}
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
			PlayerInventory inv = p.getInventory();
			ItemStack[] contents = inv.getContents();
			for(int cntr = 0;cntr < contents.length;cntr++) {
				if(amount <= 0) {
					break;
				}
				if(contents[cntr] == null || contents[cntr].getType() != material) {
					continue;
				}
				ItemStack item = contents[cntr];
				int stackSize = item.getAmount();
				int take = Math.max(amount, stackSize);
				if(stackSize == take) {
					item.setAmount(stackSize - amount);
				} else {
					amount -= stackSize;
					contents[cntr] = null;
				}
			}
			p.updateInventory();
		}
	}

	public String format(double amount) {
		return String.format("%d %s(s)", (int)Math.floor(amount), name);
	}

	public boolean hasAccount(String player) {
		return true;
	}
	
	public int getMaterialID() {
		return material.getId();
	}
}
