package io.github.jakejmattson.embedbot.commands

import com.google.gson.JsonSyntaxException
import io.github.jakejmattson.embedbot.arguments.EmbedArg
import io.github.jakejmattson.embedbot.dataclasses.*
import io.github.jakejmattson.embedbot.extensions.*
import io.github.jakejmattson.embedbot.services.*
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.arguments.*
import net.dv8tion.jda.api.entities.TextChannel

@CommandSet("Core")
fun coreCommands(embedService: EmbedService) = commands {
    command("Send") {
        description = "Send the currently loaded embed."
        requiresLoadedEmbed = true
        expect(arg(TextChannelArg("Channel"), optional = true, default = { it.channel }),
            arg(BooleanArg("shouldTrack"), optional = true, default = false))
        execute {
            val channel = it.args.component1() as TextChannel
            val shouldTrack = it.args.component2() as Boolean
            val embed = it.guild!!.getLoadedEmbed()!!

            if (embed.isEmpty)
                return@execute it.respond("This embed is empty.")

            channel.sendMessage(embed.build()).queue { message ->
                if (shouldTrack)
                    embed.copyLocation = CopyLocation(channel.id, message.id)
            }
            
            if (channel != it.channel)
                it.reactSuccess()
        }
    }

    command("Create") {
        description = "Create a new embed with this name."
        expect(WordArg("Embed Name"))
        execute {
            val embedName = it.args.component1() as String
            val wasCreated = embedService.createEmbed(it.guild!!, embedName)

            if (!wasCreated)
                return@execute it.respond("An embed with this name already exists.")

            it.reactSuccess()
        }
    }

    command("Duplicate") {
        description = "Create a new embed from an existing embed."
        expect(arg(WordArg("Embed Name")), arg(EmbedArg, optional = true, default = { it.guild!!.getLoadedEmbed() }))
        execute {
            val embedName = it.args.component1() as String
            val existingEmbed = it.args.component2() as Embed?
                ?: return@execute it.respond("Please load an embed or specify one explicitly.")

            val embed = createEmbedFromJson(embedName, existingEmbed.toJson())
            val wasCreated = embedService.addEmbed(it.guild!!, embed)

            if (!wasCreated)
                it.respond("An embed with this name already exists.")

            it.reactSuccess()
        }
    }

    command("Delete") {
        description = "Delete the embed with this name."
        expect(arg(EmbedArg, optional = true, default = { it.guild!!.getLoadedEmbed() }))
        execute {
            val embed = it.args.component1() as Embed?
                ?: return@execute it.respond("Please load an embed or specify one explicitly.")

            it.guild!!.removeEmbed(embed)

            it.reactSuccess()
        }
    }

    command("Load") {
        description = "Load the embed with this name into memory."
        expect(EmbedArg)
        execute {
            val embed = it.args.component1() as Embed
            it.guild!!.loadEmbed(embed)
            it.reactSuccess()
        }
    }

    command("Import") {
        description = "Import a JSON String as an embed."
        expect(WordArg("Embed Name"), SentenceArg("JSON"))
        execute {
            val name = it.args.component1() as String
            val json = it.args.component2() as String
            val guild = it.guild!!

            if (guild.hasEmbedWithName(name))
                return@execute it.respond("An embed with this name already exists.")

            try {
                val embed = createEmbedFromJson(name, json)
                val wasAdded = embedService.addEmbed(guild, embed)

                if (!wasAdded)
                    it.respond("An embed with this name already exists")

                it.reactSuccess()
            } catch (e: JsonSyntaxException) {
                it.respond("Invalid JSON! ${e.message?.substringAfter("Exception: ")}")
            }
        }
    }

    command("Export") {
        description = "Export the currently loaded embed to JSON."
        expect(arg(EmbedArg, optional = true, default = { it.guild!!.getLoadedEmbed() }))
        execute {
            val embed = it.args.component1() as Embed?
                ?: return@execute it.respond("Please load an embed or specify one explicitly.")

            val json = embed.toJson()

            if (json.length >= 1985)
                it.channel.sendFile(json.toByteArray(), "$${embed.name}.json").queue()
            else
                it.respond("```json\n$json```")
        }
    }
}