package pw.mihou.humpydumpy.listeners

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.javacord.api.event.server.member.ServerMemberJoinEvent
import org.javacord.api.listener.server.member.ServerMemberJoinListener
import pw.mihou.humpydumpy.HumpyDumpy
import pw.mihou.humpydumpy.database.SnapshotDatabase
import pw.mihou.humpydumpy.extensions.bson
import pw.mihou.humpydumpy.extensions.info
import pw.mihou.humpydumpy.logger.error
import pw.mihou.humpydumpy.logger.info
import pw.mihou.humpydumpy.logger.json

object DumpyUserJoinListener: ServerMemberJoinListener {

    override fun onServerMemberJoin(event: ServerMemberJoinEvent) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                HumpyDumpy.LOGGER.info("server" to event.server.info.json(), "user" to event.user.info.json(), "event" to "server_member_join")
                val result = SnapshotDatabase.connection.insertOne(event.bson())

                if (!result.wasAcknowledged()) {
                    HumpyDumpy.LOGGER.error("server" to event.server.info.json(), "user" to event.user.info.json(), "acknowledged" to false)
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

}