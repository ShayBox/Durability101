package com.shaybox.durability101;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;

import java.text.DecimalFormat;

public class CustomItemRenderer {
	public static void renderDurability(FontRenderer fr, ItemStack stack, int xPosition, int yPosition) {
		if (!stack.isEmpty()) {
			if (stack.getItem().showDurabilityBar(stack)) {
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.disableAlphaTest();
				GlStateManager.disableBlend();
				GlStateManager.scalef(0.5F, 0.5F, 0.5F);

				// Unbreaking
				int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);

				// Damage
				float damage = (float) stack.getDamage();
				float maxDamage = (float) stack.getMaxDamage();

				// String
				String string = format(((maxDamage - damage) * (unbreaking + 1)));
				int stringWidth = fr.getStringWidth(string);

				// Position
				int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
				int y = (yPosition * 2) + 18;

				// Color
				int color = stack.getItem().getRGBDurabilityForDisplay(stack);

				// Draw
				fr.drawStringWithShadow(string, x, y, color);

				GlStateManager.scalef(2.0F, 2.0F, 2.0F);
				GlStateManager.enableBlend();
				GlStateManager.enableAlphaTest();
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}
		}
	}

	private static String format(float number) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");

		if (number >= 1000000000) return decimalFormat.format(number / 1000000000) + "b";
		if (number >= 1000000) return decimalFormat.format(number / 1000000) + "m";
		if (number >= 1000) return decimalFormat.format(number / 1000) + "k";

		return Float.toString(number).replaceAll("\\.?0*$", "");
	}
}
