package com.jo.coronamap.activity

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.UiThread
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.jo.coronamap.viewModel.MainViewModel
import com.jo.coronamap.R
import com.jo.coronamap.databinding.ActivityMainBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() , OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        val content: View = binding.root
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check if the initial data is ready.
                    return if (viewModel.isReady) {
                        // The content is ready; start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        lifecycleScope.launch {
                            if ( viewModel.b) {
                                viewModel.b = false
                                viewModel.saveList()
                                delay(2000)
                                viewModel.isReady = true
                            }
                        }
                        false
                        
                    }
                }
            }
        )
        Log.d("test","Test")

        viewModel.getList()

        requestPermission{}
        init()



    }
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {

        viewModel.list.value!!.forEach {
            val marker = Marker()
            marker.position = LatLng(it.lat,it.lng)
            marker.icon = MarkerIcons.BLACK
            if (it.centerType == "중앙/권역"){
                marker.iconTintColor = Color.RED
            }else{
                marker.iconTintColor = Color.GREEN
            }

            marker.map = naverMap

            val infoWindow = InfoWindow()

            infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(context) {

                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return "주소 : "+it.address+"\n"+
                            "센터명 : "+it.centerName+"\n"+
                            "시설명 : "+it.facilityName+"\n"+
                            "전화번호 : "+it.phoneNumber+"\n"+
                            "업데이트 : "+it.updatedAt

                }
            }

            val listener = Overlay.OnClickListener { overlay ->
                val marker = overlay as Marker

                if (marker.infoWindow == null) {
                    // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                    infoWindow.open(marker)
                } else {
                    // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                    infoWindow.close()
                }

                true
            }
            marker.onClickListener = listener
        }

    }

    private fun requestPermission(logic : () -> Unit) {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    logic()
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    Toast.makeText(
                        this@MainActivity,
                        "권한을 허가해주세요.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
            .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION
            )
            .check()
    }

    private fun init(){
        val fm = supportFragmentManager
        var mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }
}