package Google;

import Utils.LogWriter;
import Utils.RequestBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Random;

public class GoogleImageSearch {

    private static final String BASE_URL = "https://www.googleapis.com/customsearch/v1?";
    private static final String API_KEY = "AIzaSyAUn4MNnLEt9AMxAPSaMNuTQkDcyROUnpI";
    private static final String CX = "006971661295744138938:0hcmjkbpkqy";

    private String mSearchTerm;

    public GoogleImageSearch(String searchTerm) {
        LogWriter.d("Search image \"" + searchTerm + "\" in Google.Images");
        mSearchTerm = URLEncoder.encode(searchTerm);
    }

    public String get() {

        String reqUrl = BASE_URL + "key=" + API_KEY + "&cx=" + CX + "&q=" + mSearchTerm + "&searchType=image";
        LogWriter.d("Get: "+reqUrl);

        try {
            JSONObject object = new JSONObject(RequestBuilder.readUrl(reqUrl));
            JSONArray items = object.getJSONArray("items");
            final int SELECT_ITEMS = Math.min(items.length(), 10);
            int randomIndex = new Random().nextInt(Math.min(items.length(), SELECT_ITEMS));
            String imageSrc = items.getJSONObject(randomIndex).getString("link");

            LogWriter.d("Found image, src = " + imageSrc);
            return imageSrc;
        } catch (JSONException | IOException ex) {
            LogWriter.e("Google search failed.", ex);
        }

        return null;
    }
}
