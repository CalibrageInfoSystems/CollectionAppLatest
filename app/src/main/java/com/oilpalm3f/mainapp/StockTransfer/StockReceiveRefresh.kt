package com.oilpalm3f.mainapp.StockTransfer

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


//Stock Transfer Receive Refresh Model
class StockReceiveRefresh {
    @Expose
    @SerializedName("Code")
    var code: String? = null
    @Expose
    @SerializedName("IsStockUpdate")
    var stockUpdate: Int = 1

    @Expose
    @SerializedName("UpdatedByUserId")
    var updatedByUserId: Int = 0

    @Expose
    @SerializedName("UpdatedDate")
    var updatedDate: String? = null

}