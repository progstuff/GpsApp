package project.projectfive.gpsapp

import project.projectfive.gpsapp.db.LocationData
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sqrt

data class DataByLocations(var pointA: LocationData, var pointB: LocationData) {
    var distance = -1.0

    fun calculateDistance(){
        var dist = -1.0
        if(pointA.isExist == true) {
            if (pointB.isExist == true) {
                val fi1 = pointA.lat.div(180).times(PI)
                val fi2 = pointB.lat.div(180).times(PI)
                val lambda1 = pointA.lon.div(180).times(PI)
                val lambda2 = pointB.lon.div(180).times(PI)

                val sigma = 2 * asin(
                    sqrt(
                        Math.pow(
                            Math.sin((fi2 - fi1) / 2),
                            2.0
                        ) + cos(fi1) * cos(fi2) * Math.pow(Math.sin((lambda2 - lambda1) / 2), 2.0)
                    )
                )
                dist = sigma * 6372795
            }
        }
        distance = dist
    }
}