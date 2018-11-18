package com.deskind.btrade.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author deski
 *
 */
@Entity
@Table(name="logins")
public class LoginMessage {
	@Id
	@Column(name="login_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "login_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date login;
	
	@Column(name = "logout_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date logout;
	
	@Column(name = "close_code")
	private String closeCode;
	
	@Column(name = "close_message")
	private String closeMessage;

	public LoginMessage() {	}

	public LoginMessage(Date login) {
		this.login = login;
	}

	public Date getLogin() {
		return login;
	}

	public void setLogin(Date login) {
		this.login = login;
	}

	public Date getLogout() {
		return logout;
	}

	public void setLogout(Date logout) {
		this.logout = logout;
	}

	public String getCloseCode() {
		return closeCode;
	}

	public void setCloseCode(String closeCode) {
		this.closeCode = closeCode;
	}

	public String getCloseMessage() {
		return closeMessage;
	}

	public void setCloseMessage(String closeMessage) {
		this.closeMessage = closeMessage;
	}
	
	
}
