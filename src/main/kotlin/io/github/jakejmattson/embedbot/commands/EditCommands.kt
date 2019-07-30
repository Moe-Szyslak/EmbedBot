package io.github.jakejmattson.embedbot.commands

import io.github.jakejmattson.embedbot.arguments.*
import io.github.jakejmattson.embedbot.dataclasses.Embed
import io.github.jakejmattson.embedbot.extensions.*
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.arguments.*
import net.dv8tion.jda.core.entities.User
import java.time.LocalDateTime

private const val commandResponseFormat = "Successfully updated the embed %s!"
private const val commandDescriptionFormat = "Set the %s for the currently loaded embed."

@CommandSet("Edit")
fun editCommands() = commands {
    command("SetAuthor") {
        description = commandDescriptionFormat.format("author")
        requiresLoadedEmbed = true
        expect(UserArg)
        execute {
            val user = it.args.component1() as User
            val embed = it.guild!!.getLoadedEmbed()!!

            val url = user.avatarUrl ?: user.defaultAvatarUrl

            embed.setAuthor(user.name, url)
            it.respond(commandResponseFormat.format("author"))
        }
    }

    command("SetColor") {
        description = commandDescriptionFormat.format("color")
        requiresLoadedEmbed = true
        expect(HexColorArg)
        execute {
            val color = it.args.component1() as Int
            val embed = it.guild!!.getLoadedEmbed()!!

            embed.setColor(color)
            it.respond(commandResponseFormat.format("color"))
        }
    }

    command("SetDescription") {
        description = commandDescriptionFormat.format("description")
        requiresLoadedEmbed = true
        expect(SentenceArg)
        execute {
            val description = it.args.component1() as String
            val embed = it.guild!!.getLoadedEmbed()!!

            embed.setDescription(description)
            it.respond(commandResponseFormat.format("description"))
        }
    }

    command("SetFooter") {
        description = commandDescriptionFormat.format("footer")
        requiresLoadedEmbed = true
        expect(UrlArg("Icon URL"), SentenceArg("Text"))
        execute {
            val url = it.args.component1() as String
            val text = it.args.component2() as String
            val embed = it.guild!!.getLoadedEmbed()!!

            embed.setFooter(text, url)
            it.respond(commandResponseFormat.format("footer"))
        }
    }

    command("SetImage") {
        description = commandDescriptionFormat.format("image")
        requiresLoadedEmbed = true
        expect(UrlArg)
        execute {
            val url = it.args.component1() as String
            val embed = it.guild!!.getLoadedEmbed()!!

            embed.setImage(url)
            it.respond(commandResponseFormat.format("image"))
        }
    }

    command("SetThumbnail") {
        description = commandDescriptionFormat.format("thumbnail")
        requiresLoadedEmbed = true
        expect(UrlArg)
        execute {
            val url = it.args.component1() as String
            val embed = it.guild!!.getLoadedEmbed()!!

            embed.setThumbnail(url)
            it.respond(commandResponseFormat.format("thumbnail"))
        }
    }

    command("SetTimestamp") {
        description = commandDescriptionFormat.format("timestamp")
        requiresLoadedEmbed = true
        execute {
            val embed = it.guild!!.getLoadedEmbed()!!
            embed.setTimestamp(LocalDateTime.now())
            it.respond(commandResponseFormat.format("timestamp"))
        }
    }

    command("SetTitle") {
        description = commandDescriptionFormat.format("title")
        requiresLoadedEmbed = true
        expect(SentenceArg)
        execute {
            val title = it.args.component1() as String
            val embed = it.guild!!.getLoadedEmbed()!!

            embed.setTitle(title)
            it.respond(commandResponseFormat.format("title"))
        }
    }

    command("Clear") {
        description = "Clear a target field from the loaded embed."
        requiresLoadedEmbed = true
        expect(arg(WordArg("Clear Target"), optional = true, default = ""))
        execute {
            val field = (it.args.component1() as String).toLowerCase()
            val embed = it.guild!!.getLoadedEmbed()!!
            val options = "Options:\nAuthor, Color, Description, Footer, Image, Thumbnail, Timestamp, Title \nAll, Fields, Non-Fields"

            with (embed) {
                when (field) {
                    "author" -> clearAuthor()
                    "color" -> clearColor()
                    "description" -> clearDescription()
                    "footer" -> clearFooter()
                    "image" -> clearImage()
                    "thumbnail" -> clearThumbnail()
                    "timestamp" -> clearTimestamp()
                    "title" -> clearTitle()

                    "all" -> clear()
                    "fields" -> clearFields()
                    "non-fields" -> clearNonFields()
                    else -> null
                }
            } ?: return@execute it.respond("Invalid field selected. $options")

            it.respond("Successfully cleared $field")
        }
    }

    command("Rename") {
        description = "Change the name of an existing embed."
        requiresLoadedEmbed = true
        expect(arg(EmbedArg, optional = true, default = { it.guild!!.getLoadedEmbed() as Any }), arg(WordArg("New Name")))
        execute {
            val targetEmbed = it.args.component1() as Embed
            val newName = it.args.component2() as String
            val guild = it.guild!!

            if (guild.hasEmbedWithName(newName))
                return@execute it.respond("An embed with this name already exists.")

            if (targetEmbed.isLoaded(guild)) {
                val updatedEmbed = guild.getEmbedByName(targetEmbed.name)!!
                updatedEmbed.name = newName
                guild.loadEmbed(updatedEmbed)
            }
            else {
                targetEmbed.name = newName
            }

            it.respond("Successfully changed the name of the embed to: $newName")
        }
    }
}