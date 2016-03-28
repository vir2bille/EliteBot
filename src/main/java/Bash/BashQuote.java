package Bash;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Random;

public class BashQuote {

    private static final String BASE_URL = "http://bash.im/byrating/";
    private static final int PAGES = 50;

    public String getRandomQuote() throws IOException {
        Random random = new Random();
        String url = BASE_URL + random.nextInt(PAGES);
        Document bashDoc = Jsoup.connect(url).get();
        Elements elements = bashDoc.select(".text");

        String randomQuote = elements
                .get(random.nextInt(elements.size()))
                .html()
                .replace("<br>", "");

        return randomQuote;
    }

}
