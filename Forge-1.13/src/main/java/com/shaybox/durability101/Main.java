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

    public static void renderDurability101(FontRenderer font, ItemStack stack, int xPosition, int yPosition) {
        if (!stack.isEmpty() && stack.isDamaged()) {
            // ItemStack information
            int damage = stack.getDamage();
            int maxDamage = stack.getMaxDamage();
            int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);

            // Create string, position, and color
            String string = format(((maxDamage - damage) * (unbreaking + 1)));
            int stringWidth = font.getStringWidth(string);
            int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
            int y = (yPosition * 2) + 18;
            int color = stack.getItem().getRGBDurabilityForDisplay(stack);

            // Draw string
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableAlphaTest();
            GlStateManager.disableBlend();
            GlStateManager.scalef(0.5F, 0.5F, 0.5F);
            font.drawStringWithShadow(string, x, y, color);
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
