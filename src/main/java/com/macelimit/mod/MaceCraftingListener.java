package com.macelimit.mod;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Listens for crafting events and cancels mace crafting if the server limit is reached.
 * Uses a mixin to intercept the crafting result slot pickup.
 */
public class MaceCraftingListener {

    public static void register() {
        MaceLimitMod.LOGGER.info("[MaceLimitMod] MaceCraftingListener registered.");
        // Actual enforcement is done via the Mixin on CraftingResultSlot
    }

    /**
     * Called from the Mixin before a crafting result is taken.
     * Returns true if crafting should be BLOCKED.
     */
    public static boolean shouldBlockMaceCraft(ItemStack craftResult, ServerPlayerEntity player) {
        if (craftResult == null || craftResult.getItem() != Items.MACE) {
            return false; // Not a mace, allow
        }

        if (player == null) {
            return false;
        }

        MinecraftServer server = player.getServer();
        if (server == null) {
            return false;
        }

        int currentMaces = MaceLimitMod.countTotalMacesOnServer(server);

        if (currentMaces >= MaceLimitMod.MAX_MACES_PER_SERVER) {
            MaceLimitMod.notifyPlayer(player,
                "Cannot craft Mace! Server limit of " + MaceLimitMod.MAX_MACES_PER_SERVER +
                " mace(s) has been reached. Current maces on server: " + currentMaces);
            MaceLimitMod.LOGGER.info("[MaceLimitMod] Blocked mace craft for player: " +
                player.getName().getString() + " (server total: " + currentMaces + ")");
            return true; // Block the craft
        }

        MaceLimitMod.LOGGER.info("[MaceLimitMod] Mace craft allowed for player: " +
            player.getName().getString() + " (server total will be: " + (currentMaces + 1) + ")");
        return false; // Allow
    }
}
