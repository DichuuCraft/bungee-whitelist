package com.hadroncfy.bungeewhitelist;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import java.util.UUID;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hadroncfy.bungeewhitelist.WhitelistEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class WhitelistPlugin extends Plugin implements IWhitelist, Listener {
    private Map<UUID, String> whitelist = new HashMap<>();
    private boolean enabled = false;
    private String kickMessage = "Vous n'étes pas dans la liste blanche!";
    private Gson gson;

    private File dataPath;

    @Override 
    public void onEnable(){
        dataPath = getDataFolder();
        gson = new GsonBuilder().setPrettyPrinting().create();

        if (!dataPath.exists()){
            dataPath.mkdirs();
        }
        loadConfig();
        loadWhitelist();
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerCommand(this, new WhitelistCommand(this));
    }
 
    private void loadConfig(){
        File configFile = new File(dataPath, "config.yml");
        if (!configFile.exists()){
            saveConfig();
            return;
        }

        try {
            Configuration config = YamlConfiguration.getProvider(YamlConfiguration.class).load(configFile);
            enabled = config.getBoolean("enabled");
            kickMessage = config.getString("kick-message");
        }
        catch (Exception e) {
            getLogger().severe("Failed to load configuration!");
        }
    }
 
    private void saveConfig(){
        File configFile = new File(dataPath, "config.yml");
        try {
            Configuration config = new Configuration();
            config.set("enabled", enabled);
            config.set("kick-message", kickMessage);
            YamlConfiguration.getProvider(YamlConfiguration.class).save(config, configFile);
        }
        catch (Exception e) {
            getLogger().severe("Failed to save configuration!");
        }
    }

    private void loadWhitelist(){
        File whitelistFile = new File(dataPath, "whitelist.json");
        if (!whitelistFile.exists()){
            saveWhitelist();
            return;
        }
        InputStream is = null;
		try {
			is = new FileInputStream(whitelistFile);
            String content = IOUtils.toString(is, Charsets.UTF_8);
            // Gson gson = new Gson();
            whitelist.clear();
            for (WhitelistEntry entry: gson.fromJson(content, WhitelistEntry[].class)){
                whitelist.put(UUID.fromString(entry.uuid), entry.name);
            }
            getLogger().info("Loaded white list");
		} catch (FileNotFoundException e) {
            getLogger().severe("File " + whitelistFile.toString() + " not found: " + e.getMessage());
        }
        catch (IOException e) {
            getLogger().severe("Cannot read white list file " + whitelistFile.toString() + ": " + e.getMessage());
        }
        finally {
            IOUtils.closeQuietly(is);
        }
    }

    private void saveWhitelist(){
        File whitelistFile = new File(dataPath, "whitelist.json");
        OutputStream os = null;
        try {
            os = new FileOutputStream(whitelistFile);
            List<WhitelistEntry> wl = new ArrayList<>();
            for (Map.Entry<UUID, String> entry: whitelist.entrySet()){
                wl.add(new WhitelistEntry(entry.getKey().toString(), entry.getValue()));
            }
            // Gson gson = new Gson();
            String content = gson.toJson(wl);
            OutputStreamWriter w = new OutputStreamWriter(os);
            w.append(content);
            w.close();
		} catch (FileNotFoundException e) {
			getLogger().severe("File " + whitelistFile.toString() + " not found: " + e.getMessage());
		} catch (IOException e){
            getLogger().severe("Cannot write file " + whitelistFile.toString() + ": " + e.getMessage());
        }
        finally {
            IOUtils.closeQuietly(os);
        }
    }

    @Override
    public boolean isWhitelisted(UUID uuid){
        return whitelist.get(uuid) != null;
    }

    @Override
    public void update(UUID uuid, String name){
        whitelist.put(uuid, name);
        saveWhitelist();
    }

    @Override
    public void remove(UUID uuid){
        whitelist.remove(uuid);
        saveWhitelist();
    }

    @EventHandler
    public void onNetworkJoin(LoginEvent e){
        PendingConnection p = e.getConnection();
        UUID uuid = p.getUniqueId();
        if (enabled){
            if (isWhitelisted(uuid)){
                if (!whitelist.get(uuid).equals(p.getName())){
                    whitelist.put(uuid, p.getName());
                    saveWhitelist();
                }
            }
            else {
                p.disconnect(new TextComponent(kickMessage));
                getLogger().info("Disconnected non-whitelisted player " + p.getName());
            }
        }
    }

	@Override
	public void reload() {
        loadWhitelist();
	}

	@Override
	public List<String> list() {
        List<String> ret = new ArrayList<>();
        for (Map.Entry<UUID, String> entry: whitelist.entrySet()){
            ret.add(entry.getValue());
        }
		return ret;
	}

	@Override
	public void enable() {
        enabled = true;
        saveConfig();
	}

	@Override
	public void disable() {
        enabled = false;
        saveConfig();
    }
    
    private UUID getUUID(String name){
        for (Map.Entry<UUID, String> entry: whitelist.entrySet()){
            if (entry.getValue().equalsIgnoreCase(name)){
                return entry.getKey();
            }
        }
        return null;
    }

    @Override    
    public Profile createUUID(String name) throws IOException{
        return getProxy().getConfig().isOnlineMode() ? MinecraftAPI.getUUIDByName(name) : MinecraftAPI.getOfflineUUID(name);
    }

	@Override
	public boolean removeByName(String name) {
        UUID uuid = getUUID(name);
        if (uuid != null){
            remove(uuid);
            return true;
        }
        else {
            return false;
        }
	}

    @Override
    public void broadcast(BaseComponent c){
        for (ProxiedPlayer player: getProxy().getPlayers()){
            player.sendMessage(ChatMessageType.CHAT, c);
        }
    }
}