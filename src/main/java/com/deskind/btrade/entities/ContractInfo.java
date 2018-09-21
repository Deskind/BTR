
package com.deskind.btrade.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


@Entity (name = "contract_info")
public class ContractInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "contract_info_id")
    private int contractInfoId;
    
    @Transient
    private int iternalId;
    
    //USER INFO
    @Column(name = "trader_name")
    private String traderName;
    @Column(name = "trader_token")
    private String traderToken;
    
    //TIME FIELDS
    @Column(name = "send_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendTime;
    
    @Column(name = "responce_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date responceTime;
    
    @Column(name = "buy_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date buyTime;
    
    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    
    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    
    //contract expiration time. For example 1 minute
    @Column(name = "expiration_time")
    private int expirationTime;
    
    
    //MONEY
    @Column(name = "buy_price")
    private float buyPrice;
    @Column(name = "payout")
    private float payout;
    @Column(name = "app_markups_persentage")
    private float appMarkupsPersentage;
    
    //CONTRACT DETAILS
    @Column(name = "transaction_id")
    private long transactionId;
    @Column(name = "contract_id")
    private long contractId;
    @Column (name = "ts")
    private String ts;
    @Column (name = "contract_type")
    private String type;
    @Column (name = "symbol")
    private String symbol;
    @Column (name = "result")
    private String result;
    
    //CONSTRUCTORS

    public ContractInfo(int iternalId) {
        this.iternalId = iternalId;
    }
    
    
    //SETTERS


    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public void setResponceTime(Date responceTime) {
        this.responceTime = responceTime;
    }

    public void setBuyTime(Date buyTime) {
        this.buyTime = buyTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setExpirationTime(int expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void setBuyPrice(float buyPrice) {
        this.buyPrice = buyPrice;
    }

    public void setPayout(float payout) {
        this.payout = payout;
    }

    public void setAppMarkupsPersentage(float appMarkupsPersentage) {
        this.appMarkupsPersentage = appMarkupsPersentage;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setIternalId(int iternalId) {
        this.iternalId = iternalId;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    public void setTraderToken(String traderToken) {
        this.traderToken = traderToken;
    }
    
    
    
    //GETTERS

    public int getId() {
        return contractInfoId;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public Date getResponceTime() {
        return responceTime;
    }

    public Date getBuyTime() {
        return buyTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public int getExpirationTime() {
        return expirationTime;
    }

    public float getBuyPrice() {
        return buyPrice;
    }

    public float getPayout() {
        return payout;
    }

    public float getAppMarkupsPersentage() {
        return appMarkupsPersentage;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public long getContractId() {
        return contractId;
    }

    public String getTs() {
        return ts;
    }

    public String getType() {
        return type;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getResult() {
        return result;
    }

    public int getIternalId() {
        return iternalId;
    }

    public String getTraderName() {
        return traderName;
    }

    public String getTraderToken() {
        return traderToken;
    }
    
    
}
