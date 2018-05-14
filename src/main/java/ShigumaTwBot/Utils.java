package ShigumaTwBot;

import ShigumaTwBot.Main;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static final String OS_NAME = System.getProperty("os.name").toLowerCase();

    public static void log(String msg) {
        String dateFormat = new SimpleDateFormat("Y-M-d HH:mm:ss ").format(new Date());
        Main.logWriter.println(dateFormat + msg);
    }

    public static void Println(String string) {
        System.out.println(string);
    }

    public static boolean isWindows() {
        return OS_NAME.startsWith("windows");
    }
}
