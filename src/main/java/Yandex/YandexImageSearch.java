package Yandex;

import Utils.LogWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Random;

public class YandexImageSearch {

    private static final String BASE_URL = "https://yandex.ru/images/search?";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0";

    private String mSearchTerm;

    public YandexImageSearch(String searchTerm) {
        LogWriter.d("Search image \"" + searchTerm + "\" in Yandex.Images");
        mSearchTerm = URLEncoder.encode(searchTerm);
    }

    public String get() {

        String reqUrl = BASE_URL + "text="+mSearchTerm;
        LogWriter.d("Get: " + reqUrl);

        try {
            Document doc = Jsoup.connect(reqUrl)
                    .userAgent(USER_AGENT)
                    .get();

            Elements imagesArray = doc.getElementsByTag("img");
            final int SELECT_ITEMS = Math.min(imagesArray.size(), 10);
            Element randomImage = imagesArray.get(1 + new Random().nextInt(SELECT_ITEMS));
            String imageSrc = randomImage.absUrl("src");

            LogWriter.d("Image src:" + imageSrc);
            return imageSrc;

        } catch (IOException e) {
            LogWriter.e("Yandex search failed.", e);
        }

        return null;
    }

}
