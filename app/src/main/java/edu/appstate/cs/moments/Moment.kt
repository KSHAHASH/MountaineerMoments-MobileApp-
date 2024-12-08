package edu.appstate.cs.moments

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

private const val YOUR_NAME = "SHAHASH KANDEL"

// NOTE: We want to use Strings here, not string resources, since in the
// future we will be allowing users of our app to enter these themselves.
// At that point, it would be impossible to use resources for this.
@JsonClass(generateAdapter = true)
@Entity
@Parcelize
data class Moment(
    @PrimaryKey @Json(name = "momentId") val id: UUID,
    @Ignore var postedBy: String = YOUR_NAME,
    var title: String,
    var description: String,
    var timestamp: Date = Date(),
    @Ignore var fromAPI: Boolean = false,
//    @Ignore var imageUrls: List<String> = emptyList()
) : Parcelable {
    constructor(id: UUID, title: String, description: String, timestamp: Date)
            : this(id, YOUR_NAME, title, description, timestamp, false)
}