package com.hadroncfy.proxywhitelist;

public class Config {
    public String kickMessage = "Vous n'étes pas dans la liste blanche!";
    public boolean enabled = false;
    public void assign(Config c){
        kickMessage = c.kickMessage;
        enabled = c.enabled;
    }
}