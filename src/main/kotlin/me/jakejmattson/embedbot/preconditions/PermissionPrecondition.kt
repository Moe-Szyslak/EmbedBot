package me.jakejmattson.embedbot.preconditions

import me.jakejmattson.embedbot.extensions.requiredPermissionLevel
import me.jakejmattson.embedbot.locale.messages
import me.jakejmattson.embedbot.services.*
import me.jakejmattson.kutils.api.dsl.command.CommandEvent
import me.jakejmattson.kutils.api.dsl.preconditions.*
import me.jakejmattson.kutils.api.extensions.jda.toMember

class PermissionPrecondition(private val permissionsService: PermissionsService) : Precondition() {
    override fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val command = event.command ?: return Fail()
        val requiredPermissionLevel = command.requiredPermissionLevel
        val guild = event.guild!!
        val member = event.author.toMember(guild)!!

        val response = when (requiredPermissionLevel) {
            Permission.BOT_OWNER -> messages.errors.MISSING_CLEARANCE + " You must be the bot owner."
            Permission.GUILD_OWNER -> messages.errors.MISSING_CLEARANCE + " You must be the guild owner."
            else -> ""
        }

        if (!permissionsService.hasClearance(member, requiredPermissionLevel))
            return Fail(response)

        return Pass
    }

}