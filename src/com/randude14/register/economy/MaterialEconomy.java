 package com.randude14.register.economy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.randude14.lotteryplus.ChatUtils;

@SerializableAs("MaterialEconomy")
@SuppressWarnings("deprecation")
public class MaterialEconomy extends Economy {
	private Material material;
	private short data = 0;
	private final String name;
	
	public MaterialEconomy(String line, String name) {
		loadMaterialData(line);
		this.name = (name == null) ? material.name() : name;
	}
	
	private MaterialEconomy(Material material, short data, String name) {
		this.material = material;
		this.data = data;
		this.name = name;
	}
	
	private void loadMaterialData(String line) {
		try {
			int index = line.indexOf(":");
			if(index > 0) {
				this.data = Short.parseShort(line.substring(index+1));
			} else {
				index = line.length();
			}
			int id = Integer.parseInt(line.substring(0, index));
			this.material = Material.getMaterial(id);
		} catch (Exception ex) {
			throw new RuntimeException("Could not load material data from: " + line, ex);
		}
	}

	public boolean hasEnough(String player, double amount) {
		amount = Math.floor(amount);
		Player p = Bukkit.getPlayer(player);
		if(p != null) {
			ItemStack currency = new EconomyItemStack(material, 1, data);
			int total = 0;
			for(ItemStack stack : p.getInventory().getContents()) {
				if(currency.isSimilar(stack)) {
					total += stack.getAmount();
				}
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
			Collection<ItemStack> col = p.getInventory().addItem(new EconomyItemStack(material, amount, data)).values();
			amount = 0;
			for(ItemStack stack : col)
				amount += stack.getAmount();
			p.updateInventory();
		}
		return amount;
	}

	public void withdraw(String player, double d) {
		if(!hasEnough(player, d)) {
			return;
		}
		int amount = (int)Math.floor(d);
		Player p = Bukkit.getPlayer(player);
		if(p != null) {
			p.getInventory().removeItem(new EconomyItemStack(material, amount, data));
			p.updateInventory();
		}
	}

	public String format(double amount) {
		return ChatUtils.getRawName("lottery.economy.item", "<material>", name, "<amount>", (int) Math.floor(amount));
	}

	public boolean hasAccount(String player) {
		return true;
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", material.getId());
		map.put("data", data);
		map.put("name", name);
		return map;
	}
	
	public MaterialEconomy deserialize(Map<String, Object> map) {
		int id = ((Number) map.get("id")).intValue();
		Material mat = Material.getMaterial(id);
		short data = ((Number) map.get("data")).shortValue();
		String name = map.get("name").toString();
		return new MaterialEconomy(mat, data, name);
	}
	
	public int getMaterialID() {
		return material.getId();
	}
}
