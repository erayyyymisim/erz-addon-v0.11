package com.example.addon.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.commands.SharedSuggestionProvider;

public class CommandExample extends Command {
    public CommandExample() {
        super("example", "Sends a message to chat.", "ex");
    }

    @Override
    public void build(LiteralArgumentBuilder<SharedSuggestionProvider> builder) {
        builder.executes(context -> {
            info("Hello from custom Meteor command!");
            return SINGLE_SUCCESS;
        });

        builder.then(literal("name").then(argument("nameArgument", StringArgumentType.word()).executes(context -> {
            String name = StringArgumentType.getString(context, "nameArgument");
            info("Hello, " + name + "!");
            return SINGLE_SUCCESS;
        })));
    }
}
