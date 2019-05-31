 package com.randude14.register.economy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.randude14.lotteryplus.util.ChatUtils;

/*
 * Used to treat in-game items as a form of currency. 
 * @see com.randude14.economy.Economy for inherited methods
 */
@SerializableAs("MaterialEconomy")
@SuppressWarnings("deprecation")   // used to suppress warnings on the two calls to player.updateInventory()
public class MaterialEconomy extends Economy {
	private Material material;
	private final String name;
	
	public MaterialEconomy(String mat, String name) {
		material = Material.matchMaterial(mat);
		this.name = (name == null) ? material.name() : name;
	}
	
	/* 
	 * @see org.bukkit.configuration.serialization.ConfigurationSerializable#serialize()
	 */
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mat", material.name());
		map.put("name", name);
		return map;
	}
	
	/*
	 * Deserializes a Material economy from a Map 
	 */
	public static MaterialEconomy deserialize(Map<String, Object> map) {
		String mat = map.get("mat").toString();
		String name = map.get("name").toString();
		return new MaterialEconomy(mat, name);
	}

	
	// Inherited methods /////////////////////////////////////////////////////////////////
	
	
	public boolean hasEnough(Player player, double amount) {
		
		amount = Math.floor(amount); // items can't have fraction amounts
		ItemStack currency = new EconomyItemStack(material, 1);
		int total = 0;
		
		// count total number of items in inventory that match our material
		for(ItemStack stack : player.getInventory().getContents()) {
			
			if(currency.isSimilar(stack)) {
				total += stack.getAmount();
			}
		}
		
		return total >= amount;
	}

	public double deposit(Player player, double d) {
		int amount = (int)Math.floor(d);
		
		// attempts to add items to inventory and returns a collection of the items it couldn't fit
		Collection<ItemStack> col = player.getInventory().addItem(new EconomyItemStack(material, amount)).values();
		amount = 0;
		
		// count total amount of items that couldn't fit
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
			player.getInventory().removeItem(new EconomyItemStack(material, amount));
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
}
