package com.randude14.lotteryplus;

import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

public enum Perm {
	
	LIST("lottery.basic.list"),
	INFO("lottery.basic.info"),
	BUY("lottery.basic.buy"),
	CLAIM("lottery.basic.claim"),
	WINNERS("lottery.basic.winners"),
	REWARD("lottery.admin.reward"),
	DRAW("lottery.admin.draw"),
	RELOAD("lottery.admin.reload"),
	RELOAD_ALL("lottery.admin.reloadall"),
	CONFIG_RELOAD("lottery.admin.creload"),
	LOAD("lottery.admin.load"),
	UNLOAD("lottery.admin.unload"),
	FORCE_SAVE("lottery.admin.save"),
	UPDATE("lottery.admin.update"),
	ADD_TO_POT("lottery.admin.addtopot"),
	CREATE("lottery.admin.create"),
	SIGN_CREATE("lottery.sign.create"),
	SIGN_REMOVE("lottery.sign.remove"),
	SIGN_USE("lottery.sign.use"),
	PARENT_BASIC("lottery.basic.*", LIST, INFO, BUY, CLAIM, WINNERS),
	PARENT_ADMIN("lottery.admin.*", REWARD, DRAW, RELOAD, RELOAD_ALL, LOAD, UNLOAD, UPDATE, ADD_TO_POT, CREATE),
	PARENT_SIGN("lottery.sign.*", SIGN_CREATE, SIGN_REMOVE, SIGN_USE),
	SUPER_PERM("lottery.*", PARENT_BASIC, PARENT_ADMIN, PARENT_SIGN);
	
	private Perm(String perm) {
		this.permission = perm;
		this.bukkitPerm = new org.bukkit.permissions.Permission(this.permission, PermissionDefault.FALSE);
	}
	
	private Perm(String value, Perm... childrenArray) {
		this(value);
		for(Perm child : childrenArray) {
			child.setParent(this);
		}
	}
	
	public void loadPermission(PluginManager pm) {
		pm.addPermission(bukkitPerm);
	}
	
	private void setParent(Perm parentValue) {
		if(this.parent != null)
			return;
		this.parent = parentValue;
		//this.bukkitPerm.addParent(this.parent.getBukkitPerm(), false);
	}
	
	public Perm getParent() {
		return parent;
	}
	
	public org.bukkit.permissions.Permission getBukkitPerm() {
		return bukkitPerm;
	}
	
	public String getPermission() {
		return permission;
	}
	
	private final org.bukkit.permissions.Permission bukkitPerm;
	private Perm parent;
	private final String permission;
}
