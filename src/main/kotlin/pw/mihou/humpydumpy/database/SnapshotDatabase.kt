package pw.mihou.humpydumpy.database

import com.mongodb.client.AggregateIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.CreateCollectionOptions
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import com.mongodb.client.model.TimeSeriesOptions
import org.bson.Document
import org.bson.conversions.Bson
import pw.mihou.humpydumpy.HumpyDumpy
import java.time.Instant

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

}