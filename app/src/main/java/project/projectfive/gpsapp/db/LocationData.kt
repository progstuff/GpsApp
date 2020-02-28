package project.projectfive.gpsapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocationData(
                        @PrimaryKey(autoGenerate = true)
                        var id:Int,
                        var lat:Double,
                        var lon:Double,
                        var alt:Double,
                        var isExist:Boolean) {
}