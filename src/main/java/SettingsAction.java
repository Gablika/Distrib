import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.roots.ui.configuration.ProjectSettingsService;
import com.intellij.openapi.options.ShowSettingsUtil;


public class SettingsAction extends AnAction {
    private CommonDataKeys DataKeys;

    public SettingsAction() {
        super("Settings");
    }

    public void actionPerformed(AnActionEvent event) {
        ShowSettingsUtil s = ShowSettingsUtil.getInstance();
        s.showSettingsDialog(event.getProject(), "Distrib Plugin Settings");
    }
}
