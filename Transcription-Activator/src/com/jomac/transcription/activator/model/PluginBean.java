package com.jomac.transcription.activator.model;

public class PluginBean {

    private String plugin;
    private String pluginName;
    private int pluginValue;

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public int getPluginValue() {
        return pluginValue;
    }

    public void setPluginValue(int pluginValue) {
        this.pluginValue = pluginValue;
    }

    @Override
    public String toString() {
        return pluginName;
    }
}
