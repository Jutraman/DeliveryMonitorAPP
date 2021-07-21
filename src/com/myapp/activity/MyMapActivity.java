package com.myapp.activity;

import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalDb;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.Overlay;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.myapp.common.BMapUtil;
import com.myweb.domain.Dingdan;

public class MyMapActivity extends FinalActivity {
	private enum E_BUTTON_TYPE {
		LOC, COMPASS, FOLLOW
	}

	// 定位相关
	LocationClient mLocClient;

	LocationData locData = null;

	public MyLocationListenner myListener = new MyLocationListenner();

	private FinalDb db;

	Dingdan dingdan;

	String dingdanid = "";

	private E_BUTTON_TYPE mCurBtnType;

	// 定位图层
	locationOverlay myLocationOverlay = null;
	// 弹出泡泡图层
	private PopupOverlay pop = null;// 弹出泡泡图层，浏览节点时使用

	private TextView popupText = null;// 泡泡view

	private View viewCache = null;

	// 地图相关，使用继承MapView的MyLocationMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	MyLocationMapView mMapView = null; // 地图View
	private MapController mMapController = null;

	// UI相关
	OnCheckedChangeListener radioButtonListener = null;
	boolean isRequest = false;// 是否手动触发请求定位
	boolean isFirstLoc = true;// 是否首次定位

	private String[] aa = new String[] { "前方堵车", "前方修路", "客户要求", "去加油" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 使用地图sdk前需先初始化BMapManager. BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
		 * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
		 */

		Intent intent = getIntent();

		db = FinalDb.create(MyMapActivity.this, "gczs.db", true);

		dingdanid = intent.getStringExtra("id");

		dingdan = db.findById(dingdanid, Dingdan.class);

		MyApplication app = (MyApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(getApplicationContext());
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapManager.init(new MyApplication.MyGeneralListener());
		}
		setContentView(R.layout.activity_map);

		mCurBtnType = E_BUTTON_TYPE.LOC;
		// 地图初始化
		mMapView = (MyLocationMapView) findViewById(R.id.bmapView);
		mMapView.getOverlays().clear();
		// 添加收货地点
		/**
		 * 在想要添加Overlay的地方使用以下代码， 比如Activity的onCreate()中
		 */
		// 准备要添加的Overlay
		double mLat1 = Double.parseDouble(dingdan.getWeidu());
		double mLon1 = Double.parseDouble(dingdan.getJindu());

		// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
		GeoPoint p1 = new GeoPoint((int) (mLat1 * 1E6), (int) (mLon1 * 1E6));

		// 准备overlay图像数据，根据实情情况修复
		Drawable mark = getResources().getDrawable(R.drawable.maker);
		// 用OverlayItem准备Overlay数据
		OverlayItem item1 = new OverlayItem(p1, "item1", "item1");
		// 使用setMarker()方法设置overlay图片,如果不设置则使用构建ItemizedOverlay时的默认设置

		// 创建IteminizedOverlay
		OverlayTest itemOverlay = new OverlayTest(mark, mMapView);
		// 将IteminizedOverlay添加到MapView中

		mMapView.getOverlays().add(itemOverlay);

		// 现在所有准备工作已准备好，使用以下方法管理overlay.
		// 添加overlay, 当批量添加Overlay时使用addItem(List<OverlayItem>)效率更高
		itemOverlay.addItem(item1);

		// 删除overlay .
		// itemOverlay.removeItem(itemOverlay.getItem(0));
		// mMapView.refresh();
		// 清除overlay
		// itemOverlay.removeAll();
		// mMapView.refresh();

		mMapController = mMapView.getController();

		mMapView.getController().setZoom(14);
		mMapView.getController().enableClick(true);
		mMapView.setBuiltInZoomControls(true);
		createPaopao();

		// 定位初始化
		mLocClient = new LocationClient(this);
		locData = new LocationData();
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		// 定位图层初始化
		myLocationOverlay = new locationOverlay(mMapView);
		// 设置定位数据
		myLocationOverlay.setData(locData);
		// 添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		// 修改定位数据后刷新图层生效
		// mMapView.refresh();

		// GeoPoint p = new GeoPoint(
		// (int) (Double.valueOf(dingdan.getWeidu()) * 1E6),
		// (int) (Double.valueOf(dingdan.getJindu()) * 1E6));

		mMapView.refresh();

		timer = new Timer();

		timer.schedule(task, 1000, 1000);
	}

	AlertDialog.Builder builder = null;

	private Timer timer;
	int a = 1;
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			runOnUiThread(new Runnable() { // UI thread
				@Override
				public void run() {
					a++;
					if (a % 15 == 0) {

						builder = new Builder(MyMapActivity.this);
						builder.setTitle("来自服务器的消息");
						builder.setMessage("您已偏离路线，请说明原因!");
						builder.setPositiveButton("选择原因",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										builder = new Builder(
												MyMapActivity.this);
										builder.setTitle("请选择原因");
										builder.setIcon(R.drawable.ic_launcher);
										builder.setSingleChoiceItems(
												aa,
												0,
												new DialogInterface.OnClickListener() {

													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														if (which == 0
																|| which == 1) {
															builder = new Builder(
																	MyMapActivity.this);
															builder.setTitle("来自服务器的消息");
															builder.setMessage("请回到原始路线!");

															builder.setNegativeButton(
																	"知道了",
																	new DialogInterface.OnClickListener() {

																		@Override
																		public void onClick(
																				DialogInterface dialog,
																				int which) {

																		}
																	});

															builder.show();
														} else if (which == 2
																|| which == 3) {
															builder = new Builder(
																	MyMapActivity.this);
															builder.setTitle("来自服务器的消息");
															builder.setMessage("继续行驶!");

															builder.setNegativeButton(
																	"知道了",
																	new DialogInterface.OnClickListener() {

																		@Override
																		public void onClick(
																				DialogInterface dialog,
																				int which) {

																		}
																	});

															builder.show();
														}
														// TODO
														// Auto-generated
														// method stub
														dialog.dismiss();
													}
												});
										builder.setNegativeButton("关闭", null);
										builder.show();
									}

								});
						builder.show();

					}

				}
			});

		}
	};

	/*
	 * 要处理overlay点击事件时需要继承ItemizedOverlay 不处理点击事件时可直接生成ItemizedOverlay.
	 */
	class OverlayTest extends ItemizedOverlay<OverlayItem> {
		// 用MapView构造ItemizedOverlay
		public OverlayTest(Drawable mark, MapView mapView) {
			super(mark, mapView);
		}

		protected boolean onTap(int index) {
			// 在此处理item点击事件
			System.out.println("item onTap: " + index);
			return true;
		}

		public boolean onTap(GeoPoint pt, MapView mapView) {
			// 在此处理MapView的点击事件，当返回 true时
			super.onTap(pt, mapView);
			return false;
		}

	}

	/**
	 * 手动触发一次定位请求
	 */
	public void requestLocClick() {
		isRequest = true;
		mLocClient.requestLocation();
		Toast.makeText(MyMapActivity.this, "正在定位……", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 修改位置图标
	 * 
	 * @param marker
	 */
	public void modifyLocationOverlayIcon(Drawable marker) {
		// 当传入marker为null时，使用默认图标绘制
		myLocationOverlay.setMarker(marker);
		// 修改图层，需要刷新MapView生效
		mMapView.refresh();
	}

	/**
	 * 创建弹出泡泡图层
	 */
	public void createPaopao() {
		viewCache = getLayoutInflater()
				.inflate(R.layout.custom_text_view, null);
		popupText = (TextView) viewCache.findViewById(R.id.textcache);
		// 泡泡点击响应回调
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {
				Log.v("click", "clickapoapo");
			}
		};
		pop = new PopupOverlay(mMapView, popListener);
		MyLocationMapView.pop = pop;
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;

			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			String a = String.valueOf(location.getLongitude());

			Double b = Double.valueOf(a);
			// 如果不显示定位精度圈，将accuracy赋值为0即可
			locData.accuracy = location.getRadius();
			// 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
			locData.direction = location.getDerect();
			// 更新定位数据
			myLocationOverlay.setData(locData);
			// 更新图层数据执行刷新后生效
			mMapView.refresh();
			// 是手动触发请求或首次定位时，移动到定位点
			if (isRequest || isFirstLoc) {
				// 移动地图到定位点
				Log.d("LocationOverlay", "receive location, animate to it");
				mMapController.animateTo(new GeoPoint(
						(int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
				isRequest = false;
				myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
				mCurBtnType = E_BUTTON_TYPE.FOLLOW;
			}
			// 首次定位完成
			isFirstLoc = false;

			// 比对距离
			Double juli = GetShortDistance(
					Double.parseDouble(dingdan.getJindu()),
					Double.parseDouble(dingdan.getWeidu()), locData.longitude,
					locData.latitude);

			if (juli < 3000)// 提醒距离小于指定
			{
				Toast.makeText(MyMapActivity.this, "客户已签收", Toast.LENGTH_LONG)
						.show();

				dingdan.setZhuangtai("已送达");

				db.update(dingdan);

				finish();
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	// 继承MyLocationOverlay重写dispatchTap实现点击处理
	public class locationOverlay extends MyLocationOverlay {

		public locationOverlay(MapView mapView) {
			super(mapView);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean dispatchTap() {
			// TODO Auto-generated method stub
			// 处理点击事件,弹出泡泡
			popupText.setBackgroundResource(R.drawable.popup);
			popupText.setText("我的位置");
			pop.showPopup(BMapUtil.getBitmapFromView(popupText), new GeoPoint(
					(int) (locData.latitude * 1e6),
					(int) (locData.longitude * 1e6)), 8);
			return true;
		}

	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		timer.cancel();
		mMapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	static double DEF_PI = 3.14159265359; // PI
	static double DEF_2PI = 6.28318530712; // 2*PI
	static double DEF_PI180 = 0.01745329252; // PI/180.0
	static double DEF_R = 6370693.5; // radius of earth

	// 适用于近距离
	public static double GetShortDistance(double lon1, double lat1,
			double lon2, double lat2) {
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

}

/**
 * 继承MapView重写onTouchEvent实现泡泡处理操作
 * 
 * @author hejin
 * 
 */
class MyLocationMapView extends MapView {
	static PopupOverlay pop = null;// 弹出泡泡图层，点击图标使用

	public MyLocationMapView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyLocationMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLocationMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!super.onTouchEvent(event)) {
			// 消隐泡泡
			if (pop != null && event.getAction() == MotionEvent.ACTION_UP)
				pop.hidePop();
		}
		return true;
	}

}
