import analysis.FileHandler;
import analysis.KeyboardShortcuts;
import analysis.ScreenShot;
import analysis.ScreenshotPopUp;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.ullink.slack.simpleslackapi.SlackUser;
import connections.JiraConnection;
import connections.SlackConnection;
import persistantStorage.DistribProfileMaker;
import persistantStorage.DistribSettingsConfigurableConfig;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class PluginController {

    private Timer profilerTimer, screenshotTimer;
    private DistribSettingsConfigurableConfig distribConfig;
    private DistribProfileMaker distribProfiler;
    private ScreenshotPopUp screenshotPopUp;
    private SlackConnection slackConnection;
    private FileHandler fileHandler;
    KeyboardShortcuts keyboardShortcuts = new KeyboardShortcuts();

    public PluginController(DistribSettingsConfigurableConfig distribConfig, DistribProfileMaker distribProfiler, ScreenshotPopUp screenshotPopUp, FileHandler fileHandler) {
        this.distribConfig = distribConfig;
        this.fileHandler = fileHandler;
        this.distribProfiler = distribProfiler;
        this.screenshotPopUp = screenshotPopUp;
        this.slackConnection = new SlackConnection(distribConfig, distribProfiler, screenshotPopUp);
    }

    public void initialize(Project project, AnActionEvent event) throws Exception {
        JiraConnection jiraConnection = new JiraConnection(distribConfig);
        final ScreenShot screenShot = new ScreenShot();
        if (!this.slackConnection.getBotSession().isConnected()) {
            this.slackConnection.connectToSlack();
        }
        this.slackConnection.setActionEvent(event);

        //this.slackConnection.fetchChannelHistory(event);
        this.slackConnection.registeringAListener(slackConnection.getUserSession());
        this.slackConnection.slackMessagePostedEventContent(slackConnection.getUserSession(), event);

        this.profilerTimer = new Timer();
        this.profilerTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                String activeFileName = getActiveFileName(event);
                if(!distribProfiler.searchKeywordinKeywords(activeFileName)) {
                    assertCountOfKeywords();
                    distribProfiler.addKeywords(activeFileName);
                }

                Iterator<String> iterator = getIssuesSumarries(jiraConnection);
                while(iterator.hasNext()) {
                    String keyword = iterator.next();
                    if(!distribProfiler.searchKeywordinKeywords(keyword)) {
                        assertCountOfKeywords();
                        distribProfiler.addKeywords(keyword);
                    }
                }
            }
        }, 0, 120000);

        this.screenshotTimer = new Timer();
        this.screenshotTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                screenShot.takeScreenshotAndSendIt(event, composeKeywords(jiraConnection, event));
            }
        }, 0, 900000);
    }

    public void stop() {
        this.profilerTimer.cancel();
        this.profilerTimer.purge();
        this.screenshotTimer.cancel();
        this.screenshotTimer.purge();
        this.slackConnection.getUserSession().removeMessagePostedListener(this.slackConnection.getMessagePostedListener());
        this.slackConnection.getUserSession().removeMessagePostedListener(this.slackConnection.getMessagePostedEventListener());

        List<String> keywords = distribProfiler.getKeywords();
        Iterator<String> iterator = keywords.iterator();

        while(iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    public String[] splitStringByDot(String string){
        String[] words = string.split("\\.");
        return words;
    }

    public String millisChangeFormat(String timeInMillis, String pattern){
        Long timeStamp = Long.parseLong(timeInMillis);
        DateFormat patternFormat = new SimpleDateFormat(pattern);

        return patternFormat.format(new Date(timeStamp));
    }

    public String composeKeywords(JiraConnection jiraConnection, AnActionEvent event) {
        SlackUser sender = this.slackConnection.getBotSession().findUserById(distribConfig.getSlackUserId());
        String keywords = sender.getUserName() + ":";

        keywords += getActiveFileName(event) + ";";

        Iterator<String> iterator = getIssuesSumarries(jiraConnection);

        while(iterator.hasNext()) {
            keywords += iterator.next() + ";";
        }

        return keywords;
    }

    public String getActiveFileName(AnActionEvent event) {
        if(FileEditorManager.getInstance(event.getProject()).getSelectedEditor() == null) {
            return "";
        }
        return FileEditorManager.getInstance(event.getProject()).getSelectedEditor().getFile().getName();
    }

    public Iterator<String> getIssuesSumarries(JiraConnection jiraConnection) {
        List<String> issuesTitles = jiraConnection.getSummariesOfAllIssues();
        return issuesTitles.iterator();
    }

    public void assertCountOfKeywords() {
        if(this.distribProfiler.getKeywordsCount() >= 100) {
            this.distribProfiler.removeKeywords(this.distribProfiler.getKeywordsCount()-1);
        }
    }
}
