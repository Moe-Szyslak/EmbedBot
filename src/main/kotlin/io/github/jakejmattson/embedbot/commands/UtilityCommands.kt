package io.github.jakejmattson.embedbot.commands

import io.github.jakejmattson.embedbot.dataclasses.getFileSystemLocation
import io.github.jakejmattson.embedbot.locale.messages
import io.github.jakejmattson.embedbot.services.GitHubService
import me.aberrantfox.kjdautils.api.dsl.command.*
import me.aberrantfox.kjdautils.extensions.stdlib.toMinimalTimeString
import java.awt.Color
import java.util.Date
import kotlin.system.exitProcess

private val startTime = Date()

@CommandSet("Utility")
fun utilityCommands(gitHubService: GitHubService) = commands {
    command("Status", "Ping", "Uptime") {
        description = messages.descriptions.STATUS
        execute { event ->
            val jda = event.discord.jda

            jda.restPing.queue { restPing ->
                event.respond {
                    color = Color(0x00bfff)

                    val seconds = (Date().time - startTime.time) / 1000

                    addField("Rest ping", "${restPing}ms")
                    addField("Gateway ping", "${jda.gatewayPing}ms")
                    addField("Total Uptime", seconds.toMinimalTimeString())
                }
            }
        }
    }

    command("Restart") {
        description = "Restart the bot via the JAR file."
        execute {
            //TODO Find the Java binary dynamically
            //val javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java"

            val currentJar = getFileSystemLocation()

            if (currentJar.extension != ".jar")
                return@execute it.respond("Could not restart. The bot needs to be running from a JAR.")

            it.respond("Restarting...")
            ProcessBuilder(arrayListOf("java", "-jar", currentJar.path)).start()
            exitProcess(0)
        }
    }

    command("Update") {
        description = "Update the bot to the latest version."
        execute {
            it.respond(gitHubService.update().message)
        }
    }
}
