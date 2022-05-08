import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import homeward.plugin.brewing.beans.BarrelInventoryData;
import homeward.plugin.brewing.enumerates.ComponentEnum;
import homeward.plugin.brewing.utils.CommonUtils;
import homeward.plugin.brewing.utils.GuiUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.inventory.InventoryView;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.*;
import java.util.List;

public class SimpleTest {
    @Test
    void testStreamMapMethod() {
        String[] configname = {"test1", "test2"};
        Arrays.stream(configname).map(v -> v + ".yml").toList().forEach(System.out::println);
    }

    @Test
    void testRegularExpression() {
        assert !"test.yml.yml.a".matches(".*.(yml|yaml)$");
    }

    @Test
    void testRegularExpressionV2() {
        String[] configname = {"plugin.yml", "plugin-a", "plugin_a", "plugin.ttt"};
        Arrays.stream(configname).toList().forEach(v -> {
            System.out.println(v.matches("([\\w-]*)\\.yml|yaml$"));
            System.out.println(v.replaceAll("([\\w-]*)\\.yml|yaml$", "$1") + ".yml");
        });
    }

    @Test
    void testRegularExpressionV3() {
        String s = "plugins\\Br\\e\\wing";
        System.out.println(s.replaceAll("\\\\", "//"));
    }

    @Test
    void testContinue() {
        List<Integer> no = new ArrayList<>(Arrays.asList(2, 11, 20));

        for (int i = 0; i < 27; i++) {
            if (no.contains(i)) continue;
            System.out.println(i);
        }
    }

    @Test
    void testCompare() {
        Set<Integer> rawSlots = new HashSet<>(Arrays.asList(32, 33, 34, 36, 38, 39, 40, 41, 42, 43, 45, 46, 47, 52, 53, 27, 28, 29, 62, 30, 31));
        // [27, 28, 29, 30, 31, 32, 33, 34, 36, 38, 39, 40, 41, 42, 43, 45, 46, 47, 52, 53, 62]

        Integer min = Collections.min(rawSlots);

        if (min == InventoryView.OUTSIDE || min == -1) {
            return;
        }

        Integer max = Collections.max(rawSlots);

        System.out.println(max);
    }

    @Test
    void testComponent() {
        TextComponent text = Component.text("\uF808" + "\uF001", NamedTextColor.WHITE);
        System.out.println(text.content());
        System.out.println(text.style().color());
        System.out.println(ChatColor.translateAlternateColorCodes('&', "&atest"));
    }

    @Test
    void testMap() {
        Map<String, List<Integer>> whoIsViewing = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        whoIsViewing.put("test", list);
        List<Integer> integerList = whoIsViewing.get("test");
        integerList.add(3);
        List<Integer> integerList1 = whoIsViewing.get("test");
        System.out.println(integerList1);
    }

    @Test
    void testEncode() {
        BarrelInventoryData inventoryData = new BarrelInventoryData()
                .setSubstrate(null)
                .setRestriction(null)
                .setYeast(null)
                .setBrewingType("dark_vine")
                .setOutPutItems("old_vines")
                .setExpectOutPut(4)
                .setActualOutPut(3)
                .setBrewingTime(5);
        byte[] encodeObject = CommonUtils.encodeBukkitObject(inventoryData);

        BarrelInventoryData o = (BarrelInventoryData) CommonUtils.decodeBukkitObject(encodeObject);
        if (o == null) return;
        System.out.println(o.getBrewingType());
    }

    @Test
    void testGsonParser() {
        String string = "Location{world=CraftWorld{name=world},x=1557.0,y=65.0,z=118.0,pitch=0.0,yaw=0.0}";
        String jsonString = string.replaceAll(".*(\\{)(.+?)},?", "$1$2,").replaceAll("=", ":");
        JsonElement jsonElement = JsonParser.parseString(jsonString);
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        JsonElement name = asJsonObject.get("name");
        System.out.println(name.getAsString());
    }
}
