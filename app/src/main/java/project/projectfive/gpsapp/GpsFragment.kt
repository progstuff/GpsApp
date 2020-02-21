package project.projectfive.gpsapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class GpsFragment: Fragment(){
    lateinit var txtLat: TextView
    lateinit var txtLong: TextView
    lateinit var txtTime: TextView

    lateinit var aButton:MaterialButton
    lateinit var aLat:TextView
    lateinit var aLon:TextView
    lateinit var bButton:MaterialButton
    lateinit var bLat:TextView
    lateinit var bLon:TextView

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 2000
    private val FASTEST_INTERVAL: Long = 1000
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10

    lateinit var gpsViewModel:GpsViewModel

    companion object {
        fun newInstance(): GpsFragment {
            return GpsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.gps_layout, container, false)
        gpsViewModel = ViewModelProviders.of(this).get(GpsViewModel::class.java)
        mLocationRequest = LocationRequest()

        txtLat = view.findViewById(R.id.txtLat);
        txtLong = view.findViewById(R.id.txtLong);
        txtTime = view.findViewById(R.id.txtTime);

        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }

        if (checkPermissionForLocation(activity as MainActivity)) {
            startLocationUpdates()
        }

        aButton = view.findViewById(R.id.ba)
        bButton = view.findViewById(R.id.bb)
        aLat = view.findViewById(R.id.lat1)
        aLon = view.findViewById(R.id.lon1)
        bLat = view.findViewById(R.id.lat2)
        bLon = view.findViewById(R.id.lon2)
        aButton.setOnClickListener {
            gpsViewModel.setPointA((txtLat.text as String).toFloat(), (txtLong.text as String).toFloat())
        }
        bButton.setOnClickListener {
            gpsViewModel.setPointB((txtLat.text as String).toFloat(), (txtLong.text as String).toFloat())
        }
        val latAObserver = Observer<Float>{data ->
            aLat.text = data.toString()
        }
        val lonAObserver = Observer<Float>{data ->
            aLon.text = data.toString()
        }
        val latBObserver = Observer<Float>{data ->
            bLat.text = data.toString()
        }
        val lonBObserver = Observer<Float>{data ->
            bLon.text = data.toString()
        }
        gpsViewModel.latA.observe(viewLifecycleOwner, latAObserver)
        gpsViewModel.lonA.observe(viewLifecycleOwner, lonAObserver)
        gpsViewModel.latB.observe(viewLifecycleOwner, latBObserver)
        gpsViewModel.lonB.observe(viewLifecycleOwner, lonBObserver)


        return view
    }

    private fun buildAlertMessageNoGps() {

        val builder = AlertDialog.Builder(activity as MainActivity)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    , 11)
            }
            .setNegativeButton("No") { dialog, id ->
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
        txtTime.text = "ОБНОВЛЕНО : " + sdf.format(date)
        txtLat.text = "" + mLastLocation.latitude
        txtLong.text = "" + mLastLocation.longitude

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
                Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show()
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