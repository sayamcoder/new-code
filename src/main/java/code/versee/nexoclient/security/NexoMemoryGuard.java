package code.versee.nexoclient.security;

import java.util.Arrays;

public class NexoMemoryGuard {

    // Safely wipes sensitive bytes from RAM memory
    public static void wipe(byte[] sensitiveData) {
        if (sensitiveData != null) {
            Arrays.fill(sensitiveData, (byte) 0);
        }
    }

    public static void wipe(char[] sensitiveData) {
        if (sensitiveData != null) {
            Arrays.fill(sensitiveData, '\0');
        }
    }
}