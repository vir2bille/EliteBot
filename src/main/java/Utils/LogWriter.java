package Utils;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogWriter {

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-d HH:mm:ss.SSS", Locale.ENGLISH);

    public static void d(String message) {
        String outMsg = formatMessage("[DEBUG] " + message);
        System.out.println(outMsg);
        writeSysLog(outMsg);
    }

    public static void e(String message) {
        String outMsg = formatMessage("[ERROR] " + message);
        System.out.println(outMsg);
        writeSysLog(outMsg);
    }

    public static void e(String message, Throwable ex) {
        if(ex instanceof TelegramApiException) {
            e(((TelegramApiException) ex).getApiResponse());
            return;
        }

        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        ex.printStackTrace(pw);
        String stackTrace = sw.getBuffer().toString();

        e(String.format("%s:\n%s", message, stackTrace));
    }

    public static void m(Message message) {

        User senderUser = message.getFrom();

        String senderFirstName = senderUser.getFirstName();
        String senderLastName = senderUser.getLastName();
        String senderUserName = senderUser.getUserName();

        Integer senderId = senderUser.getId();
        String chatId = String.valueOf(message.getChatId());

        String userName =
                senderUserName != null
                        ? "@" + senderUserName
                        : ((senderFirstName != null && senderLastName != null)
                        ? (senderFirstName + " " + senderLastName)
                        : senderId.toString());

        String out = String.format("%s(%s): %s", userName,senderId, message.getText());

        out = formatMessage(out);

        System.out.println(out);
        LogWriter.writeChatLog(chatId, out);
    }

    private static String formatMessage(String message) {
        return DATE_FORMAT.format(new Date(System.currentTimeMillis()))
                + " " + message;
    }

    static void writeChatLog(String chatId, String message) {
        appendString(chatId, message);
    }

    static void writeSysLog(String message) {
        appendString("syslog", message);
    }

    private synchronized static void appendString(String logFile, String message) {
        try {
            PropertyManager propertyManager = PropertyManager.getInstance();
            String logsDir = propertyManager.getLogDir();
            File file = new File(logsDir);
            boolean newLogFile = !file.exists();
            if (newLogFile) {
                boolean isDirCreated = file.mkdirs();
                if (!isDirCreated) {
                    return;
                }
            }
            java.io.FileWriter fw = new java.io.FileWriter(logsDir + File.separator + logFile + ".txt", true);
            fw.write(message + "\n");
            fw.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
