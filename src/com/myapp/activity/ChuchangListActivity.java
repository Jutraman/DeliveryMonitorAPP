package com.myapp.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.view.ViewInject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.myweb.domain.Dingdan;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ChuchangListActivity extends ListActivity {

	MyApplication myApp;

	private List<HashMap<String, Object>> list;

	private String[] opr = new String[] { "删除" };

	private String[] opr1 = new String[] { "删除", "送单" };

	@ViewInject(id = R.id.button1, click = "chuchangBtnClick")
	private Button button1;

	List<Map<String, Object>> jtcylist1;

	private MyApplication myapp;

	private FinalDb db;

	private final int REQUESTCODE = 1;// 返回的结果码

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_chuchang_list);

		FinalActivity.initInjectedView(this);

		myApp = (MyApplication) getApplication();

		db = FinalDb.create(ChuchangListActivity.this, "gczs.db", true);

		getListView().setOnItemClickListener(new OnItemClickListener() {
			// 第position项被单击时激发该方法。
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});

		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {

				HashMap<String, String> map = (HashMap<String, String>) getListView()
						.getItemAtPosition(position);

				String id1 = map.get("id").toString();

				String zhuangtai = map.get("zhuangtai").toString();

				if (zhuangtai.equals("当前状态:已送达")) {
					showOprDialog(id1);
				} else {
					showOprDialog1(id1);
				}

				return true;

			}
		});

	}

	public void chuchangBtnClick(View v) {

		Intent intent = new Intent();

		intent.setClass(ChuchangListActivity.this, MipcaActivityCapture.class);

		startActivityForResult(intent, REQUESTCODE);// 表示可以返回结果

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 2) {
			if (requestCode == REQUESTCODE) {
				String info = data.getStringExtra("info");// 接收返回数据
				if (info.length() > 0) {

					/*
					 * if (info.indexOf("SerialNumber:") < 0) {
					 * Toast.makeText(ChuchangListActivity.this, "二维码信息不对!",
					 * Toast.LENGTH_LONG).show(); } else { String[] aa =
					 * info.split(":"); if (aa.length != 2) {
					 * Toast.makeText(ChuchangListActivity.this, "二维码信息不对!",
					 * Toast.LENGTH_LONG).show(); } else { String bianhao =
					 * aa[1];
					 * 
					 * chuchang(bianhao); }
					 * 
					 * }
					 */
					try {
						String[] aa = info.split(",");
						Toast.makeText(ChuchangListActivity.this, info,
								Toast.LENGTH_LONG).show();
						Toast.makeText(ChuchangListActivity.this,
								String.valueOf(aa.length), Toast.LENGTH_LONG)
								.show();
						if (aa.length != 6) {
							Toast.makeText(ChuchangListActivity.this,
									"二维码信息不对!", Toast.LENGTH_LONG).show();
						} else {
							String bianhao = aa[0];

							String xingming = aa[1];

							String dianhua = aa[2];

							String dizhi = aa[3];

							String jindu = aa[4];

							String weidu = aa[5];

							jiedan(bianhao, xingming, dianhua, dizhi, jindu,
									weidu);
						}

					} catch (Exception ex) {

					}

				}

			}
		}

	}

	public void jiedan(String bianhao, String xingming, String dianhua,
			String dizhi, String jindu, String weidu) {
		List<Dingdan> ddList = db.findAllByWhere(Dingdan.class, " bianhao='"
				+ bianhao + "'", "id");
		if (ddList.size() > 0) {
			Toast.makeText(ChuchangListActivity.this, "该快递单已存在!",
					Toast.LENGTH_LONG).show();

		} else {
			Dingdan dd = new Dingdan();

			dd.setBianhao(bianhao);

			dd.setShoujianren(xingming);

			dd.setDianhua(dianhua);

			dd.setDizhi(dizhi);

			dd.setZhuangtai("未送达");

			dd.setJindu(jindu);

			dd.setWeidu(weidu);

			db.save(dd);

			Toast.makeText(ChuchangListActivity.this, "扫码成功!",
					Toast.LENGTH_LONG).show();
			init();

		}

	}

	public void init() {

		List<Dingdan> ddList = db.findAll(Dingdan.class, "id desc");

		list = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < ddList.size(); i++) {

			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("id", String.valueOf(ddList.get(i).getId()));

			map.put("bianhao",
					"订单:" + String.valueOf(ddList.get(i).getBianhao()));

			map.put("zhuangtai",
					"当前状态:" + String.valueOf(ddList.get(i).getZhuangtai()));

			map.put("shoujianren",
					"收件人:" + String.valueOf(ddList.get(i).getShoujianren()));

			map.put("dianhua",
					"电话:" + String.valueOf(ddList.get(i).getDianhua()));

			map.put("dizhi", "收件地址:" + String.valueOf(ddList.get(i).getDizhi())
					+ "(经纬度:" + ddList.get(i).getJindu() + ","
					+ ddList.get(i).getWeidu() + ")");

			list.add(map);
		}
		SimpleAdapter listAdapter = new SimpleAdapter(this, list,
				R.layout.activity_chuchang_list_item, new String[] { "id",
						"bianhao", "zhuangtai", "shoujianren", "dianhua",
						"dizhi" }, new int[] { R.id.id, R.id.bianhao,
						R.id.zhuangtai, R.id.shoujianren, R.id.dianhua,
						R.id.dizhi });
		setListAdapter(listAdapter);
	}

	private void showOprDialog(String id) {

		final String _id = id;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("请选择操作:");

		builder.setItems(opr, new DialogInterface.OnClickListener()

		{
			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (which == 0) {

					db.deleteById(Dingdan.class, _id);

					Toast.makeText(ChuchangListActivity.this, "操作成功!",
							Toast.LENGTH_LONG).show();

					init();

				}
			}
		});

		builder.create().show();
	}

	private void showOprDialog1(String id) {

		final String _id = id;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("请选择操作:");

		builder.setItems(opr1, new DialogInterface.OnClickListener()

		{
			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (which == 0) {

					db.deleteById(Dingdan.class, _id);

					Toast.makeText(ChuchangListActivity.this, "操作成功!",
							Toast.LENGTH_LONG).show();

					init();

				} else if (which == 1) {

					Intent intent = new Intent();

					intent.setClass(ChuchangListActivity.this,
							MyMapActivity.class);

					intent.putExtra("id", _id);

					startActivity(intent);

				}

			}
		});

		builder.create().show();
	}

	// 子页面关闭时调用
	@Override
	protected void onResume() {

		super.onResume();

		handler1.post(runnable);
	}

	private Runnable runnable = new Runnable() {

		public void run() {
			// 做操作
			handler1.sendEmptyMessage(1);
		}
	};

	private Handler handler1 = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {

			case 1:
				init();

				break;
			}
		};
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// getMenuInflater().inflate(R.menu.app_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		return super.onOptionsItemSelected(item);
	}

}
