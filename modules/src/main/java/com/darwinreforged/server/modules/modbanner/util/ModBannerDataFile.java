package com.darwinreforged.server.modules.modbanner.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map.Entry;

public class ModBannerDataFile {
	public static HashMap<String, String> read(File file){
		HashMap<String, String> rs = new HashMap<>();
		try {
			for(String line : Files.readAllLines(file.toPath())){
				String[] vals = line.split("-");
				rs.put(vals[0], vals[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static void write(File file, HashMap<String, String> map){
		try {
			PrintWriter writer = new PrintWriter(file);
			for(Entry<String, String> e : map.entrySet()){
				writer.println(e.getKey()+"-"+e.getValue());
			}
			writer.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}
}
