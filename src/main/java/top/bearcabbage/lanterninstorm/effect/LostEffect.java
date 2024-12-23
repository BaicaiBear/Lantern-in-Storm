package top.bearcabbage.lanterninstorm.effect;

import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class LostEffect extends StatusEffect {
    public static final StatusEffect LOST = new LostEffect();
    public static final Identifier ID = Identifier.of("lanterninstorm", "lost");
    public static final RegistryEntry<StatusEffect> LOST_EFFECT_ENTRY = Registry.registerReference(Registries.STATUS_EFFECT, ID, new LostEffect());

    public LostEffect() {
        super(StatusEffectCategory.HARMFUL, 0x996600);
    }

    public static void register() {
        Registry.register(Registries.STATUS_EFFECT, Identifier.of("lanterninstorm", "lost"), LOST);
    }

    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getHealth() > 1.0F) {
            entity.damage(entity.getDamageSources().magic(), 1.0F);
            if (entity instanceof ServerPlayerEntity player) {
                player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("You are lost!").withColor(0x996600)));
            }
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
