package edu.appstate.cs.moments.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import edu.appstate.cs.moments.Moment
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface MomentsDao {
    @Query("SELECT * FROM moment")
    fun getMoments(): Flow<List<Moment>>

    @Query("SELECT * FROM moment WHERE id=(:id)")
    suspend fun getMoment(id: UUID): Moment

    @Update
    suspend fun updateMoment(moment: Moment)

    @Insert
    suspend fun addMoment(moment: Moment)

    @Delete
    suspend fun deleteMoment(moment: Moment)}