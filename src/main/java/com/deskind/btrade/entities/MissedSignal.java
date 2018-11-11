package com.deskind.btrade.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class MissedSignal {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private int id;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	private String missedSignal;
	
	public MissedSignal(Date date, String missedSignal) {
		super();
		this.date = date;
		this.missedSignal = missedSignal;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMissedSignal() {
		return missedSignal;
	}

	public void setMissedSignal(String missedSignal) {
		this.missedSignal = missedSignal;
	}
	
	
}
