package com.shaybox.durability101.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Shadow
    public abstract MatrixStack getMatrices();

    @Inject(at = @At("RETURN"), method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
    public void drawItemInSlot(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        renderDurability101(this.getMatrices(), MinecraftClient.getInstance().textRenderer, stack, x, y);
    }

    public void renderDurability101(MatrixStack matrices, TextRenderer renderer, ItemStack stack, int xPosition, int yPosition) {
        if (!stack.isEmpty() && stack.isDamaged()) {
            // ItemStack information
            int damage = stack.getDamage();
            int maxDamage = stack.getMaxDamage();
            int unbreaking = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack);

            // Create string, position, and color
            String string = format(((maxDamage - damage) * (unbreaking + 1)));
            int stringWidth = renderer.getWidth(string);
            int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
            int y = (yPosition * 2) + 18;
            int color = stack.getItem().getItemBarColor(stack);

            // Draw string
            matrices.push();
            matrices.scale(0.5F, 0.5F, 0.5F);
            matrices.translate(0.0D, 0.0D, 750.0F);
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            renderer.draw(string, x, y, color, true, matrices.peek().getPositionMatrix(), immediate, net.minecraft.client.font.TextRenderer.TextLayerType.NORMAL, 0, 15728880, false);
            immediate.draw();
            matrices.pop();
        }
    }

    public String format(float number) {
        DecimalFormat decimalFormat = new DecimalFormat("0.#");

        if (number >= 1000000000) return decimalFormat.format(number / 1000000000) + "b";
        if (number >= 1000000) return decimalFormat.format(number / 1000000) + "m";
        if (number >= 1000) return decimalFormat.format(number / 1000) + "k";

        return Float.toString(number).replaceAll("\\.?0*$", "");
    }
}
