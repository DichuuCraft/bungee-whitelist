package com.hadroncfy.proxywhitelist;

public interface ICommandSender {
    void sendResultMessage(String msg);
    String getLabel();
}