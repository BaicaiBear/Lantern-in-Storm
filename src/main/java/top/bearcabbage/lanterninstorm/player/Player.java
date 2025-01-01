package top.bearcabbage.lanterninstorm.player;

import eu.pb4.playerdata.api.PlayerDataApi;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.lanterninstorm.LanternInStormSpiritManager;
import top.bearcabbage.annoyingeffects.AnnoyingEffects;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import static top.bearcabbage.lanterninstorm.LanternInStormSpiritManager.DISTANCE_PER_SPIRIT;


public class Player {
    private ServerPlayerEntity player;
    private BlockPos rtpSpawn;
    private static final int INIT_SPIRIT = 5;
    private static final int UPGRADE_FRAG = 2;
    private final ReentrantLock lock = new ReentrantLock();

    private static final int TICK_INTERVAL = 20;
    private static final int GRACE_TICK = 100;
    private static final int DAMAGE_INTERVAL = 10;
    private static final float DAMAGE = 2.0F;
    private int LSTick;
    private int tiredTick;
    private int invincibleTick = 0;
    private boolean safety;

    public Player(ServerPlayerEntity player) {
        this.player = player;
        NbtCompound data = PlayerDataApi.getCustomDataFor(player, LanternInStorm.LSData);
        if(data == null){
            LanternInStormSpiritManager.set_left(this.player.getUuid(), INIT_SPIRIT);
            data = new NbtCompound();
            data.putIntArray("rtpspawn", new int[]{-1});
            PlayerDataApi.setCustomDataFor(player, LanternInStorm.LSData, data);
        }
        int[] posVec = data.getIntArray("rtpspawn");
        if (posVec.length == 3) {
            this.rtpSpawn = new BlockPos(posVec[0], posVec[1], posVec[2]);
        }
        LSTick = 0;
        tiredTick = 0;
    }

    public boolean onTick() {
        if (invincibleTick > 0) {
            invincibleTick--;
        }
        return ++LSTick % TICK_INTERVAL == 0;
    }

    public void onTiredTick() {
//        this.player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 3*TICK_INTERVAL, 0, false, true));
//        if(++tiredTick >GRACE_TICK){
//            if(debuffTick++%DAMAGE_INTERVAL==0){
//                this.player.damage(player.getDamageSources().genericKill(),DAMAGE);
//            }
//        }
    }

    public int getTiredTick() {
        return tiredTick;
    }

    public void onRestTick() {
        if(--tiredTick ==0){
            this.player.removeStatusEffect(StatusEffects.WITHER);
        }
    }

    public void onUnstableTick() {
        this.player.addStatusEffect(new StatusEffectInstance(AnnoyingEffects.TANGLING_NIGHTMARE, 200));
        if (safety) {
            safety = false;
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("大鹏的噩梦正在吞噬你……!").withColor(0xAAAAAA)));
            }
        }
        //this.player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("OUT!!!").withColor(0x996600)));
//        if (this.player.getWorld().isClient) {
//            RenderSystem.setShaderFogStart(0.0F);
//            RenderSystem.setShaderFogEnd(1.0F);
//            BackgroundRenderer.applyFog(MinecraftClient.getInstance().gameRenderer.getCamera(), BackgroundRenderer.FogType.FOG_TERRAIN, 5,true, 1);
//        }
    }

    public void onDeath() {
        LSTick = invincibleTick = tiredTick = 0;
    }

    public void onGrantAdvancement(Advancement advancement) {
        LanternInStormSpiritManager.increase_left(player.getUuid(), 1);
    }

    public BlockPos getRtpSpawn() {
        if(this.rtpSpawn == null) {
            return player.getSpawnPointPosition();
        }
        return this.rtpSpawn;
    }

    public boolean setRtpSpawn(BlockPos pos) {
        if(rtpSpawn != null){
            return false;
        }
        this.rtpSpawn = pos;
        NbtCompound data = new NbtCompound();
        data.putIntArray("rtpspawn", new int[]{pos.getX(), pos.getY(), pos.getZ()});
        data.putIntArray("spawnpoint", new int[]{pos.getX(), pos.getY(), pos.getZ()});
        PlayerDataApi.setCustomDataFor(player, LanternInStorm.LSData, data);
        return true;
    }

    public int getLeftMass(){
        return LanternInStormSpiritManager.get_left(player.getUuid());
    }

    public int getTotalMass(){
        return LanternInStormSpiritManager.get_sum(player.getUuid());
    }

    // 检查玩家是否在安全区内
    public boolean isSafe() {
        if (invincibleTick > 0) {
            return true;
        }
        Map<UUID, Integer> lanterns_and_spirits = LanternInStormSpiritManager.spirit_mass.getOrDefault(player.getUuid(), null);
        if (lanterns_and_spirits == null) {
            return false;
        }
        for(UUID lantern: lanterns_and_spirits.keySet()){
            int total_num = LanternInStormSpiritManager.get_sum(lantern);
            GlobalPos pos = LanternInStormSpiritManager.lantern_pos.get(lantern);
            if (pos==null) continue;
            if (player.getEntityWorld().getRegistryKey()!=pos.dimension()) continue;
            if (player.getPos().distanceTo(pos.pos().toCenterPos()) < total_num * DISTANCE_PER_SPIRIT) return true;

        }
        return false;
    }

    public int getSpiritUpgradeCount() {
        return UPGRADE_FRAG;
    }

    public void upgradeSpirit() {
        LanternInStormSpiritManager.increase_left(this.player.getUuid(), 1);
    }

    public void setSafe() {
        this.safety = true;
    }

    public boolean getSafety() {
        return safety;
    }

    public void setInvincibleTick(int invincibleTick) {
        this.invincibleTick = invincibleTick;
    }
}
