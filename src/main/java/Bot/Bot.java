package Bot;

import Bash.BashQuote;
import CleverBot.CleverBot;
import SearchEngine.GoogleSearch;
import SearchEngine.GoogleSearchResult;
import SearchEngine.RandomEngineImageSearch;
import SearchEngine.SearchEngineException;
import Utils.ImageDownload;
import Utils.LogWriter;
import Utils.PropertyManager;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.SendChatAction;
import org.telegram.telegrambots.api.methods.SendMessage;
import org.telegram.telegrambots.api.methods.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class Bot extends TelegramLongPollingBot {

    private static final Command CMD_PICTURE = new Command("пикча", "π", "/img");
    private static final Command CMD_SPAM_STOP = new Command("не спамь!");
    private static final Command CMD_SPAM_START = new Command("спамь");
    private static final Command CMD_PIKABU = new Command("пикабу");
    private static final Command CMD_YOUTUBE = new Command("ютуб");
    private static final Command CMD_TITS = new Command("сиськи");
    private static final Command CMD_STAT = new Command("стата");
    private static final Command CMD_BASH = new Command("баш");
    private static final Command CMD_MORE = new Command("ещё");
    private static final Command CMD_CACHE = new Command("кеш");

    private String mBotToken;
    private String mBotUsername;
    private List<String> mAdmins;
    private Timer mFloodTimer;


    private String mLastMessage;
    private Integer mLastReplyId;


    public Bot(String botToken, String botUsername, List<String> admins) {
        mBotToken = botToken;
        mBotUsername = botUsername;
        mAdmins = admins;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        LogWriter.m(message);
        respondToMessage(message);
    }

    @Override
    public String getBotUsername() {
        return mBotUsername;
    }

    @Override
    public String getBotToken() {
        return mBotToken;
    }

    private void respondToMessage(Message message) {
        if (message == null) {
            return;
        }

        Long chatId = message.getChatId();

        String messageText = message.getText();
        Integer messageId = message.getMessageId();

        Message replayToMessage = message.getReplyToMessage();
        boolean replayFromBot = replayToMessage != null
                && replayToMessage.getFrom().getUserName().equalsIgnoreCase(mBotUsername);
        boolean appealedToBot = replayFromBot || messageText.toLowerCase().contains(mBotUsername.toLowerCase());

        User user = message.getFrom();
        String userId = user.getId().toString();


        boolean isUserChat = message.getChat().isUserChat();
        boolean isGroupChat = message.getChat().isGroupChat();
        boolean isAdmin = mAdmins.contains(userId);

        if (CMD_MORE.equalsMessage(messageText)) {
            messageId = mLastReplyId;
            messageText = mLastMessage;
        }

        if (CMD_TITS.equalsMessage(messageText)) {
            sendTits(chatId, messageId);
            return;
        }

        if (CMD_PICTURE.containedIn(messageText)) {
            sendPicture(CMD_PICTURE.getQuery(messageText), chatId, messageId);
            return;
        }

        if (CMD_YOUTUBE.containedIn(messageText)) {
            sendYoutubeLink(CMD_YOUTUBE.getQuery(messageText), chatId, messageId);
            return;
        }

        if (CMD_CACHE.equalsMessage(messageText)) {
            sendCachePicture(chatId, messageId);
            return;
        }

        if (CMD_BASH.equalsMessage(messageText)) {
            sendBashQuote(chatId, messageId);
            return;
        }

        if (CMD_PIKABU.equalsMessage(messageText)) {
            sendPikabuPost(chatId, messageId);
            return;
        }

        if (CMD_STAT.equalsMessage(messageText)) {
            sendStatistics(chatId);
            return;
        }

        if (CMD_SPAM_STOP.equalsMessage(messageText)) {
            stopFlood(chatId, messageId);
            return;
        }

        if (CMD_SPAM_START.containedIn(messageText)) {
            int floodInterval;
            try {
                floodInterval = Integer.parseInt(CMD_SPAM_START.getQuery(messageText));
            } catch (NumberFormatException e) {
                floodInterval = 5;
            }
            startFlood(floodInterval,chatId, messageId);
            return;
        }

        if (appealedToBot || isUserChat) {
            String question = (replayFromBot || isUserChat)
                    ? messageText
                    : new Command(mBotUsername).getQuery(messageText);
            askCleverBot(question, chatId, messageId);
            return;
        }
    }

    private void sendTits(Long chatId, Integer replyTo) {
        sendPicture(CMD_TITS.getRandomAlias(), chatId, replyTo);
    }

    private void sendPicture(String searchTerm, Long chatId, Integer replyTo) {
        mLastMessage = String.format("%s %s", CMD_PICTURE.getRandomAlias(), searchTerm);
        mLastReplyId = replyTo;

        try {
            sendChatAction("upload_photo", chatId);

            String imgUrl = RandomEngineImageSearch.getRandomImage(searchTerm);
            String imgPath = ImageDownload.download(imgUrl, searchTerm);
            sendPhoto(imgPath, chatId, replyTo);
        } catch (SearchEngineException | IOException e) {
            sendMsg(e.getMessage(), chatId, replyTo);
            LogWriter.e("sendPicture", e);
        }
    }

    private void sendMsg(String message, Long chatId, Integer replyTo) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setReplayToMessageId(replyTo);
        msg.setText(message);
        msg.setDisableWebPagePreview(false);

        try {
            sendMessage(msg);
        } catch (TelegramApiException e) {
            LogWriter.e("sendMsg", e);
        }
    }

    private void sendChatAction(String chatAction, Long chatId) {
        SendChatAction action = new SendChatAction();
        action.setAction(chatAction);
        action.setChatId(chatId.toString());

        try {
            sendChatAction(action);
        } catch (TelegramApiException e) {
            LogWriter.e("sendChatAction", e);
        }
    }

    private void sendPhoto(String imageFileName, Long chatId, Integer replyTo) {
        File imageFile = new File(imageFileName);
        if (!imageFile.exists()) {
            return;
        }

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setNewPhoto(imageFile.getAbsolutePath(), imageFileName);
        sendPhoto.setReplayToMessageId(replyTo);

        try {
            sendPhoto(sendPhoto);
        } catch (TelegramApiException e) {
            LogWriter.e("sendPhoto", e);
        }
    }

    private void sendYoutubeLink(String searchTerm, Long chatId, Integer replyTo) {
        try {
            GoogleSearch googleSearch = new GoogleSearch(searchTerm, "youtube.com");
            GoogleSearchResult searchResult = googleSearch.get();
            sendMsg(searchResult.getUnescapedUrl(), chatId, replyTo);
        } catch (SearchEngineException e) {
            LogWriter.e("sendYoutubeLink", e);
        }
    }

    private void sendCachePicture(Long chatId, Integer replyTo) {
        try {
            String cacheDir = PropertyManager.getInstance().getCacheDir();
            ArrayList<File> cache = new ArrayList<>();
            ImageDownload.listf(cacheDir, cache);
            int randomIndex = new Random().nextInt(cache.size());
            sendMsg(String.format("Всего в кеше %d пикч. Тебе досталась пикча №%d.", cache.size(), randomIndex),
                    chatId, replyTo);
            sendPhoto(cache.get(randomIndex).getAbsolutePath(), chatId, null);
        } catch (IOException e) {
            LogWriter.e("sendCachePicture", e);
        }
    }

    private void sendBashQuote(Long chatId, Integer replyTo) {
        try {
            String quote = new BashQuote().getRandomQuote();
            sendMsg(quote, chatId, replyTo);
        } catch (IOException e) {
            LogWriter.e("sendBashQuote", e);
        }
    }

    private void sendPikabuPost(Long chatId, Integer replyTo) {
        // Увы.
    }

    private void sendStatistics(Long chatId) {
        // Увы.
    }

    private void askCleverBot(String question, Long chatId, Integer replyTo) {
        String cleverAnswer = CleverBot.getInstance().ask(question);
        sendMsg(cleverAnswer, chatId, replyTo);
    }

    private void startFlood(int intervalMinutes, Long chatId, Integer replyTo) {
        if(mFloodTimer == null) {
            TimerTask floodTask = new TimerTask() {
                @Override
                public void run() {
                    sendBashQuote(chatId, null);
                }
            };

            mFloodTimer = new Timer();
            mFloodTimer.scheduleAtFixedRate(floodTask, 0L, TimeUnit.MINUTES.toMillis(intervalMinutes));
            sendMsg(String.format("Начинаю флудить с интервалом %d мин.", intervalMinutes), chatId, replyTo);

            LogWriter.d("Flood enabled in #"+chatId);
        } else {
            sendMsg("Я уже флужу!", chatId, replyTo);
        }
    }

    private void stopFlood(Long chatId, Integer replyTo) {
        if(mFloodTimer != null) {
            mFloodTimer.cancel();
            mFloodTimer.purge();
            mFloodTimer = null;

            sendMsg("Прекращаю флудить!", chatId, replyTo);
            LogWriter.d("Flood disabled.");
        } else {
            sendMsg("Я и не флудил.", chatId, replyTo);
        }
    }
}
