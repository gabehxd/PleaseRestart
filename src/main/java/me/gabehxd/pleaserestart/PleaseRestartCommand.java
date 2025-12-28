package me.gabehxd.pleaserestart;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.gabehxd.pleaserestart.utils.brigadier.BrigadierExecutor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class PleaseRestartCommand extends BrigadierExecutor {
    public PleaseRestartCommand() {
        super(dispatcher -> {
            LiteralCommandNode<CommandSender> node = dispatcher.register(LiteralArgumentBuilder.<CommandSender>literal("pleaserestart")
                    .executes(context -> {
                        PleaseRestart instance = PleaseRestart.getInstance();
                        PleaseRestartController helper = instance.getRestartController();
                        if (helper.isNeedsRestart()) {
                            if (!context.getSource().hasPermission("pleaserestart.command.pleaserestart.stop")) {
                                context.getSource().sendMessage(Component.text("You do not have permission to change the pending restart!", NamedTextColor.RED));
                                context.getSource().sendMessage(Component.text("The server is already set to restart!", NamedTextColor.RED));
                            } else {
                                context.getSource().sendMessage(Component.text("The server will no longer restart!", NamedTextColor.GOLD));
                                helper.setNeedsRestart(false);
                            }
                            return 1;
                        }
                        if (context.getSource().hasPermission("pleaserestart.command.pleaserestart.start")) {
                            helper.setNeedsRestart(true);
                            context.getSource().sendMessage(Component.text("Server will restart when there are no players on!", NamedTextColor.GOLD));
                            instance.getLogger().info("Checking player count...");
                            helper.attemptRestart();
                        }
                        else
                        {
                            context.getSource().sendMessage(Component.text("You do not have permission to begin a restart!", NamedTextColor.RED));
                        }
                        return 1;
                    })
            );

            dispatcher.getRoot().addChild(buildRedirect("plsrestart", node));
        });
    }
}
