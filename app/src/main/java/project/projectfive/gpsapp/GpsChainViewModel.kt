package project.projectfive.gpsapp


import android.app.Application
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import project.projectfive.gpsapp.db.LocationChain
import project.projectfive.gpsapp.db.LocationData
import project.projectfive.gpsapp.db.LocationDataRepository

class GpsChainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LocationDataRepository
    lateinit var chains:LiveData<List<LocationChain>>
    lateinit var currentChain:LocationChain
    lateinit var pointOne:LocationData
    lateinit var pointTwo:LocationData
    var isLoaded = false
    var isChainLoaded = false
    init {
        repository = LocationDataRepository.getInstance(application, viewModelScope)
        viewModelScope.launch(Dispatchers.IO) {
            repository.test()
        }
    }

    fun getChainsLiveData():LiveData<List<LocationChain>>{
        if(isLoaded)
            return chains
        else{
            isLoaded = true
            chains = repository.getAllChains()
            return chains
        }
    }

    fun setCurChain(ch:LocationChain, gpsViewModel:GpsViewModel){
        currentChain = ch
        val job = GlobalScope.launch(Dispatchers.IO) {
            getOnePoint()
            getTwoPoint()
        }
        job.invokeOnCompletion {
            Log.d("ITEM", "completed")
            updateCurrentPoints(gpsViewModel)
            isChainLoaded = true
        }
    }


    fun getOnePoint(){
        pointOne = repository.getPoint(currentChain.idA)

    }

    fun getTwoPoint(){
        pointTwo = repository.getPoint(currentChain.idB)
    }

    fun getCount():Int{
        return chains.value?.size ?: 0
    }

    fun updateCurrentPoints(m:GpsViewModel){
        m.setPointA(pointOne.lat,pointOne.lon,pointOne.alt)
        m.setPointB(pointTwo.lat,pointTwo.lon,pointTwo.alt)
    }

    fun deleteCurrentChain(gpsViewModel:GpsViewModel, supportActionBar: ActionBar?){
        if(isChainLoaded) {
            val job = viewModelScope.launch(Dispatchers.IO) {
                isChainLoaded = false
                val ch = chains.value
                var ind = -1
                ch?.forEachIndexed {index, element ->
                    if(element.id == currentChain.id) ind = index
                }
                var ch2 = LocationChain("",0,0)
                if(ind > -1){
                    if(ind < ((ch?.size?.minus(1)) as Int)){
                        ch2 = ch.get(ind+1)
                    }
                }
                repository.deleteChain(currentChain)
                if(!(ch2.name.equals("")))
                    setCurChain(ch2, gpsViewModel)
            }
            /*GlobalScope.launch() {
                job.invokeOnCompletion { supportActionBar?.title = currentChain.name }
            }*/

        }

    }




}