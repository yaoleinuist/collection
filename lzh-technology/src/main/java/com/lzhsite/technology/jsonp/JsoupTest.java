package com.lzhsite.technology.jsonp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * <p>reactorDome
 * <p>com.lzhsite.technology.grammar.reactor.test
 *
 * @author stony
 * @version 下午3:00
 * @since 2018/1/11
 */
public class JsoupTest {

    @Test
    public void test_14() throws IOException {
        String fileName = "/Documents/Flux-3.1.2.RELEASE.src.htm";
        String dir = System.getProperty("user.home");
        Document doc = Jsoup.parse(new File(dir + fileName), StandardCharsets.UTF_8.name());

        Elements details = doc.body().select(".details");
        Elements methodDetails = details.select(".block");

        for (Element methodDetail : methodDetails) {
            System.out.println(methodDetail.text());

        }
    }
}
