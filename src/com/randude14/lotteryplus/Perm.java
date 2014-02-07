package com.randude14.lotteryplus;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
//import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public enum Perm {
	
	LIST(new Permission("lottery.basic.list", PermissionDefault.TRUE)),
	INFO(new Permission("lottery.basic.info", PermissionDefault.TRUE)),
	BUY(new Permission("lottery.basic.buy", PermissionDefault.TRUE)),
	CLAIM(new Permission("lottery.basic.claim", PermissionDefault.TRUE)),
	WINNERS(new Permission("lottery.basic.winners", PermissionDefault.TRUE)),
	REWARD(new Permission("lottery.admin.reward", PermissionDefault.FALSE)),
	DRAW(new Permission("lottery.admin.draw", PermissionDefault.FALSE)),
	RELOAD(new Permission("lottery.admin.reload", PermissionDefault.FALSE)),
	RELOAD_ALL(new Permission("lottery.admin.reloadall", PermissionDefault.FALSE)),
	CONFIG_RELOAD(new Permission("lottery.admin.creload", PermissionDefault.FALSE)),
	LOAD(new Permission("lottery.admin.load", PermissionDefault.FALSE)),
	UNLOAD(new Permission("lottery.admin.unload", PermissionDefault.FALSE)),
	FORCE_SAVE(new Permission("lottery.admin.save", PermissionDefault.FALSE)),
	UPDATE(new Permission("lottery.admin.update", PermissionDefault.FALSE)),
	ADD_TO_POT(new Permission("lottery.admin.addtopot", PermissionDefault.FALSE)),
	CREATE(new Permission("lottery.admin.create", PermissionDefault.FALSE)),
	GUI_CREATOR(new Permission("lottery.admin.gui-creator", PermissionDefault.FALSE)),
	SIGN_CREATE(new Permission("lottery.sign.create", PermissionDefault.FALSE)),
	SIGN_REMOVE(new Permission("lottery.sign.remove", PermissionDefault.FALSE)),
	SIGN_USE(new Permission("lottery.sign.use", PermissionDefault.FALSE)),
	PARENT_BASIC(new Permission("lottery.basic.*", PermissionDefault.TRUE), LIST, INFO, BUY, CLAIM, WINNERS),
	PARENT_ADMIN(new Permission("lottery.admin.*", PermissionDefault.FALSE), REWARD, DRAW, RELOAD, RELOAD_ALL, LOAD, UNLOAD, UPDATE, ADD_TO_POT, CREATE, GUI_CREATOR),
	PARENT_SIGN(new Permission("lottery.sign.*", PermissionDefault.FALSE), SIGN_CREATE, SIGN_REMOVE, SIGN_USE),
	SUPER_PERM(new Permission("lottery.*", PermissionDefault.FALSE), PARENT_BASIC, PARENT_ADMIN, PARENT_SIGN);
	
	private Perm(Permission value) {
		this.permission = value;
	}
	
	private Perm(Permission value, Perm... children) {
		this(value);
		for(Perm child : children) {
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
	
	public boolean hasPermission(CommandSender sender) {/*
		if(sender instanceof ConsoleCommandSender) return true;
		return sender.hasPermission(permission.getName());*/return true;
	}
	
	public static void loadPermissions() {
		for(Perm perm : Perm.values()) {
			Bukkit.getPluginManager().addPermission(perm.getPermission());
		}
	}
	
	private final org.bukkit.permissions.Permission permission;
	private Perm parent;
}
