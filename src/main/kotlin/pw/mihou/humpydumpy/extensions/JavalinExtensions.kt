package pw.mihou.humpydumpy.extensions

import io.javalin.http.Context
import pw.mihou.humpydumpy.HumpyDumpy
import pw.mihou.humpydumpy.logger.objectsToDocument

/**
 * Sends a JSON result based on the given objects, this uses MongoDB's JSON writer to
 * create a neat and compact JSON body.
 *
 * @param objects The objects to add onto the JSON Object.
 * @return The context for chain-calling methods.
 */
fun Context.result(vararg objects: Pair<String, Any?>) =
    result(objectsToDocument(*objects).toJson(HumpyDumpy.JSON_WRITER_SETTINGS))