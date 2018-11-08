package com.deskind.btrade.binary.objects;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="allcontracts", schema="BTR")
public class ProfitTableEntry {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    private String name;
    private String token;
    private String tsName;
	
	private int app_id;
	private float buy_price;
	private String contract_id;
	
	@Transient
	private String longcode;
	private float payout;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date purchase_time;
	private float sell_price;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date sell_time;
	private String shortcode;
	private String transaction_id;
	private String result;

	public ProfitTableEntry(int id, String name, String token, String tsName, int app_id, float buy_price,
			String contract_id, String longcode, float payout, Date purchase_time, float sell_price, Date sell_time,
			String shortcode, String transaction_id, String result) {
		super();
		this.id = id;
		this.name = name;
		this.token = token;
		this.tsName = tsName;
		this.app_id = app_id;
		this.buy_price = buy_price;
		this.contract_id = contract_id;
		this.longcode = longcode;
		this.payout = payout;
		this.purchase_time = purchase_time;
		this.sell_price = sell_price;
		this.sell_time = sell_time;
		this.shortcode = shortcode;
		this.transaction_id = transaction_id;
		this.result = result;
	}



	public ProfitTableEntry() {
	}

	
	
	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getToken() {
		return token;
	}



	public void setToken(String token) {
		this.token = token;
	}

	public String getTsName() {
		return tsName;
	}



	public void setTsName(String tsName) {
		this.tsName = tsName;
	}



	public String getResult() {
		return result;
	}



	public void setResult(String result) {
		this.result = result;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getApp_id() {
		return app_id;
	}

	public void setApp_id(int app_id) {
		this.app_id = app_id;
	}

	public float getBuy_price() {
		return buy_price;
	}

	public void setBuy_price(float buy_price) {
		this.buy_price = buy_price;
	}

	public String getContract_id() {
		return contract_id;
	}

	public void setContract_id(String contract_id) {
		this.contract_id = contract_id;
	}

	public String getLongcode() {
		return longcode;
	}

	public void setLongcode(String longcode) {
		this.longcode = longcode;
	}

	public float getPayout() {
		return payout;
	}

	public void setPayout(float payout) {
		this.payout = payout;
	}

	public Date getPurchase_time() {
		return purchase_time;
	}

	public void setPurchase_time(Date purchase_time) {
		this.purchase_time = purchase_time;
	}

	public float getSell_price() {
		return sell_price;
	}

	public void setSell_price(float sell_price) {
		this.sell_price = sell_price;
	}

	public Date getSell_time() {
		return sell_time;
	}

	public void setSell_time(Date sell_time) {
		this.sell_time = sell_time;
	}

	public String getShortcode() {
		return shortcode;
	}

	public void setShortcode(String shortcode) {
		this.shortcode = shortcode;
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}
	
	
	
	
	
	
}
