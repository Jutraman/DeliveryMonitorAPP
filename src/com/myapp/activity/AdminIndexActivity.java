package com.myapp.activity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.view.ViewInject;
 

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Build;

public class AdminIndexActivity extends FinalActivity {

	@ViewInject(id = R.id.button1, click = "button1OnClick")
	private Button button1;
 
	@ViewInject(id = R.id.button3, click = "button3OnClick")
	private Button button3;

	TextView logininfo;

	private MyApplication myapp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_index);

		logininfo = (TextView) findViewById(R.id.logininfo);

		myapp = (MyApplication) getApplication(); // 获得自定义的应用程序MyApp

		logininfo.setText("用户[" + myapp.getUser().getUsername() + "]你好!");

	}

	public void button1OnClick(View v) {

		Intent intent = new Intent();

		intent.setClass(AdminIndexActivity.this, ChuchangListActivity.class);

		startActivity(intent);

	}

	 

	public void button3OnClick(View v) {

		Intent intent = new Intent();

		intent.setClass(AdminIndexActivity.this, UserInfoActivity.class);

		startActivity(intent);

	}

}
