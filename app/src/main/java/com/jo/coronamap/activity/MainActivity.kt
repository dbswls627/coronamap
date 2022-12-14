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

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient //???????????? gps?????? ????????????.
    lateinit var locationCallback: LocationCallback //gps?????? ?????? ????????????.



    @UiThread
    override fun onMapReady(naverMap: NaverMap) {


        //??? ????????? ????????? ?????????
        naverMap.setOnMapClickListener { pointF, latLng ->
            viewModel.fName.value=""
            setMarker()

        }
        this.naverMap = naverMap
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this) //gps ???????????? ????????????
        setUpdateLocationListener() //???????????? ???????????? ??????

        viewModel.list.value!!.forEach {

            val marker = Marker()
            markerList.add(marker)
            marker.position = LatLng(it.lat,it.lng)
            marker.icon = MarkerIcons.BLACK

            if (it.centerType == "??????/??????"){
                marker.iconTintColor = Color.RED
            }else{
                marker.iconTintColor = Color.GREEN
            }
            setMarker()



          /*  val infoWindow = InfoWindow()
            //???????????? ?????? ?????????
            infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(context) {

                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return "?????? : "+it.address+"\n"+
                            "????????? : "+it.centerName+"\n"+
                            "????????? : "+it.facilityName+"\n"+
                            "???????????? : "+it.phoneNumber+"\n"+
                            "???????????? : "+it.updatedAt

                }
            }
*/
            val listener = Overlay.OnClickListener { overlay ->
                val cameraUpdate: CameraUpdate

                if ( viewModel.fName.value.isNullOrEmpty() || viewModel.fName.value!=it.facilityName) {
                    // ???????????? ??????????????? ?????? ????????? ?????? ?????? ???
                    viewModel.setInfo(it)

                    setMarker()

                    marker.width = 100
                    marker.height = 160

                    cameraUpdate = CameraUpdate.scrollTo(LatLng(it.lat, it.lng))    //???????????????
                    naverMap.moveCamera(cameraUpdate)
                    //infoWindow.open(marker)
                } else {
                    // ???????????? ???????????? ????????? ?????? ?????? ???
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
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY //?????? ?????????
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
    }//???????????? ??????????????? ??????

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
                        "????????? ??????????????????.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
            .setDeniedMessage("????????? ??????????????????. [??????] > [??? ??? ??????] > [??????] > [??? ??????]")
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