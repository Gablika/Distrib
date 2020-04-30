import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import persistantStorage.DistribProfileMaker;
import org.jetbrains.annotations.NotNull;

public class ClearProfileAction extends AnAction {

    private DistribProfileMaker distribProfiler;

    public ClearProfileAction() {
        super("Clear profile");
        this.distribProfiler = DistribProfileMaker.getInstance();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        this.distribProfiler.removeAllKeywords();
        Messages.showMessageDialog("Info: Local profile storage cleared.", "Local profile storage cleaning", null);
    }
}
