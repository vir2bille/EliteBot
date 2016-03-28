package Utils;

import java.io.*;
import java.util.*;

public class PropertyManager {

    private static final String PROPERTIES_RESOURCE = "default.properties";
    private static final File PROPERTIES_FILE = new File("config.properties");
    private static final Object threadLock = new Object();

    private static PropertyManager instance;
    private Map<String, String> mAttrs;

    private PropertyManager() {
    }

    public static PropertyManager getInstance() throws IOException {
        if (instance == null) {
            synchronized (threadLock) {
                instance = new PropertyManager();
                instance.loadProperties();
            }
        }
        return instance;
    }

    private String getProperty(String key) {
        return mAttrs.get(key);
    }

    public String getBotToken() {
        return getProperty("BOT_TOKEN");
    }

    public String getBotUsername() {
        return getProperty("BOT_USERNAME");
    }

    public String getLogDir() {
        return getProperty("LOG_DIR");
    }

    public String getCacheDir() {
        return getProperty("CACHE_DIR");
    }

    public List<String> getAdmins() {
        final String SEPARATOR = ":";
        return Arrays.asList(getProperty("ADMINS").split(SEPARATOR));
    }

    private void loadProperties() throws IOException {

        if (!PROPERTIES_FILE.exists()) {
            exportResourceOutsideJar();
        }

        InputStream is = new FileInputStream(PROPERTIES_FILE);

        Properties properties = new Properties();
        properties.load(is);

        mAttrs = new HashMap<>(properties.size());
        for (String key : properties.stringPropertyNames()) {
            mAttrs.put(key, properties.getProperty(key));
        }
    }

    private void exportResourceOutsideJar() throws IOException {
        InputStream stream;
        OutputStream resStreamOut;
        stream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_RESOURCE);

        if (stream == null) {
            throw new IOException("Cannot load resource \"" + PROPERTIES_RESOURCE + "\" from Jar.");
        }

        int readBytes;
        byte[] buffer = new byte[4096];
        resStreamOut = new FileOutputStream(PROPERTIES_FILE);
        while ((readBytes = stream.read(buffer)) > 0) {
            resStreamOut.write(buffer, 0, readBytes);
        }
        stream.close();
        resStreamOut.close();
    }

}
