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

	// ��λ���
	LocationClient mLocClient;

	LocationData locData = null;

	public MyLocationListenner myListener = new MyLocationListenner();

	private FinalDb db;

	Dingdan dingdan;

	String dingdanid = "";

	private E_BUTTON_TYPE mCurBtnType;

	// ��λͼ��
	locationOverlay myLocationOverlay = null;
	// ��������ͼ��
	private PopupOverlay pop = null;// ��������ͼ�㣬����ڵ�ʱʹ��

	private TextView popupText = null;// ����view

	private View viewCache = null;

	// ��ͼ��أ�ʹ�ü̳�MapView��MyLocationMapViewĿ������дtouch�¼�ʵ�����ݴ���
	// ���������touch�¼���������̳У�ֱ��ʹ��MapView����
	MyLocationMapView mMapView = null; // ��ͼView
	private MapController mMapController = null;

	// UI���
	OnCheckedChangeListener radioButtonListener = null;
	boolean isRequest = false;// �Ƿ��ֶ���������λ
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ

	private String[] aa = new String[] { "ǰ���³�", "ǰ����·", "�ͻ�Ҫ��", "ȥ����" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * ʹ�õ�ͼsdkǰ���ȳ�ʼ��BMapManager. BMapManager��ȫ�ֵģ���Ϊ���MapView���ã�����Ҫ��ͼģ�鴴��ǰ������
		 * ���ڵ�ͼ��ͼģ�����ٺ����٣�ֻҪ���е�ͼģ����ʹ�ã�BMapManager�Ͳ�Ӧ������
		 */

		Intent intent = getIntent();

		db = FinalDb.create(MyMapActivity.this, "gczs.db", true);

		dingdanid = intent.getStringExtra("id");

		dingdan = db.findById(dingdanid, Dingdan.class);

		MyApplication app = (MyApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(getApplicationContext());
			/**
			 * ���BMapManagerû�г�ʼ�����ʼ��BMapManager
			 */
			app.mBMapManager.init(new MyApplication.MyGeneralListener());
		}
		setContentView(R.layout.activity_map);

		mCurBtnType = E_BUTTON_TYPE.LOC;
		// ��ͼ��ʼ��
		mMapView = (MyLocationMapView) findViewById(R.id.bmapView);
		mMapView.getOverlays().clear();
		// ����ջ��ص�
		/**
		 * ����Ҫ���Overlay�ĵط�ʹ�����´��룬 ����Activity��onCreate()��
		 */
		// ׼��Ҫ��ӵ�Overlay
		double mLat1 = Double.parseDouble(dingdan.getWeidu());
		double mLon1 = Double.parseDouble(dingdan.getJindu());

		// �ø����ľ�γ�ȹ���GeoPoint����λ��΢�� (�� * 1E6)
		GeoPoint p1 = new GeoPoint((int) (mLat1 * 1E6), (int) (mLon1 * 1E6));

		// ׼��overlayͼ�����ݣ�����ʵ������޸�
		Drawable mark = getResources().getDrawable(R.drawable.maker);
		// ��OverlayItem׼��Overlay����
		OverlayItem item1 = new OverlayItem(p1, "item1", "item1");
		// ʹ��setMarker()��������overlayͼƬ,�����������ʹ�ù���ItemizedOverlayʱ��Ĭ������

		// ����IteminizedOverlay
		OverlayTest itemOverlay = new OverlayTest(mark, mMapView);
		// ��IteminizedOverlay��ӵ�MapView��

		mMapView.getOverlays().add(itemOverlay);

		// ��������׼��������׼���ã�ʹ�����·�������overlay.
		// ���overlay, ���������Overlayʱʹ��addItem(List<OverlayItem>)Ч�ʸ���
		itemOverlay.addItem(item1);

		// ɾ��overlay .
		// itemOverlay.removeItem(itemOverlay.getItem(0));
		// mMapView.refresh();
		// ���overlay
		// itemOverlay.removeAll();
		// mMapView.refresh();

		mMapController = mMapView.getController();

		mMapView.getController().setZoom(14);
		mMapView.getController().enableClick(true);
		mMapView.setBuiltInZoomControls(true);
		createPaopao();

		// ��λ��ʼ��
		mLocClient = new LocationClient(this);
		locData = new LocationData();
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		// ��λͼ���ʼ��
		myLocationOverlay = new locationOverlay(mMapView);
		// ���ö�λ����
		myLocationOverlay.setData(locData);
		// ��Ӷ�λͼ��
		mMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		// �޸Ķ�λ���ݺ�ˢ��ͼ����Ч
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
						builder.setTitle("���Է���������Ϣ");
						builder.setMessage("����ƫ��·�ߣ���˵��ԭ��!");
						builder.setPositiveButton("ѡ��ԭ��",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										builder = new Builder(
												MyMapActivity.this);
										builder.setTitle("��ѡ��ԭ��");
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
															builder.setTitle("���Է���������Ϣ");
															builder.setMessage("��ص�ԭʼ·��!");

															builder.setNegativeButton(
																	"֪����",
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
															builder.setTitle("���Է���������Ϣ");
															builder.setMessage("������ʻ!");

															builder.setNegativeButton(
																	"֪����",
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
										builder.setNegativeButton("�ر�", null);
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
	 * Ҫ����overlay����¼�ʱ��Ҫ�̳�ItemizedOverlay ���������¼�ʱ��ֱ������ItemizedOverlay.
	 */
	class OverlayTest extends ItemizedOverlay<OverlayItem> {
		// ��MapView����ItemizedOverlay
		public OverlayTest(Drawable mark, MapView mapView) {
			super(mark, mapView);
		}

		protected boolean onTap(int index) {
			// �ڴ˴���item����¼�
			System.out.println("item onTap: " + index);
			return true;
		}

		public boolean onTap(GeoPoint pt, MapView mapView) {
			// �ڴ˴���MapView�ĵ���¼��������� trueʱ
			super.onTap(pt, mapView);
			return false;
		}

	}

	/**
	 * �ֶ�����һ�ζ�λ����
	 */
	public void requestLocClick() {
		isRequest = true;
		mLocClient.requestLocation();
		Toast.makeText(MyMapActivity.this, "���ڶ�λ����", Toast.LENGTH_SHORT).show();
	}

	/**
	 * �޸�λ��ͼ��
	 * 
	 * @param marker
	 */
	public void modifyLocationOverlayIcon(Drawable marker) {
		// ������markerΪnullʱ��ʹ��Ĭ��ͼ�����
		myLocationOverlay.setMarker(marker);
		// �޸�ͼ�㣬��Ҫˢ��MapView��Ч
		mMapView.refresh();
	}

	/**
	 * ������������ͼ��
	 */
	public void createPaopao() {
		viewCache = getLayoutInflater()
				.inflate(R.layout.custom_text_view, null);
		popupText = (TextView) viewCache.findViewById(R.id.textcache);
		// ���ݵ����Ӧ�ص�
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
	 * ��λSDK��������
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
			// �������ʾ��λ����Ȧ����accuracy��ֵΪ0����
			locData.accuracy = location.getRadius();
			// �˴��������� locData�ķ�����Ϣ, �����λ SDK δ���ط�����Ϣ���û������Լ�ʵ�����̹�����ӷ�����Ϣ��
			locData.direction = location.getDerect();
			// ���¶�λ����
			myLocationOverlay.setData(locData);
			// ����ͼ������ִ��ˢ�º���Ч
			mMapView.refresh();
			// ���ֶ�����������״ζ�λʱ���ƶ�����λ��
			if (isRequest || isFirstLoc) {
				// �ƶ���ͼ����λ��
				Log.d("LocationOverlay", "receive location, animate to it");
				mMapController.animateTo(new GeoPoint(
						(int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
				isRequest = false;
				myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
				mCurBtnType = E_BUTTON_TYPE.FOLLOW;
			}
			// �״ζ�λ���
			isFirstLoc = false;

			// �ȶԾ���
			Double juli = GetShortDistance(
					Double.parseDouble(dingdan.getJindu()),
					Double.parseDouble(dingdan.getWeidu()), locData.longitude,
					locData.latitude);

			if (juli < 3000)// ���Ѿ���С��ָ��
			{
				Toast.makeText(MyMapActivity.this, "�ͻ���ǩ��", Toast.LENGTH_LONG)
						.show();

				dingdan.setZhuangtai("���ʹ�");

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

	// �̳�MyLocationOverlay��дdispatchTapʵ�ֵ������
	public class locationOverlay extends MyLocationOverlay {

		public locationOverlay(MapView mapView) {
			super(mapView);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean dispatchTap() {
			// TODO Auto-generated method stub
			// �������¼�,��������
			popupText.setBackgroundResource(R.drawable.popup);
			popupText.setText("�ҵ�λ��");
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

	// �����ڽ�����
	public static double GetShortDistance(double lon1, double lat1,
			double lon2, double lat2) {
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

}

/**
 * �̳�MapView��дonTouchEventʵ�����ݴ������
 * 
 * @author hejin
 * 
 */
class MyLocationMapView extends MapView {
	static PopupOverlay pop = null;// ��������ͼ�㣬���ͼ��ʹ��

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
			// ��������
			if (pop != null && event.getAction() == MotionEvent.ACTION_UP)
				pop.hidePop();
		}
		return true;
	}

}
