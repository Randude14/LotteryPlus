package com.randude14.register.economy;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EconomyItemStack extends ItemStack {
	
	public EconomyItemStack(Material material, int amount) {
		super(material, amount);
	}
	
    public boolean isSimilar(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack == this) {
            return true;
        }
        if(stack.getItemMeta().hasDisplayName() || getItemMeta().hasDisplayName()) {
        	return false;
        }
        return super.isSimilar(stack);
    }
}
