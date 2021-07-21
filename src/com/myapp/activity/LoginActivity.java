package com.myapp.activity;

import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.view.ViewInject;

import com.alibaba.fastjson.JSONObject;
import com.myapp.activity.R;
import com.myweb.domain.Dingdan;
 
import com.myweb.domain.User;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

public class LoginActivity extends FinalActivity {

	MyApplication myApp;

	User user;// 存储用户登录信息

	private FinalDb db;

	@ViewInject(id = R.id.button1, click = "loginBtnClick")
	private Button button1;

	@ViewInject(id = R.id.editText1)
	private EditText etUsername;

	@ViewInject(id = R.id.editText2)
	private EditText etPwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		myApp = (MyApplication) getApplication();

		db = FinalDb.create(LoginActivity.this, "gczs.db", true);

		db.save(new User());

		db.save(new Dingdan());
 
		List<User> userList = db.findAllByWhere(User.class,
				" loginname='zhangfei'");

		if (userList.size() < 1) {

			User user = new User();

			user.setUsername("张飞");

			user.setLoginname("zhangfei");

			user.setLoginpsw("123456");

			user.setUsertype("0");

			db.save(user);
		}
		
		/*List<Dingdan> ddList = db.findAllByWhere(Dingdan.class, " bianhao='888888'", "id");
		if (ddList.size() > 0) {
			Toast.makeText(LoginActivity.this, "该快递单已存在!",
					Toast.LENGTH_LONG).show();

		} else {
			Dingdan dd = new Dingdan();

			dd.setBianhao("888888");

			dd.setShoujianren("张飞");

			dd.setDianhua("1335566885");

			dd.setDizhi("55555555");

			dd.setZhuangtai("未送达");

			dd.setJindu("121.445742");

			dd.setWeidu("31.349614");

			db.save(dd);

		

		} */

	}

	public void loginBtnClick(View v) {

		if (etUsername.getText().toString().length() < 1) {
			Toast.makeText(LoginActivity.this, "登陆账号不能为空!", Toast.LENGTH_LONG)
					.show();
			return;
		}

		if (etPwd.getText().toString().length() < 1) {
			Toast.makeText(LoginActivity.this, "登陆密码不能为空!", Toast.LENGTH_LONG)
					.show();
			return;
		}

		List<User> userList = db.findAllByWhere(User.class, " loginname='"
				+ etUsername.getText().toString() + "' and  loginpsw='"
				+ etPwd.getText().toString() + "'");

		if (userList.size() < 1) {

			Toast.makeText(LoginActivity.this, "登陆失败,请检查登陆账号和密码是否正确!",
					Toast.LENGTH_LONG).show();
		} else {

			User user = userList.get(0);

			myApp.setUser(user);

			Intent intent = new Intent();

			intent.setClass(LoginActivity.this, AdminIndexActivity.class);

			startActivity(intent);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		return super.onOptionsItemSelected(item);
	}
}
