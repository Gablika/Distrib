package connections;

import analysis.ScreenshotPopUp;
import com.ullink.slack.simpleslackapi.ChannelHistoryModule;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.ChannelHistoryModuleFactory;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;
import persistantStorage.DistribProfileMaker;
import persistantStorage.DistribSettingsConfigurableConfig;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import com.intellij.openapi.ui.popup.JBPopup;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;

public class SlackConnection extends Observable{

    private final SlackSession botSession, userSession;
    private static SlackConnection con;
    private SlackMessagePostedListener messagePostedListener, messagePostedEventListener;
    private AnActionEvent actionEvent = null;
    ScreenshotPopUp screenshotPopUp;
    DistribSettingsConfigurableConfig distribConfig;
    DistribProfileMaker distribProfiler;

    public SlackConnection(DistribSettingsConfigurableConfig distribConfig, DistribProfileMaker distribProfiler, ScreenshotPopUp screenshotPopUp){
        this.botSession = SlackSessionFactory.createWebSocketSlackSession(distribConfig.getSlackApiBotToken());
        this.userSession = SlackSessionFactory.createWebSocketSlackSession(distribConfig.getSlackApiToken());
        this.distribConfig = distribConfig;
        this.distribProfiler = distribProfiler;
        this.screenshotPopUp = screenshotPopUp;
        this.con = this;
    };

    public void setActionEvent(AnActionEvent actionEvent) {
        this.actionEvent = actionEvent;
    }

    public AnActionEvent getActionEvent() {
        return this.actionEvent;
    }

    public SlackSession getBotSession() {
        return this.botSession;
    }

    public SlackSession getUserSession() {
        return this.userSession;
    }

    public static SlackConnection getSlackConnection() {
        return con;
    }

    public SlackMessagePostedListener getMessagePostedListener() {
        return this.messagePostedListener;
    }

    public SlackMessagePostedListener getMessagePostedEventListener() {
        return this.messagePostedEventListener;
    }

    public void connectToSlack() {
        try {
            this.botSession.connect();
        } catch (IOException e) {
            System.out.println("Unable to establish connection, exiting.");
        }

        try {
            this.userSession.connect();
        } catch (IOException e) {
            System.out.println("Unable to establish connection, exiting.");
        }
    }

    public void sendImageToAChannel(String keywords, BufferedImage componentImage) throws IOException {
        SlackChannel channel = this.botSession.findChannelByName(this.distribConfig.getSlackApiChannel());

        ByteArrayOutputStream componentImageBAOS = new ByteArrayOutputStream();
        ImageIO.write(componentImage, "png", componentImageBAOS );
        componentImageBAOS.flush();

        this.botSession.sendFile(channel, componentImageBAOS.toByteArray()  , "DistribContextImage", "DistribScreen", keywords);
    }

    public void registeringAListener(SlackSession session)
    {
        this.messagePostedListener = new SlackMessagePostedListener()
        {
            @Override
            public void onEvent(SlackMessagePosted event, SlackSession session)
            {
                String messageContent = event.getMessageContent();
                SlackUser messageSender = event.getSender();
            }
        };

        session.addMessagePostedListener(this.messagePostedListener);
    }

    public void slackMessagePostedEventContent(SlackSession session, AnActionEvent actionEvent)
    {
        this.messagePostedEventListener = new SlackMessagePostedListener()
        {
            @Override
            public void onEvent(SlackMessagePosted event, SlackSession session1)
            {
                SlackChannel channel= session1.findChannelByName(distribConfig.getSlackApiChannel());
                SlackUser sender = session1.findUserById(distribConfig.getSlackUserId());
                JSONObject json = new JSONObject(event.getJsonSource());

                if (channel.getId().equals(event.getChannel().getId()) && !sender.getUserName().equals(returnUser(json.get("text").toString()))) {
                    if(json.has("text") && isRelevantForUser(returnKeywords(json.get("text").toString()))) {
                        if(json.has("files")) {
                            JSONArray jsonFilesArray = json.getJSONArray("files");
                            //showScreenshot((event, jsonFilesArray, "url_private_download");
                            showScreenshot(actionEvent, jsonFilesArray, "thumb_360", "thumb_1024", returnUser(json.get("text").toString()));
                        }
                    }
                }
            }
        };

        session.addMessagePostedListener(this.messagePostedEventListener);
    }

    public void fetchChannelHistory(AnActionEvent event)
    {
        SlackChannel channel = this.botSession.findChannelByName(this.distribConfig.getSlackApiChannel());
        ChannelHistoryModule channelHistoryModule = ChannelHistoryModuleFactory.createChannelHistoryModule(this.userSession);
        List<SlackMessagePosted> messages = channelHistoryModule.fetchHistoryOfChannel(channel.getId(),1);
        SlackUser user = this.botSession.findUserById(this.distribConfig.getSlackUserId());

        Iterator<SlackMessagePosted> iterator = messages.iterator();
        while(iterator.hasNext()) {
            JSONObject json = new JSONObject(iterator.next().getJsonSource());
            if(json.has("text")  && isRelevantForUser(returnKeywords(json.get("text").toString())) && !user.getUserName().equals(returnUser(json.get("text").toString()))) {
                if(json.has("files")) {
                    JSONArray jsonFilesArray = json.getJSONArray("files");
                    //showScreenshot((event, jsonFilesArray, "url_private_download");
                    showScreenshot(actionEvent, jsonFilesArray, "thumb_360", "thumb_1024", returnUser(json.get("text").toString()));
                }
            }
        }
    }

    public void showScreenshot(AnActionEvent event, JSONArray jsonFilesArray, String imgSizeSm, String imgSizeLg, String userName) {
        Image screenShotSm = null;
        Image screenShotLg = null;
        for (int i = 0; i < jsonFilesArray.length(); i++) {
            JSONObject jsonFilesObject = jsonFilesArray.getJSONObject(i);
            if (jsonFilesObject.has(imgSizeSm)) {
                if (jsonFilesObject.has(imgSizeLg)) {
                    screenShotLg = downloadScreenshot(jsonFilesObject.get(imgSizeLg).toString());
                }
                else
                    screenShotLg = null;
                screenShotSm = downloadScreenshot(jsonFilesObject.get(imgSizeSm).toString());
                this.screenshotPopUp.createPopUp(event, screenShotSm, screenShotLg, userName);
            }
        }
    }

    public Image downloadScreenshot(String urlToDownload) {
        Image image = null;
        try {
            URL url = new URL(urlToDownload);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + distribConfig.getSlackApiToken());
            image = ImageIO.read(connection.getInputStream());
        }
        catch (IOException e) {
        }

        return image;
    }

    public String returnUser(String message) {
        return message.split(":")[0];
    }

    public String[] returnKeywords(String message) {
        String keywords = message.split(":")[1];
        return keywords.split(";");
    }

    public boolean isRelevantForUser(String[] keywords) {
        for(int i=0; i<keywords.length; i++) {
            if(distribProfiler.searchKeywordinKeywords(keywords[i])) {
                return true;
            }
        }

        return false;
    }
}
