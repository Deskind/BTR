/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.deskind.btrade.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.deskind.btrade.binary.objects.ProfitTableEntry;
import com.deskind.btrade.dto.TraderDTO;
import com.deskind.btrade.enums.SignalStatus;
import com.deskind.btrade.utils.ContractDetails;

/**
 *
 * @author deski
 */
@Entity(name = "trader")
public class Trader implements Comparable<Trader>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trader_id")
    private int id;
    
    private String name ;
    
    private String token;
    
    @OneToMany (fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="trader_id")
    public List<TradingSystem> tsList;

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="trader_id")
    private List<Signal> receivedSignals;
    
    
    //TRANSIENT FIELDS
    @Transient
    private float balance;
    
    /*
     * Contains Profit Table Entries only with !!!SUCCESSFULLY!!! bought contracts
     */
    @Transient
    private HashMap<String, ContractDetails> contracts = new HashMap<>();
    
    /*
     * Contains Profit Table Entries only with !!!FAILED!!! to bought contracts
     */
    @Transient 
    private List<ProfitTableEntry> failedToBuy = new ArrayList<>();
    
    //CONSTRUCTORS
    public Trader(){
    	tsList = new ArrayList<>();
    	receivedSignals = new ArrayList<>();
    }
    
    public Trader(String name, String token){
    	tsList = new ArrayList<>();
    	receivedSignals = new ArrayList<>();
        this.name = name;
        this.token = token;
    }
    
    //INSTANCE METHODS
    public TraderDTO toDTO(){
        TraderDTO dto = new TraderDTO();
        dto.setName(this.getName());
        dto.setToken(this.getToken());
        
        return dto;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("Im9 trejdera: "+this.name+" ,token: " + this.token + "\n");
        for(TradingSystem ts : this.tsList){
            sb.append(ts.toString());
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    @Override
    public int compareTo(Trader trader) {
        float a = 0.0f;
        float b = 0.0f;
        
        for(TradingSystem tradingSystem : tsList){
            a+=tradingSystem.getLot();
        }
        
        for(TradingSystem tradingSystem: trader.getTsList()){
            b+=tradingSystem.getLot();
        }
        
        if(a < b){
            return  1;
        }else if(a == b){
            return 0;
        }else{
            return -1;
        }
    }
    
    public void addFailedContract(ProfitTableEntry entry) {
    	failedToBuy.add(entry);
    }
    
	public void setContracts(HashMap<String, ContractDetails> contracts) {
		this.contracts = contracts;
	}
	
	public synchronized void addNewContract(String id, ContractDetails details) {
		contracts.put(id, details);
		System.out.println("+++ Id " + id +" added , Set size is " + contracts.size());
	}

	public void addReceivedSignal(Signal signal, SignalStatus status) {
		signal.setStatus(status);
		
		receivedSignals.add(signal);
		
		System.out.println("Signal added to signals ... ");
	}

	public synchronized void removeFromContracts(String contractId) {
		contracts.remove(contractId);
	}

	//GETERS

	public List<Signal> getReceivedSignals() {
		return receivedSignals;
	}
	
	public TradingSystem getTsByName(String tsName) {
        for(TradingSystem tradingSystem : tsList){
            if(tradingSystem.getName().equals(tsName)){
                return tradingSystem;
            }
        }
        return null;
    }

	public void setTsList(List<TradingSystem> tsList) {
        this.tsList = tsList;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getToken(){
        return this.token;
    }
    
    public List<TradingSystem> getTsList(){
        return this.tsList;
    }

    public float getBalance() {
        return balance;
    }
    
    public HashMap<String, ContractDetails> getContracts() {
		return contracts;
	}

	public List<ProfitTableEntry> getFailedToBuy() {
		return failedToBuy;
	}

	public void setFailedToBuy(List<ProfitTableEntry> failedToBuy) {
		this.failedToBuy = failedToBuy;
	}

    
    
}
