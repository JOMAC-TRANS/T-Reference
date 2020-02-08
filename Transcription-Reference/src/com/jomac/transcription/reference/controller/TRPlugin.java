package com.jomac.transcription.reference.controller;

import java.util.ArrayList;
import java.util.List;

public enum TRPlugin {

    INSTANCE(-1),
    B(1) {
        @Override
        public String toString() {
            return "BAY";
        }
    }, C(2) {
        @Override
        public String toString() {
            return "Central";
        }
    }, F(4) {
        @Override
        public String toString() {
            return "Flint";
        }
//    }, G(8) {
//        @Override
//        public String toString() {
//            return "Good Samaritan";
//        }
//    }, H(0x10) {
//        @Override
//        public String toString() {
//            return "Hackettstown";
//        }
    }, L(0x20) {
        @Override
        public String toString() {
            return "Lapeer";
        }
//    },
//    M(0x40) {
//        @Override
//        public String toString() {
//            return "Macomb";
//        }
    }, O(0x80) {
        @Override
        public String toString() {
            return "Oakland";
        }
    }, S(0x100) {
        @Override
        public String toString() {
            return "Seton";
        }
//    }, SPEC3(0x200) {
//        @Override
//        public String toString() {
//            return "SPEC3";
//        }
//    }, HS(0x400) {
//        @Override
//        public String toString() {
//            return "Hospital Specifics";
//        }
    };

    private final int value;

    private TRPlugin(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    private final List<TRPlugin> plugins = new ArrayList<>();

    public List<TRPlugin> getAllPlugins() {
        if (plugins.isEmpty()) {
            for (TRPlugin p : TRPlugin.values()) {
                if (p.getValue() > 0) {
                    plugins.add(p);
                }
            }
        }
        return plugins;
    }
}
