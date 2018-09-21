/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.deskind.btrade.entities;

import com.deskind.btrade.AppServlet;
import com.deskind.btrade.dto.TradingSystemDTO;
import com.deskind.btrade.utils.Endpoint;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;

/**
 *
 * @author deski
 */
@Entity(name = "trading_system")
public class TradingSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ts_id")
    private int id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "lot")
    private float lot;
    
    @Column (name = "active")
    private boolean active;
    
    @Transient
    private Session session;
    
    //CONSTRUCTORS
    public TradingSystem() {
    }

    public TradingSystem(String name) {
        this.name = name;
    }

    public TradingSystem(String name, float lot, boolean active) {
        this.name = name;
        this.lot = lot;
        this.active = active;
    }
    
    //INSTANCE METHODS
    public TradingSystemDTO toDTO(){
        TradingSystemDTO dto = new TradingSystemDTO();
        dto.setName(this.name);
        dto.setLot(this.lot);
        dto.setActive(this.active);
        return dto;
    }

    @Override
    public String toString() {
        return "Im9 ts: " + name + " Lot: " + lot + " Active: " + active;
    }
    
    
    
    //SETTERS 

    public void setName(String name) {
        this.name = name;
    }

    public void setLot(float lot) {
        this.lot = lot;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    public void setSession(Session session){
        this.session = session;
    }
    
    public void setSession(final float lot, final Endpoint endpoint) {
        final String [] appIds = AppServlet.appIDs;
        
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
               try{
                    if(lot > 0 && lot < 30){
                        session = ContainerProvider.getWebSocketContainer().connectToServer(endpoint, new URI("wss://ws.binaryws.com/websockets/v3?app_id="+appIds[0]));
                    }else if(lot >= 30 && lot < 50){
                        session = ContainerProvider.getWebSocketContainer().connectToServer(endpoint, new URI("wss://ws.binaryws.com/websockets/v3?app_id="+appIds[1]));
                    }else if(lot >= 50){
                        session = ContainerProvider.getWebSocketContainer().connectToServer(endpoint, new URI("wss://ws.binaryws.com/websockets/v3?app_id="+appIds[2]));
                    }
                } catch (URISyntaxException ex) {
                    Logger.getLogger(TradingSystem.class.getName()).log(Level.SEVERE, null, ex);
                } catch (DeploymentException ex) {
                    Logger.getLogger(TradingSystem.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(TradingSystem.class.getName()).log(Level.SEVERE, null, ex);
                }

//                if(session != null){
//                    this.setSession(session);
//                } 
                    }
                });
        
        thread1.start();
        
        
    }

    //GETTERS

    public String getName() {
        return name;
    }

    public float getLot() {
        return lot;
    }

    public boolean isActive() {
        return active;
    }

    public Session getSession() {
        return session;
    }

}
