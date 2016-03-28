package Bot;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Command {

    private List<String> mAliases;

    public Command(String... aliases) {
        mAliases = Arrays.asList(aliases);
    }

    public boolean containedIn(String message) {
        return getQuery(message) != null;
    }

    public boolean equalsMessage(String message) {
        for (String cmd : mAliases) {
            if(cmd.equalsIgnoreCase(message))
                return true;
        }
        return false;
    }

    public String getQuery(String message) {
        String formattedMsg = message.trim().toLowerCase();
        for (String cmd : mAliases) {
            if (formattedMsg.contains(cmd)) {
                return formattedMsg.split(cmd)[1].trim();
            }
        }
        return null;
    }

    public String getRandomAlias() {
        return mAliases.get(new Random().nextInt(mAliases.size()));
    }
}
