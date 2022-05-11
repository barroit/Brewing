package homeward.plugin.brewing.utils;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Base64;

public class HomewardUtils {
    public static int getIntervalRandom(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }

    public static byte[] serializeAsBytes(@NotNull Object object) {
        byte[] encodeObject = null;

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream bukkitStream = null;

        try {
            bukkitStream = new BukkitObjectOutputStream(byteStream);
            bukkitStream.writeObject(object);
            bukkitStream.flush();

            byte[] serialized = byteStream.toByteArray();
            encodeObject = Base64.getEncoder().encode(serialized);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bukkitStream != null) {
                try {
                    bukkitStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                byteStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return encodeObject;
    }

    public static @Nullable Object deserializeBytes(byte[] bytes) {
        if (bytes == null) return null;
        Object decodedObject = null;

        byte[] toBytes = Base64.getDecoder().decode(bytes);

        ByteArrayInputStream byteStream = new ByteArrayInputStream(toBytes);
        BukkitObjectInputStream bukkitStream = null;

        try {
            bukkitStream = new BukkitObjectInputStream(byteStream);
            decodedObject = bukkitStream.readObject();
            byteStream.close();
            bukkitStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                byteStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bukkitStream != null) {
                try {
                    bukkitStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return decodedObject;
    }
}
