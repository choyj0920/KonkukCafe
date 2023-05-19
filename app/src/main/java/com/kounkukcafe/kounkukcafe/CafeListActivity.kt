package com.kounkukcafe.kounkukcafe

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.kounkukcafe.kounkukcafe.apiutil.Cafe
import com.kounkukcafe.kounkukcafe.apiutil.CafeApiManager
import com.kounkukcafe.kounkukcafe.apiutil.CafeResponseData
import com.kounkukcafe.kounkukcafe.apiutil.EmotionBody
import com.kounkukcafe.kounkukcafe.databinding.ActivityCafeListBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CafeListActivity : AppCompatActivity(), PermissionListener,MapView.POIItemEventListener {

    private lateinit var binding: ActivityCafeListBinding
    private lateinit var cafes: List<Cafe>
    private lateinit var mapView :MapView
    private lateinit var recyclerView:RecyclerView


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
        var emotion = intent.getStringExtra("emotion");


        binding=ActivityCafeListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mapView = binding.mapView

        mapView.setMapType(MapView.MapType.Standard)
        

        val bottomSheet = binding.bottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = 800  // 피크 높이를 원하는 값으로 설정합니다.

        update(emotion?:0)


    }

    fun initMapview(){
        for (cafe in cafes) {
            val marker = createMarker(cafe)
            mapView.addPOIItem(marker)
            Log.d("TAG","cafeDATA ${cafe.name}")
        }
        mapFocusCafe(cafes[0])
        mapView.setPOIItemEventListener(this)
    }

    fun update(emotion:Any){
        var _emotion=""
        if(emotion is Int && emotion.toInt()<6){
            _emotion= CafeApiManager.emotionOrder[emotion.toInt()]
        }else if (emotion is String && CafeApiManager.emotionOrder.contains(emotion.toString())){
            _emotion= emotion.toString()
        }else{
            Log.d("E","오류발생 :카페리스트를 가져올 감정을 제대로 골라주세요")
            return
        }
        Log.d("TAG","---------------${_emotion}------------------")

        // 해당 감정으로 카페리스트 가져오기
        CafeApiManager.cafeApiService.getCafelistfromEmotion(EmotionBody(_emotion))
            .enqueue(object : Callback<CafeResponseData?> {
            override fun onResponse(
                call: Call<CafeResponseData?>,
                response: Response<CafeResponseData?>
            ) {

                Log.d("TAG","cafe리스트 sucess")
                cafes= response.body()?.cafelist!!
                initMapview()

                update_recyclerview()

            }

            override fun onFailure(call: Call<CafeResponseData?>, t: Throwable) {
                Log.d("TAG","cafe리스트 fail${t}")


                // 에러 처리
            }
        })
    }
    fun update_recyclerview(){
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CafeAdapter(cafes,this)
    }

    fun mapFocusCafe(cafe: Cafe){
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(cafe.lat.toDouble(), cafe.lng.toDouble()), 3, true);
    }

    fun createMarker(cafe: Cafe): MapPOIItem {
        val marker = MapPOIItem()
        marker.itemName = cafe.name
        marker.tag = 0
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