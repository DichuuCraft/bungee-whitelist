package com.hadroncfy.proxywhitelist;

import java.io.File;

public interface IPlugin {
    public boolean isOnlineMode();
    public File getDataDirectory();
    public void broadcast(ICommandSender sender, String msg);
    public ILogger logger();
    public void loadConfig(Config config);
    public void saveConfig(Config config);
}