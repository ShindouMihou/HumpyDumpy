package pw.mihou.humpydumpy.listeners

import org.javacord.api.entity.activity.ActivityType
import org.javacord.api.event.connection.ReconnectEvent
import org.javacord.api.event.connection.ResumeEvent
import org.javacord.api.listener.connection.ReconnectListener
import org.javacord.api.listener.connection.ResumeListener

object DumpyActiveStatusListener: ResumeListener, ReconnectListener {

    override fun onResume(event: ResumeEvent) {
        event.api.updateActivity(ActivityType.WATCHING, "Humpy Dumpy sat on the wall.")
    }

    override fun onReconnect(event: ReconnectEvent) {
        event.api.updateActivity(ActivityType.WATCHING, "Humpy Dumpy sat on the wall.")
    }

}