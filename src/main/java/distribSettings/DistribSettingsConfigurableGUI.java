package distribSettings;

import analysis.KeyboardShortcuts;
import persistantStorage.DistribSettingsConfigurableConfig;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DistribSettingsConfigurableGUI {
    private JTextField slackToken;
    private JTextField getSlackUserId;
    private JTextField slackBotToken;
    private JTextField slackChannel;
    private JTextField jiraToken;
    private JTextField jiraEmail;
    private JTextField jiraUrl;
    private JPanel panelElement;
    private JTextField slackUserId;
    private JComboBox firstKeysOpen;
    private JComboBox secondaryKeysOpen;
    private JComboBox secondaryKeysClose;
    private JComboBox firstKeysClose;
    private final DistribSettingsConfigurableConfig distribConfig;
    private final KeyboardShortcuts keyboardShortcuts = new KeyboardShortcuts();

    public DistribSettingsConfigurableGUI() {
        this.distribConfig = DistribSettingsConfigurableConfig.getInstance();
    }

    public JPanel getRootElement() {
        return panelElement;
    }
    public JTextField getSlackApiToken() { return slackToken; }
    public JTextField getSlackUserId() { return slackUserId; }
    public JTextField getSlackApiBotToken() { return slackBotToken; }
    public JTextField getSlackApiChannel() { return slackChannel; }
    public JTextField getJiraApiToken() { return jiraToken; }
    public JTextField getJiraApiEmail() { return jiraEmail; }
    public JTextField getJiraUrl() { return jiraUrl; }
    public JComboBox getFirstKeysOpen() { return firstKeysOpen; }
    public JComboBox getSecondaryKeysOpen() { return secondaryKeysOpen; }
    public JComboBox getFirstKeysClose() { return firstKeysClose; }
    public JComboBox getSecondaryKeysClose() { return secondaryKeysClose; }

    public void loadSettingsState() {
        if(distribConfig.getJiraApiEmail() != null) {
            this.jiraEmail.setText(distribConfig.getJiraApiEmail());
        }

        if(distribConfig.getJiraApiToken() != null) {
            this.jiraToken.setText(distribConfig.getJiraApiToken());
        }

        if(distribConfig.getJiraUrl() != null) {
            this.jiraUrl.setText(distribConfig.getJiraUrl());
        }

        if(distribConfig.getSlackApiToken() != null) {
            this.slackToken.setText(distribConfig.getSlackApiToken());
        }

        if(distribConfig.getSlackUserId() != null) {
            this.slackUserId.setText(distribConfig.getSlackUserId());
        }

        if(distribConfig.getSlackApiBotToken() != null) {
            this.slackBotToken.setText(distribConfig.getSlackApiBotToken());
        }

        if(distribConfig.getSlackApiChannel() != null) {
            this.slackChannel.setText(distribConfig.getSlackApiChannel());
        }

        if (distribConfig.areShortcutsEmpty()) {
            distribConfig.setSecondaryKeys(keyboardShortcuts.getUnusedKeyboardShortcuts("alt"), 0);
            distribConfig.setSecondaryKeys(keyboardShortcuts.getUnusedKeyboardShortcuts("shift"), 1);
            distribConfig.setSecondaryKeys(keyboardShortcuts.getUnusedKeyboardShortcuts("ctrl"), 2);
        }

        this.firstKeysOpen.addItem("ALT");
        this.firstKeysOpen.addItem("SHIFT");
        this.firstKeysOpen.addItem("CTRL");

        this.firstKeysClose.addItem("ALT");
        this.firstKeysClose.addItem("SHIFT");
        this.firstKeysClose.addItem("CTRL");

        if(distribConfig.getOpenShortcut() != null) {
            String[] items = distribConfig.getOpenShortcut().split(" pressed ");
            this.firstKeysOpen.setSelectedItem(items[0].toUpperCase());
            addItemsToComboBox(this.secondaryKeysOpen, distribConfig.getSecondaryKeys(this.firstKeysOpen.getSelectedIndex()));
            this.secondaryKeysOpen.setSelectedItem(items[1]);
        }
        else {
            addItemsToComboBox(this.secondaryKeysOpen, distribConfig.getSecondaryKeys(this.firstKeysOpen.getSelectedIndex()));
            this.firstKeysOpen.setSelectedIndex(0);
        }

        if(distribConfig.getCloseShortcut() != null) {
            String[] items = distribConfig.getCloseShortcut().split(" pressed ");
            this.firstKeysClose.setSelectedItem(items[0].toUpperCase());
            addItemsToComboBox(this.secondaryKeysClose, distribConfig.getSecondaryKeys(this.firstKeysClose.getSelectedIndex()));
            this.secondaryKeysClose.setSelectedItem(items[1]);
        }
        else {
            addItemsToComboBox(this.secondaryKeysClose, distribConfig.getSecondaryKeys(this.firstKeysClose.getSelectedIndex()));
            this.firstKeysClose.setSelectedIndex(0);
        }

        this.firstKeysOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addItemsToComboBox(secondaryKeysOpen, distribConfig.getSecondaryKeys(firstKeysOpen.getSelectedIndex()));
            }
        });
        this.firstKeysClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addItemsToComboBox(secondaryKeysClose, distribConfig.getSecondaryKeys(firstKeysClose.getSelectedIndex()));
            }
        });
    }

    public void addItemsToComboBox(JComboBox comboBox, String[] items) {
        comboBox.removeAllItems();
        for (int i = 0; i < items.length; i++) {
            comboBox.addItem(items[i]);
        }
    }
}
