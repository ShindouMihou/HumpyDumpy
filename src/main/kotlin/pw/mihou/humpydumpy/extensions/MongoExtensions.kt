package pw.mihou.humpydumpy.extensions

import org.bson.Document
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.javacord.api.event.server.member.ServerMemberJoinEvent
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Date

/**
 * Gets the basic information of the user such as its user identifier, name
 * discriminator, creation date and its avatar at this time.
 */
val User.info
    get() = mapOf(
        "id" to id,
        "name" to name,
        "discriminator" to discriminator,
        "creation_date" to Date.from(creationTimestamp),
        "avatar" to avatar.url.toExternalForm()
    )

/**
 * Prepares a Document representation of the user information, this can be used to
 * insert into the database for a snapshot of the user's information at the time of join.
 */
fun User.bson() = Document(info)

/**
 * Prepares a Document representation of the join event, this can be used to insert into
 * the database for a snapshot of the join event.
 */
fun ServerMemberJoinEvent.bson() = Document(mapOf(
    "timestamp" to server.getJoinedAtTimestamp(user).map { Date.from(it) }.orElse(Date.from(Instant.now())),
    "server" to server.id,
    "user" to user.bson()
))

/**
 * Gets the basic information of the server such as its name and identifier.
 */
val Server.info
    get() = mapOf("id" to id, "name" to name)