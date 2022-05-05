package homeward.plugin.brewing.events;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * 单独处理事物逻辑，每个世界单独处理 不使用方法
 * 使用事件让整个逻辑更有逻辑
 * <p>
 * 只对世界文件以及世界进行操作 如果需要对GUI对象
 * 进行操作可以直接往下方添加属性 同时getter和setter
 */
public class BrewDataProcessEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private World world;
    private File worldFile;

    public BrewDataProcessEvent(World world, File worldFile) {
        this.world = world;
        this.worldFile = worldFile;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public File getWorldFile() {
        return worldFile;
    }

    public void setWorldFile(File worldFile) {
        this.worldFile = worldFile;
    }

    @Override
    public boolean isCancelled() {
        return true;
    }

    @Override
    public void setCancelled(boolean cancel) {

    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList () {
        return handlers;
    }
}
