package com.jomac.transcription.activator.controller;

import com.jomac.transcription.activator.model.PluginBean;
import java.util.ArrayList;
import java.util.List;

public enum PluginController {

    INSTANCE;
    final private List<PluginBean> plugins = new ArrayList<>();
    private final int B = 1, C = 2, F = 4, G = 8, H = 0x10, L = 0x20,
            M = 0x40, O = 0x80, S = 0x100, SPEC3 = 0x200, HS = 0x400;

    public List<PluginBean> getAllPlugins() {
        if (!plugins.isEmpty()) {
            return plugins;
        }

        PluginBean pb = new PluginBean();
        pb.setPlugin("B");
        pb.setPluginName("BAY");
        pb.setPluginValue(B);
        plugins.add(pb);

        pb = new PluginBean();
        pb.setPlugin("C");
        pb.setPluginName("Central");
        pb.setPluginValue(C);
        plugins.add(pb);

        pb = new PluginBean();
        pb.setPlugin("F");
        pb.setPluginName("Flint");
        pb.setPluginValue(F);
        plugins.add(pb);

        pb = new PluginBean();
        pb.setPlugin("G");
        pb.setPluginName("Good Samaritan");
        pb.setPluginValue(G);
        plugins.add(pb);

//        pb = new PluginBean();
//        pb.setPlugin("H");
//        pb.setPluginName("Hackettstown");
//        pb.setPluginValue(H);
//        plugins.add(pb);

        pb = new PluginBean();
        pb.setPlugin("L");
        pb.setPluginName("Lapeer");
        pb.setPluginValue(L);
        plugins.add(pb);

//        pb = new PluginBean();
//        pb.setPlugin("M");
//        pb.setPluginName("Macomb");
//        pb.setPluginValue(M);
//        plugins.add(pb);

        pb = new PluginBean();
        pb.setPlugin("O");
        pb.setPluginName("Oakland");
        pb.setPluginValue(O);
        plugins.add(pb);

        pb = new PluginBean();
        pb.setPlugin("S");
        pb.setPluginName("Seton");
        pb.setPluginValue(S);
        plugins.add(pb);

//        pb = new PluginBean();
//        pb.setPlugin("HS");
//        pb.setPluginName("Hospital Specifics");
//        pb.setPluginValue(HS);
//        plugins.add(pb);
//
//        pb = new PluginBean();
//        pb.setPlugin("SPEC3");
//        pb.setPluginName("SPEC3");
//        pb.setPluginValue(SPEC3);
//        plugins.add(pb);

        return plugins;
    }
}
