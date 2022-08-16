package pw.mihou.humpydumpy.config

object DumpyConfig {
    lateinit var MONGO_URI: String
    lateinit var DISCORD_TOKEN: String

    lateinit var JWT_SECRET: String
    lateinit var JWT_ISSUER: String

    var DISCORD_SHARDS: Int = 1
}