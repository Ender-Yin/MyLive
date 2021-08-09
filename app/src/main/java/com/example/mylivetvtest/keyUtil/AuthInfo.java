package com.example.mylivetvtest.keyUtil;

public class AuthInfo {

	private String code;

	/** 播放检查授权的一个标志 */
	private String link;
	private String msg="";
	private String key;
	private String days="0";

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	/** 左侧一级菜单列表的API */
	private String url;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "AuthInfo [code=" + code + ", link=" + link + ", key=" + key
				+ ", url=" + url + "]";
	}
}
