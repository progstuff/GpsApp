package project.projectfive.gpsapp.db

import android.app.Application
import androidx.lifecycle.LiveData

class LocationDataRepository (val locationDao:LocationDataDao){
    fun insert(locationData: LocationData) {
        locationDao.insert(locationData)
    }

    fun getPointA():LiveData<LocationData>{
        return locationDao.getCurrentPointA()
    }
    fun getPointB():LiveData<LocationData>{
        return locationDao.getCurrentPointB()
    }
}