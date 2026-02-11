package com.shaybox.durability101;

import org.joml.Matrix3x2fStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.client.IItemDecorator;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;

import java.text.DecimalFormat;

@Mod(Durability101.MODID)
public class Durability101 {

    public static final String MODID = "durability101";

    public Durability101() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            RegisterItemDecorationsEvent.BUS.addListener(this::onRegisterItemDecorations);
        }
    }

    private void onRegisterItemDecorations(final RegisterItemDecorationsEvent event) {
        Durability101.DurabilityDecorator durability101 = new Durability101.DurabilityDecorator();
        ForgeRegistries.ITEMS.getValues().stream()
            .filter(item -> item.getDefaultInstance().isDamageableItem())
            .forEach(item -> event.register(item, durability101));
    }

    private static class DurabilityDecorator implements IItemDecorator {

        public boolean render(GuiGraphics guiGraphics, Font font, ItemStack itemStack, int xPosition, int yPosition) {
            if (!itemStack.isEmpty() && itemStack.isDamaged()) {
                Matrix3x2fStack poseStack = guiGraphics.pose();

                // ItemStack information
                int unbreaking = 0;
                var level = Minecraft.getInstance().level;
                if (level != null) {
                    var enchantmentRegistry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
                    unbreaking = EnchantmentHelper.getItemEnchantmentLevel(
                        enchantmentRegistry.getOrThrow(Enchantments.UNBREAKING),
                        itemStack
                    );
                }
                int maxDamage = itemStack.getMaxDamage();
                int damage = itemStack.getDamageValue();

                // Create string, position, and color
                String string = format(((maxDamage - damage) * (unbreaking + 1)));
                int stringWidth = font.width(string);
                int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
                int y = (yPosition * 2) + 18;
                int color = itemStack.getItem().getBarColor(itemStack) | 0xFF000000;

                poseStack.pushMatrix();
                poseStack.scale(0.5F, 0.5F);

                // Draw string
                guiGraphics.drawString(font, string, x, y, color, true);

                poseStack.popMatrix();
            }

            return true;
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
