/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.deskind.btrade.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.deskind.btrade.binary.objects.ProfitTableEntry;
import com.deskind.btrade.dto.TraderDTO;

/**
 *
 * @author deski
 */
@Entity(name = "trader")
public class Trader implements Comparable<Trader>{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "trader_id")
    private int id;
    private String name ;
    private String token;
    @OneToMany (fetch = FetchType.EAGER, orphanRemoval = true)
    public List<TradingSystem> tsList = new ArrayList<>();
    
    @Transient
    private float balance;
    
//    @Transient
//    public List<ContractInfo> contractsInfoList = new ArrayList<>();
    
    @Transient 
    List<ProfitTableEntry> allcontracts = new ArrayList<>();
    
    @Transient
    private HashSet<String> contractsIDs = new HashSet<>();

    
    //CONSTRUCTORS
    public Trader(){}
    
    public Trader(String name, String token){
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
    
    public HashSet<String> getContractsIDs() {
		return contractsIDs;
	}

	public void setContractsIDs(HashSet<String> contractsIDs) {
		this.contractsIDs = contractsIDs;
	}
	
	public synchronized void addNewContractId(String id) {
		contractsIDs.add(id);
		System.out.println("+++ Id " + id +" added , Set size is " + contractsIDs.size());
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

	public synchronized void removeFromIDs(String contractId) {
		Iterator<String> iterator = contractsIDs.iterator();
		while (iterator.hasNext()) {
		    String element = iterator.next();
		    if (element.equals(contractId)) {
		        iterator.remove();
		    }
		}
	}
}
