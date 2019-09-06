package com.randude14.lotteryplus.configuration;

import java.io.File;
import java.io.FileNotFoundException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.randude14.lotteryplus.Logger;
import com.randude14.lotteryplus.LotteryPlus;

/*
 * Used as a wrapper for config files and ease of access methods for saving and loading
 */
public class CustomYaml {
	private static final LotteryPlus plugin = LotteryPlus.getInstance();
	private FileConfiguration config;
	private final File configFile;
	private final boolean loadBeforeSaving;
	
	public CustomYaml(String file) {
		this(file, false);
	}
	
	public CustomYaml(String file, boolean loadBeforeSaving) {
		configFile = new File(plugin.getDataFolder(), file);
		this.loadBeforeSaving = loadBeforeSaving;
	}
	
	/*
	 * Reloads the config
	 */
	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(configFile);
	}
	
	/*
	 * Saves the config
	 */
	public void saveConfig() {
		
		// haven't loaded config yet
		if(config == null) 
			return;
		
		// attempt to save
		try {
			if(loadBeforeSaving) { // reload file before saving so we don't overwrite changes
				reloadConfig();
			}
			config.save(configFile);
			
		// ignore if file was not found
		} catch (FileNotFoundException ex) {
		} catch (Exception ex) {
			Logger.info("plugin.exception.config.save", "<file>", configFile.getName());
			ex.printStackTrace();
		}
	}
	
	public FileConfiguration getConfig() {
		return getConfig(false);
	}
	
	/*
	 * @param reload - whether to reload the config or not
	 * @return - FileConfiguration for config. Reloads if reload is true or if config was not loaded yet
	 */
	public FileConfiguration getConfig(boolean reload) {
		if(config == null || reload) 
			reloadConfig();
		return config;
	}
	
	public boolean exists() {
		return configFile.exists();
	}
	
	/*
	 * Save the config from the jar file if it does not exist
	 */
	public void saveDefaultConfig() {
		
		if(exists()) 
			return;
		
		plugin.saveResource(configFile.getName(), false);
	}
}
