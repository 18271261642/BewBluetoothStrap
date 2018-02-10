package com.example.bozhilun.android.xinlangweibo;

public class SinaUserInfo {

	private String uid;//用户id
	private String name;//姓名
	private String avatarHd;//头像
	public String SEX;//性别



	public SinaUserInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAvatarHd() {
		return avatarHd;
	}
	public void setAvatarHd(String avatarHd) {
		this.avatarHd = avatarHd;
	}
	public String getSEX() {return SEX;}
	public void setSEX(String SEX) {this.SEX = SEX;}
}
