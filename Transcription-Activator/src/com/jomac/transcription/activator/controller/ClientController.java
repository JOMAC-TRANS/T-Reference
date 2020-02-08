package com.jomac.transcription.activator.controller;

import com.jomac.transcription.activator.model.PluginBean;
import java.util.ArrayList;
import java.util.List;

public enum ClientController {

    INSTANCE;
    List<PluginBean> plugins = new ArrayList<>();

    public List<PluginBean> getAllPrograms() {
        if (!plugins.isEmpty()) {
            return plugins;
        }

        PluginBean pb = new PluginBean();
        pb.setPlugin("Client");
        pb.setPluginName("JOMAC Transcription");
        pb.setPluginValue(1);
        plugins.add(pb);

        pb = new PluginBean();
        pb.setPlugin("Audio Player");
        pb.setPluginName("JOMAC Audio");
        pb.setPluginValue(2);
        plugins.add(pb);

        return plugins;
    }
}
