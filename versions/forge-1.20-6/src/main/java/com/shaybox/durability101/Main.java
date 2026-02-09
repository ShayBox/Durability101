package com.shaybox.durability101;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.IItemDecorator;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.text.DecimalFormat;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "durability101";

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onRegisterItemDecorations(final RegisterItemDecorationsEvent event) {
            Main.Durability101 durability101 = new Main.Durability101();
            ForgeRegistries.ITEMS.getValues().stream().filter(Item::canBeDepleted).forEach(item -> event.register(item, durability101));
        }

    }

    private static class Durability101 implements IItemDecorator {

        public boolean render(GuiGraphics guiGraphics, Font font, ItemStack itemStack, int xPosition, int yPosition) {
            if (!itemStack.isEmpty() && itemStack.isDamaged()) {
                PoseStack poseStack = guiGraphics.pose();

                // ItemStack information
                int unbreaking = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.UNBREAKING, itemStack);
                int maxDamage = itemStack.getMaxDamage();
                int damage = itemStack.getDamageValue();

                // Create string, position, and color
                String string = format(((maxDamage - damage) * (unbreaking + 1)));
                int stringWidth = font.width(string);
                int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
                int y = (yPosition * 2) + 18;
                int color = itemStack.getItem().getBarColor(itemStack);

                poseStack.pushPose();
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.translate(0.0F, 0.0F, 301.0F);

                // Draw string
                MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                font.drawInBatch(string, x, y, color, true, poseStack.last().pose(), multibuffersource$buffersource, Font.DisplayMode.NORMAL, 0, 15728880, false);
                multibuffersource$buffersource.endBatch();

                poseStack.popPose();
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
