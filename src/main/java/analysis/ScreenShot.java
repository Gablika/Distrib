package analysis;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.ui.UIUtil;
import connections.SlackConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ScreenShot {

    public ScreenShot() {}

    public void takeScreenshotAndSendIt(AnActionEvent event, String keywords) {

        JFrame frame= WindowManager.getInstance().getFrame(event.getProject());

        ApplicationManager.getApplication().invokeLater(
                new Runnable(){
                    public void run(){

                        BufferedImage componentImage = UIUtil.createImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g = componentImage.createGraphics();
                        frame.paint(g);

                        try {
                            SlackConnection.getSlackConnection().sendImageToAChannel(keywords, componentImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }
}