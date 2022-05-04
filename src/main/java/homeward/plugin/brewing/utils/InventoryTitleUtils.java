package homeward.plugin.brewing.utils;

import homeward.plugin.brewing.enumerates.EnumBase;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A really simple util for changing player inventory title dynamically
 *
 * @author Baioretto
 * @version 1.1.4
 */
public class InventoryTitleUtils {
    public static void changeTitle(Player player, EnumBase enumBase) throws Exception {
        Method playerHandler = player.getClass().getMethod("getHandle");

        // net.minecraft.server.level.EntityPlayer
        Object entityPlayer = playerHandler.invoke(player);

        // net.minecraft.server.level.EntityPlayer public PlayerConnection connection
        Field playerConnectionField = entityPlayer.getClass().getField("b");
        Object playerConnection = playerConnectionField.get(entityPlayer);

        // net.minecraft.world.entity.player.EntityHuman public Container containerMenu
        Field containerField = entityPlayer.getClass().getField("bV");
        // net.minecraft.world.inventory.ContainerPlayer
        Object containerPlayer = containerField.get(entityPlayer);

        // net.minecraft.world.inventory.Container public final int containerId
        Field containerId = containerPlayer.getClass().getField("j");

        int windowId = containerId.getInt(containerPlayer);

        sendPacket(playerConnection, entityPlayer, windowId, (TextComponent) enumBase.getComponent());
    }

    private static void sendPacket(Object playerConnection, Object entityPlayer, int windowId, TextComponent component) throws Exception {
        Class<?> packetPlayOutOpenWindow = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutOpenWindow");
        Class<?> iChatBaseComponentClass = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
        Class<?> containers = Class.forName("net.minecraft.world.inventory.Containers");
        Class<?> craftChatMessage = Class.forName("org.bukkit.craftbukkit.v1_18_R2.util.CraftChatMessage");

        // public net.minecraft.network.protocol.game.PacketPlayOutOpenWindow(int,net.minecraft.world.inventory.Containers,net.minecraft.network.chat.IChatBaseComponent)
        Constructor<?> packetOpenWindow = packetPlayOutOpenWindow.getConstructor(int.class, containers, iChatBaseComponentClass);

        // public static net.minecraft.network.chat.IChatBaseComponent[] org.bukkit.craftbukkit.v1_18_R2.util.CraftChatMessage.fromString(java.lang.String)
        Method craftChatMessageMethod = craftChatMessage.getMethod("fromString", String.class);

        TextColor color = component.style().color();
        String rawTitle = (color == null ? "" : ChatColor.valueOf(color.toString().toUpperCase()) + "") + component.content();

        Object title = ((Object[]) craftChatMessageMethod.invoke(null, rawTitle))[0];
        // public static final Containers<ContainerChest> GENERIC_9x3 = register("generic_9x3", ContainerChest::threeRows);
        Field inventory = containers.getField("c");

        Object packet = packetOpenWindow.newInstance(windowId, inventory.get(entityPlayer), title);

        sendPacket(playerConnection, packet);
    }

    private static void sendPacket(Object playerConnection, Object packet) throws Exception {
        Class<?> packetClass = Class.forName("net.minecraft.network.protocol.Packet");

        Method sendPacketMethod = playerConnection.getClass().getMethod("a", packetClass);
        sendPacketMethod.invoke(playerConnection, packet);
    }
}
