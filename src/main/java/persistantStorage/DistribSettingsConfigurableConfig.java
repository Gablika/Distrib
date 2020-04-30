package persistantStorage;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name="DistribSettingsConfigurableConfig",
        storages = {
                @Storage("StoragePathMacros.WORKSPACE_FILE")
        }
)

public class DistribSettingsConfigurableConfig implements PersistentStateComponent<DistribSettingsConfigurableConfig> {

    String slackApiToken;
    String slackUserId;
    String slackApiBotToken;
    String slackApiChannel;
    String jiraApiToken;
    String jiraApiEmail;
    String jiraUrl;
    String openShortcut;
    String closeShortcut;
    String[] primaryKeys;
    String[] altKeys;
    String[] shiftKeys;
    String[] ctrlKeys;

    @Nullable
    @Override
    public DistribSettingsConfigurableConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull DistribSettingsConfigurableConfig state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    @Nullable
    public static DistribSettingsConfigurableConfig getInstance() {
        return ServiceManager.getService(DistribSettingsConfigurableConfig.class);
    }

    public int validateSettingsConfig() {
        int countOfWrongSettingsFields = 0;

        if(getJiraApiEmail() == null || getJiraApiEmail().equals("")) {
            countOfWrongSettingsFields++;
        }

        if(getJiraApiToken() == null || getJiraApiToken().equals("")) {
            countOfWrongSettingsFields++;
        }

        if(getJiraUrl() == null || getJiraUrl().equals("")) {
            countOfWrongSettingsFields++;
        }


        if(getSlackApiToken() == null || getSlackApiToken().equals("")) {
            countOfWrongSettingsFields++;
        }

        if(getSlackUserId() == null || getSlackUserId().equals("")) {
            countOfWrongSettingsFields++;
        }

        if(getSlackApiBotToken() == null || getSlackApiBotToken().equals("")) {
            countOfWrongSettingsFields++;
        }

        if(getSlackApiChannel() == null || getSlackApiChannel().equals("")) {
            countOfWrongSettingsFields++;
        }

        return countOfWrongSettingsFields;
    }

    public String getSlackApiToken() {
        return slackApiToken;
    }

    public void setSlackApiToken(String slackApiToken) {
        this.slackApiToken = slackApiToken;
    }

    public String getSlackUserId() {
        return slackUserId;
    }

    public void setSlackUserId(String slackUserId) {
        this.slackUserId = slackUserId;
    }

    public String getSlackApiBotToken() {
        return slackApiBotToken;
    }

    public void setSlackApiBotToken(String slackApiBotToken) {
        this.slackApiBotToken = slackApiBotToken;
    }

    public String getSlackApiChannel() {
        return slackApiChannel;
    }

    public void setSlackApiChannel(String slackApiChannel) {
        this.slackApiChannel= slackApiChannel;
    }

    public String getJiraApiToken() {
        return jiraApiToken;
    }

    public void setJiraApiToken(String jiraApiToken) {
        this.jiraApiToken = jiraApiToken;
    }

    public String getJiraApiEmail() { return jiraApiEmail; }

    public void setJiraApiEmail(String jiraApiEmail) {
        this.jiraApiEmail = jiraApiEmail;
    }

    public String getJiraUrl() { return jiraUrl; }

    public void setJiraUrl(String jiraUrl) {
        this.jiraUrl = jiraUrl;
    }

    public String getOpenShortcut() { return openShortcut; }

    public void setOpenShortcut(String openShortcut) {
        this.openShortcut = openShortcut;
    }

    public String getCloseShortcut() { return closeShortcut; }

    public void setCloseShortcut(String closeShortcut) {
        this.closeShortcut = closeShortcut;
    }

    public String[] getPrimaryKeys() { return primaryKeys; }

    public void setPrimaryKeys(String[] primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public String[] getSecondaryKeys(int primary) {
        if (primary == 0) {
            return this.altKeys;
        }
        else if (primary == 1) {
            return this.shiftKeys;
        }
        else if (primary == 2) {
            return this.ctrlKeys;
        }
        return null;
    }

    public void setSecondaryKeys(String[] keys, int primary) {
        if (primary == 0) {
            this.altKeys = keys;
        }
        else if (primary == 1) {
            this.shiftKeys = keys;
        }
        else if (primary == 2) {
            this.ctrlKeys = keys;
        }
    }

    public boolean isRuntime() {
        return (this.altKeys == null || this.altKeys.length == 0) ? false : true;
    }

    public boolean areShortcutsEmpty() {
        return (this.getSecondaryKeys(1) == null || this.getSecondaryKeys(1).length <= 0) ? true : false;
    }
}
