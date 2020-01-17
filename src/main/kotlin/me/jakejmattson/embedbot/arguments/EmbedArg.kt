package me.jakejmattson.embedbot.arguments

import me.jakejmattson.embedbot.dataclasses.Embed
import me.jakejmattson.embedbot.extensions.getEmbedByName
import me.jakejmattson.embedbot.locale.messages
import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.internal.command.*

open class EmbedArg(override val name: String = "Embed") : ArgumentType<Embed>() {
    companion object : EmbedArg()

    override val examples = arrayListOf("")
    override val consumptionType = ConsumptionType.Single
    override fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Embed> {
        val guild = event.guild ?: return ArgumentResult.Error(messages.errors.MISSING_GUILD)

        val embed = guild.getEmbedByName(arg)
            ?: return ArgumentResult.Error("No such embed exists with the name: $arg")

        return ArgumentResult.Success(embed)
    }
}