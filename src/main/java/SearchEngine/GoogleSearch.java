package SearchEngine;

import Utils.LogWriter;
import Utils.RequestBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

public class GoogleSearch {

    private static final String SEARCH_URL = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&&lr=lang_ru";
    private String mQuery;
    private String mSite;


    public GoogleSearch(String query) {
        this(query, null);
    }

    public GoogleSearch(String query, String site) {
        LogWriter.d("Search \"" + query + "\" in Google");
        mQuery = URLEncoder.encode(query);
        mSite = site;
    }

    public GoogleSearchResult get() throws SearchEngineException {

        String requestUrl = SEARCH_URL
                + "&q=" + (mSite != null ? ("site:" + mSite + "%20") : "") + mQuery;

        LogWriter.d("Get: " + requestUrl);

        try {
            String response = RequestBuilder.readUrl(requestUrl);

            JSONObject responseJson = new JSONObject(response).getJSONObject("responseData");
            JSONArray resultsArray = responseJson.getJSONArray("results");

            if (resultsArray.length() > 0) {
                return new GoogleSearchResult(resultsArray.getJSONObject(0));
            }
        } catch (IOException e) {
            LogWriter.e("IOException", e);
            throw new SearchEngineException("Не нашёл.");
        }

        return null;

    }

}
