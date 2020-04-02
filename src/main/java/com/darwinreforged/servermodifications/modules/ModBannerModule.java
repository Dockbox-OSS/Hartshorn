package com.darwinreforged.servermodifications.modules;

import com.darwinreforged.servermodifications.DarwinServer;
import com.darwinreforged.servermodifications.commands.modbanner.ModBannerCommand;
import com.darwinreforged.servermodifications.commands.modbanner.ModsCommand;
import com.darwinreforged.servermodifications.exceptions.VanillaPlayerException;
import com.darwinreforged.servermodifications.modules.root.DisabledModule;
import com.darwinreforged.servermodifications.modules.root.ModuleInfo;
import com.darwinreforged.servermodifications.modules.root.PluginModule;
import com.darwinreforged.servermodifications.objects.ModData;
import com.darwinreforged.servermodifications.util.plugins.PlayerModsUtil;
import com.darwinreforged.servermodifications.util.todo.FileManager;
import com.darwinreforged.servermodifications.util.todo.ModBannerCfgManager;
import com.darwinreforged.servermodifications.util.todo.ModBannerHelper;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@DisabledModule("net.minecraftforge.fml.common.network.handshake.NetworkDispatcher.getModList notfound on dev, MCP issue? (in PlayerModsUtil)")
@ModuleInfo(id = "modbanner", name = "ModBanner", version = "1.1.5", description = "Ban Mods")
public class ModBannerModule extends PluginModule {

    public static File mod_dataF;

    public Path getConfigPath() {
        return FileManager.getYamlConfigFile(this).toPath();
    }

    public ModBannerCfgManager cfgManager;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        DarwinServer.registerCommand(new ModBannerCommand(), "modbanner", "modblacklist");
        DarwinServer.registerCommand(new ModsCommand(), "mods", "modinfo");
        File d = FileManager.getDataDirectory(this).toFile();
        if (!d.exists()) {
            d.mkdirs();
        }
        File f = new File(d, "mod_data.txt");
        mod_dataF = f;
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        reloadConfiguration();
    }

    @Listener
    public void onConnection(ClientConnectionEvent.Join e) {
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            try {
                boolean kicked = false;
                List<ModData> mods = PlayerModsUtil.getPlayerMods(e.getTargetEntity());
                List<String> bannedMods = new ArrayList<>();
                for(String blacklist : cfgManager.blackList){
                    for(ModData mod : mods){
                        if(mod.getName().contains(blacklist)){
                            bannedMods.add(mod.getName());
                            kicked = true;
                        }
                    }
                }
                List<String> lo = new ArrayList<>();
                boolean bypass = e.getTargetEntity().hasPermission("modbanner.bypass");

                for(ModData mod : mods){
                    if(bannedMods.contains(mod.getName())){
                        lo.add(mod.getCompleteData()+" (Banned)");
                    }else{
                        lo.add(mod.getCompleteData());
                    }
                }
                DarwinServer.getLogger().info(("[ModBanner] "+e.getTargetEntity().getName()+" is trying to join with these mods: "+String.join(", ", lo)+" "+(kicked ? (bypass ? "(It should be kicked but has the bypass permission)" : "(Getting kicked)") : "")));
                if(kicked && !bypass){
                    e.getTargetEntity().kick(ModBannerHelper.format(cfgManager.kickMsg.replace("%mods%", String.join(", ", bannedMods))));
                }
            } catch (VanillaPlayerException e01) {
                DarwinServer.getLogger().warn("Can not get "+e.getTargetEntity().getName()+" mods cause is not using forge!");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }).delay(1000, TimeUnit.MILLISECONDS).submit(DarwinServer.getServer());
    }

    public void reloadConfiguration(){
        try {
            cfgManager = new ModBannerCfgManager(this);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
