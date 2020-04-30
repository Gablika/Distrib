package analysis;

import  com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;

import java.util.Collection;
import java.util.Iterator;

public class KeyboardShortcuts {

    public String[] getUnusedKeyboardShortcuts(String primaryKey) {
        KeymapManager keyMapManager = KeymapManager.getInstance();
        final Keymap activeKeymap = keyMapManager.getActiveKeymap();
        String[] unusedShortcuts = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
                , "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O"
                , "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
        };

        Collection collection = activeKeymap.getActionIdList();
        Iterator itr = collection.iterator();

        while (itr.hasNext()) {
            Shortcut[] sh = activeKeymap.getShortcuts((String) itr.next());
            if (sh.length == 1) {
                for (int i = 0; i < sh.length; i++) {
                    String[] keyList = sh[i].toString().split(" ");
                    if (keyList.length == 3) {
                         for (int j = 0; j < keyList.length; j++) {
                            if (keyList[j].contains(primaryKey)) {
                                for (int k = 0; k < unusedShortcuts.length; k++) {
                                    if (unusedShortcuts[k].contains(keyList[2].replace("]",""))) {
                                        unusedShortcuts = removeItem(unusedShortcuts, k);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return unusedShortcuts;
    }

    public String[] removeItem(String[] array, int index) {
        String[] newArray = new String[array.length-1];
        int i = 0;
        for (int k = 0; k < array.length; k++) {
            if (k != index)
                newArray[i++] = array[k];
        }
        return newArray;
    }
}