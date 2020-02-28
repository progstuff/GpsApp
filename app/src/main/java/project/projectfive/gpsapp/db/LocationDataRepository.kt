package project.projectfive.gpsapp.db

import android.app.Application
import androidx.lifecycle.LiveData

class LocationDataRepository (application: Application){
    val locationDb:LocationsDataBase
    val locationDao:LocationDataDao
    init {
        locationDb = LocationsDataBase.getInstance(application.applicationContext)
        locationDao = locationDb.locationDataDao()
    }
    companion object {
        @Volatile private var INSTANCE: LocationDataRepository? = null

        fun getInstance(application: Application): LocationDataRepository = INSTANCE ?: synchronized(this){
            INSTANCE ?: LocationDataRepository(application)
        }
    }



}