package project.projectfive.gpsapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.Location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.*
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import project.projectfive.gpsapp.db.LocationData
import java.text.SimpleDateFormat
import java.util.*

class GpsFragment: Fragment(){
    lateinit var txtLat: TextView
    lateinit var txtLong: TextView
    lateinit var txtTime: TextView
    lateinit var txtAlt: TextView

    lateinit var aButton:MaterialButton
    lateinit var aLat:TextView
    lateinit var aLon:TextView
    lateinit var aAlt:TextView
    lateinit var bButton:MaterialButton
    lateinit var bLat:TextView
    lateinit var bLon:TextView
    lateinit var bAlt:TextView
    lateinit var az:TextView
    lateinit var iaz:TextView
    lateinit var dist:TextView
    lateinit var e:TextView
    lateinit var edeg:TextView
    lateinit var satelite:TextView

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 2000
    private val FASTEST_INTERVAL: Long = 1000
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10

    lateinit var gpsViewModel:GpsViewModel
    var coordsExist = false

    companion object {
        fun newInstance(): GpsFragment {
            return GpsFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gpsViewModel = ViewModelProvider(this).get(GpsViewModel::class.java)
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.gps_layout, container, false)


        mLocationRequest = LocationRequest()


        getUIElements(view)

        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }

        if (checkPermissionForLocation(activity as MainActivity)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locationManager.registerGnssStatusCallback(object: GnssStatus.Callback(){
                    override fun onStarted() {
                        super.onStarted()

                    }

                    override fun onSatelliteStatusChanged(status: GnssStatus?) {
                        super.onSatelliteStatusChanged(status)
                        satelite.text = status?.satelliteCount.toString()
                        Log.d("GPS","GPS DATA")
                    }
                })
            } else {
                //GpsSate
            }
            startLocationUpdates()
        }

        updateModel()

        updateUI()


        return view
    }

    private fun updateModel(){
        aButton.setOnClickListener {
            if(coordsExist)
                gpsViewModel.setPointA((txtLat.text as String).toDouble(), (txtLong.text as String).toDouble(), (txtAlt.text as String).toDouble())
        }
        bButton.setOnClickListener {
            if(coordsExist)
                gpsViewModel.setPointB((txtLat.text as String).toDouble(), (txtLong.text as String).toDouble(), (txtAlt.text as String).toDouble())
        }
    }

    private fun getUIElements(view:View){
        txtLat = view.findViewById(R.id.txtLat);
        txtLong = view.findViewById(R.id.txtLong);
        txtTime = view.findViewById(R.id.txtTime);
        txtAlt = view.findViewById(R.id.txtAlt);
        satelite = view.findViewById(R.id.satelites_count)

        aButton = view.findViewById(R.id.ba)
        bButton = view.findViewById(R.id.bb)
        aLat = view.findViewById(R.id.lat1)
        aLon = view.findViewById(R.id.lon1)
        aAlt = view.findViewById(R.id.alt1)
        bLat = view.findViewById(R.id.lat2)
        bLon = view.findViewById(R.id.lon2)
        bAlt = view.findViewById(R.id.alt2)
        az = view.findViewById(R.id.az)
        iaz = view.findViewById(R.id.az_inv)
        dist = view.findViewById(R.id.dist)
        e = view.findViewById(R.id.elev)
        edeg = view.findViewById(R.id.elev_deg)
    }

    private fun updateUI(){
        val pointAObserver = Observer<LocationData>{ data ->
            if(data.isExist) {
                aLat.text = "%.7f".format(data.lat).replace(",", ".")
                aLon.text = "%.7f".format(data.lon).replace(",", ".")
                aAlt.text = "%.2f".format(data.alt).replace(",", ".")
            }
        }
        val pointBObserver = Observer<LocationData>{ data ->
            if(data.isExist) {
                bLat.text = "%.7f".format(data.lat).replace(",", ".")
                bLon.text = "%.7f".format(data.lon).replace(",", ".")
                bAlt.text = "%.2f".format(data.alt).replace(",", ".")
            }
        }

        val azObserver = Observer<Double>{data ->
            if(data != 361.0)
                az.text = "%.2f".format(data).replace(",",".")
            else
                az.text = getString(R.string.wait)
        }
        val iazObserver = Observer<Double>{data ->
            if(data != 361.0)
                iaz.text = "%.2f".format(data).replace(",",".")
            else
                iaz.text = getString(R.string.wait)
        }
        val distObserver = Observer<Double>{data ->
            if(data != -1.0)
                dist.text = "%.2f".format(data).replace(",",".")
            else
                dist.text = getString(R.string.wait)
        }
        val eObserver = Observer<Double>{data ->
            if(data != 1000000.0)
                e.text = "%.2f".format(data).replace(",",".")
            else
                e.text = getString(R.string.wait)
        }
        val eDegObserver = Observer<Double>{data ->
            if(data != 361.0)
                edeg.text = "%.2f".format(data).replace(",",".")
            else
                edeg.text = getString(R.string.wait)
        }
        gpsViewModel.pointA.observe(viewLifecycleOwner, pointAObserver)
        gpsViewModel.pointB.observe(viewLifecycleOwner, pointBObserver)
        gpsViewModel.az.observe(viewLifecycleOwner,azObserver)
        gpsViewModel.iaz.observe(viewLifecycleOwner,iazObserver)
        gpsViewModel.distance.observe(viewLifecycleOwner,distObserver)
        gpsViewModel.e.observe(viewLifecycleOwner,eObserver)
        gpsViewModel.edeg.observe(viewLifecycleOwner,eDegObserver)
    }

    private fun buildAlertMessageNoGps() {

        val builder = AlertDialog.Builder(activity as MainActivity)
        builder.setMessage("Похоже местоположение отключено, включить?")
            .setCancelable(false)
            .setPositiveButton("Да") { dialog, id ->
                startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    , 11)
            }
            .setNegativeButton("Нет") { dialog, id ->
                dialog.cancel()
                activity?.finish()
            }
        val alert: AlertDialog = builder.create()
        alert.show()


    }


    protected fun startLocationUpdates() {

        // Create the location request to start receiving updates

        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.setInterval(INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(activity as MainActivity)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity as MainActivity)
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(activity as MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity as MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
            Looper.myLooper())
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // do work here
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    fun onLocationChanged(location: Location) {
        // New location has now been determined
        mLastLocation = location
        val date: Date = Calendar.getInstance().time
        val sdf = SimpleDateFormat("hh:mm:ss a")
        txtTime.text = "" + sdf.format(date)

        txtLat.text = "%.7f".format(mLastLocation.latitude).replace(",",".")
        txtLong.text = "%.7f".format(mLastLocation.longitude).replace(",",".")
        txtAlt.text = "%.2f".format(mLastLocation.altitude).replace(",",".")
        coordsExist = true
        // You can now create a LatLng Object for use with maps
    }

    private fun stoplocationUpdates() {
        mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                Toast.makeText(activity, "В доступе отказано", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                // Show the permission request
                ActivityCompat.requestPermissions(activity as MainActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }


}