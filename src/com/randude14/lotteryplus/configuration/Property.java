package com.randude14.lotteryplus.configuration;

import com.randude14.lotteryplus.ChatUtils;

/*
 * This class represents a property and can have a default value
 * @see
 *   Config.getLong(Property<Long> property) 
 *   Config.getInt(Property<Integer> property) 
 *   Config.getBoolean(Property<Boolean> property) 
 *   Config.getString(Property<String> property) 
 *   Config.getStringList(Property<List<String>> property)
 */
@SuppressWarnings("rawtypes")
public class Property<T> implements Comparable<Property> {
	private final String path;
	private final String name;
	private String description;
	private final T value;
	
	/*
	 * @param path the path that will be used in this Property
	 * @param t the default value for this property
	 */
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
	
	/*
	 * @param description the description to set
	 */
	protected Property<T> setDescription(String description) {
		this.description = description;
		return this;
	}
	
	/*
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	/*
	 * @return the name
	 */
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
