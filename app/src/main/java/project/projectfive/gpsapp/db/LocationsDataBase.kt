package project.projectfive.gpsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = arrayOf(LocationData::class), version = 1)
abstract class LocationsDataBase: RoomDatabase() {
    companion object{
        private var INSTANCE: LocationsDataBase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): LocationsDataBase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocationsDataBase::class.java,
                    "locationDataBase"
                )
                .addCallback(LocationDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    abstract fun locationDataDao():LocationDataDao

    private class LocationDatabaseCallback(private val scope:CoroutineScope): RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populate(database.locationDataDao())
                }
            }
        }

        fun populate(locationDao:LocationDataDao){
            val pointA = LocationData(0.0,0.0,0.0,true,"pointA")
            val pointB = LocationData(0.0,0.0,0.0,true,"pointB")
            locationDao.insert(pointA)
            locationDao.insert(pointB)
        }
    }

}

