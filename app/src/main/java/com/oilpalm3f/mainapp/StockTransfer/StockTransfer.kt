package com.oilpalm3f.mainapp.StockTransfer

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


//Stock Transfer Model
class StockTransfer() : Parcelable {

    @Expose
    @SerializedName("Code")
     var code: String? = null

    @Expose
    @SerializedName("FromCC")
    var fromCC: String? = null

    @Expose
    @SerializedName("VehicleNumber")
     var vehicleNumber: String? = null

    @Expose
    @SerializedName("DriverName")
     var driverName: String? = null
    @Expose
    @SerializedName("DriverMobileNumber")
    var driverMobileNumber: String? = null

    @Expose
    @SerializedName("ToCC")
     var toCC: String? = null

    @Expose
    @SerializedName("GrossWeight")
     var grossWeight: Double = 0.0

    @Expose
    @SerializedName("TareWeight")
     var tareWeight: Double = 0.0

    @Expose
    @SerializedName("NetWeight")
     var netWeight: Double = 0.0
    @Expose
    @SerializedName("WeightDifference")
     var weightDifference: Double = 0.0

    @Expose
    @SerializedName("ReceiptGeneratedDate")
     var receiptGeneratedDate: String? = null

    @Expose
    @SerializedName("ReceiptCode")
     var receiptCode: String? = null

    @Expose
    @SerializedName("TotalBunches")
     var totalBunches: Int = 0

    @Expose
    @SerializedName("RejectedBunches")
     var rejectedBunches: Int = 0

    @Expose
    @SerializedName("AcceptedBunches")
     var acceptedBunches: Int = 0

    @Expose
    @SerializedName("Remarks")
     var remarks: String? = null

    @Expose
    @SerializedName("GraderName")
     var graderName: String? = null

    @Expose
    @SerializedName("IsStockUpdate")
     var stockUpdate: Int = 0

    @Expose
    @SerializedName("IsActive")
     var active: Int = 1

    @Expose
    @SerializedName("CreatedByUserId")
     var createdByUserId: Int = 0

    @Expose
    @SerializedName("UpdatedByUserId")
     var updatedByUserId: Int = 0

    @Expose
    @SerializedName("ServerUpdatedStatus")
     var serverUpdatedStatus: Boolean = false

    @Expose
    @SerializedName("CreatedDate")
     var createdDate: String? = null

    @Expose
    @SerializedName("UpdatedDate")
     var updatedDate: String? = null



    constructor(parcel: Parcel) : this() {
        code = parcel.readString()
        fromCC = parcel.readString()
        vehicleNumber = parcel.readString()
        driverName = parcel.readString()
        driverMobileNumber = parcel.readString()
        toCC = parcel.readString()
        grossWeight = parcel.readDouble()
        tareWeight = parcel.readDouble()
        netWeight = parcel.readDouble()
        weightDifference = parcel.readDouble()
        receiptGeneratedDate = parcel.readString()
        receiptCode = parcel.readString()
        serverUpdatedStatus = parcel.readByte() != 0.toByte()
        createdDate = parcel.readString()
        updatedDate = parcel.readString()
        stockUpdate = parcel.readInt()
        active = parcel.readInt()
        createdByUserId = parcel.readInt()
        updatedByUserId = parcel.readInt()
        totalBunches = parcel.readInt()
        rejectedBunches = parcel.readInt()
        acceptedBunches = parcel.readInt()
        remarks = parcel.readString()
        graderName = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeString(fromCC)
        parcel.writeString(vehicleNumber)
        parcel.writeString(driverName)
        parcel.writeString(driverMobileNumber)
        parcel.writeString(toCC)
        parcel.writeDouble(grossWeight)
        parcel.writeDouble(tareWeight)
        parcel.writeDouble(netWeight)
        parcel.writeDouble(weightDifference)
        parcel.writeString(receiptGeneratedDate)
        parcel.writeString(receiptCode)
        parcel.writeString(createdDate)
        parcel.writeString(updatedDate)
        parcel.writeInt(stockUpdate)
        parcel.writeInt(active)
        parcel.writeInt(createdByUserId)
        parcel.writeInt(updatedByUserId)
        parcel.writeInt(totalBunches)
        parcel.writeInt(rejectedBunches)
        parcel.writeInt(acceptedBunches)
        parcel.writeString(remarks)
        parcel.writeString(graderName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StockTransfer> {
        override fun createFromParcel(parcel: Parcel): StockTransfer {
            return StockTransfer(parcel)
        }

        override fun newArray(size: Int): Array<StockTransfer?> {
            return arrayOfNulls(size)
        }
    }

}