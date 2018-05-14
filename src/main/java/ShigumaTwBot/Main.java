package ShigumaTwBot;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.components.Gpu;
import org.fusesource.jansi.AnsiConsole;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.ansi;

public class Main {


    @SuppressWarnings("SpellCheckingInspection")
    private static final String[] savageList = new String[]{

    };

    //Properties
    public boolean isTweetable;
    public boolean enableNc;
    public String defaultUsername;

    public String tmpName = "";
    public String user;

    public List<String> luckyItems;
    public List<String> loglist;
    public List<String> readedlog;

    public PrintWriter luckyItemWriter;
    public static PrintWriter logWriter;

    public String cpuName = "";
    public String gpuName = "";

    public AsyncExecutor asyncExecutor = new AsyncExecutor();

    static boolean isRunning = true;
    public AsyncTwitter asyncTwitter;

    Main() {
        AnsiConsole.systemInstall();
        this.loadMapCalls();

        if (Utils.isWindows()) {
            Components components = JSensors.get.components();
            List<Cpu> CPUs = components.cpus;
            for (Cpu cpu : CPUs) {
                System.out.println("Found CPU component: " + cpu.name);
                cpuName = cpu.name;
            }

            List<Gpu> GPUs = components.gpus;
            for (Gpu gpu : GPUs) {
                System.out.println("Found CPU component: " + gpu.name);
                gpuName = gpu.name;
            }
        } else {
            System.out.println("This program is now not running on windows");
        }

        String ConsumerKey;
        String ConsumerSecret;
        String AccessToken;
        String AccessTokenSecret;

        Properties properties = new Properties();//コンフィグ
        try {
            properties.load(new InputStreamReader(new FileInputStream("Twitterbot.properties"), "UTF-8"));
            isTweetable = Boolean.valueOf(properties.getProperty("tweetable"));
            enableNc = Boolean.valueOf(properties.getProperty("StartUpNcEnable"));
            defaultUsername = properties.getProperty("DefaultUserName");
            ConsumerKey = properties.getProperty("OAuthConsumerKey");
            ConsumerSecret = properties.getProperty("OAuthConsumerSecret");
            AccessToken = properties.getProperty("OAuthAccessToken");
            AccessTokenSecret = properties.getProperty("OAuthAccessTokenSecret");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String ON = ansi().fg(GREEN).a("ON").reset().toString();
        String OFF = ansi().fg(RED).a("OFF").reset().toString();

        System.out.println("OS: " + Utils.OS_NAME);

        //三項演算子
        System.out.println("TweetableMode:[" + (isTweetable ? ON : OFF) + "]");
        System.out.println("NameChangeMode:[" + (enableNc ? ON : OFF) + "]");


        Date time = new Date();
        System.out.println("[" + time + "]" + "Starting stream...");

        @SuppressWarnings("SpellCheckingInspection")
        Configuration conf = new ConfigurationBuilder()

                .setOAuthConsumerKey(ConsumerKey)
                .setOAuthConsumerSecret(ConsumerSecret)
                .setOAuthAccessToken(AccessToken)
                .setOAuthAccessTokenSecret(AccessTokenSecret)

                .build();

        asyncTwitter = new AsyncTwitterFactory(conf).getInstance();
        asyncTwitter.addListener(new TwitterAdapter() {
            @Override
            public void onException(TwitterException e, TwitterMethod m) {
                e.printStackTrace();
            }
        });

        if (Utils.isWindows()) {
            ScheduledExecutorService serviceExecutor = Executors.newSingleThreadScheduledExecutor();
            ScheduledFuture future = serviceExecutor.scheduleWithFixedDelay(new StartUp(this), 0, 1, TimeUnit.MINUTES);
            //new Mouse().start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                isRunning = false;
                asyncExecutor.finish();
                future.cancel(false);
                serviceExecutor.shutdown();
            }));
        }

        TwitterStream twitterStream = new TwitterStreamFactory(conf).getInstance();
        twitterStream.addListener(new StreamListener(this));
        twitterStream.user();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("shutdown......")));
    }


    public void sendReply(Status status, String text) {
        asyncTwitter.updateStatus(new StatusUpdate("@" + status.getUser().getScreenName() + " " + text).inReplyToStatusId(status.getId()));
    }

    public void sendMediaReply(Status status, String text, String pathname) {
        asyncTwitter.updateStatus(new StatusUpdate("@" + status.getUser().getScreenName() + " " + text).media(new File(pathname)).inReplyToStatusId(status.getId()));
    }

    public void loadMapCalls() {
        List<String> tmp = new ArrayList<>();
        try {
            tmp = Files.readAllLines(Paths.get("luckyItem.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.luckyItems = tmp;

        luckyItemWriter = getWriter("luckyItem.txt");
        List<String> blist = new ArrayList<>();
        try {
            blist = Files.readAllLines(Paths.get("botLog.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.loglist = blist;

        logWriter = this.getWriter("botLog.txt");
    }

    private PrintWriter getWriter(String fileName) {
        try {
            return new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(fileName), true), StandardCharsets.UTF_8), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;//when error occurred


    }

}






































//////////Super Ultra Great Delicious Wonderful Special Thanks////////@tomocrafter///////@tomocrafter2//////////