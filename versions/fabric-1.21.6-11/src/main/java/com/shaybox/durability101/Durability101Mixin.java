package com.shaybox.durability101;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;

@Mixin(DrawContext.class)
public abstract class Durability101Mixin {

    @Inject(at = @At("RETURN"), method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
    public void drawItemInSlot(TextRenderer textRenderer, ItemStack stack, int x, int y, String stackCountText, CallbackInfo ci) {
        renderDurability101((DrawContext)(Object)this, textRenderer, stack, x, y);
    }

    @Unique
    public void renderDurability101(DrawContext context, TextRenderer renderer, ItemStack itemStack, int xPosition, int yPosition) {
        final ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null || itemStack.isEmpty() || !itemStack.isDamaged()) return;

        // ItemStack Information
        DynamicRegistryManager registryManager = world.getRegistryManager();
        RegistryEntry.Reference<Enchantment> unbreaking = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING);
        int unbreakingLevel = EnchantmentHelper.getLevel(unbreaking, itemStack);
        int maxDamage = itemStack.getMaxDamage();
        int damage = itemStack.getDamage();

        // Create string, position, and color
        String string = format(((maxDamage - damage) * (unbreakingLevel + 1)));
        int stringWidth = renderer.getWidth(string);
        int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
        int y = (yPosition * 2) + 18;
        int color = itemStack.getItem().getItemBarColor(itemStack) | 0xFF000000;

        var matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.scale(0.5F, 0.5F);

        // Draw string
        context.drawText(renderer, string, x, y, color, true);

        matrices.popMatrix();
    }

    @Unique
    public String format(float number) {
        DecimalFormat decimalFormat = new DecimalFormat("0.#");

        if (number >= 1000000000) return decimalFormat.format(number / 1000000000) + "b";
        if (number >= 1000000) return decimalFormat.format(number / 1000000) + "m";
        if (number >= 1000) return decimalFormat.format(number / 1000) + "k";

        return Float.toString(number).replaceAll("\\.?0*$", "");
    }
}