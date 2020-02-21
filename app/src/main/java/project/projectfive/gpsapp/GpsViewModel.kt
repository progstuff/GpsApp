package project.projectfive.gpsapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class GpsViewModel: ViewModel() {
    var latA = MutableLiveData<Float>()
    var lonA = MutableLiveData<Float>()
    var latB = MutableLiveData<Float>()
    var lonB = MutableLiveData<Float>()

    fun setPointA(lat:Float, lon:Float){
        latA.value = lat
        lonA.value = lon
    }

    fun setPointB(lat:Float, lon:Float){
        latB.value = lat
        lonB.value = lon
    }
}