package com.hadroncfy.proxywhitelist;

import java.io.File;
import java.util.logging.Logger;

public interface IPlugin {
    public boolean isOnlineMode();
    public File getDataDirectory();
    public void broadcast(ICommandSender sender, String msg);
    public Logger getLogger();
    public void loadConfig(Config config);
    public void saveConfig(Config config);
}