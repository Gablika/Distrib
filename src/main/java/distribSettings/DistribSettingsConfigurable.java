package distribSettings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import persistantStorage.DistribSettingsConfigurableConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DistribSettingsConfigurable implements SearchableConfigurable {

    DistribSettingsConfigurableGUI gui;
    private final DistribSettingsConfigurableConfig distribConfig;

    public DistribSettingsConfigurable() {
        this.distribConfig = DistribSettingsConfigurableConfig.getInstance();
    }

    @Override
    public String getDisplayName() {
        return "Distrib Plugin Settings";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "preference.DistribSettingsConfigurable";
    }

    @NotNull
    @Override
    public String getId() {
        return "preference.DistribSettingsConfigurable";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        gui = new DistribSettingsConfigurableGUI();
        gui.loadSettingsState();
        return gui.getRootElement();
    }

    @Override
    public boolean isModified() {
        if (!gui.getSlackApiChannel().getText().isEmpty() && !gui.getSlackUserId().getText().isEmpty() && !gui.getSlackApiToken().getText().isEmpty() && !gui.getSlackApiBotToken().getText().isEmpty() && !gui.getJiraApiToken().getText().isEmpty() && !gui.getJiraApiEmail().getText().isEmpty() && !gui.getJiraUrl().getText().isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        storeUserSettings();
    }

    @Override
    public void reset() {
        distribConfig.setJiraApiEmail("");
        distribConfig.setJiraApiToken("");
        distribConfig.setJiraUrl("");
        distribConfig.setSlackApiToken("");
        distribConfig.setSlackUserId("");
        distribConfig.setSlackApiBotToken("");
        distribConfig.setSlackApiChannel("");
    }

    @Override
    public void disposeUIResources() {
        storeUserSettings();
        gui = null;
    }

    public void storeUserSettings() {
        distribConfig.setJiraApiEmail(gui.getJiraApiEmail().getText());
        distribConfig.setJiraApiToken(gui.getJiraApiToken().getText());
        distribConfig.setJiraUrl(gui.getJiraUrl().getText());
        distribConfig.setSlackApiToken(gui.getSlackApiToken().getText());
        distribConfig.setSlackUserId(gui.getSlackUserId().getText());
        distribConfig.setSlackApiBotToken(gui.getSlackApiBotToken().getText());
        distribConfig.setSlackApiChannel(gui.getSlackApiChannel().getText());
        if (gui.getFirstKeysOpen().getSelectedItem() != null) {
            distribConfig.setOpenShortcut(gui.getFirstKeysOpen().getSelectedItem().toString().toLowerCase() + " pressed " + gui.getSecondaryKeysOpen().getSelectedItem().toString());
        }
        if (gui.getFirstKeysClose().getSelectedItem() != null) {
            distribConfig.setCloseShortcut(gui.getFirstKeysClose().getSelectedItem().toString().toLowerCase() + " pressed " + gui.getSecondaryKeysClose().getSelectedItem().toString());
        }
    }
}
