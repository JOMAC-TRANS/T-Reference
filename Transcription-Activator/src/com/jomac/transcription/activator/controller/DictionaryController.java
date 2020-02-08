package com.jomac.transcription.activator.controller;

import com.jomac.transcription.activator.model.PluginBean;
import java.util.ArrayList;
import java.util.List;

public enum DictionaryController {

    INSTANCE;

    public List<PluginBean> getAllDictionary() {
        List<PluginBean> plugins = new ArrayList<>();

        PluginBean pb = new PluginBean();
        pb.setPlugin("Dictionary");
        pb.setPluginName("JOMAC DRUGS");
        pb.setPluginValue(1);
        plugins.add(pb);

        return plugins;
    }
}
