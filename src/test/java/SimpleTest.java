import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import homeward.plugin.brewing.beans.BarrelInventoryData;
import homeward.plugin.brewing.beans.SelectActionIndex;
import homeward.plugin.brewing.utils.HomewardUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.InventoryView;
import org.junit.jupiter.api.Test;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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
                .setExpectOutPut(4)
                .setActualOutPut(3)
                .setBrewingTime(5);
        byte[] encodeObject = HomewardUtils.serializeAsBytes(inventoryData);

        BarrelInventoryData o = (BarrelInventoryData) HomewardUtils.deserializeBytes(encodeObject);
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

    @Test
    void testSelectActionIndex() {
        SelectActionIndex selectActionIndex = new SelectActionIndex("s", "r", "y");

        SelectActionIndex selectActionIndex1 = new Gson().fromJson(selectActionIndex.toString(), SelectActionIndex.class);
        System.out.println(selectActionIndex1);

        JsonElement jsonElement = JsonParser.parseString(selectActionIndex.toString());
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        System.out.println(asJsonObject.get("substrate").getAsString());
    }

    @Test
    void testRegex() {
        String t = "[\"homeward:sulfur_dioxide\"]";
        System.out.println(t.replaceAll("^\"\"$", ""));
    }

    @Test
    void testRandom() {
        int max = 10;
        int min = 3;

        CountDownLatch countDownLatch = new CountDownLatch(50);

        while (countDownLatch.getCount() >= 0) {
            int i = min + (int) (Math.random() * (max - min + 1));
            System.out.println(i);
            countDownLatch.countDown();
        }
    }

    @Test
    void testInt() {
        String path = "wine.output";
        String separator = ".";
        int i1 = -1, i2;
        while ((i1 = path.indexOf(separator, i2 = i1 + 1)) != -1) {
            final String currentPath = path.substring(i2, i1);
            System.out.println(i2 + "; " + i1);
            System.out.println(currentPath);
        }
    }

    @Test
    void testDouble() {
        String test2 = "0.5";
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        String format = decimalFormat.format(Double.valueOf(test2));
        System.out.println(format);
    }

    @Test
    void testMatches() {
        System.out.println(notNumeric("1-"));

    }

    private boolean notNumeric(final CharSequence cs) {
        if (StringUtils.isEmpty(cs)) {
            return true;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i)) && cs.charAt(i) != '.') {
                return i != 0 || cs.charAt(i) != '-';
            }
        }
        return false;
    }
}
