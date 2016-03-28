package Utils;

import Exceptions.ImageSearchException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ImageDownload {

    public static String download(String sourceUrl, String query) throws IOException, ImageSearchException {
        return download(sourceUrl, query, String.valueOf(System.currentTimeMillis()));
    }

    public static String download(String sourceUrl, String query, String filePostfix) throws IOException, ImageSearchException {

        LogWriter.d("Get: " + sourceUrl + ", query=" + query + ", filePosfix=" + filePostfix);

        PropertyManager propertyManager = PropertyManager.getInstance();
        String cacheDir = propertyManager.getCacheDir();

        String safeQuery = generateSafeDirName(query);

        File saveDir = new File(cacheDir + File.separator + safeQuery);

        if (!saveDir.exists()) {
            LogWriter.d("Cache dir " + saveDir.getAbsolutePath() + " not exist.");
            boolean createDirsSuccess = saveDir.mkdirs();
            if (!createDirsSuccess) {
                throw new IOException("Cache dir " + saveDir.getAbsolutePath() + " create failed.");
            }
            LogWriter.d("Cache folder " + saveDir.getAbsolutePath() + " create success.");
        }

        URL url = new URL(sourceUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 " +
                        "(KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");


        int responseCode = httpConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {

            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (contentType == null || !contentType.contains("image") || contentType.equals("image/gif")) {
                throw new ImageSearchException("Я не умею отправлять гифки.");
            }

            LogWriter.d("HTTP_OK:\n" + " - Content-Type = " + contentType + "\n"
                    + " - Content-Disposition = " + disposition + "\n"
                    + " - Content-Length = " + contentLength);

            InputStream inputStream = httpConn.getInputStream();

            String targetFilePath = saveDir.getAbsolutePath() + File.separator + safeQuery;

            if (filePostfix != null) {
                targetFilePath += "_" + filePostfix;
            }

            String extension = "." + contentType.split("/")[1];
            targetFilePath += extension;

            LogWriter.d("Target file path: " + targetFilePath);

            FileOutputStream outputStream = new FileOutputStream(targetFilePath);

            int bytesRead;
            byte[] buffer = new byte[4096];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            return targetFilePath;
        } else {
            throw new IOException("Response code: " + responseCode);
        }
    }


    private static String generateSafeDirName(String unsafeName) {

        if(unsafeName == null || unsafeName.trim().isEmpty()) {
            return "no_name";
        }

        unsafeName = unsafeName.trim().toLowerCase();

        char[] abcCyr = {' ', 'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Б', 'Э', 'Ю', 'Я', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        String[] abcLat = {"_", "a", "b", "v", "g", "d", "e", "e", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "h", "ts", "ch", "sh", "sch", "", "i", "", "e", "ju", "ja", "A", "B", "V", "G", "D", "E", "E", "Zh", "Z", "I", "Y", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "H", "Ts", "Ch", "Sh", "Sch", "", "I", "", "E", "Ju", "Ja", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < unsafeName.length(); i++) {
            for (int x = 0; x < abcCyr.length; x++)
                if (unsafeName.charAt(i) == abcCyr[x]) {
                    builder.append(abcLat[x]);
                }
        }
        return builder.toString().trim();
    }


    public static void listf(String directoryName, ArrayList<File> files) {
        File directory = new File(directoryName);

        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                listf(file.getAbsolutePath(), files);
            }
        }
    }

}
