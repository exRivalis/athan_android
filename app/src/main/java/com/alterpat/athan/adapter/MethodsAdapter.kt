package com.alterpat.athan.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alterpat.athan.R
import com.alterpat.athan.model.CalcMethod


class MethodsAdapter(private val context: Context, private var methods: ArrayList<CalcMethod>, var clickListener: MethodClickListener) :
    RecyclerView.Adapter<MethodsAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val info: TextView = view.findViewById(R.id.info)
        val description: TextView = view.findViewById(R.id.description)
        val rootView : View = view.findViewById(R.id.rootView)

        fun start(calcMethod: CalcMethod, clickListener: MethodClickListener){
            itemView.setOnClickListener{
                clickListener.onClick(calcMethod, adapterPosition)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.method_item, parent, false)

        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return methods.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.info.text = methods[position].getInfo()
        holder.description.text = methods[position].description

        holder.start(methods[position], clickListener)

        if(methods.get(position).isSelected){
            //holder.rootView.background = context.getDrawable(R.drawable.circle_bg)
            holder.description.setTextColor(context.getColor(R.color.background_color))
            holder.info.setTextColor(context.getColor(R.color.background_color))
        }else {
            //holder.rootView.background = context.getDrawable(R.drawable.circle_bg)
            holder.description.setTextColor(context.getColor(R.color.colorText))
            holder.info.setTextColor(context.getColor(R.color.colorTextSub))
        }

    }

}

interface MethodClickListener {
    fun onClick(item: CalcMethod, position: Int)
    //fun onLongClick(view: View?, position: Int)
}

