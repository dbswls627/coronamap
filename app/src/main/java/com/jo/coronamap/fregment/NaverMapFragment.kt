package com.jo.coronamap.fregment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jo.coronamap.R
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback


class NaverMapFragment : Fragment() , OnMapReadyCallback {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_naver_map, container, false)
    }

    override fun onMapReady(p0: NaverMap) {
        TODO("Not yet implemented")
    }

}