package io.github.jakejmattson.embedbot.commands

import io.github.jakejmattson.embedbot.extensions.*
import io.github.jakejmattson.embedbot.locale.messages
import io.github.jakejmattson.embedbot.services.*
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.stdlib.toMinimalTimeString
import java.util.Date

private val startTime = Date()

@CommandSet("Utility")
fun utilityCommands(infoService: InfoService, permissionsService: PermissionsService) = commands {
    command("Ping") {
        description = messages.descriptions.PING
        execute { event ->
            event.discord.jda.restPing.queue {
                event.respond("Ping: ${it}ms\n")
            }
        }
    }

    command("BotInfo") {
        description = messages.descriptions.BOT_INFO
        execute {
            it.respond(infoService.botInfo(it.guild!!))
        }
    }

    command("Uptime") {
        description = messages.descriptions.UPTIME
        execute {
            val seconds = (Date().time - startTime.time) / 1000

            it.respondEmbed {
                title = "I have been running since"
                description = startTime.toString()

                field {
                    name = "That's been"
                    value = seconds.toMinimalTimeString()
                }
            }
        }
    }
}