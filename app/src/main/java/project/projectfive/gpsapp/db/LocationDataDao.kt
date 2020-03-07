package project.projectfive.gpsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import project.projectfive.gpsapp.db.LocationData

@Dao
interface LocationDataDao {
    @Insert
    fun insert(point: LocationData):Long

    @Query("SELECT * FROM LocationData WHERE id = :id")
    fun getPoint(id:Long):LocationData

    @Query("SELECT * FROM LocationData WHERE name = 'pointA'")
    fun getCurrentPointA():LiveData<LocationData>

    @Update
    fun update(p:LocationData)

    @Query("SELECT * FROM LocationData WHERE name = 'pointB'")
    fun getCurrentPointB():LiveData<LocationData>

    @Query("SELECT * FROM LocationData WHERE name = 't'")
    fun getTest():LocationData

    @Query("DELETE FROM LocationData")
    fun deleteAll()

    @Query("Select * FROM LocationData")
    fun getAll():LiveData<List<LocationData>>

    @Delete
    fun delete(tloc:LocationData)

    @Transaction
    fun testDB(){
        insert(LocationData(0.0,0.0,0.0,false,"t"))
        val r = getTest()
        delete(r)
    }

    @Insert
    fun insertChain(locationChain:LocationChain)

    @Query("Select * FROM LocationChain")
    fun getAllChains():LiveData<List<LocationChain>>

    @Transaction
    fun insertChainAndPoints(name:String, pointA:LocationData, pointB:LocationData){
        val idA = insert(pointA)
        val idB = insert(pointB)
        val chain = LocationChain(name, idA,idB)
        insertChain(chain)
    }
}