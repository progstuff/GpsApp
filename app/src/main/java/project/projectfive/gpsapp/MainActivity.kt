package project.projectfive.gpsapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import project.projectfive.gpsapp.db.LocationChain
import project.projectfive.gpsapp.db.LocationData

class MainActivity : AppCompatActivity() {
    lateinit var gpsViewModel:GpsViewModel
    lateinit var  gpsChainViewModel:GpsChainViewModel
    lateinit var nv:NavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gpsViewModel = ViewModelProvider(this).get(GpsViewModel::class.java)
        gpsChainViewModel = ViewModelProvider(this).get(GpsChainViewModel::class.java)
        var fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if(fragment == null){
            fragment = GpsFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container,fragment)
                .commit()
        }
        nv = findViewById(R.id.navigation)

        val pointOneObserver = Observer<LocationData>{ data ->
            if(data != null)
                gpsViewModel.setPointA(data.lat, data.lon, data.alt)
        }
        val pointTwoObserver = Observer<LocationData>{ data ->
            if(data != null)
                gpsViewModel.setPointB(data.lat, data.lon, data.alt)
        }

        val chainsObserver = Observer<List<LocationChain>>{data ->
            Log.d("CHAINS","" + data.size)
            nv.menu.removeItem(0)
            nv.menu.add("${data.size + 1}")
            nv.menu.get(nv.menu.size()-1).setOnMenuItemClickListener {
                Log.d("ITEM","" + data.size)
                val id1 = data.get(data.size - 1).idA
                val id2 = data.get(data.size - 1).idB
                gpsChainViewModel.getPointOne(id1).observe(this, pointOneObserver)
                gpsChainViewModel.getPointTwo(id2).observe(this, pointTwoObserver)
                true
            }
        }

        gpsChainViewModel.getChainsLiveData().observe(this, chainsObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save ->{
                gpsViewModel.saveChainPoints()
            }
            R.id.link ->  Log.d("TEST_DATA","2")
        }

        /*if(item.itemId == R.id.save)
            Log.d("TEST_DATA","1")
        if(item.itemId == R.id.link)
            Log.d("TEST_DATA","2")*/

        return super.onOptionsItemSelected(item)
    }










}
