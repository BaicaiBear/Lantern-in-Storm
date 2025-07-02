package top.bearcabbage.lanterninstorm.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import eu.pb4.playerdata.api.PlayerDataApi;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import top.bearcabbage.annoyingeffects.network.AnnoyingBarDisplayPayload;
import top.bearcabbage.annoyingeffects.network.AnnoyingBarStagePayload;
import top.bearcabbage.lanterninstorm.LanternInStorm;
import top.bearcabbage.annoyingeffects.AnnoyingEffects;
import top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlock;
import top.bearcabbage.lanterninstorm.lantern.LanternTimeManager;
import net.minecraft.util.math.GlobalPos;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static top.bearcabbage.lanterninstorm.LanternInStorm.*;
import static top.bearcabbage.lanterninstorm.LanternInStormAPI.safeWorlds;
import static top.bearcabbage.lanterninstorm.LanternInStormItems.FLASHLIGHT;
import static top.bearcabbage.lanterninstorm.LanternInStormItems.TALISMAN;
import static top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlock.*;


public class Player {

    private final ServerPlayerEntity player;

    private BlockPos rtpSpawn;
    private boolean safety;
    private boolean safetyPrev;
    private boolean cleared;

    private int LSTick;
    private final static int INVINCIBLE_SECONDS = 10;
    private int invincibleSec = 0;


    public Player(ServerPlayerEntity player) {
        this.player = player;
        NbtCompound data = PlayerDataApi.getCustomDataFor(player, LanternInStorm.LSData);
        if(data == null){
            data = new NbtCompound();
            data.putIntArray("rtpspawn", new int[]{-1});
            invincibleSec = INVINCIBLE_SECONDS;
        }
        int[] posVec = data.getIntArray("rtpspawn");
        if (posVec.length == 3) {
            this.rtpSpawn = new BlockPos(posVec[0], posVec[1], posVec[2]);
        }
        LSTick = 0;
        safetyPrev = safety = true;
        cleared = false;
    }

    public void onTick() {
        if (player.isSpectator() || player.isCreative()) return;
        boolean check = ++LSTick % TICK_INTERVAL == 0;
        if (check) {
            if (safeWorlds.contains(player.getServerWorld().getRegistryKey())) {
                ServerPlayNetworking.send(player, new AnnoyingBarDisplayPayload(false));
                ServerPlayNetworking.send(player, new AnnoyingBarStagePayload(0));
                return;
            }
            safetyPrev = safety;
            if (invincibleSec > 0) {
                invincibleSec--;
                safety = true;
                ServerPlayNetworking.send(player, new AnnoyingBarDisplayPayload(false));
                ServerPlayNetworking.send(player, new AnnoyingBarStagePayload(0));
                return;
            }
            // if player near a lantern
            Chunk playerChunk = player.getServerWorld().getChunk(player.getBlockPos());
            List<Chunk> chunkCheckList = List.of(playerChunk,
                    player.getWorld().getChunk(player.getBlockPos().add(16, 0, 0)),
                    player.getWorld().getChunk(player.getBlockPos().add(0, 0, 16)),
                    player.getWorld().getChunk(player.getBlockPos().add(-16, 0, 0)),
                    player.getWorld().getChunk(player.getBlockPos().add(0, 0, -16)),
                    player.getWorld().getChunk(player.getBlockPos().add(16, 0, 16)),
                    player.getWorld().getChunk(player.getBlockPos().add(-16, 0, 16)),
                    player.getWorld().getChunk(player.getBlockPos().add(16, 0, -16)),
                    player.getWorld().getChunk(player.getBlockPos().add(-16, 0, -16)));
            for (Chunk chunk : chunkCheckList) {
                safety = false;
                chunk.forEachBlockMatchingPredicate(
                        blockState -> blockState.getBlock() instanceof SpiritLanternBlock && blockState.get(STARTUP),
                        (blockPos, blockState) -> {
                            GlobalPos gpos = GlobalPos.create(player.getWorld().getRegistryKey(), blockPos);
                            if (!player.getWorld().getRegistryKey().getValue().getNamespace().equals("starry_skies")
                                    ? MathHelper.withinCubicOfRadius(player.getPos(), blockPos.toCenterPos(), LANTERN_RADIUS)
                                    : MathHelper.withinCubicOfRadius(player.getPos(), blockPos.toCenterPos(), blockState.get(RADIUS))) {
                                safety = true;
                                if (player.getWorld().getRegistryKey().getValue().getNamespace().equals("starry_skies")) {
                                    Integer remain_time = LanternTimeManager.getTime(gpos);
                                    if (remain_time == null) remain_time = 0;
                                    remain_time -= TICK_INTERVAL / 20;
                                    if (remain_time < 0) {
                                        LanternTimeManager.removeLantern(gpos);
                                        blockState.with(STARTUP, false);
                                        player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                                                Text.literal("这盏彩灯已经熄灭，再也无法抵御噩梦的侵蚀" )));
                                    } else {
                                        LanternTimeManager.setTime(gpos, remain_time);
                                        player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                                                Text.literal("这盏彩灯的剩余时间：" + remain_time / 60 + "分" + (remain_time % 60) + "秒")));
                                    }
                                }
                            }

                        }
                );
                if (safety) break;
            }
            if (safety) onLanternTick();
            // if player near his rtp spawn (beginning lantern)
            else if (rtpSpawn != null && MathHelper.withinCubicOfRadius(player.getPos(), rtpSpawn.toCenterPos(), 2*LANTERN_RADIUS)) {
                    safety = true;
                    onRTPSpawnTick();
            }
            else onUnstableTick();
        }
    }

    public void onStableTick() {
        if (this.player.isSpectator() || this.player.isCreative()) return;
        if (!safetyPrev) {
            Set<RegistryEntry<StatusEffect>> effectsToRemove = new HashSet<>();
            if (!player.getWorld().getRegistryKey().getValue().getNamespace().equals("starry_skies"))
                player.getStatusEffects().forEach((effect) -> {
                if (!effect.getEffectType().value().isBeneficial()) {
                    effectsToRemove.add(effect.getEffectType());
                }
            });
            else {
                player.getStatusEffects().forEach((effect) -> {
                    if (effect.getEffectType().equals(AnnoyingEffects.TANGLING_NIGHTMARE)
                            || effect.getEffectType().equals(AnnoyingEffects.TANGLING_DREAMS)) {
                        effectsToRemove.add(effect.getEffectType());
                    }
                });
                player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                        Text.literal("这个世界噩梦的侵蚀太严重了，彩灯也只能帮助你不再受到更多的诅咒").withColor(0xAA0000)));
            }
            effectsToRemove.forEach(player::removeStatusEffect);
            ServerPlayNetworking.send(player, new AnnoyingBarDisplayPayload(false));
            ServerPlayNetworking.send(player, new AnnoyingBarStagePayload(0));
        }
        cleared = true;
    }

    public void onRTPSpawnTick() {
        onStableTick();
    }

    public void onLanternTick() {
        onStableTick();
        TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> {
            trinkets.forEach((slot, stack) -> {
                int chargeSpeed = player.getWorld().getRegistryKey().getValue().getNamespace().equals("starry_skies") ? 2 : 60;
                if (slot.getId().contains("offhand/glove")) {
                    if (stack.isOf(FLASHLIGHT)) {
                        if (stack.getDamage() > chargeSpeed) stack.setDamage(stack.getDamage() - chargeSpeed);
                        else stack.setDamage(0);
                    }
                    else if (player.getMainHandStack().isOf(FLASHLIGHT)||player.getOffHandStack().isOf(FLASHLIGHT)) {
                        ItemStack flashlight = player.getMainHandStack().isOf(FLASHLIGHT) ? player.getMainHandStack() : player.getOffHandStack();
                        if (flashlight.getDamage() > chargeSpeed) flashlight.setDamage(flashlight.getDamage() - chargeSpeed);
                        else flashlight.setDamage(0);
                    }
                }
            });
        });
    }

    public void onUnstableTick() {
        if (this.player.isSpectator() || this.player.isCreative()) return;
        ServerPlayNetworking.send(player, new AnnoyingBarDisplayPayload(true));
//        if (safetyPrev) player.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal("噩梦正试图将你吞噬…").withColor(0xEF6F48)));
        boolean inStarrySkies = player.getWorld().getRegistryKey().getValue().getNamespace().equals("starry_skies");
        int consumeSpeed = inStarrySkies ? 2 : 1;
        // First check flashlight
        TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> trinkets.forEach((slot, stack) -> {
            if (slot.getId().contains("offhand/glove")) {
                // 手电在饰品位
                if (stack.isOf(FLASHLIGHT)) {
                    if ((!inStarrySkies) && stack.getDamage()>=stack.getMaxDamage()-consumeSpeed) {
                        this.player.addStatusEffect(new StatusEffectInstance(AnnoyingEffects.TANGLING_DREAMS, 200));
                        cleared = false;
                    }
                    else {
                        if (!cleared) {
                            cleared = true;
                            List<StatusEffectInstance> harmfulEffects = new ArrayList<>();
                            if (!inStarrySkies) player.getStatusEffects().forEach((effect) -> {
                                if (!effect.getEffectType().value().isBeneficial()) {
                                    harmfulEffects.add(effect);
                                }
                            });
                            else {
                                player.getStatusEffects().forEach((effect) -> {
                                    if (effect.getEffectType().equals(AnnoyingEffects.TANGLING_NIGHTMARE)
                                            || effect.getEffectType().equals(AnnoyingEffects.TANGLING_DREAMS)) {
                                        harmfulEffects.add(effect);
                                    }
                                });
                                player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                                        Text.literal("这个世界噩梦的侵蚀太严重了，琪露诺只能帮你不再产生新的诅咒了").withColor(0x55FFFF)));
                            }
                            if (!harmfulEffects.isEmpty()) {
                                harmfulEffects.forEach((effect) -> {
                                    if(stack.getDamage()<stack.getMaxDamage()-3) {
                                        stack.damage(2, player.getServerWorld(), player, item -> {});
                                        player.removeStatusEffect(effect.getEffectType());
                                    } else cleared = false;
                                });
                            }
                        }
                        stack.damage(consumeSpeed, player.getServerWorld(), player, item -> {});;
                    }
                }
                // 护符在饰品位
                else if (stack.isOf(TALISMAN)) {
                    if(stack.getDamage()>=stack.getMaxDamage()-consumeSpeed) {
                        stack.damage(consumeSpeed, player.getServerWorld(), player, item -> {});
                        if (player.getInventory().contains((itemStack) -> itemStack.isOf(TALISMAN))) {
                            for (int i=PlayerInventory.MAIN_SIZE-1;i>0;i--) {
                                ItemStack flashlight = player.getInventory().getStack(i);
                                if (flashlight.isOf(TALISMAN)) {
                                    TrinketItem.equipItem(player, flashlight);
                                    break;
                                }
                            }
                        }
                    }
                    else stack.damage(consumeSpeed, player.getServerWorld(), player, item -> {});
                    this.player.addStatusEffect(new StatusEffectInstance(AnnoyingEffects.TANGLING_DREAMS, 200));
                    cleared = false;
                }
                else {
                    // 手电在手上
                    if (player.getMainHandStack().isOf(FLASHLIGHT)||player.getOffHandStack().isOf(FLASHLIGHT)) {
                        ItemStack flashlight = player.getMainHandStack().isOf(FLASHLIGHT) ? player.getMainHandStack() : player.getOffHandStack();
                        if ((!inStarrySkies) && flashlight.getDamage()>=flashlight.getMaxDamage()-consumeSpeed) {
                            this.player.addStatusEffect(new StatusEffectInstance(AnnoyingEffects.TANGLING_DREAMS, 200));
                            cleared = false;
                        }
                        else {
                            if (!cleared) {
                                cleared = true;
                                List<StatusEffectInstance> harmfulEffects = new ArrayList<>();
                                if (!inStarrySkies) player.getStatusEffects().forEach((effect) -> {
                                    if (!effect.getEffectType().value().isBeneficial()) {
                                        harmfulEffects.add(effect);
                                    }
                                });
                                else {
                                    player.getStatusEffects().forEach((effect) -> {
                                        if (effect.getEffectType().equals(AnnoyingEffects.TANGLING_NIGHTMARE)
                                                || effect.getEffectType().equals(AnnoyingEffects.TANGLING_DREAMS)) {
                                            harmfulEffects.add(effect);
                                        }
                                    });
                                    player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                                            Text.literal("这个世界噩梦的侵蚀太严重了，琪露诺只能帮你不再产生新的诅咒了").withColor(0x55FFFF)));
                                }
                                if (!harmfulEffects.isEmpty()) {
                                    harmfulEffects.forEach((effect) -> {
                                        if(flashlight.getDamage()<flashlight.getMaxDamage()-3) {
                                            flashlight.damage(2, player.getServerWorld(), player, item -> {});
                                            player.removeStatusEffect(effect.getEffectType());
                                        } else cleared = false;
                                    });
                                }
                            }
                            flashlight.damage(consumeSpeed, player.getServerWorld(), player, item -> {});;
                        }
                    }
                    // 护符在手上
                    else if (player.getMainHandStack().isOf(TALISMAN)||player.getOffHandStack().isOf(TALISMAN)) {
                        ItemStack talisman = player.getMainHandStack().isOf(TALISMAN) ? player.getMainHandStack() : player.getOffHandStack();
                        talisman.damage(1, player.getServerWorld(), player, item -> {});
                        this.player.addStatusEffect(new StatusEffectInstance(AnnoyingEffects.TANGLING_DREAMS, 200));
                    } else {
                        this.player.addStatusEffect(new StatusEffectInstance(AnnoyingEffects.TANGLING_NIGHTMARE, 200));
                    }
                    cleared = false;
                }
            }
        }));
    }

    public void onDeath() {
        LSTick = invincibleSec = 0;
    }

    public void onRespawn() {
        invincibleSec = INVINCIBLE_SECONDS;
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

    public boolean getSafety() {
        return safety;
    }

    public boolean getSafetyPrev() {
        return safetyPrev;
    }

    public void setInvincibleSec(int invincibleSec) {
        this.invincibleSec = invincibleSec;
    }

    public void initTrinkets() {

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
