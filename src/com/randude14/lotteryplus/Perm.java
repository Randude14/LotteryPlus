package com.randude14.lotteryplus;
 
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.randude14.lotteryplus.util.ChatUtils;
 
/*
 * This class is used for determining whether a player has permission to use certain commands or use specific objects
 */
public enum Perm {
   
    SUPER_PERM(new Permission("lottery.*", PermissionDefault.FALSE)),
    PARENT_BASIC(new Permission("lottery.basic.*", PermissionDefault.FALSE), SUPER_PERM),
    PARENT_ADMIN(new Permission("lottery.admin.*", PermissionDefault.FALSE), SUPER_PERM),
    PARENT_SIGN(new Permission("lottery.sign.*", PermissionDefault.FALSE), SUPER_PERM),
    LIST(new Permission("lottery.basic.list", PermissionDefault.TRUE), PARENT_BASIC),
    INFO(new Permission("lottery.basic.info", PermissionDefault.TRUE), PARENT_BASIC),
    BUY(new Permission("lottery.basic.buy", PermissionDefault.TRUE), PARENT_BASIC),
    CLAIM(new Permission("lottery.basic.claim", PermissionDefault.TRUE), PARENT_BASIC),
    WINNERS(new Permission("lottery.basic.winners", PermissionDefault.TRUE), PARENT_BASIC),
    REWARD(new Permission("lottery.admin.reward", PermissionDefault.FALSE), PARENT_ADMIN),
    VERSION(new Permission("lottery.admin.version", PermissionDefault.FALSE), PARENT_ADMIN),
    DRAW(new Permission("lottery.admin.draw", PermissionDefault.FALSE), PARENT_ADMIN),
    RELOAD(new Permission("lottery.admin.reload", PermissionDefault.FALSE), PARENT_ADMIN),
    RELOAD_ALL(new Permission("lottery.admin.reloadall", PermissionDefault.FALSE), PARENT_ADMIN),
    CONFIG_RELOAD(new Permission("lottery.admin.creload", PermissionDefault.FALSE), PARENT_ADMIN),
    LOAD(new Permission("lottery.admin.load", PermissionDefault.FALSE), PARENT_ADMIN),
    UNLOAD(new Permission("lottery.admin.unload", PermissionDefault.FALSE), PARENT_ADMIN),
    FORCE_SAVE(new Permission("lottery.admin.save", PermissionDefault.FALSE), PARENT_ADMIN),
    UPDATE(new Permission("lottery.admin.update", PermissionDefault.FALSE), PARENT_ADMIN),
    ADD_TO_POT(new Permission("lottery.admin.addtopot", PermissionDefault.FALSE), PARENT_ADMIN),
    CREATE(new Permission("lottery.admin.create", PermissionDefault.FALSE), PARENT_ADMIN),
    SIGN_CREATE(new Permission("lottery.sign.create", PermissionDefault.FALSE), PARENT_SIGN),
    SIGN_REMOVE(new Permission("lottery.sign.remove", PermissionDefault.FALSE), PARENT_SIGN),
    SIGN_USE(new Permission("lottery.sign.use", PermissionDefault.TRUE), PARENT_SIGN);
   
    private Perm(Permission permission) {
        this(permission, null);
    }
   
    private Perm(Permission permission, Perm parent) { 
        this.permission = permission;
        this.parent = parent;
        if(parent != null)
        	this.permission.addParent(parent.getPermission(), true);
    }
   
    /*
     * Return the parent for this permission
     */
    public Perm getParent() {
        return parent;
    }
   
    /*
     * Return the Bukkit version of this permission
     */
    public Permission getPermission() {
        return permission;
    }
    
    /*
     * Check if user has permission
     * 
     * @param sender - user to check
     */
    public boolean hasPermission(CommandSender sender) {
    	return sender.hasPermission(permission);
    }
   
    /*
     * Check if user has permission. If they do not, sends a warning message that they do not
     * 
     * @param sender - user to check
     */
    public boolean checkPermission(CommandSender sender) {
		if (!sender.hasPermission(permission)) {
			ChatUtils.send(sender, "plugin.error.permission");
			return false;
		}
		return true;
	}
   
    /*
     * Load all permissions to the Bukkit server
     */
    public static void loadPermissions() {
        for(Perm perm : Perm.values()) {
            Bukkit.getPluginManager().addPermission(perm.getPermission());
        }
    }
   
    private final Permission permission; // the Bukkit version of this permission
    private Perm parent;                 // the parent of this permission
}