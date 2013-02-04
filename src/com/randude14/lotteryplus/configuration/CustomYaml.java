package com.randude14.lotteryplus.configuration;

import java.io.File;
import java.io.FileNotFoundException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.randude14.lotteryplus.Logger;
import com.randude14.lotteryplus.LotteryPlus;

public class CustomYaml {
	private static final LotteryPlus plugin = LotteryPlus.getInstance();
	private final FileConfiguration config;
	private final File configFile;
	
	public CustomYaml(String file) {
		this(file, true);
	}
	
	public CustomYaml(String file, boolean load) {
		configFile = new File(plugin.getDataFolder(), file);
		config = new YamlConfiguration();
		if(load)
			this.reloadConfig();
	}
	
	public void reloadConfig() {
		try {
			config.load(configFile);
		} catch (FileNotFoundException ex) {
		} catch (Exception ex) {
			Logger.info("plugin.exception.config.load", "<file>", configFile.getName());
			ex.printStackTrace();
		}
	}
	
	public void saveConfig() {
		try {
			config.save(configFile);
		} catch (FileNotFoundException ex) {
		} catch (Exception ex) {
			Logger.info("plugin.exception.config.save", "<file>", configFile.getName());
			ex.printStackTrace();
		}
	}
	
	public FileConfiguration getConfig() {
		return config;
	}
	
	public boolean exists() {
		return configFile.exists();
	}
	
	public void saveDefaultConfig() {
		if(exists())
			return;
		plugin.saveResource(configFile.getName(), false);
	}
}
