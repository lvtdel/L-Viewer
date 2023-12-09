package NET.util;

import java.io.FileOutputStream;
import java.io.IOException;

public class FileSupport {
    public static void saveToFile(String filePath, byte[] data) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
