package com.qureka.skool.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.qureka.skool.SoundInfo
import com.qureka.skool.databinding.AdapterDetailsPageRowBinding
import com.qureka.skool.utils.OnRecyclerViewClick
import com.qureka.skool.utils.Utils

class SoundAdapter(
    private val arrayList: ArrayList<SoundInfo>,
    private val onRecyclerViewClick: OnRecyclerViewClick,
) : RecyclerView.Adapter<SoundAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return arrayList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(classData: ArrayList<SoundInfo>) {
        arrayList.clear()
        arrayList.addAll(classData)
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<SoundInfo> = arrayList

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val soundInfo = arrayList[position]
        with(holder.binding) {
            Utils.getImage(ivCategoryName.context, soundInfo.soundImageName)?.let {
                ivCategoryName.setImageDrawable(it)
            }
            tvCategoryName.text = soundInfo.nameSound
        }
        holder.binding.root.setOnClickListener {
            onRecyclerViewClick.onClick(
                holder.adapterPosition,
                arrayList[position].isUnlock
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterDetailsPageRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(val binding: AdapterDetailsPageRowBinding) :
        RecyclerView.ViewHolder(binding.root)

}

