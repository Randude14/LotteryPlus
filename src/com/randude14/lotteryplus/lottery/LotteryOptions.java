package com.randude14.lotteryplus.lottery;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.randude14.lotteryplus.configuration.Config;
import com.randude14.lotteryplus.configuration.Property;

public class LotteryOptions {
	private final Map<String, Object> options = new HashMap<String, Object>();
	
	public LotteryOptions(Map<String, Object> map) {
		this.options.putAll(map);
	}
	
	public boolean contains(String key) {
		return options.containsKey(key);
	}
	
	public void remove(String key) {
		options.remove(key);
	}
	
	public long getLong(Property<Long> property) {
		return getLong(property.getName(), Config.getLong(property));
	}
	
	public long getLong(Property<Long> property, long def) {
		return getLong(property.getName(), def);
	}
	
	public long getLong(String path) {
		return getLong(path, 0);
	}
	
	public long getLong(String path, long def) {
		Object value = options.get(path);
		if(value != null) {
			try {
				if(value instanceof Number) {
					return ((Number) value).longValue();
				} else {
					return Long.parseLong(value.toString());
				}
			} catch (Exception ex) {
			}
		}
		return def;
	}
	
	public double getDouble(Property<Double> property) {
		return getDouble(property.getName(), Config.getDouble(property));
	}
	
	public double getDouble(Property<Double> property, double def) {
		return getDouble(property.getName(), def);
	}
	
	public double getDouble(String path) {
		return getDouble(path, 0);
	}
	
	public double getDouble(String path, double def) {
		Object value = options.get(path);
		if(value != null) {
			try {
				if(value instanceof Number) {
					return ((Number) value).doubleValue();
				} else {
					return Double.parseDouble(value.toString());
				}
			} catch (Exception ex) {
			}
		}
		return def;
	}
	
	public int getInt(Property<Integer> property) {
		return getInt(property.getName(), Config.getInt(property));
	}
	
	public int getInt(Property<Integer> property, int def) {
		return getInt(property.getName(), def);
	}
	
	public int getInt(String path) {
		return getInt(path, 0);
	}
	
	public int getInt(String path, int def) {
		Object value = options.get(path);
		if(value != null) {
			try {
				if(value instanceof Number) {
					return ((Number) value).intValue();
				} else {
					return Integer.parseInt(value.toString());
				}
			} catch (Exception ex) {
			}
		}
		return def;
	}
	
	public boolean getBoolean(Property<Boolean> property) {
		return getBoolean(property.getName(), Config.getBoolean(property));
	}
	
	public boolean getBoolean(Property<Boolean> property, boolean def) {
		return getBoolean(property.getName(), def);
	}
	
	public boolean getBoolean(String path) {
		return getBoolean(path, false);
	}
	
	public boolean getBoolean(String path, boolean def) {
		Object value = options.get(path);
		if(value != null) {
			return new Boolean(value.toString());
		}
		return def;
	}
	
	public String getString(Property<String> property) {
		return getString(property.getName(), Config.getString(property));
	}
	
	public String getString(Property<String> property, String def) {
		return getString(property.getName(), def);
	}
	
	public String getString(String path) {
		return getString(path, "");
	}
	
	public String getString(String path, String def) {
		Object value = options.get(path);
		if(value != null) {
			return value.toString();
		}
		return def;
	}
	
	public void set(Property<?> property, Object value) {
		set(property.getName(), value);
	}
	
	public void set(String path, Object value) {
		options.put(path, value);
	}
	
	public Set<String> keySet() {
		return options.keySet();
	}
	
	public Map<String, Object> getValues() {
		return options;
	}
}
