package com.macelimit.mod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaceLimitMod implements ModInitializer {

    public static final String MOD_ID = "macelimit";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Maximum number of maces allowed per server (across all players)
    public static final int MAX_MACES_PER_SERVER = 1;

    @Override
    public void onInitialize() {
        LOGGER.info("[MaceLimitMod] Mace Limit Mod initialized! Max maces allowed: " + MAX_MACES_PER_SERVER);

        // Register the craft event listener
        MaceCraftingListener.register();
    }

    /**
     * Counts total maces across all online players' inventories and the server.
     */
    public static int countTotalMacesOnServer(MinecraftServer server) {
        int count = 0;
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            for (ItemStack stack : player.getInventory().main) {
                if (stack.getItem() == Items.MACE) {
                    count += stack.getCount();
                }
            }
            // Check offhand
            for (ItemStack stack : player.getInventory().offHand) {
                if (stack.getItem() == Items.MACE) {
                    count += stack.getCount();
                }
            }
        }
        return count;
    }

    public static void notifyPlayer(ServerPlayerEntity player, String message) {
        player.sendMessage(
            Text.literal("[MaceLimitMod] ").formatted(Formatting.GOLD)
                .append(Text.literal(message).formatted(Formatting.RED)),
            false
        );
    }
}
