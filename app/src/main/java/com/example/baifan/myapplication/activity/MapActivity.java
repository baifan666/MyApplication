package com.example.baifan.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.baifan.myapplication.R;
import com.example.baifan.myapplication.adapter.PoiListAdapter;
import com.example.baifan.myapplication.application.ExitApplication;
import com.example.baifan.myapplication.utils.MyOverLay;

import java.util.ArrayList;

public class MapActivity extends Activity {
    private ImageView back;
    private MapView myMapView = null;//地图控件
    private BaiduMap myBaiduMap;//百度地图对象
    public LocationClient mLocationClient;
    public BDAbstractLocationListener myListener = new MyLocationListener();
    private LatLng latLng,ll;
    private PoiSearch mPoiSearch;
    private ArrayList<PoiInfo> poiInfos = new ArrayList<PoiInfo>();
    private PoiListAdapter adapter;
    private ListView iv_item;
    private boolean isFirstLoc = true; // 是否首次定位
    private BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.marker);//构建Marker图标
    private EditText city,keyword;
    private Button search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       // SDKInitializer.initialize(this);
        setContentView(R.layout.activity_map);
        //将该Activity添加到ExitApplication实例中，
        ExitApplication.getInstance().addActivity(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        back = (ImageView) findViewById(R.id.backImg); //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        city = (EditText)findViewById(R.id.city);
        keyword = (EditText)findViewById(R.id.keyword);
        search = (Button)findViewById(R.id.search);

        iv_item = (ListView)findViewById(R.id.iv_item);
        iv_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PoiInfo poi = poiInfos.get(i);
                Intent intent=new Intent();
                intent.putExtra("address",poi.address);
                MapActivity.this.setResult(RESULT_OK, intent);// 设置回传数据。
                finish();
            }
        });

        myMapView = (MapView) findViewById(R.id.baiduMapView);

        myBaiduMap = myMapView.getMap();
        //普通地图
        myBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        // 开启定位图层
        myBaiduMap.setMyLocationEnabled(true);


        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        //配置定位SDK参数
        initLocation();
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        //开启定位
        mLocationClient.start();
        //图片点击事件，回到定位点
        mLocationClient.requestLocation();
        //地图状态改变相关监听
        myBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {

            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                myBaiduMap.clear();
                LatLng cenpt = mapStatus.target;
                getdata(cenpt);
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .position(cenpt)
                        .icon(bitmap);
                //在地图上添加Marker，并显示
                myBaiduMap.addOverlay(option);
            }

            @Override
            public void onMapStatusChange(MapStatus arg0) {

            }
        });
        // 创建PoiSearch实例
        mPoiSearch = PoiSearch.newInstance();
        //POI检索的监听对象
        OnGetPoiSearchResultListener resultListener = new OnGetPoiSearchResultListener() {
            //获得POI的检索结果，一般检索数据都是在这里获取
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                //如果搜索到的结果不为空，并且没有错误
                if (poiResult != null && poiResult.error == PoiResult.ERRORNO.NO_ERROR) {
                    MyOverLay overlay = new MyOverLay(myBaiduMap, mPoiSearch);//这传入search对象，因为一般搜索到后，点击时方便发出详细搜索
                    //设置数据,这里只需要一步，

                    overlay.setData(poiResult);
                    //添加到地图
                    overlay.addToMap();
                    //将显示视图拉倒正好可以看到所有POI兴趣点的缩放等级
                    overlay.zoomToSpan();//计算工具
                    //设置标记物的点击监听事件
                    myBaiduMap.setOnMarkerClickListener(overlay);
                    poiInfos.clear();
                    poiInfos = (ArrayList<PoiInfo>) poiResult.getAllPoi();
                    adapter = new PoiListAdapter(MapActivity.this,R.layout.local_item, poiInfos);
                    iv_item.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplication(), "搜索不到你需要的信息！", Toast.LENGTH_SHORT).show();
                }
            }
            //获得POI的详细检索结果，如果发起的是详细检索，这个方法会得到回调(需要uid)
            //详细检索一般用于单个地点的搜索，比如搜索一大堆信息后，选择其中一个地点再使用详细检索
            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(getApplication(), "抱歉，未找到结果",
                            Toast.LENGTH_SHORT).show();
                } else {// 正常返回结果的时候，此处可以获得很多相关信息
                    Toast.makeText(getApplication(), poiDetailResult.getName() + ": "
                                    + poiDetailResult.getAddress(),
                            Toast.LENGTH_LONG).show();

                }
            }
            //获得POI室内检索结果
            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
            }
        };
        /**
        *设置监听
        */
        mPoiSearch.setOnGetPoiSearchResultListener(resultListener);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发起检索
                PoiCitySearchOption poiCity = new PoiCitySearchOption();
                poiCity.keyword(keyword.getText().toString()).city(city.getText().toString());//这里还能设置显示的个数，默认显示10个
                mPoiSearch.searchInCity(poiCity);//执行搜索，搜索结束后，在搜索监听对象里面的方法会被回调
                //mPoiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(keyword.getText().toString()));

            }
        });
    }

    //配置定位SDK参数
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        /**
         * 设置定位模式 Battery_Saving 低功耗模式 Device_Sensors 仅设备(Gps)模式 Hight_Accuracy
         * 高精度模式
         /   */
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        option.setOpenGps(true); // 打开gps

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    private void getdata(LatLng ll) {
        GeoCoder geoCoder = GeoCoder.newInstance();
        //
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            // 反地理编码查询结果回调函数
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                    Toast.makeText(MapActivity.this, "抱歉，未能找到结果",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                poiInfos = (ArrayList<PoiInfo>) result.getPoiList();
                adapter = new PoiListAdapter(MapActivity.this,R.layout.local_item, poiInfos);
                iv_item.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            // 地理编码查询结果回调函数
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null
                        || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                }
            }
        };
        // 设置地理编码检索监听者
        geoCoder.setOnGetGeoCodeResultListener(listener);
        //
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
    }

    //实现BDLocationListener接口,BDLocationListener为结果监听接口，异步获取定位结果
    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            // 设置定位数据
            myBaiduMap.setMyLocationData(locData);
            // 当不需要定位图层时关闭定位图层
            //mBaiduMap.setMyLocationEnabled(false);
            if (isFirstLoc) {
                isFirstLoc = false;
                ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                myBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    // GPS定位结果
                    Toast.makeText(MapActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    // 网络定位结果
                    Toast.makeText(MapActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();

                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                    // 离线定位结果
                    Toast.makeText(MapActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();

                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    Toast.makeText(MapActivity.this, "服务器错误，请检查", Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    Toast.makeText(MapActivity.this, "网络错误，请检查", Toast.LENGTH_SHORT).show();
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    Toast.makeText(MapActivity.this, "手机模式错误，请检查是否飞行", Toast.LENGTH_SHORT).show();
                }
                getdata(ll);
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .position(ll)
                        .icon(bitmap);
                //在地图上添加Marker，并显示
                myBaiduMap.addOverlay(option);
            }
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        myMapView.onDestroy();
    }




    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        myMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        myMapView.onPause();
    }
}
