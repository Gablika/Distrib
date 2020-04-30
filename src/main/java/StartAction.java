import analysis.FileHandler;
import analysis.ScreenshotPopUp;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import persistantStorage.DistribProfileMaker;
import persistantStorage.DistribSettingsConfigurableConfig;

import java.awt.*;
import java.io.IOException;

public class StartAction extends AnAction {
    private CommonDataKeys DataKeys;
    PluginController pluginController ;
    private final DistribSettingsConfigurableConfig distribConfig;
    private final DistribProfileMaker distribProfiler;
    private final ScreenshotPopUp screenshotPopUp;
    private  final FileHandler fileHandler;

    public StartAction() {
        super("Start");
        this.distribConfig = DistribSettingsConfigurableConfig.getInstance();
        this.distribProfiler = DistribProfileMaker.getInstance();
        this.fileHandler = new FileHandler();
        this.screenshotPopUp = new ScreenshotPopUp(JBPopupFactory.getInstance(), this.distribConfig);
        this.pluginController = new PluginController(this.distribConfig, this.distribProfiler, this.screenshotPopUp, this.fileHandler);
    }

    public void actionPerformed(AnActionEvent event) {
        Presentation presentation = event.getPresentation();

        if (presentation.getText().equals("Stop")) {
            presentation.setText("Start");
            pluginController.stop();
            try {
                fileHandler.writeToLogFile("INFO: plugin stopped!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            if(distribConfig.validateSettingsConfig() > 0) {
                Messages.showMessageDialog("Warning: Add Jira/Slack API parameters in File -> Settings -> Tools -> Distrib App Settings", "Invalid Jira/Slack API parameters", null);
                return;
            }
            presentation.setText("Stop");
            try {
                pluginController.initialize(event.getProject(), event);
                fileHandler.writeToLogFile("INFO: plugin started!");
            } catch (AWTException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
