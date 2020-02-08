package com.jomac.transcription.activator.utility;

import com.hccs.util.SHA1Digester;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

public enum CodeUtility {

    INSTANCE;

    public String digest(String s) {
        return SHA1Digester.digest(s);
    }

    public String decode(String s) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(s));
    }

    public String encode(String s) {
        return Base64.encodeBase64String(StringUtils.getBytesUtf8(s));
    }
}
