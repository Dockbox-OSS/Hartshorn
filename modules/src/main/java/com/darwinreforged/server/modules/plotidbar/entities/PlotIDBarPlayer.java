package com.darwinreforged.server.modules.plotidbar.entities;

import com.darwinreforged.server.modules.plotidbar.PlotIdBarModule;

import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;

public class PlotIDBarPlayer {
	private ServerBossBar IDBossBar;
	
	public ServerBossBar getIDBar() {
		return IDBossBar;
	}
	public void setIDBar(ServerBossBar bar) {
		IDBossBar = bar;
	}
	
	private ServerBossBar MemBossBar;
	
	public ServerBossBar getMemBar() {
		return MemBossBar;
	}
	public void setMemBar(ServerBossBar bar) {
		MemBossBar = bar;
	}
	
	private boolean BarBool = false;
	private boolean MembersBool = false;
	public void clearBars() {
		this.IDBossBar = null;
		this.MemBossBar = null;
	}
	public void setBarBool(Boolean bool) {
		this.BarBool = bool;
	}
	
	public void setMembersBool(Boolean bool) {
		this.MembersBool = bool;
	}
	
	public boolean getBarBool() {
		return BarBool;
	}
	
	public boolean getMembersBool() {
		return MembersBool;
	}
	
	private String lastPlot;
	public void setLastPlot(String string) {
		this.lastPlot = string;
	}
	public String getLastPlot() {
		return lastPlot;
	}
	
	public boolean hasPlotTime;
	
	public void setPlotTime(Boolean bool) {
		this.hasPlotTime = bool;
	}
	
	public boolean getPlotTime() {
		return hasPlotTime;
	}
	public Player player;
	
	public void setPlayer(Player player) {
		this.player = player;
		
	}
	public Player getPlayer() {
		return this.player;
	}
	public PlotIDBarPlayer(Player player) {

		if (PlotIdBarModule.allPlayers.containsKey(player.getUniqueId())) {
		this.setIDBar(PlotIdBarModule.allPlayers.get(player.getUniqueId()).getIDBar());
		this.setMemBar(PlotIdBarModule.allPlayers.get(player.getUniqueId()).getMemBar());
		this.setBarBool(PlotIdBarModule.allPlayers.get(player.getUniqueId()).getBarBool());
		this.setMembersBool(PlotIdBarModule.allPlayers.get(player.getUniqueId()).getMembersBool());
		this.setLastPlot(PlotIdBarModule.allPlayers.get(player.getUniqueId()).getLastPlot());
		this.setPlotTime(PlotIdBarModule.allPlayers.get(player.getUniqueId()).getPlotTime());
		this.setPlayer(PlotIdBarModule.allPlayers.get(player.getUniqueId()).getPlayer());
		}
		else {
			this.setPlayer(player);
			if (PlotIdBarModule.toggledID.contains(player.getUniqueId())) {
				this.setBarBool(true);
			}
			if (PlotIdBarModule.toggledMembers.contains(player.getUniqueId())) {
				this.setMembersBool(true);
			}
		}
	}
}
