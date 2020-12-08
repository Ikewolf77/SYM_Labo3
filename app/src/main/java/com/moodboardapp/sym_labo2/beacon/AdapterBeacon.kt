package com.moodboardapp.sym_labo2.beacon

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.moodboardapp.sym_labo2.R
import org.w3c.dom.Text


class AdapterBeacon : ArrayAdapter<BeaconModel> {

    private var dataSet: ArrayList<BeaconModel>
    var mContext: Context

    private class ViewHolder {
        lateinit var txtUUID: TextView
        lateinit var txtMin: TextView
        lateinit var txtMaj: TextView
        lateinit var txtRSSI: TextView
    }

    constructor(dataSet: ArrayList<BeaconModel>, context: Context) : super(context, R.layout.beacon_item, dataSet) {
        this.dataSet = dataSet
        this.mContext = context
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Get the data item for this position

        // Get the data item for this position
        var dataModel: BeaconModel? = getItem(position)

        if(dataModel == null) {
            dataModel = BeaconModel("unknown", 0, 0, 0)
        }

        // Check if an existing view is being reused, otherwise inflate the view
        // Check if an existing view is being reused, otherwise inflate the view
        val viewHolder: ViewHolder // view lookup cache stored in tag

        val variableConvertView: View

        if (convertView == null) {
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            variableConvertView = inflater.inflate(R.layout.beacon_item, parent, false)
            viewHolder.txtUUID = variableConvertView.findViewById(R.id.UUID) as TextView
            viewHolder.txtMin = variableConvertView.findViewById(R.id.MIN) as TextView
            viewHolder.txtMaj = variableConvertView.findViewById(R.id.MAJ) as TextView
            viewHolder.txtRSSI = variableConvertView.findViewById(R.id.RSSI) as TextView
            variableConvertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            variableConvertView = convertView
        }

        viewHolder.txtUUID.setText(dataModel.UUID)
        viewHolder.txtMin.setText(dataModel.MIN.toString())
        viewHolder.txtMaj.setText(dataModel.MAJ.toString())
        viewHolder.txtRSSI.setText(dataModel.RSSI.toString())
        // Return the completed view to render on screen
        // Return the completed view to render on screen
        return variableConvertView!!
    }
}