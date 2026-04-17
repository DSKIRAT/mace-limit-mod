package com.macelimit.mixin;

import com.macelimit.mod.MaceCraftingListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin that intercepts when a player picks up an item from the crafting result slot.
 * If the item is a Mace and the server limit is reached, the pickup is cancelled.
 */
@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {

    @Inject(
        method = "onTakeItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        // Only run on server side
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        if (MaceCraftingListener.shouldBlockMaceCraft(stack, serverPlayer)) {
            // Cancel the pickup — ingredients stay in the crafting grid
            ci.cancel();
        }
    }
}
