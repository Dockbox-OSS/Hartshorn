package com.darwinreforged.servermodifications.objects;
import com.darwinreforged.servermodifications.plugins.PlotIDBarPlugin;
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

		if (PlotIDBarPlugin.allPlayers.containsKey(player.getUniqueId())) {
		this.setIDBar(PlotIDBarPlugin.allPlayers.get(player.getUniqueId()).getIDBar());
		this.setMemBar(PlotIDBarPlugin.allPlayers.get(player.getUniqueId()).getMemBar());
		this.setBarBool(PlotIDBarPlugin.allPlayers.get(player.getUniqueId()).getBarBool());
		this.setMembersBool(PlotIDBarPlugin.allPlayers.get(player.getUniqueId()).getMembersBool());
		this.setLastPlot(PlotIDBarPlugin.allPlayers.get(player.getUniqueId()).getLastPlot());
		this.setPlotTime(PlotIDBarPlugin.allPlayers.get(player.getUniqueId()).getPlotTime());
		this.setPlayer(PlotIDBarPlugin.allPlayers.get(player.getUniqueId()).getPlayer());
		}
		else {
			this.setPlayer(player);
			if (PlotIDBarPlugin.toggledID.contains(player.getUniqueId())) {
				this.setBarBool(true);
			}
			if (PlotIDBarPlugin.toggledMembers.contains(player.getUniqueId())) {
				this.setMembersBool(true);
			}
		}
	}
}
