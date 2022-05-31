package homeward.plugin.brewing.utilitie;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

@UtilityClass
@SuppressWarnings("unused")
public class BrewingUtils {
    public boolean isDisabled(YamlConfiguration configuration) {
        List<String> headerList = configuration.options().getHeader();
        return headerList.size() != 0 && headerList.get(0).equalsIgnoreCase("disable");
    }

    public String capitalizeFirst(String str) {
        return str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1).toLowerCase(Locale.ROOT);
    }

    public String getPath(ConfigurationSection section, String current) {
        return getPath(section.getCurrentPath(), current);
    }

    public String getPath(String path, String append) {
        return path + '.' + append;
    }

    public int getIntervalRandom(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }

    public byte[] serializeAsBytes(@NotNull Object object) {
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

    public @Nullable Object deserializeBytes(byte[] bytes) {
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

    public boolean isInteger(String integer) {
        return Double.compare(2147483648.0, Double.parseDouble(integer)) > 0 && Double.compare(-2147483649.0, Double.parseDouble(integer)) < 0;
    }

    public boolean notNumeric(final CharSequence cs) {
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
