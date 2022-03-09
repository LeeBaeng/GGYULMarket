package com.leebaeng.ggyulmarket.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.common.ext.dpToPx
import com.leebaeng.ggyulmarket.common.ext.getPriceCommaFormatWithWon
import com.leebaeng.ggyulmarket.common.ext.getTimeGapFormatString
import com.leebaeng.ggyulmarket.databinding.ItemMarketBinding
import java.util.*

class MarketListAdapter : ListAdapter<MarketModel, MarketListAdapter.ViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMarketBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(currentList[position])
    }

    inner class ViewHolder(private val binding: ItemMarketBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(model: MarketModel) {

            binding.txtTitle.text = model.title
            binding.txtDate.text = Date(model.createdAt).getTimeGapFormatString()
            binding.txtPrice.text = model.price.getPriceCommaFormatWithWon()

            fun setSubInfo(layout: ViewGroup, txtView: TextView, data: Int?) {
                layout.isVisible = if (data != null && data > 0) {
                    txtView.text = data.toString()
                    true
                } else false
            }

            setSubInfo(binding.layoutTalkCount, binding.txtTalkCount, model.talkCnt)
            setSubInfo(binding.layoutLikeCount, binding.txtLikeCount, model.likeCnt)

            if (!model.imgUrl.isNullOrEmpty() && model.imgUrl[0].isNotEmpty()) {
                Glide.with(binding.root)
                    .load(model.imgUrl[0])
                    .transform(CenterCrop(), RoundedCorners(10f.dpToPx(binding.root.context)))
                    .placeholder(R.drawable.ic_baseline_shopping_cart_24)
                    .into(binding.imgThumbnail)
            } else binding.imgThumbnail.setImageResource(R.drawable.ic_baseline_shopping_cart_24)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<MarketModel>() {
            override fun areItemsTheSame(oldItem: MarketModel, newItem: MarketModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: MarketModel, newItem: MarketModel): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
