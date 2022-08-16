package pw.mihou.humpydumpy.extensions

import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.util.logging.ExceptionLogger
import pw.mihou.nexus.features.command.facade.NexusCommandEvent
import java.awt.Color

fun NexusCommandEvent.replyWith(vararg embeds: EmbedBuilder) = respondNow()
    .addEmbeds(*embeds)
    .respond()
    .exceptionally(ExceptionLogger.get())

fun NexusCommandEvent.replyWith(content: String) = respondNow()
    .setContent(content)
    .respond()
    .exceptionally(ExceptionLogger.get())

fun NexusCommandEvent.replyWithAsEphemeral(vararg embeds: EmbedBuilder) = respondNowAsEphemeral()
    .addEmbeds(*embeds)
    .respond()
    .exceptionally(ExceptionLogger.get())

fun NexusCommandEvent.replyWithAsEphemeral(content: String) = respondNowAsEphemeral()
    .setContent(content)
    .respond()
    .exceptionally(ExceptionLogger.get())

fun NexusCommandEvent.replyWithError(error: String) = replyWithAsEphemeral(embed.setColor(Color.RED).setDescription(error))
val NexusCommandEvent.embed
    get() = EmbedBuilder().setAuthor(user).setTimestampToNow().setColor(Color.CYAN)