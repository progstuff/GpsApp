package project.projectfive.gpsapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import project.projectfive.gpsapp.db.LocationChain
import project.projectfive.gpsapp.db.LocationDataRepository

class GpsChainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LocationDataRepository
    lateinit var chains:LiveData<List<LocationChain>>
    var isLoaded = false
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
}