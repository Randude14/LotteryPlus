package com.randude14.lotteryplus.configuration;

import com.randude14.lotteryplus.util.ChatUtils;

/*
 * Represents a property on the main config file. Each has a default value
 * Also used in @see com.randude14.lotteryplus.lottery.LotteryProperties
 */
public class Property<T> implements Comparable<Property<?>> {
	private final String path;
	private final String name;
	private String description;
	private final T defaultValue;
	
	protected Property(String path, T t) {
		
		if(path == null || t == null) {
			throw new IllegalArgumentException("path or t cannot be null.");
		}
		
		this.path = path;
		this.defaultValue = t;
		int index = path.lastIndexOf('.');
		if(index < 0) 
			this.name = path;
		else
			this.name = path.substring(index+1);
	}
	
	/*
	 * Set the description of the property.
	 * 
	 * @param description - description to set to
	 * @return - this class to daisy chain on declaration
	 */
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
		return defaultValue;
	}
	
	/*
	 * @param prop - property to compare to
	 * @return - the value by comparing property names, ignoring case
	 */
	public int compareTo(Property<?> prop) {
		return getName().compareToIgnoreCase(prop.getName());
	}
	
	/*
	 * @return - property as a string, add description if not null
	 */
	public String toString() {
		if(description != null) {
			return getName() + ": " + ChatUtils.getRawName(description);
		} else {
			return getName();
		}
	}

}
