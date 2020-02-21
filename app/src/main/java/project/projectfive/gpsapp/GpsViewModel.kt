package project.projectfive.gpsapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.Math.pow
import java.lang.Math.sin
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sqrt


class GpsViewModel: ViewModel() {
    var latA = MutableLiveData<Double>()
    var lonA = MutableLiveData<Double>()
    var latB = MutableLiveData<Double>()
    var lonB = MutableLiveData<Double>()
    var az = MutableLiveData<Double>()
    var distance = MutableLiveData<Double>()
    var aExst = false
    var bExst = false

    fun setPointA(lat:Double, lon:Double){
        latA.value = lat
        lonA.value = lon
        aExst = true
        updateAz()
        updateDistance()
    }

    fun setPointB(lat:Double, lon:Double){
        latB.value = lat
        lonB.value = lon
        bExst = true
        updateAz()
        updateDistance()
    }

    fun updateAz(){
        if(aExst and bExst){
            az.value = 1.0
        }
    }
    fun updateDistance(){
        if(aExst and bExst){
            val fi1 = latA.value?.div(180)?.times(PI) as Double
            val fi2 = latB.value?.div(180)?.times(PI) as Double
            val lambda1 = lonA.value?.div(180)?.times(PI) as Double
            val lambda2 = lonB.value?.div(180)?.times(PI) as Double

            val sigma = 2* asin(sqrt(pow(sin((fi2 - fi1)/2),2.0) + cos(fi1)*cos(fi2)*pow(sin((lambda2 - lambda1)/2),2.0)))
            val dist = sigma * 6372795
            distance.value = dist
        }
    }
}