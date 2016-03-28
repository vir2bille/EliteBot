import Bot.Bot;
import Utils.LogWriter;
import Utils.PropertyManager;
import com.sun.javafx.fxml.PropertyNotFoundException;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.io.IOException;
import java.util.List;

public final class Main {

    private static TelegramBotsApi api = new TelegramBotsApi();

    public static void main(String[] args) {
        LogWriter.d("Program started.");
        try {
            PropertyManager properties = PropertyManager.getInstance();

            String botToken = properties.getBotToken();
            String botUsername = properties.getBotUsername();
            List<String> botAdmins = properties.getAdmins();

            LogWriter.d(String.format("Register bot: %s %s", botUsername, botToken));

            if (botToken == null || botUsername == null) {
                throw new PropertyNotFoundException("Property file invalid.");
            }

            TelegramLongPollingBot bot = new Bot(botToken, botUsername, botAdmins);
            api.registerBot(bot);

            LogWriter.d("Registration success");

        } catch (IOException | TelegramApiException | PropertyNotFoundException e) {
            LogWriter.e("Registration failed", e);
        }

    }

}
