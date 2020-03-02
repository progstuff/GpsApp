package project.projectfive.gpsapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocationData(

    @ColumnInfo(name = "lat")var lat:Double,
    @ColumnInfo(name = "lon")var lon:Double,
    @ColumnInfo(name = "alt")var alt:Double,
    @ColumnInfo(name = "isExist")var isExist:Boolean,
    @ColumnInfo(name = "name")var pointName:String,
    @PrimaryKey(autoGenerate = true)
                        var id:Long = 0) {
}

@Entity
data class LocationChain(
    @ColumnInfo(name = "lat")var idA:Long,
    @ColumnInfo(name = "lon")var idB:Long,
    @PrimaryKey(autoGenerate = true)var id:Long = 0
)