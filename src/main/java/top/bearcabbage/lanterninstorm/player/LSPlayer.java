package top.bearcabbage.lanterninstorm.player;

import eu.pb4.playerdata.api.PlayerDataApi;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.lanterninstorm.team.LSTeam;

import java.util.concurrent.locks.ReentrantLock;

import static top.bearcabbage.lanterninstorm.utils.LSMath.HorizontalDistance;

public class LSPlayer {
    private ServerPlayerEntity player;
    private BlockPos rtpSpawn;
    private BlockPos spawnPoint;
    private RegistryKey<World> spawnWorld;
    private int level;
    private boolean isTeamed;
    private LSTeam team;
    private final ReentrantLock lock = new ReentrantLock();

    private static final int TICK_INTERVAL = 20;
    private static final int GRACE_TICK = 100;
    private static final int DAMAGE_INTERVAL = 10;
    private static final float DAMAGE = 2.0F;
    private int LSTick;
    private int unsafeTick;
    private int damageTick;

    public LSPlayer(ServerPlayerEntity player) {
        this.player = player;
        NbtCompound data = PlayerDataApi.getCustomDataFor(player, LanternInStorm.LSData);
        if(data == null){
            level = 0;
            isTeamed = false;
            data = new NbtCompound();
            data.putInt("level", 0);
            data.putIntArray("rtpspawn", new int[]{-1});
            data.putIntArray("spawnpoint", new int[]{-1});
            data.putBoolean("spawn-in-overworld", true);
            PlayerDataApi.setCustomDataFor(player, LanternInStorm.LSData, data);
        }
        int[] posVec = data.getIntArray("rtpspawn");
        if (posVec.length == 3) {
            this.rtpSpawn = new BlockPos(posVec[0], posVec[1], posVec[2]);
        }
        int[] spawnVec = data.getIntArray("spawnpoint");
        if (spawnVec.length == 3) {
            this.spawnPoint = new BlockPos(spawnVec[0], spawnVec[1], spawnVec[2]);
        } else {
            this.spawnPoint = player.getServerWorld().getSpawnPos();
        }
        boolean spawnInOverworld = data.getBoolean("spawn-in-overworld");
        this.spawnWorld = spawnInOverworld ? World.OVERWORLD : World.NETHER;
        this.level = data.getInt("level");
        LSTick = 0;
        unsafeTick = 0;
    }

    public boolean onTick() {
        if (LSTick == 0) {
            return true;
        }
        LSTick = (LSTick + 1) % TICK_INTERVAL;
        return false;
    }

    public void onUnsafeTick() {
        this.player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 3*TICK_INTERVAL, 0, false, true));
        if(++unsafeTick>GRACE_TICK){
            if(damageTick++%DAMAGE_INTERVAL==0){
                this.player.damage(player.getDamageSources().genericKill(),DAMAGE);
            }
        }
    }

    public void onSafeTick() {
        if(--unsafeTick==0){
            this.player.removeStatusEffect(StatusEffects.BLINDNESS);
            damageTick = 0;
        }
    }

    public int getCELevel() {
        return this.level;
    }

    public  boolean setCELevel(int level) {
        if(level<0||level>4) return false;
        this.level = level;
        NbtCompound data = new NbtCompound();
        data.putInt("level", level);
        PlayerDataApi.setCustomDataFor(player, LanternInStorm.LSData, data);
        return true;
    }

    public void levelUP() {
        if (this.level < LSLevel.LEVELS.size() - 1) {
            this.setCELevel(this.level+1);
        }
    }

    public BlockPos getRtpSpawn() {
        if(this.rtpSpawn == null) {
            return spawnPoint;
        }
        return this.rtpSpawn;
    }

    public BlockPos getSpawnPoint(){
        if(this.spawnPoint == null){
            return player.getServerWorld().getSpawnPos();
        }
        return this.spawnPoint;
    }

    public RegistryKey<World> getSpawnWorld(){
        if(this.spawnWorld == null){
            return World.OVERWORLD;
        }
        return this.spawnWorld;
    }

    public boolean setRtpSpawn(BlockPos pos) {
        if(rtpSpawn != null){
            return false;
        }
        this.rtpSpawn = pos;
        this.spawnWorld = World.OVERWORLD;
        this.spawnPoint = pos;
        NbtCompound data = new NbtCompound();
        data.putIntArray("rtpspawn", new int[]{pos.getX(), pos.getY(), pos.getZ()});
        data.putIntArray("spawnpoint", new int[]{pos.getX(), pos.getY(), pos.getZ()});
        PlayerDataApi.setCustomDataFor(player, LanternInStorm.LSData, data);
        return true;
    }

    public boolean setSpawnPoint(RegistryKey<World> worldRegistryKey, BlockPos pos, float Yaw) {
        if(rtpSpawn == null){
            rtpSpawn = player.getServerWorld().getSpawnPos();
        }
        if (worldRegistryKey == World.OVERWORLD) {
            if (HorizontalDistance(rtpSpawn, pos) <= LSLevel.RADIUS.get(this.level)) {
                this.spawnWorld = worldRegistryKey;
                this.spawnPoint = pos;
                NbtCompound data = new NbtCompound();
                data.putIntArray("spawnpoint", new int[]{pos.getX(), pos.getY(), pos.getZ()});
                data.putBoolean("spawn-in-overworld", true);
                PlayerDataApi.setCustomDataFor(player, LanternInStorm.LSData, data);
                this.player.sendMessage(Text.of("成功设置重生点！"));
                this.player.sendMessage(Text.of("[CE]您的探索范围中心已更新为[" + String.valueOf(pos.getX())  + "," + String.valueOf(pos.getZ()) + "]"));
                return true;
            }
            this.player.sendMessage(Text.of("[CE]重生点设置失败！重生点超出原始探索范围！"));
            return false;
        } else if (worldRegistryKey == World.NETHER) {
            if (HorizontalDistance(rtpSpawn, pos.multiply(8)) <= LSLevel.RADIUS.get(this.level)) {
                this.spawnWorld = worldRegistryKey;
                this.spawnPoint = pos;
                NbtCompound data = new NbtCompound();
                data.putIntArray("spawnpoint", new int[]{pos.getX(), pos.getY(), pos.getZ()});
                data.putBoolean("spawn-in-overworld", false);
                PlayerDataApi.setCustomDataFor(player, LanternInStorm.LSData, data);
                this.player.sendMessage(Text.of("成功设置重生点！"));
                this.player.sendMessage(Text.of("[CE]您的探索范围中心已更新为[" + String.valueOf(pos.getX()*8) + "," + String.valueOf(pos.getZ()*8) + "]"));
                return true;
            }
            this.player.sendMessage(Text.of("[CE]重生点设置失败！重生点超出原始探索范围！"));
            return false;
        }
        return false;
    }


    public boolean isTeamed() {
        return isTeamed;
    }

    public boolean joinTeam(LSTeam newTeam) {
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

    public LSTeam getTeam(){
        return this.team;
    }


    public float getSpawnAngle() {
        return player.getYaw();
    }

    public RegistryKey<World> getSpawnPointDimension() {
        return this.spawnWorld;
    }

    public double getRadiusForTeam(){
        if(this.level==LSLevel.LEVELS.getLast()){
            return LSLevel.RADIUS.get(LSLevel.RADIUS.size()-2);
        }
        return LSLevel.RADIUS.get(this.level);
    }
    public void onDeath() {
        LSTick = damageTick = unsafeTick = 0;
    }

    public int getUnsafeTick() {
        return unsafeTick;
    }

}
