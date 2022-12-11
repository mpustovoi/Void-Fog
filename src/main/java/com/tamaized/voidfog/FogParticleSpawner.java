package com.tamaized.voidfog;

import com.tamaized.voidfog.api.Voidable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class FogParticleSpawner {

    private static final int RADIUS = 16;

    private BlockPos randomPos(Random rand) {
        return new BlockPos(rand.nextInt(RADIUS), rand.nextInt(RADIUS), rand.nextInt(RADIUS));
    }

    public void update(World world, Entity entity, Voidable dimension) {
        BlockPos playerPos = entity.getBlockPos();

        Random rand = world.getRandom();

        float entityAltitude = dimension.isVoidFogDisabled(entity, world) ? 15F : (float)(entity.getY() - world.getBottomY());
        int fadeStart = VoidFog.config.maxFogHeight;
        float fadeOffset = VoidFog.config.fadeStartOffset;
        float fadeEnd = fadeStart - fadeOffset;
        float entityDelta = Math.max(0, Math.min(1, (1 - (entityAltitude - fadeEnd) / fadeOffset)));

        for (int pass = 0; (pass < VoidFog.config.voidParticleDensity * entityDelta); pass++) {
            if (entityAltitude <= fadeStart) {
                BlockPos pos = randomPos(rand).subtract(randomPos(rand)).add(playerPos);
                BlockState state = world.getBlockState(pos);

                if (state.isAir() && world.getFluidState(pos).isEmpty() && pos.getY() - world.getBottomY() <= fadeStart) {
                    if (rand.nextInt(8 * ((!VoidFog.config.scaleWithDifficulty) ? 1 : (world.getDifficulty().getId() + 1))) <= fadeStart) {
                        boolean nearBedrock = dimension.isNearBedrock(pos, world);

                        world.addParticle(nearBedrock ? ParticleTypes.ASH : ParticleTypes.MYCELIUM,
                                pos.getX() + rand.nextFloat(),
                                pos.getY() + rand.nextFloat(),
                                pos.getZ() + rand.nextFloat(),
                                0,
                                nearBedrock ? rand.nextFloat() : 0,
                                0);
                    }
                }
            } else break;
        }
    }
}







