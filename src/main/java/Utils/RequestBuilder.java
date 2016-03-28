package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RequestBuilder {

    private String mBaseUrl;
    private Map<String, String> mParams;

    public RequestBuilder(String baseUrl) {
        mBaseUrl = baseUrl;
        mParams = new HashMap<>();
    }

    public static String readUrl(String urlString) throws IOException {
        BufferedReader reader;
        URL url = new URL(urlString);
        reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder buffer = new StringBuilder();
        int read;
        char[] chars = new char[1024];
        while ((read = reader.read(chars)) != -1)
            buffer.append(chars, 0, read);

        reader.close();
        return buffer.toString();
    }

    public RequestBuilder put(String key, Object value) {
        mParams.put(key, String.valueOf(value));
        return this;
    }

    public RequestBuilder putAll(Map<String, String> map) {
        mParams.putAll(map);
        return this;
    }

    public String buildUrl() {
        String url = mBaseUrl + "?";
        for (String key : mParams.keySet())
            url += String.format(
                    "%s=%s&", key, URLEncoder.encode(mParams.get(key)));
        return url;
    }

}
