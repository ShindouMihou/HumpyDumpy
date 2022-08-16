package pw.mihou.humpydumpy.logger

import ch.qos.logback.classic.Logger
import org.bson.Document
import pw.mihou.humpydumpy.HumpyDumpy

fun objectsToDocument(vararg objects: Pair<String, Any?>): Document {
    val json = Document()

    for ((key, value) in objects) {
        json.append(key, value)
    }

    return json
}

fun Map<String, *>.json() = Document(this)

fun Logger.info(vararg objects: Pair<String, Any>) {
    this.info(objectsToDocument(*objects).toJson(HumpyDumpy.JSON_WRITER_SETTINGS))
}

fun Logger.error(vararg objects: Pair<String, Any>) {
    this.error(objectsToDocument(*objects).toJson(HumpyDumpy.JSON_WRITER_SETTINGS))
}

fun Logger.error(throwable: Throwable, vararg objects: Pair<String, Any>) {
    this.error(objectsToDocument(*objects).toJson(HumpyDumpy.JSON_WRITER_SETTINGS), throwable)
}

fun Logger.debug(vararg objects: Pair<String, Any>) {
    this.debug(objectsToDocument(*objects).toJson(HumpyDumpy.JSON_WRITER_SETTINGS))
}

fun Logger.warn(vararg objects: Pair<String, Any>) {
    this.warn(objectsToDocument(*objects).toJson(HumpyDumpy.JSON_WRITER_SETTINGS))
}