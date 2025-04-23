package com.wonginnovations.arcana.client.gui;

import com.wonginnovations.arcana.ArcanaConfig;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ConfigScreen extends Screen {

    private static final int TITLE_HEIGHT = 8;

    private static final int OPTIONS_LIST_TOP_HEIGHT = 24;
    private static final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
    private static final int OPTIONS_LIST_ITEM_HEIGHT = 25;

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int DONE_BUTTON_TOP_OFFSET = 26;

    private final Screen parentScreen;

    private OptionsList optionsList;

    public ConfigScreen(Screen parentScreen) {
        super(Component.literal("Arcana Config"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();
        this.optionsList = new OptionsList(
                this.minecraft, this.width, this.height,
                OPTIONS_LIST_TOP_HEIGHT,
                this.height - OPTIONS_LIST_BOTTOM_OFFSET,
                OPTIONS_LIST_ITEM_HEIGHT
        );

        this.optionsList.addBig(new OptionInstance<>("config.client.custom_book_width", OptionInstance.noTooltip(),
                (gs, value) -> Component.literal(Component.translatable("config.client.custom_book_width") + ": " + (value < 0 ? Component.translatable("config.client.auto") : String.format("%d", value))),
                new OptionInstance.IntRange(-1, 200),
                ArcanaConfig.CUSTOM_BOOK_WIDTH.get(),
                value -> ArcanaConfig.CUSTOM_BOOK_WIDTH.set(value)
        ));

        this.optionsList.addBig(new OptionInstance<>("config.client.custom_book_height", OptionInstance.noTooltip(),
                (gs, value) -> Component.literal(Component.translatable("config.client.custom_book_height") + ": " + (value < 0 ? Component.translatable("config.client.auto") : String.format("%d", value))),
                new OptionInstance.IntRange(-1, 200),
                ArcanaConfig.CUSTOM_BOOK_HEIGHT.get(),
                value -> ArcanaConfig.CUSTOM_BOOK_HEIGHT.set(value)
        ));

        this.optionsList.addBig(new OptionInstance<>("config.client.book_text_scaling", OptionInstance.noTooltip(),
                (gs, value) -> Component.literal(Component.translatable("config.client.book_text_scaling") + ": " + String.format("%.1f", round(value * 1.5 + .5, 1))),
                OptionInstance.UnitDouble.INSTANCE,
                ArcanaConfig.BOOK_TEXT_SCALING.get(),
                value -> ArcanaConfig.BOOK_TEXT_SCALING.set(round(value * 1.5 + .5, 1))
        ));

        this.optionsList.addBig(new OptionInstance<>("config.client.wand_hud_x", OptionInstance.noTooltip(),
                (gs, value) -> Component.literal(Component.translatable("config.client.wand_hud_x") + ": " + String.format("%d", value)),
                new OptionInstance.IntRange(0, 16),
                ArcanaConfig.WAND_HUD_X.get(),
                value -> ArcanaConfig.WAND_HUD_X.set(value)
        ));

        this.optionsList.addBig(new OptionInstance<>("config.client.wand_hud_y", OptionInstance.noTooltip(),
                (gs, value) -> Component.literal(Component.translatable("config.client.wand_hud_y") + ": " + String.format("%d", value)),
                new OptionInstance.IntRange(0, 16),
                ArcanaConfig.WAND_HUD_Y.get(),
                value -> ArcanaConfig.WAND_HUD_Y.set(value)
        ));

        this.optionsList.addBig(new OptionInstance<>("config.client.wand_hud_scaling", OptionInstance.noTooltip(),
                (gs, value) -> Component.literal(Component.translatable("config.client.wand_hud_scaling") + ": " + String.format("%.1f", round(value * 1.5 + .5, 1))),
                OptionInstance.UnitDouble.INSTANCE,
                ArcanaConfig.WAND_HUD_SCALING.get(),
                value -> ArcanaConfig.WAND_HUD_SCALING.set(round(value * 1.5 + .5, 1))
        ));

        this.optionsList.addBig(OptionInstance.createBoolean("config.client.wand_hud_left",
                ArcanaConfig.WAND_HUD_LEFT.get(),
                value -> ArcanaConfig.WAND_HUD_LEFT.set(value)
        ));

        this.optionsList.addBig(OptionInstance.createBoolean("config.client.wand_hud_top",
                ArcanaConfig.WAND_HUD_TOP.get(),
                value -> ArcanaConfig.WAND_HUD_TOP.set(value)
        ));

        this.optionsList.addBig(OptionInstance.createBoolean("config.client.block_huds_top",
                ArcanaConfig.BLOCK_HUDS_TOP.get(),
                value -> ArcanaConfig.BLOCK_HUDS_TOP.set(value)
        ));

        this.optionsList.addBig(new OptionInstance<>("config.client.jar_animation_speed", OptionInstance.noTooltip(),
                (gs, value) -> Component.literal(Component.translatable("config.client.jar_animation_speed") + ": " + String.format("%.1f", round(value * 1.5 + .5, 1))),
                OptionInstance.UnitDouble.INSTANCE,
                ArcanaConfig.JAR_ANIMATION_SPEED.get(),
                value -> ArcanaConfig.JAR_ANIMATION_SPEED.set(round(value * 1.5 + .5, 1))
        ));

        this.optionsList.addBig(OptionInstance.createBoolean("config.client.no_jar_animation",
                ArcanaConfig.NO_JAR_ANIMATION.get(),
                value -> ArcanaConfig.NO_JAR_ANIMATION.set(value)
        ));

        // Allow above options to be manipulated
        this.addWidget(this.optionsList);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, press -> this.onClose()).bounds((this.width - BUTTON_WIDTH) / 2, this.height - DONE_BUTTON_TOP_OFFSET, BUTTON_WIDTH, BUTTON_HEIGHT).build());

//        this.addRenderableWidget(new Button(
//                (this.width - BUTTON_WIDTH) / 2,
//                this.height - DONE_BUTTON_TOP_OFFSET,
//                BUTTON_WIDTH, BUTTON_HEIGHT,
//                getTextComponentOrEmpty(I18n.get("gui.done")),
//                button -> this.onClose()
//        ));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        this.optionsList.render(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.drawCenteredString(this.font, this.title.getString(), this.width / 2, TITLE_HEIGHT, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parentScreen);
    }

    private static double round(double val, int numDecimals) {
        double rVal = val;
        for (int i = 0; i < numDecimals; i++) rVal *= 10;
        rVal = Math.round(rVal);
        for (int i = 0; i < numDecimals; i++) rVal /= 10;
        return rVal;
    }
}
