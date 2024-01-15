package ir.vcx.util;

import java.util.Random;

public class StringUtil {
    private static final String characters = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String generateHash(int length) {

        StringBuilder sb = new StringBuilder();

        Random random = new Random();

        int index;
        for (int i = 0; i < length; i++) {
            index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
}
