package top.bearcabbage.lanterninstorm.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class NightmareEffect extends StatusEffect {
    public static final StatusEffect NIGHTMARE = new NightmareEffect();
    public static final Identifier ID = Identifier.of("lanterninstorm", "nightmare");
    public static final RegistryEntry<StatusEffect> NIGHTMARE_EFFECT_ENTRY = Registry.registerReference(Registries.STATUS_EFFECT, ID, new NightmareEffect());

    public NightmareEffect() {
        super(StatusEffectCategory.HARMFUL, 0x996600);
    }

    public static void register() {
        Registry.register(Registries.STATUS_EFFECT, Identifier.of("lanterninstorm", "nightmare"), NIGHTMARE);
    }

    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getHealth() > 1.0F) {
            entity.damage(entity.getDamageSources().magic(), 1.0F);
        }

        return true;
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int i = 25 >> amplifier;
        if (i > 0) {
            return duration % i == 0;
        } else {
            return true;
        }
    }

}
