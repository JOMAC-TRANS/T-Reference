package com.jomac.transcription.reference;

//DB User: request.activator@gmail.com
//DB Pass: qwerty123
public enum PluginFileLink {
    B("https://www.dropbox.com/s/aqlqf9e20zmy99u/reference_B.zip?raw=1") {
        @Override
        public String toString() {
            return "reference_B.zip";
        }
    },
    C("https://www.dropbox.com/s/2h9rzxbxjlb6jc5/reference_C.zip?raw=1") {
        @Override
        public String toString() {
            return "reference_C.zip";
        }
    },
    F("https://www.dropbox.com/s/3i67z1f43ddxyli/reference_F.zip?raw=1") {
        @Override
        public String toString() {
            return "reference_F.zip";
        }
    },
    L("https://www.dropbox.com/s/s43gxz5ztc1g7uk/reference_L.zip?raw=1") {
        @Override
        public String toString() {
            return "reference_L.zip";
        }
    },
    S("https://www.dropbox.com/s/gypeknh2kvrt6jo/reference_S.zip?raw=1") {
        @Override
        public String toString() {
            return "reference_S.zip";
        }
    },
    O("https://www.dropbox.com/s/euwy0fdykjhyqt2/reference_O.zip?raw=1") {
        @Override
        public String toString() {
            return "reference_O.zip";
        }
    };

    private final String pathLink;

    private PluginFileLink(String value) {
        this.pathLink = value;
    }

    public String getLink() {
        return pathLink;
    }
}
