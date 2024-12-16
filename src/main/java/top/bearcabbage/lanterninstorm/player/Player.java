package top.bearcabbage.lanterninstorm.player;

import eu.pb4.playerdata.api.PlayerDataApi;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.lanterninstorm.LanternInStormSpiritManager;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;


public class Player {
    private ServerPlayerEntity player;
    private BlockPos rtpSpawn;
    private static final int INIT_SPIRIT = 5;
    private static final int DISTANCE_PER_SPIRIT = 10;
    private final ReentrantLock lock = new ReentrantLock();

    private static final int TICK_INTERVAL = 20;
    private static final int GRACE_TICK = 100;
    private static final int DAMAGE_INTERVAL = 10;
    private static final float DAMAGE = 2.0F;
    private int LSTick;
    private int tiredTick;
    private int debuffTick;

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
            debuffTick = 0;
        }
    }

    public void onUnstableTick() {
        this.player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 200));
    }

    public void onDeath() {
        LSTick = debuffTick = tiredTick = 0;
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

    public ActionResult distributeSpirits(SpiritLanternEntity lanternEntity, int spirits) {
        UUID player = this.player.getUuid();
        UUID lantern = lanternEntity.getUuid();
        int player_left = LanternInStormSpiritManager.get_left(player);
        int lantern_left = LanternInStormSpiritManager.get(player, lantern);
        if(spirits > player_left){
            this.player.sendMessage(Text.of("灵魂不够了～～～"));
            return ActionResult.FAIL;
        }
        if(spirits + lantern_left < 0){
            this.player.sendMessage(Text.of("灯笼空了…………"));
            return ActionResult.FAIL;
        }
        LanternInStormSpiritManager.increase_left(player, -spirits);
        LanternInStormSpiritManager.increase(player, lantern, spirits);
        this.player.sendMessage(Text.of("成功分配了"+spirits+"个灵魂"));
        return ActionResult.SUCCESS;

    }

    public int getLeftMass(){
        return LanternInStormSpiritManager.get_left(player.getUuid());
    }

    public int getTotalMass(){
        return LanternInStormSpiritManager.get_sum(player.getUuid());
    }

    // 检查玩家是否在安全区内
    public boolean isSafe() {
        Map<UUID, Integer> lanterns_and_spirits = LanternInStormSpiritManager.spirit_mass.getOrDefault(player.getUuid(), null);
        if (lanterns_and_spirits == null) {
            return false;
        }
        for(UUID lantern: lanterns_and_spirits.keySet()){
            if(lantern==null) continue;
            int spirit_num = lanterns_and_spirits.get(lantern);
            if (spirit_num == 0) continue;
            GlobalPos pos = LanternInStormSpiritManager.lantern_pos.get(lantern);
            if (player.getEntityWorld().getRegistryKey()!=pos.dimension()) continue;
            if (player.getPos().distanceTo(pos.pos().toCenterPos()) < spirit_num * DISTANCE_PER_SPIRIT) return true;

        }
//
//        for (SpiritLanternEntity lantern : player.getEntityWorld().getNonSpectatingEntities(SpiritLanternEntity.class,
//                Box.of(player.getPos(), MAX_SPIRIT_RADIUS * 2, MAX_SPIRIT_RADIUS * 2, MAX_SPIRIT_RADIUS * 2))) {
//            int spirit_mass = SpiritManager.get(player.getUuid(), lantern.getUuid());
//            if (spirit_mass == 0) continue;
//            if (HorizontalDistance(player.getPos(), lantern.getPos()) < spirit_mass * SPIRIT_RADIUS) return true;
//        }
        return false;
    }

}
