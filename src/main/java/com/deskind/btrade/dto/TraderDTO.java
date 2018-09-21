/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.deskind.btrade.dto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author deski
 */
public class TraderDTO {
    private String name;
    private String token;
    
    private List<TradingSystemDTO> tsListDTO = new ArrayList<>();

    public void setName(String name) {
        this.name = name;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTsListDTO(List<TradingSystemDTO> tsListDTO) {
        this.tsListDTO = tsListDTO;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public List<TradingSystemDTO> getTsListDTO() {
        return tsListDTO;
    }
    
    
}
