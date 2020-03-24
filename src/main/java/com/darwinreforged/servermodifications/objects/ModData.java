package com.darwinreforged.servermodifications.objects;

public interface ModData {
	public String getName();
	public String getVersion();
	
	default String getCompleteData(){
		return getName()+" "+getVersion();
	}
}
