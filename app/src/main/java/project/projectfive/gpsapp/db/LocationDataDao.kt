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

    @Query("SELECT * FROM LocationData WHERE pointName = 'pointA'")
    fun getCurrentPointA():LiveData<LocationData>

    @Query("SELECT * FROM LocationData WHERE pointName = 'pointB'")
    fun getCurrentPointB():LiveData<LocationData>

    @Query("DELETE FROM LocationData")
    fun deleteAll()

    @Query("Select * FROM LocationData")
    fun getAll():List<LocationData>
}