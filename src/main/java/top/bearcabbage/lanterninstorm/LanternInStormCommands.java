package top.bearcabbage.lanterninstorm;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import top.bearcabbage.lanterninstorm.interfaces.PlayerAccessor;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

//注册命令并引用对应的TeamManager方法 包括一些简单的使用者判断
public class LanternInStormCommands {

    private static int sendSuccessFeedback(ServerCommandSource source, String message) {
        if (source.getEntity() instanceof ServerPlayerEntity player) {
            player.sendMessage(Text.of(message));
        } else {
            System.out.println(message);
        }
        return 1;
    }

    private static int sendErrorFeedback(ServerCommandSource source, String errorMessage) {
        if (source.getEntity() instanceof ServerPlayerEntity player) {
            player.sendMessage(Text.of(errorMessage));
        } else {
            System.out.println(errorMessage);
        }
        return 0;
    }

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        // ce 命令及其子命令
        LiteralArgumentBuilder<ServerCommandSource> LisRoot = literal("lis")
                .requires(source -> source.hasPermissionLevel(0));

        // 查询level
        LisRoot.then(literal("mass")
                .executes(context -> {
                    // 如果没有提供targetPlayer参数，获取命令发出者作为默认查询对象
                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity targetPlayer = (ServerPlayerEntity) source.getEntity();
                    if (targetPlayer == null) {
                        return sendErrorFeedback(source, "该命令只能由玩家执行");
                    }
                    int mass = (targetPlayer instanceof PlayerAccessor cePlayerAccessor) ? cePlayerAccessor.getLS().getTotalMass() : -1;
                   // int mass = (targetPlayer instanceof PlayerAccessor cePlayerAccessor) ? cePlayerAccessor.getLS().getSpirit().getMass() : -1;
                    return sendSuccessFeedback(source, "您的灵魂质量为: " + mass + "g");
                })
                .then(argument("targetPlayer", EntityArgumentType.player())
                        .executes(context -> {
                            // 如果提供了targetPlayer参数，使用参数指定的玩家
                            ServerCommandSource source = context.getSource();
                            ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "targetPlayer");
                            int mass = 0;//(targetPlayer instanceof PlayerAccessor cePlayerAccessor) ? cePlayerAccessor.getLS().getSpirit().getMass() : -1;
                            return sendSuccessFeedback(source, targetPlayer.getName().getLiteralString() + " 的灵魂质量为: " + mass + "g");
                        })
                ));

        // 设置level子命令
        LisRoot.then(argument("targetPlayer", EntityArgumentType.player())
                .then(literal("add")
                        .then(argument("mass", IntegerArgumentType.integer(0))
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "targetPlayer");
                                    int mass = IntegerArgumentType.getInteger(context, "mass");
                                    LanternInStormSpiritManager.increase_left(targetPlayer.getUuid(),mass);
                                    if (targetPlayer instanceof PlayerAccessor cePlayerAccessor && true) {
                                        return sendSuccessFeedback(source, "成功增重 " + targetPlayer.getName().getLiteralString() + " 的灵魂 " + mass + "g");
                                    } else {
                                        return sendErrorFeedback(source, "失败了呜呜呜");
                                    }
                                })
                        )
                )
                .then(literal("set")
                        .then(argument("mass", IntegerArgumentType.integer(0))
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "targetPlayer");
                                    int mass = IntegerArgumentType.getInteger(context, "mass");
                                    LanternInStormSpiritManager.set_left(targetPlayer.getUuid(),mass);
                                    if (targetPlayer instanceof PlayerAccessor cePlayerAccessor && true) {
                                        return sendSuccessFeedback(source, "成功增重 " + targetPlayer.getName().getLiteralString() + " 的灵魂 " + mass + "g");
                                    } else {
                                        return sendErrorFeedback(source, "失败了呜呜呜");
                                    }
                                })
                        )
                )
                .then(literal("check")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "targetPlayer");
                            int mass = IntegerArgumentType.getInteger(context, "mass");
                            LanternInStormSpiritManager.set_left(targetPlayer.getUuid(),mass);
                            if (targetPlayer instanceof PlayerAccessor cePlayerAccessor && true) {
                                return sendSuccessFeedback(source, "成功增重 " + targetPlayer.getName().getLiteralString() + " 的灵魂 " + mass + "g");
                            } else {
                                return sendErrorFeedback(source, "失败了呜呜呜");
                            }
                        })
                )
                .requires(source -> source.hasPermissionLevel(0))
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    if (source.getEntity() instanceof ServerPlayerEntity player) {
                        int mass = (player instanceof PlayerAccessor cePlayerAccessor) ? cePlayerAccessor.getLS().getTotalMass() : -1;
                        sendSuccessFeedback(source, "您的灵魂值为: " + mass + "");
                        return mass; // 返回等级信息
                    }
                    return 0;
                })
        );
        dispatcher.register(LisRoot);
    }
}