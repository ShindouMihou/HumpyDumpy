package pw.mihou.humpydumpy.database

import com.mongodb.client.AggregateIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.*
import org.bson.Document
import org.bson.conversions.Bson
import pw.mihou.humpydumpy.HumpyDumpy
import pw.mihou.humpydumpy.extensions.ISO_DATE_TIME
import java.time.Instant
import java.util.Date

object SnapshotDatabase {

    val connection: MongoCollection<Document> = HumpyDumpy.MONGO.getDatabase("humpy_dumpy")
        .getCollection("snapshots")

    fun index() {
        if (!HumpyDumpy.MONGO.getDatabase("humpy_dumpy").listCollectionNames().any { it == "snapshots" }) {
            HumpyDumpy.MONGO.getDatabase("humpy_dumpy")
                .createCollection("snapshots",
                    CreateCollectionOptions().timeSeriesOptions(
                        TimeSeriesOptions("timestamp").metaField("server")
                    )
                )
        }
    }

    /**
     * Collects all the joins of a given server given a range of time in the form of a date.
     * <br><br>
     * For example, if we want to collect all joins that happened from a given date to another given date then
     * we can use both dates as the parameter. (straightforward).
     *
     * @param server The server to collect.
     * @param before The before range to filter the data.
     * @param after The after range to filter the data.
     * @return A frozen snapshot of the joins from the given server within the given range of time.
     */
    fun range(server: Long, before: Instant? = null, after: Instant? = null): AggregateIterable<Document> {
        val filters = mutableListOf<Bson>(Filters.eq("server", server))

        if (before != null) {
            filters.add(Filters.lte("timestamp", before))
        }

        if (after != null) {
            filters.add(Filters.gte("timestamp", after))
        }

        return connection.aggregate(listOf(
            Aggregates.match(Filters.and(filters)),
            Aggregates.project(Projections.exclude("_id"))
        ))
    }

    fun range(server: Long, from: Long? = null, to: Long? = null): AggregateIterable<Document> {
        if (from == null && to == null)
            throw IllegalArgumentException("To and From cannot be null at the same time.")

        val times = timestamps(server, from, to).map { entry ->
            val timestamps = entry.value.map { document ->
                document.getList("timestamp", Date::class.java)
                    .map { it }
                    .maxOf { it }
            }.sorted()

            return@map entry.key to timestamps.first()
        }.toMap()

        // This may cause confusion like I did but before should be mapped with to
        // and after should be mapped with from because (timestamp < before && timestamp > after).
        val before: Instant? = times[to]?.toInstant()
        val after: Instant? = times[from]?.toInstant()

        HumpyDumpy.LOGGER.info("Before: $before, After: $after")
        return range(server, before, after)
    }

    private fun timestamps(server: Long, vararg users: Long?) = connection.aggregate(listOf(
        Aggregates.match(
            Filters.eq("server", server)
        ),
        Aggregates.match(
            Filters.or(users.filterNotNull().map { user -> Filters.eq("user.id", user) })
        ),
        Aggregates.group(
            "\$user.id",
            Accumulators.addToSet("timestamp","\$timestamp")
        )
    )).groupBy { it.getLong("_id") }

}