package com.deskind.btrade.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.deskind.btrade.enums.SignalStatus;
import com.deskind.btrade.utils.DateUtil;

@Entity
@Table(name="received_signals")
public class Signal {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "signal_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
	
	@Enumerated(EnumType.STRING)
    @Column(length = 10)
    private SignalStatus status;
	
	@Column(name="signal_type")
	private String type;
	private String duration;
	
	@Column(name="duration_unit")
	private String durationUnit;
	private String symbol;
	
	@Column(name="ts")
	private String tsName;
	
	public Signal() {};
	

	public Signal(Date date, String type, String duration, String durationUnit, String symbol, String tsName) {
		this.date = date;
		this.type = type;
		this.duration = duration;
		this.durationUnit = durationUnit;
		this.symbol = symbol;
		this.tsName = tsName;
	}
	
	@Override
	public String toString() {
		return String.format("Signal => Date: %s"
									+ " Type => %s "
									+ " Duration => %s"
									+ " Duration_unit => %s"
									+ " Symbol => %s"
									+ " Ts_name => %s",
									DateUtil.getGMTDate(), type, duration, durationUnit, symbol, tsName);
	}
	
	
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public SignalStatus getStatus() {
		return status;
	}

	public void setStatus(SignalStatus status) {
		this.status = status;
	}

	public String getType() {
		type = type.toUpperCase();
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getDurationUnit() {
		return durationUnit;
	}

	public void setDurationUnit(String durationUnit) {
		this.durationUnit = durationUnit;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getTsName() {
		return tsName;
	}

	public void setTsName(String tsName) {
		this.tsName = tsName;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
