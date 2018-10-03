
package com.deskind.btrade.utils;

import com.deskind.btrade.AppServlet;
import static com.deskind.btrade.AppServlet.sendedSignalsCounter;
import com.deskind.btrade.entities.ContractInfo;
import com.deskind.btrade.entities.Trader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ClientEndpoint
public class Endpoint {
    
    //Date formatter
//    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    //COUNTERS
    public static int numberOfBoughtContracts;
    
    //FLAG
    private boolean subscribedOnTransactionUpdates = false;
    public static boolean isPayoutOk = false;
    
    public Trader trader;
    
    public Endpoint(Trader trader){
        this.trader = trader;
    }
        
    @OnOpen
    public void onOpen (Session session) throws IOException, InterruptedException{
        System.out.println("+++OnOpen. Trader name is " + trader.name + " trader token is: " + trader.token);
        session.getBasicRemote().sendText("{\"authorize\":\""+trader.getToken()+"\"}");
    }
    
    @OnMessage
    public void onMessage(String message, Session session){
        //getting message type
        JsonParser parser = new JsonParser();
        String messageType = parser.parse(message).getAsJsonObject().get("msg_type").getAsString();
        
        switch(messageType){
            case "authorize": {
                JsonElement errorElement = parser.parse(message).getAsJsonObject().get("error");
                
                if(errorElement == null){//if no error element in server responce
                    trader.authorized = true;
                }else{
                    trader.authorized = false;
                }
                return;
            }
            
            case "balance": {
                JsonElement balanceElement = parser.parse(message).getAsJsonObject().get("balance");
                
                if(balanceElement != null){
                    trader.setBalance(balanceElement.getAsJsonObject().get("balance").getAsFloat());
                }
                return;
            }
            
            
            case "buy": {
                
                JsonElement errorElement = parser.parse(message).getAsJsonObject().get("error");
                
                if(errorElement == null){//if no error element in server responce
                    
                    numberOfBoughtContracts++;
                    
                    //subscribe on transaction updates at time of first contract buy
                    if(!subscribedOnTransactionUpdates){
                        try {
                        session.getBasicRemote().sendText("{\"transaction\": 1,\"subscribe\": 1}");
                        //trigger flag to prevent second time subscription
                        subscribedOnTransactionUpdates = true;
                    } catch (IOException ex) {
                        Logger.getLogger(Endpoint.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    }
                    
                    
                    long responceTime = System.currentTimeMillis();
                    int iternalId = parser.parse(message).getAsJsonObject().get("passthrough").getAsJsonObject().get("iternalId").getAsInt();
                    JsonObject buyObject = parser.parse(message).getAsJsonObject().get("buy").getAsJsonObject();
                    JsonObject parametersObject = parser.parse(message).getAsJsonObject().get("echo_req").getAsJsonObject().get("parameters").getAsJsonObject();
                    
                    for(ContractInfo contractInfo : trader.contractsInfoList){
                        if(contractInfo.getIternalId() == iternalId){
                            long startTime = buyObject.get("start_time").getAsLong();
                            int expirationTime = parametersObject.get("duration").getAsInt();
                            
                            contractInfo.setTraderName(trader.getName());
                            contractInfo.setTraderToken(trader.getToken());   
                            

                            try {
                                
                                //response time 
                                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date responceDate = dateFormatter.parse(dateFormatter.format(new Date(responceTime)));
                                contractInfo.setResponceTime(responceDate);
                                
                                //buy time
                                Date buyTime = dateFormatter.parse(dateFormatter.format(new Date(buyObject.get("purchase_time").getAsLong()*1000)));
                                contractInfo.setBuyTime(buyTime);
                                
                                //start time
                                Date start = dateFormatter.parse(dateFormatter.format(new Date(startTime * 1000)));
                                contractInfo.setStartTime(start);
                                
                                //end time 
                                Date end = dateFormatter.parse(dateFormatter.format(new Date((startTime+expirationTime*60)*1000)));
                                contractInfo.setEndTime(end);
                            } catch (ParseException ex) {
                                Logger.getLogger(Endpoint.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            
                            contractInfo.setExpirationTime(expirationTime);
                            
                            contractInfo.setBuyPrice(buyObject.get("buy_price").getAsFloat());
                            contractInfo.setPayout(buyObject.get("payout").getAsFloat());
                            contractInfo.setAppMarkupsPersentage(parametersObject.get("app_markup_percentage").getAsFloat());
                            contractInfo.setType(parametersObject.get("contract_type").getAsString());
                            contractInfo.setSymbol(parametersObject.get("symbol").getAsString());
                            contractInfo.setContractId(buyObject.get("contract_id").getAsLong());
                            contractInfo.setTransactionId(buyObject.get("transaction_id").getAsLong());
                        }
                    }
                                        
                }else{//if buy response message contains error 
                    //parse message and get error object
                    JsonObject jsonObject = parser.parse(message).getAsJsonObject().get("error").getAsJsonObject();
                    String errorCode = jsonObject.get("code").getAsString();
                    String errorMessage = jsonObject.get("message").getAsString();
                    
                    int iternalId = parser.parse(message).getAsJsonObject().get("passthrough").getAsJsonObject().get("iternalId").getAsInt();
                                        
                    for(int i = 0 ; i<trader.contractsInfoList.size(); i++){
                        ContractInfo contractInfo = trader.contractsInfoList.get(i);
                        if(contractInfo.getIternalId() == iternalId){
                            
                            //set up data
                            contractInfo.setTraderName(trader.getName());
                            contractInfo.setTraderToken(trader.getToken());
                            
                            //write error code and message to result column in contract_info table
                            contractInfo.setResult("Code: " + errorCode + " Message: " + errorMessage);
                            
                            Date responceTime = contractInfo.getResponceTime();
                            
                            //save to db
                            HibernateUtil.saveContractInfo(contractInfo);
                            //remove from collection
                            trader.contractsInfoList.remove(i);
                        }
                    }
                }
                
                return;
            }
            
            case "transaction": {                
                JsonObject transactionObject = parser.parse(message).getAsJsonObject().get("transaction").getAsJsonObject();
                JsonElement actionElement = transactionObject.get("action");
                if(actionElement != null){
                    if(actionElement.getAsString().equals("sell")){
                        long contractId = transactionObject.get("contract_id").getAsLong();
                        float amount = transactionObject.get("amount").getAsFloat();
                        for(int i = 0 ; i<trader.contractsInfoList.size(); i++){
                            ContractInfo contractInfo = trader.contractsInfoList.get(i);
                            if(contractInfo.getContractId() == contractId){
                                if(amount > 0){
                                    contractInfo.setResult("+");
                                    System.out.println("Test message => we take PROFIT");
                                }else{
                                    contractInfo.setResult("-");
                                    System.out.println("Test message => we take LOSS");
                                }
                                //save to db
                                HibernateUtil.saveContractInfo(contractInfo);
                                
                                //remove from collection
                                trader.contractsInfoList.remove(i);
                            }
                        }
                    }else{
                    }
                
                }
                
                
                return;
            }
            
            case "proposal": {
                processProposal(message, parser);
                return;
            }
            
            
        }
    }
    
    @OnClose
    public void onClose(Session session){
        System.out.println("Trader : " +trader.name + "+++@OnClose call");
        subscribedOnTransactionUpdates = false;
        try {
            session.close();
        } catch (IOException ex) {
            Logger.getLogger(Endpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processProposal(String message, JsonParser parser) {
        float askPrice = parser.parse(message).getAsJsonObject().get("proposal").getAsJsonObject().get("ask_price").getAsFloat();
        float payout = parser.parse(message).getAsJsonObject().get("proposal").getAsJsonObject().get("payout").getAsFloat();
        
        float binaryProposedPayout = (payout-askPrice)*100/askPrice;
        
        System.out.println("BINARY PROPOSED PAYOUT IS = > " + binaryProposedPayout);
        
        if(binaryProposedPayout > AppServlet.minimalPayout){
            isPayoutOk = true;
        }else{
            isPayoutOk = false;
        }
    }
    
}
