package project.projectfive.gpsapp.db

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope

class LocationDataRepository (application: Application, scope:CoroutineScope){
    val locationDb:LocationsDataBase
    val locationDao:LocationDataDao
    init {
        locationDb = LocationsDataBase.getDatabase(application.applicationContext, scope)
        locationDao = locationDb.locationDataDao()
    }

    companion object {
        @Volatile private var INSTANCE: LocationDataRepository? = null

        fun getInstance(application: Application, scope: CoroutineScope): LocationDataRepository = INSTANCE ?: synchronized(this){
            INSTANCE ?: LocationDataRepository(application, scope)
        }
    }

    fun insertChain(name:String, pointA:LocationData, pointB:LocationData) {
        locationDao.insertChainAndPoints(name, pointA, pointB)
    }

    fun getAllChains():LiveData<List<LocationChain>>{
        return locationDao.getAllChains()
    }

    fun test(){
        locationDao.testDB()
    }

    fun update(locationData: LocationData){
        locationDao.update(locationData)
    }

    fun getPointA():LiveData<LocationData>{
        return locationDao.getCurrentPointA()
    }
    fun getPointB():LiveData<LocationData>{
        return locationDao.getCurrentPointB()
    }
    fun getPoint(id:Long):LocationData{
        return locationDao.getPoint(id)
    }

    fun getAll():LiveData<List<LocationData>>{
        return locationDao.getAll()
    }




}