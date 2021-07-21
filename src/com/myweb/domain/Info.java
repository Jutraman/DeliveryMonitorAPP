package com.myweb.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Info implements Serializable {
	private static final long serialVersionUID = -758459502806858414L;

	// ¾«¶È
	private double latitude;
	// Î³¶È
	private double longitude;

	private String id;

	private String qiaoliangmingcheng;

	private String jindu;

	private String weidu;

	private String xiangao;

	private String xiankuan;

	private String tixingjuli;

	public Info() {
	}

	public Info(String id, double latitude, double longitude,
			String qiaoliangmingcheng, String jindu, String weidu,
			String xiangao, String xiankuan, String tixingjuli) {
		super();
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;

		this.qiaoliangmingcheng = qiaoliangmingcheng;
		this.jindu = jindu;
		this.weidu = weidu;
		this.xiangao = xiangao;
		this.xiankuan = xiankuan;
		this.tixingjuli = tixingjuli;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQiaoliangmingcheng() {
		return qiaoliangmingcheng;
	}

	public void setQiaoliangmingcheng(String qiaoliangmingcheng) {
		this.qiaoliangmingcheng = qiaoliangmingcheng;
	}

	public String getJindu() {
		return jindu;
	}

	public void setJindu(String jindu) {
		this.jindu = jindu;
	}

	public String getWeidu() {
		return weidu;
	}

	public void setWeidu(String weidu) {
		this.weidu = weidu;
	}

	public String getXiangao() {
		return xiangao;
	}

	public void setXiangao(String xiangao) {
		this.xiangao = xiangao;
	}

	public String getXiankuan() {
		return xiankuan;
	}

	public void setXiankuan(String xiankuan) {
		this.xiankuan = xiankuan;
	}

	public String getTixingjuli() {
		return tixingjuli;
	}

	public void setTixingjuli(String tixingjuli) {
		this.tixingjuli = tixingjuli;
	}

}
