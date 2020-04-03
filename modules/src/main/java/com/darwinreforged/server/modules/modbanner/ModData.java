package com.darwinreforged.server.modules.modbanner;

public interface ModData {
	public String getName();
	public String getVersion();
	
	default String getCompleteData(){
		return getName()+" "+getVersion();
	}
}
