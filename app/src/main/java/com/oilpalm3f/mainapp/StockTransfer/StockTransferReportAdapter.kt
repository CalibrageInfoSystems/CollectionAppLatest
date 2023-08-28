package com.oilpalm3f.mainapp.StockTransfer

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.oilpalm3f.mainapp.R
import com.oilpalm3f.mainapp.collectioncenter.onPrintOptionSelected
import com.oilpalm3f.mainapp.database.DataAccessHandler

import java.util.ArrayList
import java.util.zip.Inflater

//Stock Transfer Report Adapter
class StockTransferReportAdapter(private val context: Context) : RecyclerView.Adapter<StockTransferReportAdapter.StockTransferViewHolder>() {


    private var mList: List<StockTransferReportModel>? = null
    private var item: StockTransferReportModel? = null
    private val dbHandler: DataAccessHandler
    private var onPrintSelected: onPrintOptionSelected? = null

    companion object {
        private val LOG_TAG = StockTransferReportAdapter::class.java.name
    }

    init {
        mList = ArrayList()
        dbHandler = DataAccessHandler(context)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): StockTransferViewHolder {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        @SuppressLint("InflateParams")
        val inflater = layoutInflater.inflate(R.layout.stocktransfer_reportitem, null)

        return StockTransferViewHolder(inflater)
    }


    //Binding Data
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StockTransferViewHolder, position: Int) {
        item = mList!![position]
        if (item != null) {
            holder.stCode.text = item!!.code!!.trim { it <= ' ' }
            holder.dateTime.text = item!!.receiptGeneratedDate!!.trim { it <= ' ' }
            holder.fromCC.text = item!!.fromCC
            holder.toCC.text = item!!.toCC
            holder.netWeight.text = "" + item!!.netWeight
            holder.vehicleNumber.text = item!!.vehicleNumber!!.trim { it <= ' ' }
            holder.stCreatedBy.text = item!!.userName!!.trim { it <= ' ' }
            holder.fromCCInCharge.text = item!!.inchargeName!!.trim { it <= ' ' }

            holder.printBtn.setOnClickListener {
                if (null != onPrintSelected) {
                    onPrintSelected!!.printOptionSelected(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    fun updateAdapter(list: List<StockTransferReportModel>) {
        this.mList = list
        notifyDataSetChanged()
    }

    inner class StockTransferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stCode: TextView = itemView.findViewById<View>(R.id.stCode) as TextView
        val dateTime: TextView = itemView.findViewById<View>(R.id.tv_dateandtimestamp) as TextView
        val fromCC: TextView = itemView.findViewById<View>(R.id.fromCCTv) as TextView
        val toCC: TextView = itemView.findViewById<View>(R.id.tcCCTv) as TextView
        val netWeight: TextView = itemView.findViewById<View>(R.id.netWeightTv) as TextView
        val vehicleNumber: TextView = itemView.findViewById<View>(R.id.vehicleNumberTv) as TextView
        val stCreatedBy: TextView = itemView.findViewById<View>(R.id.tvcreatedby) as TextView
        val fromCCInCharge: TextView = itemView.findViewById<View>(R.id.tvccagentname) as TextView
        val printBtn: ImageView = itemView.findViewById<View>(R.id.printBtn) as ImageView

    }

    fun setonPrintSelected(onPrintSelected: onPrintOptionSelected) {
        this.onPrintSelected = onPrintSelected
    }


}
