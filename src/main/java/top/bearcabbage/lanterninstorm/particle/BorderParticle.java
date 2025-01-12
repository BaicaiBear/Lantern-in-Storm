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

}