package com.darwinreforged.servermodifications.modules;

import com.darwinreforged.servermodifications.commands.modbanner.ModBannerCommand;
import com.darwinreforged.servermodifications.commands.modbanner.ModsCommand;
import com.darwinreforged.servermodifications.exceptions.VanillaPlayerException;
import com.darwinreforged.servermodifications.modules.root.ModuleInfo;
import com.darwinreforged.servermodifications.modules.root.PluginModule;
import com.darwinreforged.servermodifications.objects.ModData;
import com.darwinreforged.servermodifications.util.plugins.PlayerModsUtil;
import com.darwinreforged.servermodifications.util.todo.ModBannerCfgManager;
import com.darwinreforged.servermodifications.util.todo.ModBannerHelper;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ModuleInfo(id="modbanner", name="ModBanner", version="1.1.5", description="Ban Mods")
public class ModBannerModule extends PluginModule {

    public static ModBannerModule instance;
    //	public static HashMap<String, List<ModData>> lastData = new HashMap<>();
    public static File mod_dataF;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path configPath;

    @Inject
    public Logger log;

    public Path getConfigPath(){
        return configPath;
    }

    public ModBannerCfgManager cfgManager;

    @Listener
    public void onGamePreInitializationEvent(GamePreInitializationEvent e){
        instance = this;

        Sponge.getCommandManager().register(this, new ModBannerCommand(), "modbanner", "modblacklist");
        Sponge.getCommandManager().register(this, new ModsCommand(), "mods", "modinfo");
        File d = Sponge.getGame().getSavesDirectory().resolve("data/modbanner").toFile();
        if(!d.exists()){
            d.mkdirs();
        }
        File f = new File(d, "mod_data.txt");
        mod_dataF = f;
        if(!f.exists()){
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
                log.info(("[ModBanner] "+e.getTargetEntity().getName()+" is trying to join with these mods: "+String.join(", ", lo)+" "+(kicked ? (bypass ? "(It should be kicked but has the bypass permission)" : "(Getting kicked)") : "")));
                if(kicked && !bypass){
                    e.getTargetEntity().kick(ModBannerHelper.format(cfgManager.kickMsg.replace("%mods%", String.join(", ", bannedMods))));
                }
            } catch (VanillaPlayerException e01) {
                log.warn("Can not get "+e.getTargetEntity().getName()+" mods cause is not using forge!");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }).delay(1000, TimeUnit.MILLISECONDS).submit(ModBannerModule.instance);
    }

    public void reloadConfiguration(){
        try {
            cfgManager = new ModBannerCfgManager(this);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
