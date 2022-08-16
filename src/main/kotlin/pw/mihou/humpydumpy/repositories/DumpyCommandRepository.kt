package pw.mihou.humpydumpy.repositories

import pw.mihou.humpydumpy.HumpyDumpy
import pw.mihou.humpydumpy.commands.HelpCommand
import pw.mihou.humpydumpy.commands.TokenCommand
import pw.mihou.humpydumpy.logger.info

object DumpyCommandRepository {

    fun load() {
        HumpyDumpy.NEXUS.listenMany(TokenCommand, HelpCommand)

        // We don't have server only commands, so we can hardcode this at one instead.
        HumpyDumpy.NEXUS.synchronizer.synchronize(1).thenAccept {
            HumpyDumpy.LOGGER.info("event" to "command_synchronization")
        }.exceptionally {
            it.printStackTrace()
            return@exceptionally null
        }
    }

}