package edu.appstate.cs.moments.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.appstate.cs.moments.Moment

@Database(entities = [ Moment::class ], version=1)
@TypeConverters(MomentsTypeConverters::class)
abstract class MomentsDatabase : RoomDatabase() {
    abstract fun momentsDao(): MomentsDao
}