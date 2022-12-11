package com.tamaized.voidfog.modmenu;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.common.client.gui.GameGui;
import com.minelittlepony.common.client.gui.element.AbstractSlider;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.client.gui.element.Label;
import com.minelittlepony.common.client.gui.element.Slider;
import com.minelittlepony.common.client.gui.element.Toggle;
import com.tamaized.voidfog.Settings;
import com.tamaized.voidfog.VoidFog;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

class OptionsScreen extends GameGui {
    private static final Text TITLE = Text.translatable("menu.voidfog.title");
    private static final Text PARTICLES_MIN = Text.translatable("menu.voidfog.particles.min");
    private static final Text PARTICLES_MAX = Text.translatable("menu.voidfog.particles.max");
    private static final Text PARTICLES_DEF = Text.translatable("menu.voidfog.particles.default");

    public OptionsScreen(@Nullable Screen parent) {
        super(TITLE, parent);
    }

    @Override
    public void init() {
        int left =  width / 2 - 100;
        int row = height / 4 - 25;

        Settings config = VoidFog.config;

        addButton(new Label(width / 2, 25)).setCentered().getStyle()
                .setText(getTitle());

        addButton(new Slider(left, row += 10, 0, 10000, config.voidParticleDensity))
            .onChange(config::setParticleDensity)
            .setTextFormat(this::formatValue);

        addButton(new Slider(left, row += 30, 0, 383, config.maxFogHeight))
            .onChange(config::setFogHeight)
            .setTextFormat(this::formatFogHeight);

        addButton(new Toggle(left, row += 30, config.enabled))
            .onChange(enabled -> config.enabled = enabled)
            .getStyle()
                .setText("menu.voidfog.enabled");

        addButton(new Toggle(left, row += 25, config.prettyFog))
                .onChange(enabled -> config.prettyFog = enabled)
                .getStyle()
                    .setTooltip("menu.voidfog.prettyFog.tooltip")
                    .setText("menu.voidfog.prettyFog");

        addButton(new Toggle(left, row += 25, config.scaleWithDifficulty))
            .onChange(enabled -> config.scaleWithDifficulty = enabled)
            .getStyle()
                .setText("menu.voidfog.scale");

        addButton(new Toggle(left, row += 25, config.disableInCreative))
            .onChange(enabled -> config.disableInCreative = enabled)
            .getStyle()
                .setText("menu.voidfog.creative");

        addButton(new Toggle(left, row += 25, config.respectTorches))
            .onChange(enabled -> config.respectTorches = enabled)
            .getStyle()
                .setText("menu.voidfog.torches");

        addButton(new Toggle(left, row += 25, config.imABigBoi))
            .onChange(enabled -> config.imABigBoi = enabled)
            .getStyle()
                .setTooltip("menu.voidfog.bigboi.tooltip")
                .setText("menu.voidfog.bigboi");

        addButton(new Button(left, row += 35)
            .onClick(sender -> finish()))
            .getStyle()
                .setText("gui.done");
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public void close() {
        VoidFog.config.save();
        super.close();
    }

    private Text formatValue(AbstractSlider<Float> sender) {
        float value = sender.getValue();

        if (value <= 0) {
            return PARTICLES_MIN;
        }

        if (value >= 10000) {
            return PARTICLES_MAX;
        }

        if (value == 1000) {
            return PARTICLES_DEF;
        }

        return Text.translatable("menu.voidfog.particles", (int)Math.floor(value));
    }

    private Text formatFogHeight(AbstractSlider<Float> sender) {
        return Text.translatable("menu.voidfog.fogheight", (int)(double)(sender.getValue()) - 64);
    }
}
