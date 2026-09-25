package code.versee.nexoclient.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class SafeVeinminerMod {
    public static boolean enabled = true;
    private static final int MAX_BLOCKS = 24; // Prevents lag

    public static void mineVein(Level level, BlockPos startPos, BlockState state) {
        if (!enabled || level == null) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Block targetBlock = state.getBlock();
        String blockName = targetBlock.getDescriptionId().toLowerCase();

        // Safe Check: Only mine ORE blocks
        if (!blockName.contains("ore") && !blockName.contains("ancient_debris")) {
            return;
        }

        ItemStack tool = mc.player.getMainHandItem();

        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(startPos);
        visited.add(startPos);

        int mined = 0;

        while (!queue.isEmpty() && mined < MAX_BLOCKS) {
            BlockPos current = queue.poll();

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos neighbor = current.offset(dx, dy, dz);

                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            BlockState nState = level.getBlockState(neighbor);

                            if (nState.getBlock() == targetBlock) {
                                // SAFE DURABILITY CHECK: Stops before breaking tool!
                                if (tool.isDamageableItem() && (tool.getMaxDamage() - tool.getDamageValue()) <= 2) {
                                    return;
                                }

                                if (mc.gameMode != null) {
                                    mc.gameMode.destroyBlock(neighbor);
                                    mined++;
                                    queue.add(neighbor);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}