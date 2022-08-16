package pw.mihou.humpydumpy.commands

import org.javacord.api.entity.permission.PermissionType
import pw.mihou.humpydumpy.extensions.embed
import pw.mihou.humpydumpy.extensions.replyWithAsEphemeral
import pw.mihou.nexus.features.command.facade.NexusCommandEvent
import pw.mihou.nexus.features.command.facade.NexusHandler

object HelpCommand: NexusHandler {

    private const val name = "help"
    private const val description = "Views information about how to use Humpy Dumpy."

    private val enabledInDms = false
    private val defaultEnabledForPermissions = listOf(PermissionType.MANAGE_SERVER)

    override fun onEvent(event: NexusCommandEvent) {
        event.replyWithAsEphemeral(
            event.embed
                .setDescription(
                    "**Humpy Dumpy** is a simple Discord bot that aims to provide a snapshot of the join logs given a range of time for servers. " +
                            "\n\nTo learn more about how to use Humpy Dumpy, please read the GitHub repository at https://github.com/ShindouMihou/HumpyDumpy"
                )
        )
    }
}