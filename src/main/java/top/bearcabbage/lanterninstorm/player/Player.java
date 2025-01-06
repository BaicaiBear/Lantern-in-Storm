package top.bearcabbage.lanterninstorm.player;

import eu.pb4.playerdata.api.PlayerDataApi;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.annoyingeffects.AnnoyingEffects;
import top.bearcabbage.lanterninstorm.utils.Config;

import java.util.*;

import static top.bearcabbage.lanterninstorm.block.SpiritLanternBlock.STARTUP;


public class Player {
    private static final int INIT_SPIRIT = 5;
    private static final int UPGRADE_FRAG = 2;
    private static final int RADIUS = 8;
    private static final int TICK_INTERVAL = 20;

    private final ServerPlayerEntity player;
    private final Set<GlobalPos> lanterns = new HashSet<>();

    private BlockPos rtpSpawn;
    private int spirit;
    private boolean safety;

    private int LSTick;
    private int invincibleTick = 0;


    public Player(ServerPlayerEntity player) {
        this.player = player;
        NbtCompound data = PlayerDataApi.getCustomDataFor(player, LanternInStorm.LSData);
        if(data == null){
            data = new NbtCompound();
            data.putIntArray("rtpspawn", new int[]{-1});
            data.putInt("spirit", INIT_SPIRIT);
            PlayerDataApi.setCustomDataFor(player, LanternInStorm.LSData, data);
        }
        spirit = data.getInt("spirit");
        int[] posVec = data.getIntArray("rtpspawn");
        if (posVec.length == 3) {
            this.rtpSpawn = new BlockPos(posVec[0], posVec[1], posVec[2]);
        }
        LSTick = 0;

        Config lanternData = new Config(PlayerDataApi.getPathFor(player).resolve("lantern_data.json"));
        try {
            lanterns.clear();
            var tmpSet = lanternData.get("lanterns", Set.class);
            for (var pos : tmpSet) {
                Map value = (Map) pos;
                Identifier new_dimension = Identifier.of((String) ((Map) ((Map) value.get("dimension")).get("value")).get("namespace"), (String) ((Map) ((Map) value.get("dimension")).get("value")).get("path"));
                Map posMap = (Map) value.get("pos");
                int x = ((Double) posMap.get("x")).intValue();
                int y = ((Double) posMap.get("y")).intValue();
                int z = ((Double) posMap.get("z")).intValue();
                BlockPos new_pos = new BlockPos(x, y, z);
                GlobalPos new_global_pos = new GlobalPos(RegistryKey.of(RegistryKeys.WORLD, new_dimension), new_pos);
                lanterns.add(new_global_pos);
            }
        } catch (Exception e) {
            LanternInStorm.LOGGER.warn("No Lantern Data, generating blank format...");
            lanternData.set("lanterns", new HashSet<>());
            lanternData.save();
        }
    }

    public boolean onTick() {
        if (invincibleTick > 0) {
            invincibleTick--;
        }
        boolean check = ++LSTick % TICK_INTERVAL == 0;
        if (check) {
            Chunk playerChunk = player.getServerWorld().getChunk(player.getBlockPos());
            List<Chunk> chunkCheckList = List.of(playerChunk,
                    player.getWorld().getChunk(player.getBlockPos().add(RADIUS, 0, 0)),
                    player.getWorld().getChunk(player.getBlockPos().add(0, 0, RADIUS)),
                    player.getWorld().getChunk(player.getBlockPos().add(-RADIUS, 0, 0)),
                    player.getWorld().getChunk(player.getBlockPos().add(0, 0, -RADIUS)));
            for (Chunk chunk : chunkCheckList) {
                safety = false;
                chunk.forEachBlockMatchingPredicate(
                        blockState -> blockState.getBlock().equals(LanternInStorm.WHITE_PAPER_LANTERN) && blockState.get(STARTUP),
                        (blockPos, blockState) -> {
                            if (player.getPos().distanceTo(blockPos.toCenterPos()) < RADIUS) {
                                safety = true;
                            }
                        }
                );
                if (safety) break;
            }
        }
        return check;
    }

    public void onUnstableTick() {
        if (this.player.isSpectator() || this.player.isCreative()) return;
        this.player.addStatusEffect(new StatusEffectInstance(AnnoyingEffects.TANGLING_NIGHTMARE, 200));
        if (safety) {
            safety = false;
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("大鹏的噩梦正在吞噬你……!").withColor(0xAAAAAA)));
            }
        }
    }

    public void onDeath() {
        LSTick = invincibleTick = 0;
    }

    public BlockPos getRtpSpawn() {
        if(this.rtpSpawn == null) {
            return player.getSpawnPointPosition();
        }
        return this.rtpSpawn;
    }

    public void setRtpSpawn(BlockPos pos) {
        if(rtpSpawn != null){
            return;
        }
        this.rtpSpawn = pos;
        NbtCompound data = new NbtCompound();
        data.putIntArray("rtpspawn", new int[]{pos.getX(), pos.getY(), pos.getZ()});
        data.putIntArray("spawnpoint", new int[]{pos.getX(), pos.getY(), pos.getZ()});
        PlayerDataApi.setCustomDataFor(player, LanternInStorm.LSData, data);
    }

    public int getSpiritUpgradeCount() {
        return UPGRADE_FRAG;
    }

    public boolean getSafety() {
        return safety;
    }

    public void setInvincibleTick(int invincibleTick) {
        this.invincibleTick = invincibleTick;
    }

    public int getSpirit() {
        return spirit;
    }

    public void setSpirit(int spirit) {
        this.spirit = spirit;
        NbtCompound data = PlayerDataApi.getCustomDataFor(player, LanternInStorm.LSData);
        data.putInt("spirit", spirit);
        PlayerDataApi.setCustomDataFor(player, LanternInStorm.LSData, data);
    }

    public boolean addSpirit(int spirit) {
        if (spirit < 0 && this.spirit + spirit < 0) {
            return false;
        }
        this.spirit += spirit;
        NbtCompound data = PlayerDataApi.getCustomDataFor(player, LanternInStorm.LSData);
        data.putInt("spirit", this.spirit);
        PlayerDataApi.setCustomDataFor(player, LanternInStorm.LSData, data);
        return true;
    }

    public boolean addLantern(RegistryKey<World> world, BlockPos blockPos) {
        GlobalPos pos = new GlobalPos(world, blockPos);
        if (spirit==0) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("灵魂不够了……")));
            return false;
        }
        spirit--;
        lanterns.add(pos);
        Config lanternData = new Config(PlayerDataApi.getPathFor(player).resolve("lantern_data.json"));
        lanternData.set("lanterns", lanterns);
        lanternData.save();
        player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("又点亮了一盏路灯！").withColor(0xFCA106)));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("附近边长16的立方体稳定下来了").withColor(0xBBBBBB)));
        player.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal("你的灵魂剩余："+getSpirit()+"点")));
        return true;
    }

    public boolean removeLantern(RegistryKey<World> world, BlockPos blockPos) {
        GlobalPos pos = new GlobalPos(world, blockPos);
        if (!lanterns.contains(pos)) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("灯笼里不是你的灵魂……")));
            return false;
        }
        spirit++;
        lanterns.remove(pos);
        Config lanternData = new Config(PlayerDataApi.getPathFor(player).resolve("lantern_data.json"));
        lanternData.set("lanterns", lanterns);
        lanternData.save();
        player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("一盏路灯熄灭了").withColor(0x815C94)));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("你感受到噩梦的迫近...").withColor(0xBBBBBB)));
        player.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal("你的灵魂剩余："+getSpirit()+"点")));
        return true;
    }
}
