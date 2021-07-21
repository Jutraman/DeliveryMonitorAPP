package com.myweb.domain;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
import net.tsz.afinal.db.sqlite.DbModel;

@Table(name = "dingdan")
public class Dingdan {

	@Id(column = "id")
	private int id;

	public int getId() {
		return id;
	}

	private String bianhao;

	private String shoujianren;

	private String dianhua;

	private String dizhi;
	
	private String zhuangtai;
	
	private String jindu;
	
	private String weidu;

	public String getBianhao() {
		return bianhao;
	}

	public void setBianhao(String bianhao) {
		this.bianhao = bianhao;
	}

	public String getShoujianren() {
		return shoujianren;
	}

	public void setShoujianren(String shoujianren) {
		this.shoujianren = shoujianren;
	}

	public String getDianhua() {
		return dianhua;
	}

	public void setDianhua(String dianhua) {
		this.dianhua = dianhua;
	}

	public String getDizhi() {
		return dizhi;
	}

	public void setDizhi(String dizhi) {
		this.dizhi = dizhi;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getZhuangtai() {
		return zhuangtai;
	}

	public void setZhuangtai(String zhuangtai) {
		this.zhuangtai = zhuangtai;
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

}
