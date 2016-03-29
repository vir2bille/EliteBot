package Bot;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

class Command {

    private List<String> mAliases;

    Command(String... aliases) {
        mAliases = Arrays.asList(aliases);
    }

    boolean containedIn(String message) {
        return getQuery(message) != null;
    }

    boolean equalsMessage(String message) {
        for (String cmd : mAliases) {
            if(cmd.equalsIgnoreCase(message))
                return true;
        }
        return false;
    }

    String getQuery(String message) {
        String formattedMsg = message.trim().toLowerCase();
        for (String cmd : mAliases) {
            if (formattedMsg.contains(cmd)) {
                return formattedMsg.split(cmd)[1].trim();
            }
        }
        return null;
    }

    String getRandomAlias() {
        return mAliases.get(new Random().nextInt(mAliases.size()));
    }
}
