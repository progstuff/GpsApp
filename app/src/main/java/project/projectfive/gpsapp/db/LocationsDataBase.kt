package project.projectfive.gpsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(LocationData::class), version = 1)
abstract class LocationsDataBase: RoomDatabase() {
    companion object{
        @Volatile private var INSTANCE: LocationsDataBase? = null

        fun getInstance(context: Context): LocationsDataBase = INSTANCE ?: synchronized(this){
            INSTANCE ?: buildDatabase(context).also{INSTANCE = it}
        }

        fun buildDatabase(context: Context) = Room.databaseBuilder(context.applicationContext,
            LocationsDataBase::class.java, "Location.db").build()
    }

    abstract fun locationDataDao():LocationDataDao
}