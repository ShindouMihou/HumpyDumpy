package pw.mihou.humpydumpy.server

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.mongodb.client.AggregateIterable
import io.javalin.Javalin
import io.javalin.http.ContentType
import io.javalin.http.Context
import org.bson.Document
import pw.mihou.humpydumpy.HumpyDumpy
import pw.mihou.humpydumpy.config.DumpyConfig
import pw.mihou.humpydumpy.database.SnapshotDatabase
import pw.mihou.humpydumpy.extensions.MODIFIED_RFC_1123_DATE
import pw.mihou.humpydumpy.extensions.result
import pw.mihou.humpydumpy.logger.info
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException
import java.util.concurrent.TimeUnit

object DumpyHttpServer {

    private val NUMBER_ONLY_REGEX = "^[0-9]*\$".toRegex()
    private val SERVER: Javalin = Javalin.create {
        it.defaultContentType = "application/json"
        it.ignoreTrailingSlashes = true
        it.enableCorsForAllOrigins()
        it.showJavalinBanner = false
    }

    private const val HUMPY_DUMPY_LYRICS = "Humpy Dumpy sat on a wall. Humpy Dumpy had a great fall. " +
            "All the king's horses and all the king's men; " +
            "Couldn't Put Humpy together again."

    fun start() {
        SERVER.error(404) { context ->
            context.result(
                "message" to HUMPY_DUMPY_LYRICS,
                "fun_fact" to "It's intentionally called Humpy Dumpy and not Humpty Dumpty.",
                "documentations" to "https://github.com/ShindouMihou/HumpyDumpy"
            )
        }

        SERVER.get("/range.json") { context ->
            val result = range(context) ?: return@get

            val onlyIds = context.queryParam("onlyIds")?.equals("true", ignoreCase = true) ?: false

            if (onlyIds) {
                context.result("data" to result.map { it.getEmbedded(listOf("user"), Document::class.java).getLong("id") })
                return@get
            }

            context.result("data" to result)
        }

        SERVER.get("/range.txt") { context ->
            val delimiter = context.queryParam("delimiter") ?: "\n"

            val result = range(context)?.joinToString(delimiter) {
                it.getEmbedded(listOf("user"), Document::class.java).getLong("id").toString()
            } ?: return@get

            context.status(200).contentType(ContentType.TEXT_PLAIN).result(result)
        }

        SERVER.exception(NumberFormatException::class.java) { exception, context ->
            context.status(400).result("error" to "Invalid number provided for a field.", "message" to exception.message)
        }

        SERVER.exception(DateTimeParseException::class.java) { exception, context ->
            context.status(400).result(
                "error" to "Invalid date-time provided for a field, " +
                    "make sure to use Modified RFC 1123 datetime (example: 14 Jun 2017 07:00:00 GMT)",
                "message" to exception.message
            )
        }

        SERVER.after { context ->
            HumpyDumpy.LOGGER.info(
                "route" to context.path(),
                "server" to (context.queryParam("server") ?: "N/A"),
                "before" to (context.queryParam("before") ?: "N/A"),
                "after" to (context.queryParam("after") ?: "N/A")
            )
        }

        SERVER.start(2004)
        HumpyDumpy.LOGGER.info("event" to "http_server_started")
    }

    /**
     * Handles the validation and processing for the range query routes, this queries the database for the data
     * of the server in a given range of time and also validates whether the token is authorized to view tokens
     * under the given context.
     *
     * @param context The context of the request.
     * @return The results of the query, or null if not validated.
     */
    private fun range(context: Context): AggregateIterable<Document>? {
        val token = context.queryParam("token")

        if (token == null) {
            context.status(400).result("error" to "Invalid or missing token parameter.")
            return null
        }

        val beforeInString = context.queryParam("before")
        val afterInString = context.queryParam("after")

        try {

            val algorithm = Algorithm.HMAC256(DumpyConfig.JWT_SECRET)
            val verifier = JWT.require(algorithm)
                .withIssuer(DumpyConfig.JWT_ISSUER)
                .build()

            val decoded = verifier.verify(token)
            val server: Long? = decoded.getClaim("authorized_for").asLong()

            if (server == null) {
                context.status(400).result("error" to "Invalid token parameter.")
                return null
            }

            var before: Instant? = null

            var after: Instant? = null

            if (beforeInString != null) {
                before = if (beforeInString.matches(NUMBER_ONLY_REGEX)) {
                    ZonedDateTime.now(ZoneId.of("UTC")).toInstant().minusMillis(TimeUnit.MINUTES.toMillis(beforeInString.toLong()))
                } else {
                    beforeInString.MODIFIED_RFC_1123_DATE().toInstant()
                }
            }

            if (afterInString != null) {
                after = if (afterInString.matches(NUMBER_ONLY_REGEX)) {
                    ZonedDateTime.now(ZoneId.of("UTC")).toInstant().minusMillis(TimeUnit.MINUTES.toMillis(afterInString.toLong()))
                } else {
                    afterInString.MODIFIED_RFC_1123_DATE().toInstant()
                }
            }

            if (before == null && after == null) {
                context.status(400).result("error" to "Invalid or missing before or after parameters.")
                return null
            }

            return SnapshotDatabase.range(server, before, after)
        } catch (exception: JWTVerificationException) {
            context.status(401).result("error" to "Invalid or expired authorization token.")
            return null
        }
    }

}