package pw.mihou.humpydumpy

import ch.qos.logback.classic.Logger
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import org.slf4j.LoggerFactory
import pw.mihou.humpydumpy.config.DumpyConfig
import pw.mihou.nexus.Nexus
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object HumpyDumpy {

    val MONGO: MongoClient = MongoClients.create(MongoClientSettings.builder()
        .applicationName("Humpy Dumpy")
        .applyConnectionString(ConnectionString(DumpyConfig.MONGO_URI))
        .retryWrites(true)
        .retryReads(true)
        .build()
    )

    val LOGGER = LoggerFactory.getLogger("HumpyDumpy") as Logger

    val NEXUS: Nexus = Nexus.builder().build()

    val JSON_WRITER_SETTINGS: JsonWriterSettings = JsonWriterSettings
        .builder()
        .outputMode(JsonMode.RELAXED)
        .dateTimeConverter { value, writer ->
            writer.writeString(
                DateTimeFormatter.RFC_1123_DATE_TIME.format(
                    Instant.ofEpochMilli(value)
                .atOffset(ZoneOffset.UTC)))
        }
        .objectIdConverter { _, writer ->
            writer.writeNull()
        }
        .build()

    // A modified variant of the RFC 1123 that doesn't contain the day of the week.
    // This looks like this: 16 Aug 2022 15:36:11 GMT
    val MODIFIED_RFC_1123_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy H:m:s z")

}