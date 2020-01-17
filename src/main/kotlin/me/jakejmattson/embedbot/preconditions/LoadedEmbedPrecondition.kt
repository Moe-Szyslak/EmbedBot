package me.jakejmattson.embedbot.preconditions

import me.jakejmattson.embedbot.extensions.*
import me.jakejmattson.embedbot.locale.messages
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.*

@Precondition
fun produceHasLoadedEmbedPrecondition() = precondition {
    val command = it.container[it.commandStruct.commandName] ?: return@precondition Pass

    val guild = it.guild ?: return@precondition Fail(messages.errors.MISSING_GUILD)

    if (command.requiresLoadedEmbed && !guild.hasLoadedEmbed())
        return@precondition Fail(messages.errors.MISSING_EMBED)

    return@precondition Pass
}