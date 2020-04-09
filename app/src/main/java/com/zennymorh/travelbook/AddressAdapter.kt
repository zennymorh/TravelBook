package com.zennymorh.travelbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng

typealias ItemClickListener = (String, LatLng) -> Unit

class AddressAdapter(var nameList: ArrayList<String> , var locationList: ArrayList<LatLng>, var listener: ItemClickListener):
    RecyclerView.Adapter<AddressAdapter.AddressHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AddressHolder(inflater, parent)
    }

    override fun getItemCount(): Int = nameList.size

    override fun onBindViewHolder(
        holder: AddressHolder,
        position: Int
    ) {
        val name: String = nameList[position]
        holder.bind(name)
    }

    fun updateList(addressList: ArrayList<String>, locationsList: ArrayList<LatLng>) {
        nameList = addressList
        locationList = locationsList
        notifyDataSetChanged()
    }

    inner class AddressHolder(inflater: LayoutInflater, parent: ViewGroup):
        RecyclerView.ViewHolder(inflater.inflate(R.layout.location_text_view, parent,
        false)), View.OnClickListener {
        override fun onClick(v: View?) {
            val name = nameList[adapterPosition]
            val latlng = locationList[adapterPosition]
            listener.invoke(name, latlng)
        }

        private var locationText: TextView? = null

        init {
            locationText = itemView.findViewById(R.id.location)
            itemView.setOnClickListener(this)
        }

        fun bind(name: String) {
            locationText?.text = name
        }
    }

}