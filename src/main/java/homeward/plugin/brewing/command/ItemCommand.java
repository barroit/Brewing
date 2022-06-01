package homeward.plugin.brewing.command;

import homeward.plugin.brewing.Container;
import homeward.plugin.brewing.enumerate.ItemType;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.annotations.Optional;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Command("brewing")
@SuppressWarnings("unused")
public class ItemCommand extends CommandBase {
    @SubCommand("get")
    public void getItem(CommandSender commandSender, ItemType type, String id, @Optional Integer amount) {
        if (!(commandSender instanceof HumanEntity player)) return;

        ItemStack itemStack = Container.ITEM_STACK_MAP.get(type).get(id);
        if (itemStack == null) return;

        int maxStackSize = itemStack.getMaxStackSize();
        if (maxStackSize == -1 || amount == null || amount < 1) itemStack.setAmount(1);
        else itemStack.setAmount(Math.min(maxStackSize, amount));

        player.getInventory().addItem(itemStack);
    }

    @CompleteFor("get")
    public List<String> commandCompletion(List<String> arguments, CommandSender sender) {
        List<String> completionList = new LinkedList<>();

        if (arguments.size() == 1) {
            return Arrays.stream(ItemType.values()).map(ItemType::getString).filter(s -> s.contains(arguments.get(0))).toList();
        }

        ItemType type = ItemType.getItemType(arguments.get(0).toUpperCase(Locale.ROOT));
        if (type == null) return null;
        Map<String, ItemStack> map = Container.ITEM_STACK_MAP.get(type);
        if (map == null) return null;

        if (arguments.size() == 2) {
            return map.keySet().stream().filter(s -> s.contains(arguments.get(1))).toList();
        }

        if (arguments.size() == 3) {
            ItemStack itemStack = map.get(arguments.get(1));
            if (itemStack == null) return null;
            int maxStackSize = itemStack.getMaxStackSize();
            if (maxStackSize > 1) return IntStream.rangeClosed(1, maxStackSize).mapToObj(Integer::toString).filter(s -> s.contains(arguments.get(2))).collect(Collectors.toList());
            return Arrays.stream(new String[]{"1"}).filter(s -> s.contains(arguments.get(2))).toList();
        }

        return null;
    }
}
