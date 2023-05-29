package com.tamaized.voidfog;

import com.tamaized.voidfog.api.Voidable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InsanityEngine {
    private int timeToNextSound = 0;
    private int insanityBuildUp;

    private final Sound[] events = new Sound[] {
            Sound.of(SoundEvents.ENTITY_POLAR_BEAR_WARNING),
            Sound.of(SoundEvents.AMBIENT_CAVE),
            Sound.of(SoundEvents.ENTITY_CREEPER_PRIMED),
            Sound.of(SoundEvents.ENTITY_ZOMBIE_DESTROY_EGG),
            Sound.of(SoundEvents.BLOCK_CHEST_CLOSE),
            Sound.of(SoundEvents.UI_TOAST_IN),
            Sound.of(SoundEvents.BLOCK_COMPOSTER_READY),
            Sound.of(SoundEvents.BLOCK_METAL_STEP),
            Sound.of(SoundEvents.UI_BUTTON_CLICK),
            Sound.of(SoundEvents.ENTITY_ZOGLIN_ANGRY),
            Sound.of(SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON),
            Sound.of(SoundEvents.ENTITY_ZOMBIE_STEP)
    };

    public void update(World world, Entity entity, Voidable dimension) {

        if (!dimension.hasInsanity(BlockPos.ofFloored(entity.getEyePos()), world)) {
            return;
        }

        float brightness = FogRenderer.getLight(entity);

        if (brightness > 0.3F) {
            insanityBuildUp = 0;
            return;
        }

        double y = entity.getEyeY();
        int rarity = getRarity(y, world);

        insanityBuildUp += y < 0 ? -y : 1;

        if (insanityBuildUp > 100) {
            timeToNextSound -= insanityBuildUp / 60;
        }

        if (timeToNextSound-- > 0) {
            return;
        }

        timeToNextSound = 20 + rarity + world.random.nextInt(
                Math.max(250, 120 + rarity)
        );

        doAScary(world, entity.getBlockPos());
    }

    private int getRarity(double y, World world) {
        // higher value = lower probability
        // max ---- y -0-- min
        y -= world.getBottomY();
        y ++;
        return 1000 * (int)y;
    }

    private void doAScary(World world, BlockPos pos) {
        Sound event = events[world.random.nextInt(events.length)];
        float pitch = 1 + world.random.nextFloat();
        event.play(world, pos, 1, pitch);
    }

    interface Sound {
        static Sound of(SoundEvent event) {
            return (world, pos, volume, pitch) -> {
                world.playSound(MinecraftClient.getInstance().player, pos, event, SoundCategory.AMBIENT, volume, pitch);
            };
        }

        static Sound of(RegistryEntry<SoundEvent> event) {
            return (world, pos, volume, pitch) -> {
                world.playSound(MinecraftClient.getInstance().player, pos.getX(), pos.getY(), pos.getZ(), event, SoundCategory.AMBIENT, volume, pitch, world.getRandom().nextLong());
            };
        }

        void play(World world, BlockPos pos, float volume, float pitch);
    }
}
