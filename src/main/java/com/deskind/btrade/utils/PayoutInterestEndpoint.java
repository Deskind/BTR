/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.deskind.btrade.utils;

import com.deskind.btrade.AppServlet;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

/**
 *
 * @author deski
 */

@ClientEndpoint
public class PayoutInterestEndpoint {
    private String type, duration, durationUnit, symbol;
    private float payout;
    private int parentThreadId;
    
    //CONSTRUCTOR

    public PayoutInterestEndpoint(String type, String duration, String durationUnit, String symbol, int parentThreadId) {
        this.type = type;
        this.duration = duration;
        this.durationUnit = durationUnit;
        this.symbol = symbol;
        this.parentThreadId = parentThreadId;
    }
    
    //SETTERS
    public void setPayout(float payout) {
        this.payout = payout;
    }
    
    //GETTERS

    public float getPayout() {
        return payout;
    }
    
    @OnOpen
    public void onOpen(Session session) {
        try {
            //        session.getBasicRemote().sendText("{\"proposal\": 1,\"amount\": \"1\",\"basis\": \"payout\", \"contract_type\": \""+type+"\", \"currency\": \"USD\", \"duration\": \""+duration+"\", \"duration_unit\": \""+durationUnit+"\", \"symbol\": \""+symbol+"\"}");
            session.getBasicRemote().sendText("{\"proposal\": 1,\"amount\": \"1\",\"basis\": \"payout\", \"contract_type\": \""+type+"\", \"currency\": \"USD\", \"duration\": \""+duration+"\", \"duration_unit\": \""+durationUnit+"\", \"symbol\": \""+symbol+"\", \"passthrough\":{\"threadId\":"+parentThreadId+"}}");
        } catch (IOException ex) {
            Logger.getLogger(PayoutInterestEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @OnMessage
    public void onMessage(String message, Session session){
        JsonParser parser = new JsonParser();
        String messageType = parser.parse(message).getAsJsonObject().get("msg_type").getAsString();
        
        switch(messageType){
            case "proposal": {
                processProposal(message, parser);
                return;
            } 
        }
    }
    
    private void processProposal(String message, JsonParser parser) {
        float askPrice = parser.parse(message).getAsJsonObject().get("proposal").getAsJsonObject().get("ask_price").getAsFloat();
        float payout = parser.parse(message).getAsJsonObject().get("proposal").getAsJsonObject().get("payout").getAsFloat();
        String threadId = parser.parse(message).getAsJsonObject().get("passthrough").getAsJsonObject().get("threadId").getAsString();
        
        float binaryProposedPayout = (payout-askPrice)*100/askPrice;
        
        System.out.println("---"+threadId+"---VbIPLATA BINARY = > " + binaryProposedPayout);
        
        setPayout(binaryProposedPayout);       
    }
}
