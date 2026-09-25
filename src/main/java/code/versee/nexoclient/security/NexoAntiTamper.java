package code.versee.nexoclient.security;

import net.fabricmc.loader.api.FabricLoader;
import java.lang.management.ManagementFactory;
import java.util.List;

public class NexoAntiTamper {

    public static void checkEnvironment() {
        // Dev Environment (IntelliJ / Gradle) mein safe development ke liye bypass
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            return;
        }

        List<String> args = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String arg : args) {
            String lower = arg.toLowerCase();

            // IntelliJ internal agent ko allow karein
            if (lower.contains("idea_rt.jar")) {
                continue;
            }

            // Real Hackers ke agents / debuggers ko block karein
            if (lower.contains("-javaagent") ||
                    lower.contains("-xdebug") ||
                    lower.contains("-agentlib") ||
                    lower.contains("-xrunjdwp") ||
                    lower.contains("frida") ||
                    lower.contains("cheatengine")) {

                System.err.println("[NexoSecurity] Illegal Environment Detected! Terminating.");
                Runtime.getRuntime().halt(1337);
            }
        }
    }
}