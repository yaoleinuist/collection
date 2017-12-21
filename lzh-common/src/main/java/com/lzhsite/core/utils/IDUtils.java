//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lzhsite.core.utils;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

public class IDUtils {
    private static final String CODE = "9214035678";

    public IDUtils() {
    }

    public static Long decode(String eid) {
        try {
            Long.parseLong(eid);
        } catch (NumberFormatException var3) {
            return null;
        }

        try {
            eid = eid.substring(2, eid.length());
            StringBuilder e = new StringBuilder();

            for(int v = 0; v < eid.length(); ++v) {
                e.append("9214035678".indexOf(eid.charAt(v)));
            }

            Long var5 = Long.valueOf(e.toString());
            return Long.valueOf((var5.longValue() - 183729L) / 37L);
        } catch (Exception var4) {
            return null;
        }
    }

    public static String encode(Long id) {
        String eid = String.valueOf(id.longValue() * 37L + 183729L);
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < eid.length(); ++i) {
            builder.append("9214035678".charAt(Integer.parseInt(String.valueOf(eid.charAt(i)))));
        }

        return "10" + builder.toString();
    }

    public static String getUUIDFromGenerators() {
        TimeBasedGenerator gen = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
        String uuid = gen.generate().toString();
        return uuid.replaceAll("-", "");
    }
}
