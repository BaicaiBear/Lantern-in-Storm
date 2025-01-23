package top.bearcabbage.lanterninstorm.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.pb4.playerdata.api.PlayerDataApi;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.annoyingeffects.AnnoyingEffects;
import top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlock;
import top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlocks;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static top.bearcabbage.lanterninstorm.LanternInStorm.*;
import static top.bearcabbage.lanterninstorm.LanternInStormAPI.safeWorlds;
import static top.bearcabbage.lanterninstorm.LanternInStormItems.TALISMAN;
import static top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlock.STARTUP;


public class Player {

    private final ServerPlayerEntity player;
    private final Set<GlobalPos> lanterns = new HashSet<>();

    private BlockPos rtpSpawn;
    private int spirit;
    private boolean safety;
    private boolean safetyPrev;
    private boolean hasTalismanPrev;

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
            invincibleTick = 20;
        }
        spirit = data.getInt("spirit");
        int[] posVec = data.getIntArray("rtpspawn");
        if (posVec.length == 3) {
            this.rtpSpawn = new BlockPos(posVec[0], posVec[1], posVec[2]);
        }
        LSTick = 0;

        Config lanternData = new Config(PlayerDataApi.getPathFor(player).resolve("lantern_data.json"));
        lanterns.clear();
        var tmpSet = lanternData.get("lanterns", Set.class);
        if (tmpSet == null) {
            LanternInStorm.LOGGER.warn("No Lantern Data, generating blank format...");
            lanternData.set("lanterns", new HashSet<>());
            lanternData.save();
        } else for (var pos : tmpSet) {
            Map value = (Map) pos;
            Identifier new_dimension = null;
            BlockPos new_pos;
            int x = 0;
            int y = 0;
            int z = 0;
            try {
                new_dimension = Identifier.of((String) ((Map) ((Map) value.get("dimension")).get("value")).get("namespace"), (String) ((Map) ((Map) value.get("dimension")).get("value")).get("path"));
                Map posMap = (Map) value.get("pos");
                x = ((Double) posMap.get("x")).intValue();
                y = ((Double) posMap.get("y")).intValue();
                z = ((Double) posMap.get("z")).intValue();
            } catch (Exception e) {
                if (e instanceof NullPointerException) {
                    new_dimension = Identifier.of((String) ((Map) ((Map) value.get("comp_2207")).get("field_25138")).get("field_13353"), (String) ((Map) ((Map) value.get("comp_2207")).get("field_25138")).get("field_13355"));
                    Map posMap = (Map) value.get("comp_2208");
                    x = ((Double) posMap.get("field_11175")).intValue();
                    y = ((Double) posMap.get("field_11174")).intValue();
                    z = ((Double) posMap.get("field_11173")).intValue();
                } else {
                    LanternInStorm.LOGGER.error(e.toString());
                }
            }
            new_pos = new BlockPos(x, y, z);
            GlobalPos new_global_pos = new GlobalPos(RegistryKey.of(RegistryKeys.WORLD, new_dimension), new_pos);
            lanterns.add(new_global_pos);
        }
        safetyPrev = safety = true;
        hasTalismanPrev = false;
    }

    public boolean onTick() {
        if (safeWorlds.contains(player.getServerWorld().getRegistryKey())) {
            return false;
        }
        if (invincibleTick > 0) {
            invincibleTick--;
        }
        boolean check = ++LSTick % TICK_INTERVAL == 0;
        if (check) {
            safetyPrev = safety;
            // if player near his rtp spawn (beginning lantern)
            if (rtpSpawn != null && MathHelper.withinCubicOfRadius(player.getPos(), rtpSpawn.toCenterPos(), 2*LANTERN_RADIUS)) {
                safety = true;
                onStableTick();
                return true;
            }
            // if player near a lantern
            Chunk playerChunk = player.getServerWorld().getChunk(player.getBlockPos());
            List<Chunk> chunkCheckList = List.of(playerChunk,
                    player.getWorld().getChunk(player.getBlockPos().add(LANTERN_RADIUS, 0, 0)),
                    player.getWorld().getChunk(player.getBlockPos().add(0, 0, LANTERN_RADIUS)),
                    player.getWorld().getChunk(player.getBlockPos().add(-LANTERN_RADIUS, 0, 0)),
                    player.getWorld().getChunk(player.getBlockPos().add(0, 0, -LANTERN_RADIUS)),
                    player.getWorld().getChunk(player.getBlockPos().add(LANTERN_RADIUS, 0, LANTERN_RADIUS)),
                    player.getWorld().getChunk(player.getBlockPos().add(-LANTERN_RADIUS, 0, LANTERN_RADIUS)),
                    player.getWorld().getChunk(player.getBlockPos().add(LANTERN_RADIUS, 0, -LANTERN_RADIUS)),
                    player.getWorld().getChunk(player.getBlockPos().add(-LANTERN_RADIUS, 0, -LANTERN_RADIUS)));
            for (Chunk chunk : chunkCheckList) {
                safety = false;
                chunk.forEachBlockMatchingPredicate(
                        blockState -> blockState.getBlock() instanceof SpiritLanternBlock && blockState.get(STARTUP),
                        (blockPos, blockState) -> {
                            if (MathHelper.withinCubicOfRadius(player.getPos(), blockPos.toCenterPos(), LANTERN_RADIUS)) {
                                safety = true;
                            }
                        }
                );
                if (safety) break;
            }
            if (safety) onStableTick();
            else onUnstableTick();
        }
        return check;
    }

    public void onStableTick() {
        if (this.player.isSpectator() || this.player.isCreative()) return;
        if (!safetyPrev) {
            Set<RegistryEntry<StatusEffect>> effectsToRemove = new HashSet<>();
            player.getStatusEffects().forEach((effect) -> {
                if (!effect.getEffectType().value().isBeneficial()) {
                    effectsToRemove.add(effect.getEffectType());
                }
            });
            effectsToRemove.forEach(player::removeStatusEffect);
            player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("世界稳定下来了").withColor(0xF6DEAD)));
        }
    }

    public void onUnstableTick() {
        if (this.player.isSpectator() || this.player.isCreative()) return;
        if (player.getMainHandStack().isOf(TALISMAN)||player.getOffHandStack().isOf(TALISMAN)) {
            ItemStack talisman = player.getMainHandStack().isOf(TALISMAN) ? player.getMainHandStack() : player.getOffHandStack();
            talisman.damage(1, player.getServerWorld(), player, item -> {
            });
            if (safetyPrev) {
                player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("但手中的熊之符令人安心").withColor(0x996633)));
            } else if (!hasTalismanPrev){
//                player.clearStatusEffects();
                player.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal("拿起熊之符你似乎感到好些了").withColor(0xF6DEAD)));
            }
            this.player.addStatusEffect(new StatusEffectInstance(AnnoyingEffects.TANGLING_DREAMS, 200));
            hasTalismanPrev = true;
        } else {
            this.player.addStatusEffect(new StatusEffectInstance(AnnoyingEffects.TANGLING_NIGHTMARE, 200));
            hasTalismanPrev = false;
        }
        if (safetyPrev) {
            player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("鹏的噩梦正在吞噬你!").withColor(0xAAAAAA)));
        }
    }

    public void onDeath() {
        LSTick = invincibleTick = 0;
    }

    public BlockPos getOriginalRtpSpawn() {
        return this.rtpSpawn;
    }

    public BlockPos getRtpSpawn() {
        if(this.rtpSpawn == null) {
            return player.getSpawnPointPosition();
        }
        return this.rtpSpawn;
    }

    public void setRtpSpawn(BlockPos pos) {
        NbtCompound data = PlayerDataApi.getCustomDataFor(player, LanternInStorm.LSData);
        if(pos == null){
            this.rtpSpawn = null;
            data.putIntArray("rtpspawn", new int[]{-1});
        }
        else {
            this.rtpSpawn = pos;
            data.putIntArray("rtpspawn", new int[]{pos.getX(), pos.getY(), pos.getZ()});
        }
        PlayerDataApi.setCustomDataFor(player, LanternInStorm.LSData, data);
    }

    public int getSpiritUpgradeCount() {
        return UPGRADE_FRAG;
    }

    public boolean getSafety() {
        return safety;
    }

    public boolean getSafetyPrev() {
        return safetyPrev;
    }

    public void setInvincibleTick(int invincibleTick) {
        this.invincibleTick = invincibleTick;
    }

    public int getSpirit() {
        return spirit;
    }

    public boolean setSpirit(int spirit) {
        if (spirit < 0) return false;
        this.spirit = spirit;
        NbtCompound data = PlayerDataApi.getCustomDataFor(player, LanternInStorm.LSData);
        data.putInt("spirit", spirit);
        PlayerDataApi.setCustomDataFor(player, LanternInStorm.LSData, data);
        player.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal("你的灵魂剩余："+getSpirit()+"点")));
        return true;
    }

    public boolean addSpirit(int spirit) {
        if (spirit < 0 && this.spirit + spirit < 0) {
            return false;
        }
        this.spirit += spirit;
        NbtCompound data = PlayerDataApi.getCustomDataFor(player, LanternInStorm.LSData);
        data.putInt("spirit", this.spirit);
        PlayerDataApi.setCustomDataFor(player, LanternInStorm.LSData, data);
        player.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal("你的灵魂剩余："+getSpirit()+"点")));
        return true;
    }

    public boolean addLantern(RegistryKey<World> world, BlockPos blockPos) {
        GlobalPos pos = new GlobalPos(world, blockPos);
        if (!addSpirit(-1)) {
            player.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("灵魂不够了……")));
            return false;
        }
        lanterns.add(pos);
        Config lanternData = new Config(PlayerDataApi.getPathFor(player).resolve("lantern_data.json"));
        lanternData.set("lanterns", lanterns);
        lanternData.save();
        player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("又点亮了一盏路灯！").withColor(0xFCA106)));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("附近半径"+LANTERN_RADIUS+"的立方体稳定下来了").withColor(0xBBBBBB)));
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


    public static class Config {
        private final Path filePath;
        private JsonObject jsonObject;
        private final Gson gson;

        public Config(Path filePath) {
            this.filePath = filePath;
            this.gson = new GsonBuilder().setPrettyPrinting().create();
            try {
                if (Files.notExists(filePath.getParent())) {
                    Files.createDirectories(filePath.getParent());
                }
                if (Files.notExists(filePath)) {
                    Files.createFile(filePath);
                    try (FileWriter writer = new FileWriter(filePath.toFile())) {
                        writer.write("{}");
                    }
                }

            } catch (IOException e) {
                LOGGER.error(e.toString());
            }
            load();
        }

        public void load() {
            try (FileReader reader = new FileReader(filePath.toFile())) {
                this.jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (IOException e) {
                this.jsonObject = new JsonObject();
            }
        }

        public void save() {
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                gson.toJson(jsonObject, writer);
            } catch (IOException e) {
                LOGGER.error(e.toString());
            }
        }

        public void set(String key, Object value) {
            jsonObject.add(key, gson.toJsonTree(value));
        }

        public <T> T get(String key, Class<T> clazz) {
            return gson.fromJson(jsonObject.get(key), clazz);
        }

    }

    public static class MathHelper {
        public static boolean withinCubicOfRadius (Vec3d pos1, Vec3d pos2, double radius) {
            double dx = Math.abs(pos1.x - pos2.x);
            double dy = Math.abs(pos1.y - pos2.y);
            double dz = Math.abs(pos1.z - pos2.z);
            return dx <= radius && dy <= radius && dz <= radius;
        }

        public static boolean withinHexagonOfRadius (Vec3d pos1, Vec3d pos2, double radius, double height) {
            double dx = Math.abs(pos1.x - pos2.x);
            double dy = Math.abs(pos1.y - pos2.y);
            double dz = Math.abs(pos1.z - pos2.z);
            return !(dy > height) && !(dx > radius) && !(dz > radius * 0.8660254038) && !(dz > (radius - dx) * 0.8660254038);
        }
    }
}
