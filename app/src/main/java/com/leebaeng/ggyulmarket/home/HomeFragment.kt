package com.leebaeng.ggyulmarket.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home){
    private lateinit var binding: FragmentHomeBinding
    private lateinit var marketListAdapter : MarketListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        marketListAdapter = MarketListAdapter()
        marketListAdapter.submitList(mutableListOf<MarketModel>().apply{
            add(MarketModel("0", "aaa", 1000000L, 1000, null, 1, 0))
            add(MarketModel("1", "bbb", 100000000L, 2000, null, 0, 3))
            add(MarketModel("2", "ccc", 10000000000L, 5000, null, 0, 0))
            add(MarketModel("3", "ddd", 1000000000000L, 15000, null, 3, 10))
            add(MarketModel("4", "eee", 100000000000000L, 105000, null, 100, 1000))
        })

        binding = FragmentHomeBinding.bind(view)
        binding.rcvMarketList.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvMarketList.adapter = marketListAdapter

    }
}