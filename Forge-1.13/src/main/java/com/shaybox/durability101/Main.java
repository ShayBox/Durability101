package com.shaybox.durability101;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

import java.text.DecimalFormat;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "durability101";

    public static void renderDurability101(FontRenderer fontRenderer, ItemStack itemStack, int xPosition, int yPosition) {
        if (!itemStack.isEmpty() && itemStack.isDamaged()) {
            // ItemStack information
            int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, itemStack);
            int maxDamage = itemStack.getMaxDamage();
            int damage = itemStack.getDamage();

            // Create string, position, and color
            String string = format(((maxDamage - damage) * (unbreaking + 1)));
            int stringWidth = fontRenderer.getStringWidth(string);
            int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
            int y = (yPosition * 2) + 18;
            int color = itemStack.getItem().getRGBDurabilityForDisplay(itemStack);

            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableAlphaTest();
            GlStateManager.disableBlend();
            GlStateManager.scalef(0.5F, 0.5F, 0.5F);

            // Draw string
            fontRenderer.drawStringWithShadow(string, x, y, color);

            GlStateManager.scalef(2.0F, 2.0F, 2.0F);
            GlStateManager.enableBlend();
            GlStateManager.enableAlphaTest();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
        }
    }

    public static String format(float number) {
        DecimalFormat decimalFormat = new DecimalFormat("0.#");

        if (number >= 1000000000) return decimalFormat.format(number / 1000000000) + "b";
        if (number >= 1000000) return decimalFormat.format(number / 1000000) + "m";
        if (number >= 1000) return decimalFormat.format(number / 1000) + "k";

        return Float.toString(number).replaceAll("\\.?0*$", "");
    }

}
