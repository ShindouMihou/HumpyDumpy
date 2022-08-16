package pw.mihou.humpydumpy.extensions

import pw.mihou.humpydumpy.HumpyDumpy
import java.time.ZonedDateTime

/**
 * Translates the string into a [ZonedDateTime] given that the string matches the
 * modified RFC 1123 date time format.
 * <br><br>
 * The modified RFC 1123 format is basically the RFC 1123 but without the week of day.
 * An example of it would be: `3 Jun 2008 11:05:30 GMT`
 *
 * @return A [ZonedDateTime] of the String.
 */
fun String.MODIFIED_RFC_1123_DATE(): ZonedDateTime {
    return ZonedDateTime.parse(this, HumpyDumpy.MODIFIED_RFC_1123_FORMATTER)
}