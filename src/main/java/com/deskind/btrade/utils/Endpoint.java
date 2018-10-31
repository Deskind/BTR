
package com.deskind.btrade.utils;

import com.deskind.btrade.AppServlet;
import static com.deskind.btrade.AppServlet.contractInfoIternalCounter;
import static com.deskind.btrade.AppServlet.dateFormatter;
import com.deskind.btrade.entities.ContractInfo;
import com.deskind.btrade.entities.Trader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.BorderLayout;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import org.hibernate.mapping.Set;

@ClientEndpoint
public class Endpoint {
    
    //Date formatter
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    //COUNTERS
    
    
    //FLAG
    private boolean subscribedOnTransactionUpdates = false;
    
    //SUBSCRIPTIONS MAP
    public HashSet<String> subscriptions = new HashSet<String>();
    
    public Trader trader;
    
    public Endpoint(Trader trader){
        this.trader = trader;
    }
        
    @OnOpen
    public void onOpen (Session session) throws IOException, InterruptedException{
        System.out.println("+++Trader " + trader.getName() + " ONLINE");
        session.getAsyncRemote().sendText("{\"authorize\":\""+trader.getToken()+"\"}");
    }
    
    @OnMessage
    public void onMessage(String message, Session session) throws ParseException{
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
                
                ContractInfo contractInfo = null;
                int contractInfoCollectionIndex = -1;
                
                JsonElement errorElement = parser.parse(message).getAsJsonObject().get("error");
                JsonElement buyElement = parser.parse(message).getAsJsonObject().get("buy");
                JsonObject passthroughObject = parser.parse(message).getAsJsonObject().get("passthrough").getAsJsonObject();
                
                int iternalId = passthroughObject.get("iternalId").getAsInt();
                String streamId = passthroughObject.get("streamId").getAsString();
                int threadId = passthroughObject.get("threadId").getAsInt();
                
                
                
                subscriptions.remove(streamId);
               
                //find contract info in collection
                for(int i = 0; i < trader.contractsInfoList.size();i++){
                    ContractInfo ci = trader.contractsInfoList.get(i);
                    if(ci.getIternalId() == iternalId){
                        contractInfo = ci;
                        contractInfoCollectionIndex = i;
                        break;
                    }
                }
                
                if(errorElement == null){//if no error element in server responce   
                    JsonObject buyObject = buyElement.getAsJsonObject();
                    float buyPrice = buyObject.get("buy_price").getAsFloat();
                    float payout = buyObject.get("payout").getAsFloat();
                    
                    AppServlet.logs.get(threadId).add("---"+threadId+"---Trader " + trader.getName() + " KUPIL KONTRACT NA SUMMY = > " + buyPrice + " S VbIPLATOY = > " + payout);
                    
                    //subscribe on transaction updates at time of first contract buy
                    if(!subscribedOnTransactionUpdates){
                        if(session.isOpen() && session != null){
                            session.getAsyncRemote().sendText("{\"transaction\": 1,\"subscribe\": 1}");
                        }
                        //trigger flag to prevent second time subscription
                        subscribedOnTransactionUpdates = true;
                    
                    }
                    
                    
                    if(contractInfo != null){
                        //response time 
                        long responceTime = System.currentTimeMillis();
                        Date responceDate = dateFormatter.parse(dateFormatter.format(new Date(responceTime)));
                        contractInfo.setResponceTime(responceDate);
                        
                        //start time
                        long startTime = buyObject.get("start_time").getAsLong();
                        Date start = dateFormatter.parse(dateFormatter.format(new Date(startTime * 1000)));
                        contractInfo.setStartTime(start);
                        
                        //buy time
                        Date buyTime = dateFormatter.parse(dateFormatter.format(new Date(buyObject.get("purchase_time").getAsLong()*1000)));
                        contractInfo.setBuyTime(buyTime);
                        
                        //end time 
                        int expirationTime = contractInfo.getExpirationTime();
                        long expirationTimeInSeconds = expirationTime * 60;
                        long expirationTimeInMilliseconds = (startTime * 1000) + (expirationTimeInSeconds * 1000);
                        Date endTime = dateFormatter.parse(dateFormatter.format(new Date(expirationTimeInMilliseconds)));
                        contractInfo.setEndTime(endTime);
                        
                        //contract and transaction id
                        contractInfo.setContractId(buyObject.get("contract_id").getAsLong());
                        contractInfo.setTransactionId(buyObject.get("transaction_id").getAsLong());
                    }else{
                        System.out.println("!!!NE UDALOS' NAJTI CONTRACT INFO S TAKIM ITERNAL_ID!!!");
                    }
                       
                }else{//if buy response message contains error 
                    //parse message and get error object
                    JsonObject jsonObject = parser.parse(message).getAsJsonObject().get("error").getAsJsonObject();
                    String errorCode = jsonObject.get("code").getAsString();
                    String errorMessage = jsonObject.get("message").getAsString();
                    
                                        
                    
                    if(contractInfo.getIternalId() == iternalId){

                        //set up data
                        contractInfo.setTraderName(trader.getName());
                        contractInfo.setTraderToken(trader.getToken());

                        //write error code and message to result column in contract_info table
                        contractInfo.setResult("Code: " + errorCode + " Message: " + errorMessage);

                        Date responceTime = contractInfo.getResponceTime();
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
                                }else{
                                    contractInfo.setResult("-");
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
                processProposal(message, parser, session);
                return;
            }
            
            
        }
    }
    
    @OnClose
    public void onClose(Session session, CloseReason closeReason){
        System.out.println("+++Trader " + trader.getName() + " OFFLINE, close reason is = > " + closeReason.getReasonPhrase() );
        subscribedOnTransactionUpdates = false;
    }

    private void processProposal(String message, JsonParser parser, Session session) {
        //passthrough object
        JsonObject passthroughObject = parser.parse(message).getAsJsonObject().get("passthrough").getAsJsonObject();
        
        int threadId = passthroughObject.get("threadId").getAsInt();
        String tsName = passthroughObject.get("tsName").getAsString();
        String duration = passthroughObject.get("duration").getAsString();
        String contractType = passthroughObject.get("contractType").getAsString();
        String symbol = passthroughObject.get("symbol").getAsString();
        
        //error element check
        JsonElement errorElement = parser.parse(message).getAsJsonObject().get("error");
        if(errorElement != null){
            JsonObject errorObject = errorElement.getAsJsonObject();
            String errorMessage = errorObject.get("message").getAsString();
            String errorCode = errorObject.get("code").getAsString();
            //NOTE: new line at the end 
            AppServlet.logs.get(threadId).add("---"+threadId+"---Proposal for trader - > " + trader.getName() + " contatains error code - > " + errorCode + " and error message ->" + errorMessage + "\n");
//            System.out.print("---"+threadId+"---Proposal for trader - > " + trader.getName() + " contatains error code - > " + errorCode + " and error message ->" + errorMessage + "\n");
            return;
        }
        
        //object will store info about contract
        ContractInfo contractInfo = new ContractInfo(contractInfoIternalCounter++);
        
        //proposal object
        JsonObject proposalObject = parser.parse(message).getAsJsonObject().get("proposal").getAsJsonObject();
        
        float askPrice = proposalObject.get("ask_price").getAsFloat();
        float payout = proposalObject.get("payout").getAsFloat();
        String id = proposalObject.get("id").getAsString();
        
        forgetPriceProposalStream(id, session, threadId);
        
        //payout calculation
        float binaryProposedPayout = (payout-askPrice)*100/askPrice;
        
        //log about payout interest
        AppServlet.logs.get(threadId).add("---"+threadId+"---VbIPLATA BINARY DL9 TREJDERA: "+ trader.getName() + " = > " + binaryProposedPayout);
//        System.out.println("---"+threadId+"---VbIPLATA BINARY DL9 TREJDERA: "+ trader.getName() + " = > " + binaryProposedPayout);
               
        if(binaryProposedPayout > AppServlet.minimalPayout){//payout is OK
                
                //prepare json object for sending
                //outer object
                JsonObject jsonToSend = new JsonObject();
                jsonToSend.addProperty("buy", id);
                jsonToSend.addProperty("price", 1000);
                //inner object
                JsonObject passthroughToSendObject = new JsonObject();
                passthroughToSendObject.addProperty("iternalId", contractInfo.getIternalId());
                passthroughToSendObject.addProperty("streamId", id);
                passthroughToSendObject.addProperty("threadId", threadId);
                passthroughToSendObject.addProperty("tsName", tsName);
                passthroughToSendObject.addProperty("duration", duration);
                passthroughToSendObject.addProperty("contractType", contractType);
                passthroughToSendObject.addProperty("symbol", symbol);
                //add inner to outer
                jsonToSend.add("passthrough", passthroughToSendObject);
                
                if(session.isOpen() && session != null){
                    session.getAsyncRemote().sendText(jsonToSend.toString());
                }
                
                
                contractInfo.setTraderName(trader.getName());
                contractInfo.setTraderToken(trader.getToken());
                contractInfo.setTs(tsName);
                contractInfo.setType(contractType);
                contractInfo.setBuyPrice(trader.getTsByName(tsName).getLot());
                contractInfo.setPayout(payout);
                contractInfo.setSymbol(symbol);
                contractInfo.setExpirationTime(Integer.valueOf(duration));
                

                //get and wtire send time
                try {
                    Date sendTime = dateFormatter.parse(dateFormatter.format(new Date(System.currentTimeMillis())));
                    contractInfo.setSendTime(sendTime);
                } catch (ParseException ex) {
                    Logger.getLogger(AppServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                //add contract info into collection
                trader.contractsInfoList.add(contractInfo);
                
        }
    }

    private void forgetPriceProposalStream(final String id, final Session session, final int threadId) {

            if(!subscriptions.contains(id)){
                addStreamIdToSet(id);

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //asking binary to 'forget' about stream with 'id'
                        if(subscriptions.contains(id)){
                            if(session.isOpen() && session != null){
                                session.getAsyncRemote().sendText("{\"forget\": \""+id+"\"}");
                            }
                            
                            //remove stream id from set
                            deleteStreamIdFromSet(id);
                            
                            AppServlet.logs.get(threadId).add("---"+threadId+"---Trader " + trader.getName() + " OTPISALS9 OT PRICE PROPOSAL");
//                            System.out.println("---"+threadId+"---Trader " + trader.getName() + " OTPISALS9 OT PRICE PROPOSAL");
                        }
                    }
                }, AppServlet.timeToWaitBetterProposal*1000);
            }
        
    }
    
    private synchronized void addStreamIdToSet(String id){
        subscriptions.add(id);
    }
    
    private synchronized void deleteStreamIdFromSet(String id){
        subscriptions.remove(id);
    }
    
}
