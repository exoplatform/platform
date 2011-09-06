package org.exoplatform.platform.gadget.services.LoginHistory;

public class LastLoginBean {
	private String userId;
	private String userName;
	private long lastLogin;
	private long beforeLastLogin;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Long getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(long lastLogin) {
		this.lastLogin = lastLogin;
	}
	public Long getBeforeLastLogin() {
		return beforeLastLogin;
	}
	public void setBeforeLastLogin(long beforeLastLogin) {
		this.beforeLastLogin = beforeLastLogin;
	}
}
