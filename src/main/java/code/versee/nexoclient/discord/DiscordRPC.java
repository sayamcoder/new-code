package code.versee.nexoclient.discord;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class DiscordRPC {
    // Discord Client ID (Aap apni Discord Developer Portal wali ID bhi daal sakte hain)
    private static final String CLIENT_ID = "1537887823166963882";
    private static RandomAccessFile ipcPipe;
    private static boolean running = false;

    public static void start() {
        if (running) return;
        running = true;

        new Thread(() -> {
            try {
                // Windows Discord IPC Named Pipe
                ipcPipe = new RandomAccessFile("\\\\.\\pipe\\discord-ipc-0", "rw");

                // 1. Handshake send karein
                String handshake = "{\"v\": 1, \"client_id\": \"" + CLIENT_ID + "\"}";
                sendPacket(0, handshake);

                // Response read karein
                readResponse();

                // 2. Activity / Status set karein
                long startTime = System.currentTimeMillis() / 1000L;
                updatePresence("Playing NexoClient", "Main Menu", startTime);

            } catch (Exception e) {
                System.out.println("[NexoClient] Discord open nahi mila ya IPC connect nahi hua: " + e.getMessage());
            }
        }, "NexoClient-DiscordRPC").start();
    }

    public static void updatePresence(String state, String details, long startTimestamp) {
        if (ipcPipe == null) return;
        try {
            String payload = "{"
                    + "\"cmd\": \"SET_ACTIVITY\","
                    + "\"args\": {"
                    + "  \"pid\": " + ProcessHandle.current().pid() + ","
                    + "  \"activity\": {"
                    + "    \"state\": \"" + state + "\","
                    + "    \"details\": \"" + details + "\","
                    + "    \"timestamps\": {\"start\": " + startTimestamp + "},"
                    + "    \"assets\": {"
                    + "      \"large_image\": \"logo\","
                    + "      \"large_text\": \"NexoClient\""
                    + "    }"
                    + "  }"
                    + "},"
                    + "\"nonce\": \"" + System.currentTimeMillis() + "\""
                    + "}";

            sendPacket(1, payload);
        } catch (Exception ignored) {}
    }

    private static void sendPacket(int opcode, String json) throws Exception {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(8 + bytes.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(opcode);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        ipcPipe.write(buffer.array());
    }

    private static void readResponse() {
        try {
            byte[] header = new byte[8];
            ipcPipe.readFully(header);
            ByteBuffer buf = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN);
            buf.getInt(); // opcode
            int length = buf.getInt();
            byte[] body = new byte[length];
            ipcPipe.readFully(body);
        } catch (Exception ignored) {}
    }
}