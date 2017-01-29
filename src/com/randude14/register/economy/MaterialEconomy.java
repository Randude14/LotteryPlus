 package com.randude14.register.economy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
			this.material = Material.matchMaterial(line.substring(0, index));
		} catch (Exception ex) {
			throw new RuntimeException("Could not load material data from: " + line, ex);
		}
	}

	public boolean hasEnough(Player player, double amount) {
		amount = Math.floor(amount);
		ItemStack currency = new EconomyItemStack(material, 1, data);
		int total = 0;
		for(ItemStack stack : player.getInventory().getContents()) {
			if(currency.isSimilar(stack)) {
				total += stack.getAmount();
			}
		}
		return total >= amount;
	}

	public double deposit(Player player, double d) {
		int amount = (int)Math.floor(d);
		Collection<ItemStack> col = player.getInventory().addItem(new EconomyItemStack(material, amount, data)).values();
		amount = 0;
		for(ItemStack stack : col)
			amount += stack.getAmount();
		player.updateInventory();
		return amount;
	}
	
	public double deposit(String player, double d) {
		return d;
	}

	public void withdraw(Player player, double d) {
		if(!hasEnough(player, d)) {
			return;
		}
		int amount = (int)Math.floor(d);
		if(player != null) {
			player.getInventory().removeItem(new EconomyItemStack(material, amount, data));
			player.updateInventory();
		}
	}

	public String format(double amount) {
		return ChatUtils.getRawName("lottery.economy.item", "<material>", name, "<amount>", (int) Math.floor(amount));
	}

	public boolean hasAccount(Player player) {
		return true;
	}
	
	public boolean hasAccount(String playerName) {
		return true;
	}
	
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mat", material.name());
		map.put("data", data);
		map.put("name", name);
		return map;
	}
	
	public MaterialEconomy deserialize(Map<String, Object> map) {
		Material mat;
		if(map.containsKey("id")) mat = Material.matchMaterial(map.get("id").toString());
		else mat = Material.matchMaterial(map.get("mat").toString());
		short data = ((Number) map.get("data")).shortValue();
		String name = map.get("name").toString();
		return new MaterialEconomy(mat, data, name);
	}
}
