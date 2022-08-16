package pw.mihou.humpydumpy

import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.activity.ActivityType
import org.javacord.api.entity.intent.Intent
import org.javacord.api.util.logging.ExceptionLogger
import pw.mihou.dotenv.Dotenv
import pw.mihou.humpydumpy.config.DumpyConfig
import pw.mihou.humpydumpy.database.SnapshotDatabase
import pw.mihou.humpydumpy.listeners.DumpyActiveStatusListener
import pw.mihou.humpydumpy.listeners.DumpyUserJoinListener
import pw.mihou.humpydumpy.logger.error
import pw.mihou.humpydumpy.logger.info
import pw.mihou.humpydumpy.repositories.DumpyCommandRepository
import pw.mihou.humpydumpy.server.DumpyHttpServer
import java.io.File

fun main() {
    try {
        Dotenv.asReflective(File(".env"), true).reflectTo(DumpyConfig::class.java)

        if (DumpyConfig.JWT_ISSUER.isNullOrEmpty()) {
            throw IllegalStateException("JWT_ISSUER is not configured, please configure that first on the .env")
        }

        if (DumpyConfig.JWT_SECRET.isNullOrEmpty()) {
            throw IllegalStateException("JWT_SECRET is not configured, please configure that first on the .env")
        }

        if (DumpyConfig.DISCORD_TOKEN.isNullOrEmpty()) {
            throw IllegalStateException("DISCORD_TOKEN is not configured, please configure that first on the .env")
        }

        if (DumpyConfig.MONGO_URI.isNullOrEmpty()) {
            throw IllegalStateException("MONGO_URI is not configured, please configure that first on the .env")
        }

        HumpyDumpy.LOGGER.info("event" to "snapshot_database_indexing")
        SnapshotDatabase.index()

        HumpyDumpy.LOGGER.info("event" to "discord_shards_startup", "shards" to DumpyConfig.DISCORD_SHARDS)
        DiscordApiBuilder()
            .setToken(DumpyConfig.DISCORD_TOKEN)
            .addListener(HumpyDumpy.NEXUS)
            .addListener(DumpyUserJoinListener)
            .addListener(DumpyActiveStatusListener)
            .setTotalShards(DumpyConfig.DISCORD_SHARDS)
            .setUserCacheEnabled(false)
            .setIntents(Intent.GUILD_MEMBERS, Intent.GUILDS)
            .loginAllShards()
            .forEach { future -> future.thenAccept(::onShardLogin).exceptionally(ExceptionLogger.get()) }

        DumpyHttpServer.start()
        DumpyCommandRepository.load()
    } catch (exception: Exception) {
        HumpyDumpy.LOGGER.error("error" to "Failed to proceed with initial start-up procedures.")
        exception.printStackTrace()
    }
}

private fun onShardLogin(shard: DiscordApi) {
    HumpyDumpy.NEXUS.shardManager.put(shard)
    HumpyDumpy.LOGGER.info("event" to "shard_login", "shard" to shard.currentShard)

    shard.updateActivity(ActivityType.WATCHING, "Humpy Dumpy sat on the wall.")
    shard.setAutomaticMessageCacheCleanupEnabled(true)
    shard.setMessageCacheSize(10, 60 * 60)
    shard.setReconnectDelay { it * 2 }
}