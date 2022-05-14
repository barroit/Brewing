package homeward.plugin.brewing.utils;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Base64;

public class HomewardUtils {
    public static String getPath(ConfigurationSection section, String current) {
        return section.getCurrentPath() + '.' + current;
    }

    public static String getPath(String path, String append) {
        return path + '.' + append;
    }

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

    public static boolean notInteger(String integer) {
        return Double.compare(2147483648.0, Double.parseDouble(integer)) <= 0 || Double.compare(-2147483649.0, Double.parseDouble(integer)) >= 0;
    }

    public static boolean notNumeric(final CharSequence cs) {
        if (StringUtils.isEmpty(cs)) {
            return true;
        }
        final int sz = cs.length();
        boolean hasPeriod = false;
        for (int i = 0; i < sz; i++) {
            char c = cs.charAt(i);
            if (!Character.isDigit(cs.charAt(i))) {
                if (!hasPeriod && c == '.') {
                    hasPeriod = true;
                    continue;
                } else if (i == 0) {
                    if (c == '-') {
                        continue;
                    } else if (c == '+') {
                        continue;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
