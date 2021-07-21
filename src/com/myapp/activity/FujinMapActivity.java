package com.myapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
 
import com.myapp.activity.MyOrientationListener.OnOrientationListener;
import com.myapp.common.GLFont;
import com.myweb.domain.Info;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
 

public class FujinMapActivity extends Activity {

	 /*

	SQLiteDatabase db;
	/**
	 * 地图控件
	
	private MapView mMapView = null;
	 
	private BaiduMap mBaiduMap;
	 
	private LocationClient mLocationClient;
	 
	public MyLocationListener mMyLocationListener;
	 
	private LocationMode mCurrentMode = LocationMode.NORMAL;
	 
	private volatile boolean isFristLocation = true;

	 
	private double mCurrentLantitude;
	private double mCurrentLongitude;
	 
	private float mCurrentAccracy;
	 
	private MyOrientationListener myOrientationListener;
	 
	private int mXDirection;

	 
	private String[] mStyles = new String[] { "地图模式【正常】", "地图模式【跟随】",
			"地图模式【罗盘】" };
	private int mCurrentStyle = 0;
	// 初始化全局 bitmap 信息，不用时及时 recycle
	private BitmapDescriptor mIconMaker;
	 
	private RelativeLayout mMarkerInfoLy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());

		setContentView(R.layout.fujin_map_activity);
		// 第一次定位
		isFristLocation = true;
		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.id_bmapView);
		mMarkerInfoLy = (RelativeLayout) findViewById(R.id.id_marker_info);
		// 获得地图的实例
		mBaiduMap = mMapView.getMap();
		mIconMaker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
		// 初始化定位
		initMyLocation();
		// 初始化传感器
		initOritationListener();
		initMarkerClickEvent();
		initMapClickEvent();
		 
	}

	public void initData() {
		
		/*dbhelp = DataBase.getInstance(FujinMapActivity.this);

		db = dbhelp.getReadableDatabase();

		Cursor cursor = db.rawQuery(
				"select * from qiaoliang order by id desc ", new String[] {});

		List<Info> infos = new ArrayList<Info>();

		while (cursor.moveToNext()) {

			Info info = new Info();

			info.setId(cursor.getString(cursor.getColumnIndex("id")));

			info.setQiaoliangmingcheng(cursor.getString(cursor
					.getColumnIndex("qiaoliangmingcheng")));
				
			info.setJindu(cursor.getString(cursor.getColumnIndex("jindu")));
			
			info.setLatitude(Double.valueOf(cursor.getString(cursor.getColumnIndex("jindu"))));

			info.setWeidu(cursor.getString(cursor.getColumnIndex("weidu")));
			
			info.setLongitude(Double.valueOf(cursor.getString(cursor.getColumnIndex("weidu"))));

			info.setXiangao("限高:"+cursor.getString(cursor.getColumnIndex("xiangao")));

			info.setXiankuan("限宽:"+cursor.getString(cursor.getColumnIndex("xiankuan")));

			info.setTixingjuli(cursor.getString(cursor
					.getColumnIndex("tixingjuli")));
			
			Double juli=this.GetShortDistance(info.getLongitude(), info.getLatitude(), mCurrentLongitude, mCurrentLantitude);

			if(juli<Double.valueOf(info.getTixingjuli().toString()))//提醒距离小于指定
			{
			   infos.add(info);
			}
		}

		if (infos.size() > 0) {
			addInfosOverlay(infos);
		}

	}

	private void initMapClickEvent() {
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				mMarkerInfoLy.setVisibility(View.GONE);
				mBaiduMap.hideInfoWindow();

			}
		});
	}

	private void initMarkerClickEvent() {
		// 对Marker的点击
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(final Marker marker) {
				// 获得marker中的数据
				Info info = (Info) marker.getExtraInfo().get("info"); 

			InfoWindow mInfoWindow;
				// 生成一个TextView用户在地图中显示InfoWindow
				TextView location = new TextView(getApplicationContext());
				location.setBackgroundResource(R.drawable.location_tips);
				location.setPadding(30, 20, 30, 50);
				location.setText(info.getQiaoliangmingcheng());
				// 将marker所在的经纬度的信息转化成屏幕上的坐标
				final LatLng ll = marker.getPosition();
				Point p = mBaiduMap.getProjection().toScreenLocation(ll);
				p.y -= 47;
				LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
				// 为弹出的InfoWindow添加点击事件
				mInfoWindow = new InfoWindow(location, llInfo,
						new OnInfoWindowClickListener() {

							@Override
							public void onInfoWindowClick() {
								// 隐藏InfoWindow
								mBaiduMap.hideInfoWindow();
							}
						});
				// 显示InfoWindow
				mBaiduMap.showInfoWindow(mInfoWindow);
				// 设置详细信息布局为可见
				mMarkerInfoLy.setVisibility(View.VISIBLE);
				// 根据商家信息为详细信息布局设置信息
				 
				return true;
			}
		});
	}
 

	 

	 
	private void initOritationListener() {
		myOrientationListener = new MyOrientationListener(
				getApplicationContext());
		myOrientationListener
				.setOnOrientationListener(new OnOrientationListener() {
					@Override
					public void onOrientationChanged(float x) {
						mXDirection = (int) x;
						// 构造定位数据
						MyLocationData locData = new MyLocationData.Builder()
								.accuracy(mCurrentAccracy)
								// 此处设置开发者获取到的方向信息，顺时针0-360
								.direction(mXDirection)
								.latitude(mCurrentLantitude)
								.longitude(mCurrentLongitude).build();
						// 设置定位数据
						mBaiduMap.setMyLocationData(locData);
						// 设置自定义图标
						BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
								.fromResource(R.drawable.navi_map_gps_locked);
						MyLocationConfigeration config = new MyLocationConfigeration(
								mCurrentMode, true, mCurrentMarker);
						mBaiduMap.setMyLocationConfigeration(config);

					}
				});
	}
 
	private void initMyLocation() {
		// 定位初始化
		mLocationClient = new LocationClient(this);
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		// 设置定位的相关配置
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
		
	
	}

	 
	public void addInfosOverlay(List<Info> infos) {
		mBaiduMap.clear();
		LatLng latLng = null;
		OverlayOptions overlayOptions = null;
		Marker marker = null;
		for (Info info : infos) {
			// 位置
			latLng = new LatLng(info.getLatitude(), info.getLongitude());
			//生成自定义标签
			BitmapDescriptor bdA =BitmapDescriptorFactory.fromBitmap(GLFont.getImage(100, 25, info.getQiaoliangmingcheng(), 15,Color.WHITE));//此处就是生成一个带文字的mark图片
			overlayOptions = new MarkerOptions().position(latLng)
					.icon(bdA).zIndex(5).perspective(true)
                    .title(info.getQiaoliangmingcheng());
			
			// 图标
			//overlayOptions = new MarkerOptions().position(latLng).title("测试")
					//.icon(mIconMaker).zIndex(5);
		 	marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
			Bundle bundle = new Bundle();
			bundle.putSerializable("info", info);
			marker.setExtraInfo(bundle);
		}
		// 将地图移到到最后一个经纬度位置
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.setMapStatus(u);
	}
	 

 
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {

			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			// 构造定位数据
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(mXDirection).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mCurrentAccracy = location.getRadius();
			// 设置定位数据
			mBaiduMap.setMyLocationData(locData);
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();
			initData();
			// 设置自定义图标
			BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
					.fromResource(R.drawable.navi_map_gps_locked);
			MyLocationConfigeration config = new MyLocationConfigeration(
					mCurrentMode, true, mCurrentMarker);
			mBaiduMap.setMyLocationConfigeration(config);
			// 第一次定位时，将地图位置移动到当前位置
			if (isFristLocation) {
				isFristLocation = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

	}

	 
	private void center2myLoc() {
		LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);
	}

	@Override
	protected void onStart() {
		// 开启图层定位
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		// 开启方向传感器
		myOrientationListener.start();
		super.onStart();
	}

	@Override
	protected void onStop() {
		// 关闭图层定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();

		// 关闭方向传感器
		myOrientationListener.stop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
		mIconMaker.recycle();
		mMapView = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}
	
	
	static double DEF_PI = 3.14159265359; // PI
    static double DEF_2PI= 6.28318530712; // 2*PI
    static double DEF_PI180= 0.01745329252; // PI/180.0
    static double DEF_R =6370693.5; // radius of earth
            //适用于近距离
    public  double GetShortDistance(double lon1, double lat1, double lon2, double lat2)
    {
        double ew1, ns1, ew2, ns2;
        double dx, dy, dew;
        double distance;
        // 角度转换为弧度
        ew1 = lon1 * DEF_PI180;
        ns1 = lat1 * DEF_PI180;
        ew2 = lon2 * DEF_PI180;
        ns2 = lat2 * DEF_PI180;
        // 经度差
        dew = ew1 - ew2;
        // 若跨东经和西经180 度，进行调整
        if (dew > DEF_PI)
        dew = DEF_2PI - dew;
        else if (dew < -DEF_PI)
        dew = DEF_2PI + dew;
        dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
        dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
        // 勾股定理求斜边长
        distance = Math.sqrt(dx * dx + dy * dy);
        return distance;
    }
    */
}
