package top.bearcabbage.lanterninstorm.player;

import eu.pb4.playerdata.api.PlayerDataApi;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.lanterninstorm.interfaces.PlayerManagerAccessor;
import top.bearcabbage.lanterninstorm.team.Team;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import static top.bearcabbage.lanterninstorm.utils.Math.HorizontalDistance;

public class Player {
    private ServerPlayerEntity player;
    private BlockPos rtpSpawn;
    private boolean isTeamed;
    private Team team;
    private PlayerSpirit spirit;
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
            spirit = new PlayerSpirit(player, true);
            isTeamed = false;
            data = new NbtCompound();
            data.putInt("level", 0);data.putIntArray("rtpspawn", new int[]{-1});
            PlayerDataApi.setCustomDataFor(player, LanternInStorm.LSData, data);
        }
        int[] posVec = data.getIntArray("rtpspawn");
        if (posVec.length == 3) {
            this.rtpSpawn = new BlockPos(posVec[0], posVec[1], posVec[2]);
        }
        spirit = new PlayerSpirit(player, false);
        LSTick = 0;
        tiredTick = 0;
    }

    public boolean onTick() {
        return ++LSTick % TICK_INTERVAL == 0;
    }

    public void onTiredTick() {
        this.player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 3*TICK_INTERVAL, 0, false, true));
        if(++tiredTick >GRACE_TICK){
            if(debuffTick++%DAMAGE_INTERVAL==0){
                this.player.damage(player.getDamageSources().genericKill(),DAMAGE);
            }
        }
    }

    public void onRestTick() {
        if(--tiredTick ==0){
            this.player.removeStatusEffect(StatusEffects.BLINDNESS);
            debuffTick = 0;
        }
    }

    public void onUnstableTick() {}

    public void onDeath() {
        LSTick = debuffTick = tiredTick = 0;
    }

    public PlayerSpirit getSpirit() {
        return this.spirit;
    }

    public void onGrantAdvancement(Advancement advancement) {
        this.spirit.addMass(1);
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

    public boolean isTeamed() {
        return isTeamed;
    }

    public boolean joinTeam(Team newTeam) {
        lock.lock();
        try {
            if(this.isTeamed || newTeam == null) {
                return false;
            }
            isTeamed = true;
            this.team = newTeam;
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean quitTeam() {
        lock.lock();
        try {
            if(!this.isTeamed) {
                return false;
            }
            isTeamed = false;
            this.team = null;
            return true;
        } finally {
            lock.unlock();
        }
    }

    public Team getTeam(){
        return this.team;
    }


    public int getTiredTick() {
        return tiredTick;
    }

    public static ServerPlayerEntity uuidToPlayer(MinecraftServer server, UUID uuid) {
        PlayerManagerAccessor playerManager = (PlayerManagerAccessor) server.getPlayerManager();
        return playerManager.uuid2Player(uuid);
    }

}
