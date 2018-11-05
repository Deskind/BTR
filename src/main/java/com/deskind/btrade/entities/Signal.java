package com.deskind.btrade.entities;

public class Signal {
	private String type;
	private String duration;
	private String durationUnit;
	private String symbol;
	private String tsName;
	
	public Signal(String type, String duration, String durationUnit, String symbol, String tsName) {
		super();
		this.type = type;
		this.duration = duration;
		this.durationUnit = durationUnit;
		this.symbol = symbol;
		this.tsName = tsName;
	}
	
	@Override
	public String toString() {
		return String.format("Signal => type: %s "
									+ "duration: %s"
									+ "duration_unit: %s"
									+ "symbol: %s"
									+ "ts_name %s", type, duration, durationUnit, symbol, tsName);
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
