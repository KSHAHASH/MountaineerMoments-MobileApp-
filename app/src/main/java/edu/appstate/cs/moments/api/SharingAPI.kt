package edu.appstate.cs.moments.api

import edu.appstate.cs.moments.Moment
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import java.io.File

interface SharingAPI {
    @POST("/moment")
    suspend fun shareMoment(@Body moment: Moment)

    @POST("/file")
    suspend fun shareMomentFile(@Body body: MultipartBody)
}