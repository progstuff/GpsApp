package project.projectfive.gpsapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import project.projectfive.gpsapp.db.LocationChain
import project.projectfive.gpsapp.db.LocationData
import project.projectfive.gpsapp.db.LocationDataRepository
import project.projectfive.gpsapp.db.LocationsDataBase
import java.lang.Math.*
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sqrt


class GpsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository:LocationDataRepository

    lateinit var pointA:LiveData<LocationData>
    lateinit var pointB:LiveData<LocationData>

    var az = MutableLiveData<Double>()
    var iaz = MutableLiveData<Double>()
    var distance = MutableLiveData<Double>()
    var e = MutableLiveData<Double>()
    var edeg = MutableLiveData<Double>()
    lateinit var locs:LiveData<List<LocationData>>
    var isLoadedA:Boolean = false
    var isLoadedB:Boolean = false
    init {
        repository = LocationDataRepository.getInstance(application, viewModelScope)

        viewModelScope.launch(Dispatchers.IO) {
            repository.test()
        }
    }

    fun getAPoint():LiveData<LocationData>{
        if(isLoadedA)
            return pointA
        else {
            isLoadedA = true
            pointA = repository.getPointA()
            return pointA
        }
    }
    fun getBPoint():LiveData<LocationData>{
        if(isLoadedB)
            return pointB
        else {
            isLoadedB = true
            pointB = repository.getPointB()
            return pointB
        }
    }

    fun updateCalculatedData(){
        updateAz()
        updateDistance()
        updateElev()
    }

    fun setPointA(lat:Double, lon:Double, alt:Double){

        val v = pointA.value

        v?.lat = lat
        v?.lon = lon
        v?.alt = alt

        viewModelScope.launch(Dispatchers.IO) {
            repository.update(v as LocationData)
        }
        updateCalculatedData()
    }

    fun setPointB(lat:Double, lon:Double, alt:Double){
        val v = pointB.value

        v?.lat = lat
        v?.lon = lon
        v?.alt = alt

        viewModelScope.launch(Dispatchers.IO) {
            repository.update(v as LocationData)
        }


        updateCalculatedData()
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
        } else {
            this.az.value = 361.0
            iaz.value = 361.0
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
        } else {
            distance.value = -1.0
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
        } else {
            e.value = 1000000.0
            edeg.value = 361.0
        }
    }

    fun saveChainPoints(){
        if(pointA.value?.isExist == true) {
            if (pointB.value?.isExist == true) {
                val ida = pointA.value?.id  ?: -1L
                val idb = pointA.value?.id  ?: -1L
                if(ida != -1L && idb != -1L ) {
                    var lat = pointA.value?.lat ?: -1
                    var lon = pointA.value?.lat ?: -1
                    var alt = pointA.value?.lat ?: -1
                    val a = LocationData(lat as Double, lon as Double, alt as Double, true, "p")

                    lat = pointB.value?.lat ?: -1
                    lon = pointB.value?.lat ?: -1
                    alt = pointB.value?.lat ?: -1
                    val b = LocationData(lat as Double, lon as Double, alt as Double, true, "p")
                    Log.d("TEST_DATA","1")
                    viewModelScope.launch (Dispatchers.IO){
                        repository.insertChain(a,b)
                    }

                }
            }
        }
    }

}