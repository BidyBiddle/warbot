import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;
import org.pircbotx.*;

public class BotConnect implements Runnable {

    protected HashMap bot;
    protected String host;

    public BotConnect(HashMap bot, String host) {
        this.bot = bot;
        this.host = host;
    }

    public void run() {
        try {
            ((PircBotX) this.bot.get("pircbotx")).connect(host);
        }
        catch(Exception exception) {
            return;        
        }
    }
}
