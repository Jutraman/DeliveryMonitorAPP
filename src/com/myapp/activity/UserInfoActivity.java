package com.myapp.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.view.ViewInject;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
 
import com.myweb.domain.User;

public class UserInfoActivity extends FinalActivity {

	@ViewInject(id = R.id.loginname)
	private TextView loginname;

	@ViewInject(id = R.id.loginpsw)
	private EditText loginpsw;
	
	@ViewInject(id = R.id.username)
	private EditText username;

	private FinalDb db;

	@ViewInject(id = R.id.addbtn, click = "addBtnOnClick")
	private Button addbtn;

	String userid = "";

	MyApplication myApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_userinfo);	

		db = FinalDb.create(UserInfoActivity.this, "gczs.db", true);

		 
 
		myApp = (MyApplication) getApplication(); // 获得自定义的应用程序MyApp
		
		User user = new User();

		user = db.findById(userid, User.class);

		loginname.setText(myApp.getUser().getLoginname());

		loginpsw.setText(myApp.getUser().getLoginpsw());

		username.setText(myApp.getUser().getUsername());
		 
	}


	public void addBtnOnClick(View v) {
 
		if (loginpsw.getText().toString().length() < 1) {
			Toast.makeText(UserInfoActivity.this, "密码不能为空!",
					Toast.LENGTH_LONG).show();
			return;
		}
		if (username.getText().toString().length() < 1) {
			Toast.makeText(UserInfoActivity.this, "厂商不能为空!",
					Toast.LENGTH_LONG).show();
			return;
		}
 
		 

		User user = myApp.getUser();
	 
		user.setLoginpsw(loginpsw.getText().toString());

		user.setUsername(username.getText().toString());
 
		db.update(user);
		
		myApp.setUser(user);
 
		Toast.makeText(UserInfoActivity.this, "修改成功!", Toast.LENGTH_LONG)
				.show();

		 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.add_question, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will

		return super.onOptionsItemSelected(item);
	}
}
