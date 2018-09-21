/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.deskind.btrade.dto;

/**
 *
 * @author deski
 */
public class TradingSystemDTO {
    private String name;
    private float lot;
    private boolean active;

    public void setName(String name) {
        this.name = name;
    }

    public void setLot(float lot) {
        this.lot = lot;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public float getLot() {
        return lot;
    }

    public boolean isActive() {
        return active;
    }
    
    
}
