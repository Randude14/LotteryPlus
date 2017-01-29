package com.randude14.lotteryplus.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Properties {
	private Map<String, String> props = new HashMap<String, String>();
	private Properties defaults = null;
	
	public Properties() {
		
	}
	
	public void setDefaults(Properties def) {
		this.defaults = def;
	}
	
	public Properties getDefaults() {
		return this.defaults;
	}
	
	public void load(File file) throws IOException {
		load(new FileInputStream(file));
	}
	
	public void load(InputStream stream) throws IOException {
		Scanner scan = new Scanner(stream);
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			if(line.contains("=")) {
				int i = line.indexOf('=');
				String key = line.substring(0, i);
				String value = line.substring(i+1);
				props.put(key, value);
			}
		}
		scan.close();
	}
	
	public void save(File file, String comments) throws IOException {
		PrintWriter writer = new PrintWriter(file);
		if(comments != null) {
			for(String comment : comments.split("\n")) {
				writer.println("# " + comment);
			}
		}
		writer.println();
		for(String key : props.keySet()) {
			writer.println(key + "=" + props.get(key));
		}
		writer.close();
	}
	
	public String getProperty(String key) {
		if(defaults != null)
			return getProperty(key, defaults.getProperty(key, key));
		else
			return getProperty(key, key);
	}
	
	public String getProperty(String key, String def) {
		String value = props.get(key);
		return (value == null) ? def : value;
	}
	
	public void clear() {
		props.clear();
	}
	
	public String toString() {
		return "Properties[" + props.toString() + "]";
	}
}
