package analysis;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.*;
import com.intellij.openapi.wm.ex.WindowManagerEx;
import com.intellij.util.ui.UIUtil;
import persistantStorage.DistribSettingsConfigurableConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ScreenshotPopUp {

    ScreenshotPopUp screenshotPopUp;
    JBPopupFactory jbPopupFactory;
    private JPanel screenshotPanel = new JPanel();
    private JLabel screenshotLabel = new JLabel();
    private JPanel screenshotPanelLg = new JPanel();
    private JLabel screenshotLabelLg = new JLabel();
    private DistribSettingsConfigurableConfig distribConfig;
    private Timer timer = null;
    private FileHandler fileHandler = new FileHandler();
    private JBPopup jbpopup = null;
    private JBPopup jbpopupLg = null;
    private ComponentPopupBuilder smallBuilder = null;
    private ComponentPopupBuilder largeBuilder = null;
    private Image screenshotLg = null;

    public ScreenshotPopUp(JBPopupFactory jbPopupFactory, DistribSettingsConfigurableConfig distribConfig) {
        this.screenshotPopUp = this;
        this.jbPopupFactory = jbPopupFactory;
        this.distribConfig = distribConfig;
    }

    public JLabel getScreenshotLabel() {
        return this.screenshotLabel;
    }

    public void setDistribConfig(DistribSettingsConfigurableConfig distribConfig) { this.distribConfig = distribConfig; }

    public DistribSettingsConfigurableConfig getDistribConfig() { return this.distribConfig; }

    public ComponentPopupBuilder createAndSetupBuilder(Boolean small, JPanel screenshotPanel, JLabel screenshotLabel, String userName, AnActionEvent event) {
        ComponentPopupBuilder builder = this.jbPopupFactory.createComponentPopupBuilder(screenshotLabel, screenshotPanel)
                .setCancelOnClickOutside(true)
                .setNormalWindowLevel(true)
                .setResizable(true)
                .setMayBeParent(true)
                .setRequestFocus(true)
                .setTitle("User: " + userName)
                .setMovable(true)
                .setFocusable(true)
                .setCancelOnWindowDeactivation(true)
                .setCancelKeyEnabled(true)
                .addListener(new JBPopupAdapter() {
                    @Override
                    public void onClosed(LightweightWindowEvent winEvent) {
                        MouseListener[] mouseListeners = getScreenshotLabel().getMouseListeners();
                        for (MouseListener mouseListener : mouseListeners) {
                            getScreenshotLabel().removeMouseListener(mouseListener);
                        }
                        FileEditorManager.getInstance(event.getProject()).getSelectedEditor().getPreferredFocusedComponent()
                                .unregisterKeyboardAction(KeyStroke.getKeyStroke(distribConfig.getOpenShortcut() == null ? "pressed F10" : distribConfig.getOpenShortcut()));
                        FileEditorManager.getInstance(event.getProject()).getSelectedEditor().getPreferredFocusedComponent()
                                .unregisterKeyboardAction(KeyStroke.getKeyStroke(distribConfig.getCloseShortcut() == null ? "shift alt pressed O" : distribConfig.getCloseShortcut()));
                        if(timer != null && timer.isRunning() && jbpopup != null) {
                            jbpopup.dispose();
                            killTimer();
                            try {
                                fileHandler.writeToLogFile("INFO: screenshot closed by user (clicked outside popup)!");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (jbpopup == null) {
                            String results = Messages.showOkCancelDialog("Was that screenshot relevant for you?", "Quick questionnaire", "YES", "NO", null) == 2 ? "NO]" : "YES]";
                            try {
                                fileHandler.writeToLogFile("INFO: screenshot closed by user (clicked outside popup)!");
                                fileHandler.writeToLogFile("RESPONSE: Was that screenshot relevant for you? [" + results);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        if (small) {
            String shortcutToOpen = distribConfig.getOpenShortcut() == null ? "pressed F10" : distribConfig.getOpenShortcut() + " to open | ";
            String shortcutToClose = distribConfig.getCloseShortcut() == null ? "shift alt pressed O" : distribConfig.getCloseShortcut() + " to finish";

            builder.setAdText(shortcutToOpen + shortcutToClose);
        }

        return builder;
    }

    public void killTimer() {
        if (this.timer != null) {
            this.timer.stop();
            this.timer = null;
        }
    }

    public void cancelPopUp() {
        JBPopup jbpopup = this.jbpopup;
        if (jbpopup != null && jbpopup.isVisible()) {
            jbpopup.cancel();
            jbpopup.dispose();
        }
    }

    public void resizePopUp() {
        this.cancelPopUp();
        this.jbpopup = null;

        this.jbpopupLg = this.largeBuilder.createPopup();
        this.jbpopupLg.showInFocusCenter();
    }

    public void createPopUp(AnActionEvent event, Image screenshotSm, Image screenshotLg, String userName) {
        ImageIcon icon = new ImageIcon(screenshotSm);
        ImageIcon iconLg = new ImageIcon(screenshotLg);
        this.screenshotLabel.setIcon(icon);
        this.screenshotLabelLg.setIcon(iconLg);
        this.screenshotLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                killTimer();
                try {
                    fileHandler.writeToLogFile("INFO: screenshot opened!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                resizePopUp();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }
        });

        if (this.smallBuilder == null) {
            this.smallBuilder = createAndSetupBuilder(true, screenshotPanel, screenshotLabel, userName, event);
            this.largeBuilder = createAndSetupBuilder(false, screenshotPanelLg, screenshotLabelLg, userName, event);
        }

        this.jbpopup = smallBuilder.createPopup();


        FileEditorManager.getInstance(event.getProject()).getSelectedEditor().getPreferredFocusedComponent()
                .registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                killTimer();
                jbpopup.cancel();
                try {
                    fileHandler.writeToLogFile("INFO: screenshot closed by user (pressed Hotkey)!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }, KeyStroke.getKeyStroke(distribConfig.getCloseShortcut() == null ? "shift alt pressed O" : distribConfig.getCloseShortcut()), FileEditorManager.getInstance(event.getProject()).getSelectedEditor().getPreferredFocusedComponent().WHEN_FOCUSED);

        FileEditorManager.getInstance(event.getProject()).getSelectedEditor().getPreferredFocusedComponent()
                .registerKeyboardAction(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        killTimer();
                        resizePopUp();
                        try {
                            fileHandler.writeToLogFile("INFO: screenshot opened by user (pressed Hotkey)!");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, KeyStroke.getKeyStroke(distribConfig.getOpenShortcut() == null ? "pressed F10" : distribConfig.getOpenShortcut()), FileEditorManager.getInstance(event.getProject()).getSelectedEditor().getPreferredFocusedComponent().WHEN_FOCUSED);


        Component component = event.getInputEvent().getComponent();
        Rectangle r = WindowManagerEx.getInstanceEx().getScreenBounds();
        ApplicationManager.getApplication().invokeLater(
                new Runnable(){
                    public void run(){
                        try {
                            fileHandler.writeToLogFile("INFO: screenshot showed!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        jbpopup.showInScreenCoordinates(component, new Point(r.width, component.getHeight() + 50));

                        timer = UIUtil.createNamedTimer("Popup timeout", 10000, new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent event) {
                                if(jbpopup != null) {
                                    jbpopup.dispose();
                                    try {
                                        fileHandler.writeToLogFile("INFO: screenshot automatically closed (waiting time expired)!");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();
                    }
                }
        );
    }

}
