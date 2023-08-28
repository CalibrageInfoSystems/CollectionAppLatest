package com.oilpalm3f.mainapp.StockTransfer

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//Stock Transfer Report Model
class StockTransferReportModel {
    @Expose
    @SerializedName("Code")
    var code: String? = null

    @Expose
    @SerializedName("ReceiptCode")
    var receiptCode: String? = null

    @Expose
    @SerializedName("ReceiptGeneratedDate")
    var receiptGeneratedDate: String? = null

    @Expose
    @SerializedName("FromCC")
    var fromCC: String? = null

    @Expose
    @SerializedName("ToCC")
    var toCC: String? = null

    var fromCCId: String? = null
    var toCCId: String? = null

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
    @SerializedName("VehicleNumber")
    var vehicleNumber: String? = null
    @Expose
    @SerializedName("DriverMobileNumber")
    var driverMobileNumber: String? = null
    @Expose
    @SerializedName("UserName")
    var userName: String? = null
    @Expose
    @SerializedName("DriverName")
    var driverName: String? = null
    @Expose
    @SerializedName("InchargeName")
    var inchargeName:String? = null

    @Expose
    @SerializedName("createdDate")
    var createdDate:String? = null


}