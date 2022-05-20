package homeward.plugin.brewing.loaders;


import homeward.plugin.brewing.Main;

import java.io.File;
import java.util.Arrays;

public class ItemPropertiesLoader {
    public void loadItems() {
        File items = new File(Main.getInstance().getDataFolder(), "items");
        System.out.println(Arrays.toString(items.listFiles()));
    }

    private volatile static ItemPropertiesLoader instance;

    private ItemPropertiesLoader() {}

    public static ItemPropertiesLoader getInstance() {
        if (null == instance) {
            synchronized (ItemPropertiesLoader.class) {
                if (null == instance) {
                    instance = new ItemPropertiesLoader();
                }
            }
        }
        return instance;
    }
}
