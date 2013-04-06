package com.randude14.lotteryplus.configuration;

import com.randude14.lotteryplus.ChatUtils;

@SuppressWarnings("rawtypes")
public class Property<T> implements Comparable<Property> {
	private final String path;
	private final String name;
	private String description;
	private final T value;
	
	protected Property(String path, T t) {
		if(path == null || t == null) {
			throw new IllegalArgumentException("path or t cannot be null.");
		}
		this.path = path;
		this.value = t;
		int index = path.lastIndexOf('.');
		if(index < 0) 
			this.name = path;
		else
			this.name = path.substring(index+1);
	}
	
	protected Property<T> setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getName() {
		return name;
	}
	
	public T getDefaultValue() {
		return value;
	}
	
	public int compareTo(Property prop) {
		return getName().compareToIgnoreCase(prop.getName());
	}
	
	public String toString() {
		if(description != null) {
			return getName() + ": " + ChatUtils.getRawName(description);
		} else {
			return getName();
		}
	}
}
