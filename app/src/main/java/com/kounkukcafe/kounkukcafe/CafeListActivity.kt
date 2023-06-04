package com.kounkukcafe.kounkukcafe

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.kounkukcafe.kounkukcafe.apiutil.*
import com.kounkukcafe.kounkukcafe.databinding.ActivityCafeListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CafeListActivity : AppCompatActivity(), PermissionListener,MapView.POIItemEventListener {

    private lateinit var binding: ActivityCafeListBinding
    private var cafes: List<Cafe> = listOf()
    private lateinit var mapView :MapView
    private lateinit var recyclerView:RecyclerView
    private var isamplification:Boolean=false


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (Build.VERSION.SDK_INT >= 23) {
            TedPermission.create()
                .setPermissionListener(this)
                .setRationaleMessage("위치 정보 제공이 필요한 서비스입니다.")
                .setDeniedMessage("[설정] -> [권한]에서 권한 변경이 가능합니다.")
                .setDeniedCloseButtonText("닫기")
                .setGotoSettingButtonText("설정")
                .setRationaleTitle("HELLO")
                .setPermissions(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                .check()
        }

        //이전 액티비티에서 값가져오기
        var emotion = ApiManager.curSimpleEmotion!!
        isamplification=intent.getBooleanExtra ("isamplification",true)



        binding=ActivityCafeListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.title.text="${emotion.result} "+if(isamplification) "증폭" else "상쇄"

        mapView = binding.mapView

        mapView.setMapType(MapView.MapType.Standard)
        mapView.removeAllPOIItems();
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)


        val bottomSheet = binding.bottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isFitToContents=false
        bottomSheetBehavior.expandedOffset=900

        bottomSheetBehavior.peekHeight = 500  // 피크 높이를 원하는 값으로 설정합니다.

        update(emotion.result)



    }

    fun initMapview(){
        mapView.removeAllPOIItems();

        for (i in cafes.indices) {
            val marker = createMarker(i)
            mapView.addPOIItem(marker)
            Log.d("TAG","cafeDATA ${cafes[i].name}")
        }
        mapFocusCafe(0)
        mapView.setPOIItemEventListener(this)
    }


    fun update(emotion:String){

        Log.d("TAG","---------------${emotion}------------------")

        // 해당 감정으로 카페리스트 가져오기
        CafeApiManager.cafeApiService.getCafelistfromEmotion(EmotionBody(emotion))
            .enqueue(object : Callback<CafeResponseData?> {
            override fun onResponse(
                call: Call<CafeResponseData?>,
                response: Response<CafeResponseData?>
            ) {

                Log.d("TAG","cafe리스트 sucess")
                cafes = if(this@CafeListActivity.isamplification){
                    Log.d("TAG","감정 증폭리스트")

                    response.body()?.cafelist!!
                }else{
                    Log.d("TAG","감정 상쇄리스트")

                    response.body()?.negativecafelist!!
                }
                lifecycleScope.launch(Dispatchers.Main) {
                    initMapview()
                    update_recyclerview()
                }


            }

            override fun onFailure(call: Call<CafeResponseData?>, t: Throwable) {
                Log.d("TAG","cafe리스트 fail${t}")


                // 에러 처리
            }
        })
    }
    @SuppressLint("ClickableViewAccessibility")
    fun update_recyclerview(){
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CafeAdapter(cafes,this)

        recyclerView.isNestedScrollingEnabled = true;
        recyclerView.setOnTouchListener(OnTouchListener { v, event ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN ->                         // Disallow NestedScrollView to intercept touch events.
                    v.parent.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP ->                         // Allow NestedScrollView to intercept touch events.
                    v.parent.requestDisallowInterceptTouchEvent(false)
            }

            // Handle RecyclerView touch events.
            v.onTouchEvent(event)
            true
        })
    }

    fun mapFocusCafe(index: Int){
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(cafes[index].lat.toDouble(), cafes[index].lng.toDouble()), 3, true);
        mapView.selectPOIItem(mapView.findPOIItemByTag(index),false)
    }

    fun createMarker(index:Int): MapPOIItem {
        var cafe=cafes[index]
        val marker = MapPOIItem()
        marker.itemName = cafe.name
        marker.tag = index
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(cafe.lat.toDouble(), cafe.lng.toDouble())
        marker.markerType = MapPOIItem.MarkerType.BluePin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin

        Log.d("TAG","마커 생성 ${cafe.name}")

        return marker
    }




    override fun onPOIItemSelected(mapView: MapView?, mapPOIItem: MapPOIItem?) {
        // 마커 이름을 이용해 카페 목록에서 해당 카페 찾기
        val cafeName = mapPOIItem!!.itemName
        val position = cafes.indexOfFirst { it.name == cafeName }

        // 해당 위치로 스크롤
        if (position != -1) {
            recyclerView.smoothScrollToPosition(position)
        }
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }

    override fun onPermissionGranted() {

    }

    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
    }


}