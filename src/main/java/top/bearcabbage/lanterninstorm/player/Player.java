package top.bearcabbage.lanterninstorm.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import eu.pb4.playerdata.api.PlayerDataApi;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
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
import top.bearcabbage.lanterninstorm.LanternInStormItems;
import top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlock;

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
import static top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlock.STARTUP;


public class Player {

    private final ServerPlayerEntity player;
    private final Set<GlobalPos> lanterns = new HashSet<>();

    private BlockPos rtpSpawn;
    private int spirit;
    private boolean safety;
    private boolean safetyPrev;
    private boolean cleared;
    private boolean hasTalismanPrev;

    private int LSTick;
    private final static int INVINCIBLE_TICK = 10;
    private int invincibleSec = 0;


    public Player(ServerPlayerEntity player) {
        this.player = player;
        NbtCompound data = PlayerDataApi.getCustomDataFor(player, LanternInStorm.LSData);
        if(data == null){
            data = new NbtCompound();
            data.putIntArray("rtpspawn", new int[]{-1});
            data.putInt("spirit", INIT_SPIRIT);
            PlayerDataApi.setCustomDataFor(player, LanternInStorm.LSData, data);
            invincibleSec = INVINCIBLE_TICK;
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
        cleared = false;
    }

    public boolean onTick() {
        if (safeWorlds.contains(player.getServerWorld().getRegistryKey())) {
            return false;
        }
        boolean check = ++LSTick % TICK_INTERVAL == 0;
        if (check) {
            safetyPrev = safety;
            if (invincibleSec > 0) {
                invincibleSec--;
                safety = true;
                return false;
            }
            // if player near his rtp spawn (beginning lantern)
            if (rtpSpawn != null && MathHelper.withinCubicOfRadius(player.getPos(), rtpSpawn.toCenterPos(), 2*LANTERN_RADIUS)) {
                safety = true;
                onRTPSpawnTick();
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
            if (safety) onLanternTick();
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
        cleared = true;
    }

    public void onRTPSpawnTick() {
        onStableTick();
    }

    public void onLanternTick() {
        onStableTick();
        TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> {
            trinkets.forEach((slot, stack) -> {
                if (slot.getId().contains("offhand/glove")) {
                    if (stack.isOf(FLASHLIGHT)) {
                        if (stack.getDamage() > 2) stack.setDamage(stack.getDamage() - 2);
                        else stack.setDamage(0);
                    }
                    else if (player.getMainHandStack().isOf(FLASHLIGHT)||player.getOffHandStack().isOf(FLASHLIGHT)) {
                        ItemStack flashlight = player.getMainHandStack().isOf(FLASHLIGHT) ? player.getMainHandStack() : player.getOffHandStack();
                        if (flashlight.getDamage() > 2) flashlight.setDamage(flashlight.getDamage() - 2);
                        else flashlight.setDamage(0);
                    }
                }
            });
        });
    }

    public void onUnstableTick() {
        if (this.player.isSpectator() || this.player.isCreative()) return;
        if (safetyPrev) player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("噩梦正试图将你吞噬…").withColor(0xEF6F48)));
        // First check flashlight
        TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> trinkets.forEach((slot, stack) -> {
            if (slot.getId().contains("offhand/glove")) {
                // 手电在饰品位
                if (stack.isOf(FLASHLIGHT)) {
                    if (stack.getDamage()==stack.getMaxDamage()-1) {
                        this.player.addStatusEffect(new StatusEffectInstance(AnnoyingEffects.TANGLING_DREAMS, 200));
                        cleared = false;
                    }
                    else {
                        if (!cleared) {
                            cleared = true;
                            List<StatusEffectInstance> harmfulEffects = new ArrayList<>();
                            player.getStatusEffects().forEach((effect) -> {
                                if (!effect.getEffectType().value().isBeneficial()) {
                                    harmfulEffects.add(effect);
                                }
                            });
                            if (!harmfulEffects.isEmpty()) {
                                harmfulEffects.forEach((effect) -> {
                                    if(stack.getDamage()<stack.getMaxDamage()-3) {
                                        stack.damage(2, player.getServerWorld(), player, item -> {});
                                        player.removeStatusEffect(effect.getEffectType());
                                    } else cleared = false;
                                });
                            }
                        }
                        stack.damage(1, player.getServerWorld(), player, item -> {});;
                    }
                }
                // 护符在饰品位
                else if (stack.isOf(TALISMAN)) {
                    if(stack.getDamage()==stack.getMaxDamage()-1) {
                        stack.damage(1, player.getServerWorld(), player, item -> {});
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
                    else stack.damage(1, player.getServerWorld(), player, item -> {});
                    this.player.addStatusEffect(new StatusEffectInstance(AnnoyingEffects.TANGLING_DREAMS, 200));
                    cleared = false;
                }
                else {
                    // 手电在手上
                    if (player.getMainHandStack().isOf(FLASHLIGHT)||player.getOffHandStack().isOf(FLASHLIGHT)) {
                        ItemStack flashlight = player.getMainHandStack().isOf(FLASHLIGHT) ? player.getMainHandStack() : player.getOffHandStack();
                        if (flashlight.getDamage()==flashlight.getMaxDamage()-1) {
                            this.player.addStatusEffect(new StatusEffectInstance(AnnoyingEffects.TANGLING_DREAMS, 200));
                            cleared = false;
                        }
                        else {
                            if (!cleared) {
                                cleared = true;
                                List<StatusEffectInstance> harmfulEffects = new ArrayList<>();
                                player.getStatusEffects().forEach((effect) -> {
                                    if (!effect.getEffectType().value().isBeneficial()) {
                                        harmfulEffects.add(effect);
                                    }
                                });
                                if (!harmfulEffects.isEmpty()) {
                                    harmfulEffects.forEach((effect) -> {
                                        if(flashlight.getDamage()<flashlight.getMaxDamage()-3) {
                                            flashlight.damage(2, player.getServerWorld(), player, item -> {});
                                            player.removeStatusEffect(effect.getEffectType());
                                        } else cleared = false;
                                    });
                                }
                            }
                            flashlight.damage(1, player.getServerWorld(), player, item -> {});;
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
        invincibleSec = INVINCIBLE_TICK;
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

    public void setInvincibleSec(int invincibleSec) {
        this.invincibleSec = invincibleSec;
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
        int spiritCost = 1;
        switch (player.getServerWorld().getRegistryKey().getValue().toString()) {
            case "minecraft:overworld":
                spiritCost = 1;
                break;
            case "minecraft:the_nether":
                spiritCost = 8;
                break;
            case "minecraft:the_end":
                spiritCost = 10;
                break;
        }
        if (!addSpirit(-spiritCost)) {
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
        int spiritCost = 1;
        switch (player.getServerWorld().getRegistryKey().getValue().toString()) {
            case "minecraft:overworld":
                spiritCost = 1;
                break;
            case "minecraft:the_nether":
                spiritCost = 8;
                break;
            case "minecraft:the_end":
                spiritCost = 10;
                break;
        }
        spirit+=spiritCost;
        lanterns.remove(pos);
        Config lanternData = new Config(PlayerDataApi.getPathFor(player).resolve("lantern_data.json"));
        lanternData.set("lanterns", lanterns);
        lanternData.save();
        player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("一盏路灯熄灭了").withColor(0x815C94)));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("你感受到噩梦的迫近...").withColor(0xBBBBBB)));
        player.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal("你的灵魂剩余："+getSpirit()+"点")));
        return true;
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
