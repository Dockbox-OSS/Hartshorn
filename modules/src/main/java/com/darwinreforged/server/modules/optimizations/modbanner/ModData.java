package com.darwinreforged.server.modules.optimizations.modbanner;

public interface ModData {
	public String getName();
	public String getVersion();
	
	default String getCompleteData(){
		return getName()+" "+getVersion();
	}
}
