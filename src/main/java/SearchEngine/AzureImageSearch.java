package SearchEngine;

import Utils.LogWriter;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Random;

public class AzureImageSearch {

    private static final String BASE_URL = "https://api.datamarket.azure.com/Bing/Search/v1/Image?";
    private static final String ACCOUNT_KEY = "VeRL2qo2CmA1oDKGnAzp2Da1Ye7+1b0mW06Ccjm7L9Y";

    private String mSearchTerm;

    public AzureImageSearch(String searchTerm) {
        LogWriter.d("Search image \"" + searchTerm + "\" in Azure");
        mSearchTerm = URLEncoder.encode(searchTerm);
    }

    public String get() {

        String reqUrl = (BASE_URL + "Query=%27SEARCH_TERM%27&" + "Adult=%27Off%27&"
                + "$format=json").replace("SEARCH_TERM", mSearchTerm);
        LogWriter.d("Get: " + reqUrl);

        byte[] accountKeyBytes = Base64.encodeBase64((ACCOUNT_KEY + ":" + ACCOUNT_KEY).getBytes());
        String accountKeyEnc = new String(accountKeyBytes);

        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            HttpGet request = new HttpGet(reqUrl);
            request.addHeader("Authorization", "Basic " + accountKeyEnc);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpClient.execute(request, responseHandler);
            JSONObject responseJson = new JSONObject(responseBody).getJSONObject("d");
            JSONArray imagesArray = responseJson.getJSONArray("results");
            if (imagesArray.length() > 0) {
                final int SELECT_ITEMS = Math.min(imagesArray.length(), 10);
                int randomIndex = new Random().nextInt(Math.min(imagesArray.length(), SELECT_ITEMS));
                JSONObject randomImg = imagesArray.getJSONObject(randomIndex);

                String imageSrc = randomImg.getString("MediaUrl");
                LogWriter.d("Found image, src = " + imageSrc);
                return imageSrc;
            }

        } catch (Exception e) {
            LogWriter.e("Azure search failed.", e);
        }

        return null;
    }


}
