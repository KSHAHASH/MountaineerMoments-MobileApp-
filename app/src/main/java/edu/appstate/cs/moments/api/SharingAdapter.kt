package edu.appstate.cs.moments.api

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

class SharingAdapter {
    @ToJson
    fun uuidToJson(id: UUID): String = id.toString()

    @FromJson
    fun jsonToUUID(id: String) = UUID.fromString(id)

    @ToJson
    fun timestampToJson(ts: Date): String = SimpleDateFormat.getDateTimeInstance().format(ts)

    @FromJson
    fun jsonToTimestamp(ts: String): Date = SimpleDateFormat.getDateTimeInstance().parse(ts) ?: Date()
}