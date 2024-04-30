package com.shaybox.durability101.mixin;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;

@Mixin(RenderItem.class)
public class RenderItemMixin {

    @Inject(at = @At("RETURN"), method = "renderItemOverlayIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
    public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text, CallbackInfo ci) {
        renderDurability101(fr, stack, xPosition, yPosition, text);
    }

    public void renderDurability101(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text) {
        if (!stack.isEmpty() && stack.isItemDamaged()) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
//            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);

            // ItemStack information
            int damage = stack.getItemDamage();
            int maxDamage = stack.getMaxDamage();
            int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);

            // Create string, position, and color
            String string = format(((maxDamage - damage) * (unbreaking + 1)));
            int stringWidth = fr.getStringWidth(string);
            int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
            int y = (yPosition * 2) + 18;
            int color = stack.getItem().getRGBDurabilityForDisplay(stack);

            // Draw string
            fr.drawStringWithShadow(string, x, y, color);

            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
//            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
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
