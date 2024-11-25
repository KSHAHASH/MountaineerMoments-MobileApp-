package edu.appstate.cs.moments

import com.squareup.moshi.Moshi
import edu.appstate.cs.moments.api.SharingAPI
import edu.appstate.cs.moments.api.SharingAdapter
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.io.File
import java.util.concurrent.TimeUnit

const val ENDPOINT_URL = "https://moments-restful-82390038769.us-east1.run.app/";

class SharingRepository {
    private val sharingAPI: SharingAPI

    init {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val moshiConfig = Moshi.Builder()
            .add(SharingAdapter())
            .build()

        // NOTE: Uncomment the .addInterceptor call if you want detailed logging information
        // to appear in Logcat. This can be useful if you are having trouble getting something
        // related to your API calls working.
        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(logging)
            .callTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(ENDPOINT_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshiConfig))
            .client(okHttpClient)
            .build()

        sharingAPI = retrofit.create()
    }

    suspend fun shareMoment(moment: Moment) {
        sharingAPI.shareMoment(moment)
    }

    suspend fun shareMomentFile(file: File) {
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, file.asRequestBody())
            .build()
        sharingAPI.shareMomentFile(body)
    }
}