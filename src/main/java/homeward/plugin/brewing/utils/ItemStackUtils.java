package homeward.plugin.brewing.utils;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author caizii
 */
public class ItemStackUtils {

    /**
     * 编译OBJ序列化
     */
    public static String encodeObject(Object object) {
        String encodeObject = null;

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream bukkitStream = null;

        try {
            bukkitStream = new BukkitObjectOutputStream(byteStream);
            bukkitStream.writeObject(object);
            bukkitStream.flush();

            byte[] serialized = byteStream.toByteArray();
            encodeObject = Base64.getEncoder().encodeToString(serialized);
            // do not close stream here ×
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // close stream here √
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

    /**
     * 编译OBJ反序列化
     */
    public static Object decodeObject(String stringObject) {
        Object decodedObject = null;

        byte[] toBytes = Base64.getDecoder().decode(stringObject);

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
