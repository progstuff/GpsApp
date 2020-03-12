package project.projectfive.gpsapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.TextView


import androidx.core.app.ActivityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import project.projectfive.gpsapp.db.LocationChain
import project.projectfive.gpsapp.db.LocationData

class MainActivity : AppCompatActivity() {
    lateinit var gpsViewModel:GpsViewModel
    lateinit var  gpsChainViewModel:GpsChainViewModel
    lateinit var nv:NavigationView
    lateinit var dl:DrawerLayout
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
        dl = findViewById(R.id.dl)
        val pointOneObserver = Observer<LocationData>{ data ->
            if(data != null) {
                //gpsViewModel.setPointA(data.lat, data.lon, data.alt)
                Log.d("TEST","1")
            }
        }
        val pointTwoObserver = Observer<LocationData>{ data ->
            if(data != null){
                //gpsViewModel.setPointB(data.lat, data.lon, data.alt)
            }

        }

        val chainsObserver = Observer<List<LocationChain>>{data ->
            Log.d("CHAINS","" + data.size)
            nv.menu.clear()
            for(d in data){
                nv.menu.add("${d.name}")

                nv.menu.get(nv.menu.size()-1).setOnMenuItemClickListener {
                    Log.d("ITEM", d.name)
                    supportActionBar?.title = d.name
                    gpsChainViewModel.setCurChain(d,gpsViewModel)
                    dl.closeDrawer(Gravity.LEFT)
                    true
                }
            }
        }

        gpsChainViewModel.getChainsLiveData().observe(this, chainsObserver)
        supportActionBar?.title = "ПЫНЯГА"
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_dehaze_black_18dp);
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save ->{
                gpsViewModel.saveChainPoints("${gpsChainViewModel.getCount() + 1}")
            }
            R.id.link ->{

            }
            R.id.delete -> {
                val s = gpsChainViewModel.getCurrentChainName()
                if(!s.equals("")) {
                    val builder = MaterialAlertDialogBuilder(this)
                    val dialogLayout = layoutInflater.inflate(R.layout.delete_dialog, null)
                    builder.setView(dialogLayout)
                    val alert = builder.create()
                    alert.show()
                    val t = dialogLayout.findViewById<TextView>(R.id.name)
                    t.text = s + " ?"

                    val ok = dialogLayout.findViewById<MaterialButton>(R.id.ok)
                    ok.setOnClickListener {
                        supportActionBar?.title = gpsChainViewModel.getNextChainName()
                        gpsChainViewModel.deleteCurrentChain(gpsViewModel, supportActionBar)
                        alert.dismiss()
                    }
                    val cancel = dialogLayout.findViewById<MaterialButton>(R.id.cancel)
                    cancel.setOnClickListener{
                        alert.dismiss()
                    }
                }

            }
            else -> {
                if(dl.isDrawerOpen(Gravity.LEFT))
                    dl.closeDrawer(Gravity.LEFT)
                else
                    dl.openDrawer(Gravity.LEFT)
            }
            //R.id.link ->  Log.d("TEST_DATA","2")
        }

        /*if(item.itemId == R.id.save)
            Log.d("TEST_DATA","1")
        if(item.itemId == R.id.link)
            Log.d("TEST_DATA","2")*/

        return super.onOptionsItemSelected(item)
    }










}
