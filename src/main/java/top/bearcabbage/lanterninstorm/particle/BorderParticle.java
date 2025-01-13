package top.bearcabbage.lanterninstorm.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import top.bearcabbage.lanterninstorm.LanternInStorm;

public class BorderParticle {
    public static final SimpleParticleType border = register("border", FabricParticleTypes.simple());
    private static SimpleParticleType register(String name, SimpleParticleType type) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(LanternInStorm.MOD_ID,name),type);
    }
    public static void registerModParticles(){
    }
    /*
    @Environment(EnvType.CLIENT)
    public class EndRodParticle extends AnimatedParticle {
        EndRodParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
            super(world, x, y, z, spriteProvider, 0.0125F);
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.velocityZ = velocityZ;
            this.scale *= 0.75F;
            this.maxAge = 60 + this.random.nextInt(12);
            this.setSpriteForAge(spriteProvider);
        }



        @Environment(EnvType.CLIENT)
        public static class Factory implements ParticleFactory<SimpleParticleType> {
            private final SpriteProvider spriteProvider;

            public Factory(SpriteProvider spriteProvider) {
                this.spriteProvider = spriteProvider;
            }

            public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
                return new net.minecraft.client.particle.EndRodParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            }
        }
    }
    //ani
    @Environment(EnvType.CLIENT)
    public class AnimatedParticle extends SpriteBillboardParticle {
        protected final SpriteProvider spriteProvider;

        protected AnimatedParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider, float upwardsAcceleration) {
            super(world, x, y, z);
            this.velocityMultiplier = 0.91F;
            this.gravityStrength = upwardsAcceleration;
            this.spriteProvider = spriteProvider;
        }

        @Override
        public void tick() {
            super.tick();
            this.setSpriteForAge(this.spriteProvider);
            if (this.age > this.maxAge / 2) {
                this.setAlpha(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
            }
        }
    }*/
}