package project.projectfive.gpsapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.Math.*
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sqrt


class GpsViewModel: ViewModel() {
    val pointA = MutableLiveData<LocationData>()
    val pointB = MutableLiveData<LocationData>()
    //val calculatedData = MutableLiveData<LocationData>()
    var az = MutableLiveData<Double>()
    var iaz = MutableLiveData<Double>()
    var distance = MutableLiveData<Double>()
    var e = MutableLiveData<Double>()
    var edeg = MutableLiveData<Double>()

    init {
        val data = LocationData(0.0,0.0,0.0, false)
        pointA.value = data
        pointB.value = data
    }

    fun setPointA(lat:Double, lon:Double, alt:Double){
        val data = LocationData(lat,lon,alt, true)
        pointA.value = data
        updateAz()
        updateDistance()
        updateElev()
    }

    fun setPointB(lat:Double, lon:Double, alt:Double){
        val data = LocationData(lat,lon,alt, true)
        pointB.value = data
        updateAz()
        updateDistance()
        updateElev()
    }

    fun updateAz(){

        if(pointA.value?.isExist == true) {
            if (pointB.value?.isExist == true) {
                val fi1 = (pointA.value as LocationData).lat.div(180).times(PI)
                val fi2 = (pointB.value as LocationData).lat.div(180).times(PI)
                val lambda1 = (pointA.value as LocationData).lon.div(180).times(PI)
                val lambda2 = (pointB.value as LocationData).lon.div(180).times(PI)
                val dlambda = lambda2 - lambda1
                var az = atan2(
                    sin(dlambda) * cos(fi2),
                    cos(fi1) * sin(fi2) - sin(fi1) * cos(fi2) * cos(dlambda)
                )
                az = az / (PI) * 180
                if (az < 0) {
                    az = az + 360
                }
                this.az.value = az
                iaz.value = (az + 180.0) % 360
            }
        }

    }

    fun updateDistance(){
        if(pointA.value?.isExist == true) {
            if (pointB.value?.isExist == true) {
                val fi1 = (pointA.value as LocationData).lat.div(180).times(PI)
                val fi2 = (pointB.value as LocationData).lat.div(180).times(PI)
                val lambda1 = (pointA.value as LocationData).lon.div(180).times(PI)
                val lambda2 = (pointB.value as LocationData).lon.div(180).times(PI)

                val sigma = 2 * asin(
                    sqrt(
                        pow(
                            sin((fi2 - fi1) / 2),
                            2.0
                        ) + cos(fi1) * cos(fi2) * pow(sin((lambda2 - lambda1) / 2), 2.0)
                    )
                )
                val dist = sigma * 6372795
                distance.value = dist
            }
        }
    }

    fun updateElev(){
        if(pointA.value?.isExist == true) {
            if (pointB.value?.isExist == true) {
                val a1 = (pointA.value as LocationData).alt
                val a2 = (pointB.value as LocationData).alt
                e.value = a2 - a1
                if(distance.value as Double > 0)
                    edeg.value = atan((a2-a1)/(distance.value) as Double)/PI*180
            }
        }
    }
}