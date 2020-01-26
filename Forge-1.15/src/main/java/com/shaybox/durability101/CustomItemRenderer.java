package com.shaybox.durability101;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;

import java.text.DecimalFormat;

public class CustomItemRenderer {
	public static void renderDurability(FontRenderer fr, ItemStack stack, int xPosition, int yPosition) {
		if (!stack.isEmpty()) {
			if (stack.getItem().showDurabilityBar(stack)) {
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
				MatrixStack matrixStack = new MatrixStack();
				matrixStack.translate(0.0D, 0.0D, (double)(500.0F));
				matrixStack.scale(0.5F, 0.5F, 0.5F);
				IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
				fr.renderString(string, x, y, color, true, matrixStack.getLast().getPositionMatrix(), irendertypebuffer$impl, false, 0, 15728880);
				irendertypebuffer$impl.finish();
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
