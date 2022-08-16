package pw.mihou.humpydumpy.commands

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.interaction.SlashCommandOption
import org.javacord.api.interaction.SlashCommandOptionChoice
import org.javacord.api.interaction.SlashCommandOptionType
import pw.mihou.humpydumpy.config.DumpyConfig
import pw.mihou.humpydumpy.extensions.embed
import pw.mihou.humpydumpy.extensions.replyWithAsEphemeral
import pw.mihou.humpydumpy.extensions.replyWithError
import pw.mihou.nexus.features.command.facade.NexusCommandEvent
import pw.mihou.nexus.features.command.facade.NexusHandler
import java.time.Instant
import java.util.concurrent.TimeUnit

object TokenCommand: NexusHandler {

    private const val name = "token"
    private const val description = "Generates a short-lived token to access this server's join logs from the API."

    private val enabledInDms = false
    private val defaultEnabledForPermissions = listOf(PermissionType.MANAGE_SERVER)

    private val options = listOf(
        SlashCommandOption.createWithChoices(
            SlashCommandOptionType.LONG,
            "lifespan",
            "The lifespan of this token before it expires.",
            true,
            listOf(
                SlashCommandOptionChoice.create("5 minutes", 5L),
                SlashCommandOptionChoice.create("10 minutes", 10L),
                SlashCommandOptionChoice.create("15 minutes", 15L),
                SlashCommandOptionChoice.create("20 minutes", 20L),
                SlashCommandOptionChoice.create("30 minutes", 30L),
                SlashCommandOptionChoice.create("40 minutes", 40L),
                SlashCommandOptionChoice.create("50 minutes", 50L),
                SlashCommandOptionChoice.create("1 hour", 60L)
            )
        )
    )

    override fun onEvent(event: NexusCommandEvent) {
        val server = event.server.orElse(null)

        if (server == null) {
            event.replyWithError("You cannot use this command in private messages.")
            return
        }

        val lifespan = event.interaction.getOptionLongValueByName("lifespan").orElseThrow()

        try {
            val algorithm = Algorithm.HMAC256(DumpyConfig.JWT_SECRET)
            val token = JWT.create()
                .withExpiresAt(Instant.now().plusMillis(TimeUnit.MINUTES.toMillis(lifespan)))
                .withClaim("authorized_for", server.id)
                .withIssuedAt(Instant.now())
                .withIssuer(DumpyConfig.JWT_ISSUER)
                .sign(algorithm)

            event.replyWithAsEphemeral(
                event.embed.setTitle("Your token has been generated.")
                    .setDescription(
                        "**Do not disclose this token easily** because this token will expose your server's join logs until it is expired. These " +
                            "tokens do not have a way to revoke other than waiting for its expiration time which is as short-lived as possible. " +
                            "\nYou may use this token to dump your join logs from a range of time using the public API." +
                            "\n```" +
                            "\n$token" +
                            "\n```"
                    )
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            event.replyWithError("An error occurred while trying to generate a token, please report this issue " +
                    "to https://github.com/ShindouMihou/HumpyDumpy")
        }


    }
}