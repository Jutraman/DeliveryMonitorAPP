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
	 * ��ͼ�ؼ�
	
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

	 
	private String[] mStyles = new String[] { "��ͼģʽ��������", "��ͼģʽ�����桿",
			"��ͼģʽ�����̡�" };
	private int mCurrentStyle = 0;
	// ��ʼ��ȫ�� bitmap ��Ϣ������ʱ��ʱ recycle
	private BitmapDescriptor mIconMaker;
	 
	private RelativeLayout mMarkerInfoLy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ע��÷���Ҫ��setContentView����֮ǰʵ��
		SDKInitializer.initialize(getApplicationContext());

		setContentView(R.layout.fujin_map_activity);
		// ��һ�ζ�λ
		isFristLocation = true;
		// ��ȡ��ͼ�ؼ�����
		mMapView = (MapView) findViewById(R.id.id_bmapView);
		mMarkerInfoLy = (RelativeLayout) findViewById(R.id.id_marker_info);
		// ��õ�ͼ��ʵ��
		mBaiduMap = mMapView.getMap();
		mIconMaker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
		// ��ʼ����λ
		initMyLocation();
		// ��ʼ��������
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

			info.setXiangao("�޸�:"+cursor.getString(cursor.getColumnIndex("xiangao")));

			info.setXiankuan("�޿�:"+cursor.getString(cursor.getColumnIndex("xiankuan")));

			info.setTixingjuli(cursor.getString(cursor
					.getColumnIndex("tixingjuli")));
			
			Double juli=this.GetShortDistance(info.getLongitude(), info.getLatitude(), mCurrentLongitude, mCurrentLantitude);

			if(juli<Double.valueOf(info.getTixingjuli().toString()))//���Ѿ���С��ָ��
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
		// ��Marker�ĵ��
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(final Marker marker) {
				// ���marker�е�����
				Info info = (Info) marker.getExtraInfo().get("info"); 

			InfoWindow mInfoWindow;
				// ����һ��TextView�û��ڵ�ͼ����ʾInfoWindow
				TextView location = new TextView(getApplicationContext());
				location.setBackgroundResource(R.drawable.location_tips);
				location.setPadding(30, 20, 30, 50);
				location.setText(info.getQiaoliangmingcheng());
				// ��marker���ڵľ�γ�ȵ���Ϣת������Ļ�ϵ�����
				final LatLng ll = marker.getPosition();
				Point p = mBaiduMap.getProjection().toScreenLocation(ll);
				p.y -= 47;
				LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
				// Ϊ������InfoWindow��ӵ���¼�
				mInfoWindow = new InfoWindow(location, llInfo,
						new OnInfoWindowClickListener() {

							@Override
							public void onInfoWindowClick() {
								// ����InfoWindow
								mBaiduMap.hideInfoWindow();
							}
						});
				// ��ʾInfoWindow
				mBaiduMap.showInfoWindow(mInfoWindow);
				// ������ϸ��Ϣ����Ϊ�ɼ�
				mMarkerInfoLy.setVisibility(View.VISIBLE);
				// �����̼���ϢΪ��ϸ��Ϣ����������Ϣ
				 
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
						// ���춨λ����
						MyLocationData locData = new MyLocationData.Builder()
								.accuracy(mCurrentAccracy)
								// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
								.direction(mXDirection)
								.latitude(mCurrentLantitude)
								.longitude(mCurrentLongitude).build();
						// ���ö�λ����
						mBaiduMap.setMyLocationData(locData);
						// �����Զ���ͼ��
						BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
								.fromResource(R.drawable.navi_map_gps_locked);
						MyLocationConfigeration config = new MyLocationConfigeration(
								mCurrentMode, true, mCurrentMarker);
						mBaiduMap.setMyLocationConfigeration(config);

					}
				});
	}
 
	private void initMyLocation() {
		// ��λ��ʼ��
		mLocationClient = new LocationClient(this);
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		// ���ö�λ���������
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
		
	
	}

	 
	public void addInfosOverlay(List<Info> infos) {
		mBaiduMap.clear();
		LatLng latLng = null;
		OverlayOptions overlayOptions = null;
		Marker marker = null;
		for (Info info : infos) {
			// λ��
			latLng = new LatLng(info.getLatitude(), info.getLongitude());
			//�����Զ����ǩ
			BitmapDescriptor bdA =BitmapDescriptorFactory.fromBitmap(GLFont.getImage(100, 25, info.getQiaoliangmingcheng(), 15,Color.WHITE));//�˴���������һ�������ֵ�markͼƬ
			overlayOptions = new MarkerOptions().position(latLng)
					.icon(bdA).zIndex(5).perspective(true)
                    .title(info.getQiaoliangmingcheng());
			
			// ͼ��
			//overlayOptions = new MarkerOptions().position(latLng).title("����")
					//.icon(mIconMaker).zIndex(5);
		 	marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
			Bundle bundle = new Bundle();
			bundle.putSerializable("info", info);
			marker.setExtraInfo(bundle);
		}
		// ����ͼ�Ƶ������һ����γ��λ��
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.setMapStatus(u);
	}
	 

 
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {

			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null)
				return;
			// ���춨λ����
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(mXDirection).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mCurrentAccracy = location.getRadius();
			// ���ö�λ����
			mBaiduMap.setMyLocationData(locData);
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();
			initData();
			// �����Զ���ͼ��
			BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
					.fromResource(R.drawable.navi_map_gps_locked);
			MyLocationConfigeration config = new MyLocationConfigeration(
					mCurrentMode, true, mCurrentMarker);
			mBaiduMap.setMyLocationConfigeration(config);
			// ��һ�ζ�λʱ������ͼλ���ƶ�����ǰλ��
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
		// ����ͼ�㶨λ
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		// �������򴫸���
		myOrientationListener.start();
		super.onStart();
	}

	@Override
	protected void onStop() {
		// �ر�ͼ�㶨λ
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();

		// �رշ��򴫸���
		myOrientationListener.stop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// ��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onDestroy();
		mIconMaker.recycle();
		mMapView = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// ��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// ��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onPause();
	}
	
	
	static double DEF_PI = 3.14159265359; // PI
    static double DEF_2PI= 6.28318530712; // 2*PI
    static double DEF_PI180= 0.01745329252; // PI/180.0
    static double DEF_R =6370693.5; // radius of earth
            //�����ڽ�����
    public  double GetShortDistance(double lon1, double lat1, double lon2, double lat2)
    {
        double ew1, ns1, ew2, ns2;
        double dx, dy, dew;
        double distance;
        // �Ƕ�ת��Ϊ����
        ew1 = lon1 * DEF_PI180;
        ns1 = lat1 * DEF_PI180;
        ew2 = lon2 * DEF_PI180;
        ns2 = lat2 * DEF_PI180;
        // ���Ȳ�
        dew = ew1 - ew2;
        // ���綫��������180 �ȣ����е���
        if (dew > DEF_PI)
        dew = DEF_2PI - dew;
        else if (dew < -DEF_PI)
        dew = DEF_2PI + dew;
        dx = DEF_R * Math.cos(ns1) * dew; // �������򳤶�(��γ��Ȧ�ϵ�ͶӰ����)
        dy = DEF_R * (ns1 - ns2); // �ϱ����򳤶�(�ھ���Ȧ�ϵ�ͶӰ����)
        // ���ɶ�����б�߳�
        distance = Math.sqrt(dx * dx + dy * dy);
        return distance;
    }
    */
}
