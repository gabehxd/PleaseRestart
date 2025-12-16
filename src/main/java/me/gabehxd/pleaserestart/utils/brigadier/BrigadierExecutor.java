package me.gabehxd.pleaserestart.utils.brigadier;

import com.google.common.collect.ObjectArrays;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Creates autocomplete suggestions and onCommand handling automatically.
 */
public class BrigadierExecutor implements TabExecutor {
    protected final CommandDispatcher<CommandSender> commandDispatcher =  new CommandDispatcher<>();

    /**
     * Returning 1 from an executor in the LiteralArgumentBuilder will act as true and 0 and below will act as false.
     *
     * @param dispatcherConsumer The consumer to register commands with.
     */
    public BrigadierExecutor(@NonNull Consumer<CommandDispatcher<CommandSender>> dispatcherConsumer) {
        dispatcherConsumer.accept(commandDispatcher);
    }

    public void registerCommand(PluginCommand command) {
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    public final boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, @NonNull String[] args) {
        try {
            int result = commandDispatcher.execute(getCommandString(alias, args), sender);
            if (result <= 0) {
                sendUsageMessage(sender);
                return true;
            }
        } catch (CommandSyntaxException e) {
            if (e.getMessage() != null)
                sender.sendMessage(Component.text(e.getMessage(), NamedTextColor.RED));

            sendUsageMessage(sender);
            return true;
        }
        return true;
    }

    @Override
    public final @NonNull List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, @NonNull String[] args) {
        String commandString = getCommandString(alias, args);
        Suggestions suggestions = commandDispatcher.getCompletionSuggestions(commandDispatcher.parse(commandString, sender)).join();
        return suggestions.getList().stream().map(Suggestion::getText).collect(Collectors.toList());
    }

    protected void sendUsageMessage(@NonNull CommandSender sender) {
        sender.sendMessage(Component.text("Usages:", NamedTextColor.RED));
        for (String s : commandDispatcher.getAllUsage(commandDispatcher.getRoot(), sender, true)) {
            sender.sendMessage(
                    Component.text().append(Component.text("/", NamedTextColor.RED).append(Component.text(s, NamedTextColor.RED)))
                            .build());
        }
    }

    protected String getCommandString(String alias, String[] args) {
        return String.join(" ", ObjectArrays.concat(alias, args));
    }

    /**
     * Returns a literal node that redirects its execution to
     * the given destination node.
     *
     * <p>This method is taken from MIT licensed code in the Velocity project, see
     * <a href="https://github.com/VelocityPowered/Velocity/blob/b88c573eb11839a95bea1af947b0c59a5956368b/proxy/src/main/java/com/velocitypowered/proxy/util/BrigadierUtils.java#L33">
     * Velocity's BrigadierUtils class</a></p>
     *
     * @param alias       the command alias
     * @param destination the destination node
     * @return the built node
     */
    protected static <S> LiteralCommandNode<S> buildRedirect(
            final String alias,
            final CommandNode<S> destination
    ) {
        // Redirects only work for nodes with children, but break the top argument-less command.
        // Manually adding the root command after setting the redirect doesn't fix it.
        // (See https://github.com/Mojang/brigadier/issues/46) Manually clone the node instead.
        LiteralArgumentBuilder<S> builder = LiteralArgumentBuilder
                .<S>literal(alias)
                .requires(destination.getRequirement())
                .forward(
                        destination.getRedirect(),
                        destination.getRedirectModifier(),
                        destination.isFork()
                )
                .executes(destination.getCommand());
        for (final CommandNode<S> child : destination.getChildren()) {
            builder.then(child);
        }
        return builder.build();
    }
}
