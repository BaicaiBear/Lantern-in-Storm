package top.bearcabbage.lanterninstorm;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.NbtDataStorage;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlocks;
import top.bearcabbage.lanterninstorm.lantern.BeginningLanternEntity;
import top.bearcabbage.lanterninstorm.lantern.BorderParticle;
import top.bearcabbage.lanterninstorm.player.LiSPlayer;
import top.bearcabbage.lanterninstorm.player.Player;
import top.bearcabbage.lanterninstorm.player.PlayerEventHandler;
import top.bearcabbage.lanterninstorm.lantern.LanternTimeManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import static top.bearcabbage.lanterninstorm.LanternInStormItems.FLASHLIGHT;


public class LanternInStorm implements ModInitializer {
	public static final String MOD_ID = "lantern-in-storm";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final NbtDataStorage LSData = new NbtDataStorage("LS_Data");

	public int serverTick = 0;

	public static final int TICK_INTERVAL = 20;
	public static final int LANTERN_RADIUS = 8;

	@Override
	public void onInitialize() {
		// 获取配置文件
		PlayerDataApi.register(LSData);
		// 加载彩灯时间数据
		LanternTimeManager.load();
		// 注册内容
		SpiritLanternBlocks.initialize();
		BeginningLanternEntity.initialize();
		LanternInStormItems.initialize();
		// 注册事件
		PlayerEventHandler.register();
		ServerPlayConnectionEvents.INIT.register((handler, server) -> {
			PlayerEventHandler.onTick(handler.player);
		});
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			((LiSPlayer)newPlayer).getLS().onRespawn();
		});
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (++serverTick % (60 * TICK_INTERVAL) == 0) LanternTimeManager.saveIfDirty();
		});

		//注册边界粒子
		BorderParticle.registerModParticles();

		// 注册服务器关闭时保存彩灯时间
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			LanternTimeManager.saveIfDirty();
		});
	}
}