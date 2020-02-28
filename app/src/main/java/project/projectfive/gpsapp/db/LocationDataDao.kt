package project.projectfive.gpsapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import project.projectfive.gpsapp.db.LocationData

@Dao
interface LocationDataDao {
    @Insert
    fun insert(point: LocationData)

    @Query("SELECT * FROM LocationData WHERE id = 0")
    fun getCurrentPointA():LiveData<LocationData>

    @Query("SELECT * FROM LocationData WHERE id = 1")
    fun getCurrentPointB():LiveData<LocationData>
}