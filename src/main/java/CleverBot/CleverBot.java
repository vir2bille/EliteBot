package CleverBot;

import Utils.LogWriter;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class CleverBot {

    private static final String BASE_URL = "https://cleverbot.io/1.0/";
    private static final String USER = "l4q1U76SKtTGyI5h";
    private static final String KEY = "A9sZDEyJruopvrBNhHcfuOU4d45Xj6nr";

    private static String mNick;

    private static CleverBot mInstance;

    private CleverBot() {
    }

    public static CleverBot getInstance() {
        if (mInstance == null) {
            mInstance = new CleverBot();
            mInstance.logIn();
        }

        return mInstance;
    }

    private static String decodeMessage(String message) {
        String target = "";

        if (!message.contains("|")) {
            return message;
        }

        String fields[] = StringUtils.split(message, '|');

        for (String field : fields) {
            String utfCode = field.substring(0, 4);
            String end = field.substring(4, field.length());
            int intValue = Integer.parseInt(utfCode, 16);
            char ch = (char) intValue;
            target += (ch + end);
        }

        return target;
    }

    private void logIn() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(BASE_URL).path("create");

        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.add("user", USER);
        formData.add("key", KEY);

        Response response = target.request()
                .post(Entity.form(formData));

        String responseString = response.readEntity(String.class);

        LogWriter.d("logIn result "+responseString);

        mNick = new JSONObject(responseString).getString("nick");
    }

    public String ask(String text) {

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(BASE_URL).path("ask");

        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
        formData.add("user", USER);
        formData.add("key", KEY);
        formData.add("nick", mNick);
        formData.add("text", text);

        Response response = target.request()
                .post(Entity.form(formData));

        String string = new JSONObject(response.readEntity(String.class)).getString("response");

        LogWriter.d("ask result "+string);

        return decodeMessage(string);

    }

}
