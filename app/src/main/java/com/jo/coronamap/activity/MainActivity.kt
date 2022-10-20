package com.jo.coronamap.activity

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.jo.coronamap.R
import com.jo.coronamap.databinding.ActivityMainBinding
import com.jo.coronamap.viewModel.MainViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.MarkerIcons
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() , OnMapReadyCallback {

    private lateinit var naverMap: NaverMap
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    var markerList = ArrayList<Marker>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        /* val content: View = binding.root
         content.viewTreeObserver.addOnPreDrawListener(
             object : ViewTreeObserver.OnPreDrawListener {
                 override fun onPreDraw(): Boolean {
                     // Check if the initial data is ready.
                     return if (viewModel.isReady) {
                         // The content is ready; start drawing.
                         content.viewTreeObserver.removeOnPreDrawListener(this)
                         viewModel.getList()
                         init()
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
         )*/

        viewModel.getList()
        init()
        requestPermission {}

        viewModel.fName.observe(this,{
            Log.d("test",viewModel.fName.value.toString())
            if (viewModel.fName.value.isNullOrEmpty()){
                binding.info.visibility= View.GONE
            }else{
                binding.info.visibility= View.VISIBLE
            }
        })

        binding.button.setOnClickListener {
            setUpdateLocationListener()
        }
    }

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient //자동으로 gps값을 받아온다.
    lateinit var locationCallback: LocationCallback //gps응답 값을 가져온다.



    @UiThread
    override fun onMapReady(naverMap: NaverMap) {


        //맵 클릭시 정보창 없애기
        naverMap.setOnMapClickListener { pointF, latLng ->
            viewModel.fName.value=""
            setMarker()

        }
        this.naverMap = naverMap
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this) //gps 자동으로 받아오기
        setUpdateLocationListener() //내위치를 가져오는 코드

        viewModel.list.value!!.forEach {

            val marker = Marker()
            markerList.add(marker)
            marker.position = LatLng(it.lat,it.lng)
            marker.icon = MarkerIcons.BLACK

            if (it.centerType == "중앙/권역"){
                marker.iconTintColor = Color.RED
            }else{
                marker.iconTintColor = Color.GREEN
            }
            setMarker()



          /*  val infoWindow = InfoWindow()
            //네이버맵 제공 정보창
            infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(context) {

                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return "주소 : "+it.address+"\n"+
                            "센터명 : "+it.centerName+"\n"+
                            "시설명 : "+it.facilityName+"\n"+
                            "전화번호 : "+it.phoneNumber+"\n"+
                            "업데이트 : "+it.updatedAt

                }
            }
*/
            val listener = Overlay.OnClickListener { overlay ->
                val cameraUpdate: CameraUpdate

                if ( viewModel.fName.value.isNullOrEmpty() || viewModel.fName.value!=it.facilityName) {
                    // 정보창이 비어있거나 다른 마커를 클릭 했을 때
                    viewModel.setInfo(it)

                    setMarker()

                    marker.width = 75
                    marker.height = 120

                    cameraUpdate = CameraUpdate.scrollTo(LatLng(it.lat, it.lng))    //카메라이동
                    naverMap.moveCamera(cameraUpdate)
                    //infoWindow.open(marker)
                } else {
                    // 열어있는 정보창의 마커를 클릭 했을 때
                    setMarker()
                    viewModel.fName.value = ""
                    //infoWindow.close()
                }

                true
            }
            marker.map = naverMap
            marker.onClickListener = listener
        }

    }

    fun setMarker() {
        markerList.forEach {
            it.width = 50
            it.height = 80
        }
    }


    @SuppressLint("MissingPermission")
    fun setUpdateLocationListener() {
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY //높은 정확도
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for ((i, location) in locationResult.locations.withIndex()) {
                    Log.d("location: ", "${location.latitude}, ${location.longitude}")
                    setLastLocation(location)
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }//좌표계를 주기적으로 갱신

    fun setLastLocation(location: Location) {
        val myLocation = LatLng(location.latitude, location.longitude)
        val marker = Marker()
        marker.position = myLocation

        marker.map = naverMap
        marker.icon = OverlayImage.fromResource(R.drawable.ic_baseline_circle_24)
        val cameraUpdate = CameraUpdate.scrollTo(myLocation)
        naverMap.moveCamera(cameraUpdate)


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
        binding.info.visibility= View.GONE
        val fm = supportFragmentManager
        var mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }
}