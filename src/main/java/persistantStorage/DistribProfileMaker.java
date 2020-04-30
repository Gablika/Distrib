package persistantStorage;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(
        name="persistantStorage.DistribProfileMaker",
        storages = {
                @Storage("protest.xml")
        },
        reloadable = true
)


    public class DistribProfileMaker implements PersistentStateComponent<DistribProfileMaker> {

    public List<String> keywords;

    public DistribProfileMaker getState() {
        return this;
    }

    public void loadState(DistribProfileMaker state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    @Nullable
    public static DistribProfileMaker getInstance() {
        return ServiceManager.getService(DistribProfileMaker.class);
   }

    public List<String> getKeywords() {
        return keywords;
    }

    public int getKeywordsCount() {
        if(this.keywords == null) {
            return 0;
        }
        return keywords.size();
    }

    public boolean addKeywords(String keyword) {
        if(this.keywords == null) {
            this.keywords = new ArrayList<>();
        }
        return this.keywords.add(keyword);
    }

    public String removeKeywords(int index) {
        if(this.keywords == null) {
            return "";
        }
        return this.keywords.remove(index);
    }

    public void removeAllKeywords() {
        if(this.keywords == null) {
            return;
        }
        this.keywords.clear();
    }

    public boolean searchKeywordinKeywords(String keyword) {
        if(this.keywords == null) {
            return false;
        }
        return this.keywords.contains(keyword);
    }

}
