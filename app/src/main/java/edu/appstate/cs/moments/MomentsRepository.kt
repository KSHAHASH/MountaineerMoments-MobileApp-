package edu.appstate.cs.moments

import android.content.Context
import androidx.room.Room
import edu.appstate.cs.moments.database.MomentsDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

private const val DATABASE_NAME = "moments-database"
class MomentsRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
) {
    private val database: MomentsDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            MomentsDatabase::class.java,
            DATABASE_NAME
        )
        .build()

    fun getMoments(): Flow<List<Moment>> = database.momentsDao().getMoments()

    suspend fun getMoment(id: UUID): Moment = database.momentsDao().getMoment(id)

    fun updateMoment(moment: Moment) {
        coroutineScope.launch {
            database.momentsDao().updateMoment(moment)
        }
    }

    suspend fun addMoment(moment: Moment) {
        database.momentsDao().addMoment(moment)
    }

    fun deleteMoment(moment: Moment) {
        coroutineScope.launch {
            database.momentsDao().deleteMoment(moment)
        }
    }

    companion object {
        private var INSTANCE: MomentsRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = MomentsRepository(context)
            }
        }

        fun get(): MomentsRepository {
            return INSTANCE
                ?: throw IllegalStateException("MomentsRepository must be initialized")
        }
    }
}