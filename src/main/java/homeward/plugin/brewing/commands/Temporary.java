package homeward.plugin.brewing.commands;

import dev.lone.itemsadder.api.CustomStack;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static homeward.plugin.brewing.utils.HomewardUtils.deserializeBytes;
import static homeward.plugin.brewing.utils.HomewardUtils.serializeAsBytes;

@Command("testSerialize")
public class Temporary extends CommandBase {

    @Default
    public void testSerialize(CommandSender commandSender) {
        Player player = (Player) commandSender;

        ItemStack vanilla = new ItemStack(Material.TROPICAL_FISH);
        ItemStack grapeInHand = CustomStack.byItemStack(player.getInventory().getItemInMainHand()).getItemStack();
        ItemStack grape = CustomStack.getInstance("homeward:grape").getItemStack();

        System.out.println("grapeInHand: \n" + grapeInHand);
        System.out.println("grape: \n" + grape);

        // region
        System.out.println(grapeInHand.hashCode()); // -363005669
        System.out.println(grape.hashCode()); // -363005669
        System.out.println(grape.hashCode() == grapeInHand.hashCode()); // true
        System.out.println("equals: " + grapeInHand.equals(grape)); // false
        System.out.println("Amount: " + (grapeInHand.getAmount() == grape.getAmount())); // true
        System.out.println("isSimilar: " + grapeInHand.isSimilar(grape)); // false
        System.out.println("Type: " + (grapeInHand.getType() == grape.getType())); // true
        System.out.println("Durability: " + (grapeInHand.getDurability() == grape.getDurability())); // true
        System.out.println("hasItemMeta: " + (grapeInHand.hasItemMeta() == grape.hasItemMeta())); // true
        System.out.println("ItemMeta: " + Bukkit.getItemFactory().equals(grapeInHand.getItemMeta(), grapeInHand.getItemMeta())); // true
        // endregion

        System.out.println(isSimilar(grapeInHand, grape)); // true

        ItemStack itemStack = new ItemStack(grape.getType());
        itemStack.setItemMeta(grape.getItemMeta());
        deserializeBytes(serializeAsBytes(itemStack));

        deserializeBytes(serializeAsBytes(vanilla)); // working fine
        deserializeBytes(serializeAsBytes(grapeInHand)); // working fine
        deserializeBytes(serializeAsBytes(grape)); // exception
    }

    public boolean isSimilar(ItemStack stack1, ItemStack stack2) {
        if (stack2 == null) {
            return false;
        }
        if (stack2 == stack1) {
            return true;
        }
        Material comparisonType =
                (stack1.getType().isLegacy()) ? Bukkit.getUnsafe().fromLegacy(stack1.getData(), true) : stack1.getType();

        return comparisonType == stack2.getType() &&
                stack1.getDurability() == stack2.getDurability() &&
                stack1.hasItemMeta() == stack2.hasItemMeta() &&
                (stack1.hasItemMeta() ? Bukkit.getItemFactory().equals(stack1.getItemMeta(), stack2.getItemMeta()) : true);
    }
}
