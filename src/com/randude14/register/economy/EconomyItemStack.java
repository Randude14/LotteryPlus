package com.randude14.register.economy;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/*
 * Extends ItemStack to override ItemStack#isSimilar()
 */
public class EconomyItemStack extends ItemStack {
	
	protected EconomyItemStack(Material material, int amount) {
		super(material, amount);
	}
	
	/*
	 * Check if this EconomyItemStack and the other stack are similar
	 * 
	 * @param stack - stack to check
	 * @return - if they are similar
	 */
	@Override
    public boolean isSimilar(ItemStack stack) {
		
        if (stack == null) {
            return false;
        }
        
        if (stack == this) {
            return true;
        }
        
        // players can change the name of their items in-game and can fool plugins
        // that do not check if they have changed them or not
        if(stack.getItemMeta().hasDisplayName() || getItemMeta().hasDisplayName()) {
        	return false;
        }
        
        // use ItemStack#isSimilar() if other conditions are false
        return super.isSimilar(stack);
    }
}
