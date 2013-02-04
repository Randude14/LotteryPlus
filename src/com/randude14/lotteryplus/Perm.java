package com.randude14.lotteryplus;

import org.bukkit.permissions.Permission;

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
	
	private Perm(String value) {
		this.permission = new Permission(value);
	}
	
	private Perm(String value, Perm... childrenArray) {
		this(value);
		for(Perm child : childrenArray) {
			child.setParent(this);
		}
	}
	
	private void setParent(Perm parentValue) {
		if(this.parent != null)
			return;
		this.parent = parentValue;
	}
	
	public Perm getParent() {
		return parent;
	}
	
	public Permission getPermission() {
		return permission;
	}
	
	private final org.bukkit.permissions.Permission permission;
	private Perm parent;
}
