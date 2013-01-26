package com.randude14.lotteryplus.configuration;

public class Property<T> {
	private final String path;
	private final String name;
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
	
	public String getPath() {
		return path;
	}
	
	public String getName() {
		return name;
	}
	
	public T getDefaultValue() {
		return value;
	}
}
