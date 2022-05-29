package homeward.plugin.brewing.commands;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import dev.triumphteam.gui.guis.BaseGui;
import homeward.plugin.brewing.loaders.ItemPropertiesLoader;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Command("mock")
@Alias("m")
public class MockRecipesPreviewTest extends CommandBase {
    private final Map<Player, BaseGui> playerGuiInstanceMap;

    public MockRecipesPreviewTest() {
        playerGuiInstanceMap = new LinkedHashMap<>();
    }

    @Default
    public void defaultAction(CommandSender commandSender) {
        Player player = (Player) commandSender;

        ItemPropertiesLoader.getInstance().loadItems();

        // if (playerGuiInstanceMap.containsKey(player)) {
        //     playerGuiInstanceMap.get(player).open(player);
        // } else {
        //     BaseGui gui = RecipesPreviewGui.getInstance(6, 21).getGui();
        //     gui.open(player);
        //     playerGuiInstanceMap.put(player, gui);
        // }
    }

    @SubCommand("nbt")
    public void testNBT(CommandSender commandSender) {
        Player player = (Player) commandSender;
        TextComponent text1 = Component.text(" ", NamedTextColor.DARK_AQUA);
        TextComponent text2 = Component.text("Tier: ", NamedTextColor.GRAY);
        TextComponent text3 = Component.text("COMMON", NamedTextColor.GRAY, TextDecoration.BOLD);
        TextComponent lore = Component.text().append(text1, text2, text3).decoration(TextDecoration.ITALIC, false).build();

        ItemStack itemStack = new ItemStack(Material.APPLE);
        itemStack.editMeta(ItemMeta.class, meta -> {
            meta.lore(List.of(lore));
        });

        player.getInventory().addItem(itemStack);

        // System.out.println(itemStack.getItemMeta().lore());

        NBTContainer nbtContainer = NBTItem.convertItemtoNBT(itemStack);
        System.out.println(nbtContainer.getCompound("tag"));

        player.setFoodLevel(player.getFoodLevel() + 5);
        player.setSaturation(player.getSaturation() + 5);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0, false, false, false));
    }
}
