package com.lzhsite.enums;

public enum SignType {
    MD5("MD5"),
    RSA("RSA"),
    DSA("DSA");

    private String value;

    private SignType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static boolean isSignType(String name) {
        SignType[] arr$ = values();
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            SignType signType = arr$[i$];
            if(signType.getValue().equals(name)) {
                return true;
            }
        }

        return false;
    }
}