package com.darwinreforged.server.modules.optimizations.modbanner.util;

import com.darwinreforged.server.modules.optimizations.modbanner.ModBannerModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ModBannerCfgManager {
	ModBannerModule pl;

	public List<String> blackList = new ArrayList<>();
	public String kickMsg = "You can't use %mods% in this server";
	public long kickDelay = 1000;
	
	public ModBannerCfgManager(ModBannerModule pl) throws IOException {
		this.pl = pl;
		
		File file = pl.getConfigPath().toFile();
		if(!file.exists()){
			file.getParentFile().mkdirs();
			file.createNewFile();
			save();
			return;
		}
		
		load();
	}
	
	public void load(){
		Path p = pl.getConfigPath();
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(p).build();
		ConfigurationNode rootNode = loader.createEmptyNode(ConfigurationOptions.defaults());
		try {
			rootNode = loader.load();
			blackList.clear();
			blackList.addAll(rootNode.getNode("blacklist").getList(new Function<Object, String>() {
				@Override
				public String apply(Object t) {
					return (String) t;
				}
			}));
			kickMsg = rootNode.getNode("kickmsg").getString();
			kickDelay = rootNode.getNode("kickDelay").getLong();
		} catch (IOException e) {
			save();
		}
	}
	
	public void save(){
		Path p = pl.getConfigPath();
		ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(p).build();
		ConfigurationNode rootNode = loader.createEmptyNode(ConfigurationOptions.defaults());
		try {
			rootNode.getNode("kickmsg").setValue(kickMsg);
			rootNode.getNode("blacklist").setValue(blackList);
			rootNode.getNode("kickDelay").setValue(kickDelay);
			loader.save(rootNode);
		} catch (IOException e) {
			rootNode = loader.createEmptyNode();
		}
	}
}
