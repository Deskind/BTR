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
public class BalanceDTO {
    private String token;
    private float balance;

    public BalanceDTO(String token, float balance) {
        this.token = token;
        this.balance = balance;
    }
}
