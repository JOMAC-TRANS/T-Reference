package com.jomac.transcription.reference;

//DB User: request.activator@gmail.com
//DB Pass: qwerty123
public enum ReferenceFileLink {
    GUIDELINES("https://www.dropbox.com/s/y571ibo93399x4b/Guidelines_and_Instructions.doc?raw=1") {
        @Override
        public String toString() {
            return "Guidelines_and_Instructions.doc";
        }
    },
    NORMALLABS("https://www.dropbox.com/s/7tjsw0fs7gasz4o/NormalLabs.html?raw=1") {
        @Override
        public String toString() {
            return "NormalLabs.html";
        }
    },
    DRUGS_PHRASES("https://www.dropbox.com/s/fs29d2556qjdqdt/Psych_Drugs_phrases.xls?raw=1") {
        @Override
        public String toString() {
            return "Psych_Drugs_phrases.xls";
        }
    },
    TR_DICTATORS("https://www.dropbox.com/s/97h6u2oskau0vu1/TR-Dictators.xls?raw=1") {
        @Override
        public String toString() {
            return "TR-Dictators.xls";
        }
    };

    private final String pathLink;

    private ReferenceFileLink(String value) {
        this.pathLink = value;
    }

    public String getLink() {
        return pathLink;
    }
}
