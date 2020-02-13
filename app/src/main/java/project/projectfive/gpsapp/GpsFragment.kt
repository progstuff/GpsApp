package project.projectfive.gpsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class GpsFragment: Fragment() {
    companion object{
        fun newInstance():GpsFragment{
            return GpsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.gps_layout, container, false)

        return view
    }
}