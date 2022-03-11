package com.leebaeng.ggyulmarket.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.leebaeng.ggyulmarket.ui.AddMarketItemActivity
import com.leebaeng.ggyulmarket.R
import com.leebaeng.ggyulmarket.common.constants.DBKey
import com.leebaeng.ggyulmarket.databinding.FragmentHomeBinding
import com.leebaeng.ggyulmarket.model.MarketModel
import com.leebaeng.util.log.LLog

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var marketListAdapter: MarketListAdapter
    private lateinit var marketListDB: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var dbListener: ChildEventListener

    private val marketList = mutableListOf<MarketModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        marketList.clear()
        auth = Firebase.auth
        marketListDB = Firebase.database.reference.child(DBKey.TABLE_MARKET_LIST)
        dbListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                LLog.warn("onChildAdded $snapshot")
                val model = snapshot.getValue(MarketModel::class.java)
                model ?: return

                marketList.add(model)
                marketListAdapter.submitList(marketList)
                marketListAdapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                LLog.warn("onChildChanged $snapshot")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                LLog.warn("onChildRemoved $snapshot")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                LLog.warn("onChildMoved $snapshot")
            }

            override fun onCancelled(error: DatabaseError) {
                LLog.warn("onCancelled $error")
            }
        }
        marketListDB.addChildEventListener(dbListener)

        marketListAdapter = MarketListAdapter()
        binding = FragmentHomeBinding.bind(view)
        binding.rcvMarketList.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvMarketList.adapter = marketListAdapter
        binding.btnAddNewItem.setOnClickListener {
            if (auth.currentUser != null) {
                startActivity(Intent(requireContext(), AddMarketItemActivity::class.java))
            } else {
                startActivity(Intent(requireContext(), AddMarketItemActivity::class.java))
                Snackbar.make(view, "로그인이 필요 합니다.", Snackbar.LENGTH_SHORT).show()
                //TODO LoginActivity로 이동
            }
        }

    }

    override fun onResume() {
        super.onResume()
        marketListAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        marketListDB.removeEventListener(dbListener)
    }
}