
package com.deskind.btrade;

import com.deskind.btrade.dto.BalanceDTO;
import com.deskind.btrade.dto.TraderDTO;
import com.deskind.btrade.dto.TradingSystemDTO;
import com.deskind.btrade.entities.ContractInfo;
import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;
import com.deskind.btrade.utils.Endpoint;
import com.deskind.btrade.utils.HibernateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "AppServlet", urlPatterns = {"/AppServlet", "/app"})
public class AppServlet extends HttpServlet {
    //date formatter
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    //colections
    public static List<Trader> traders = new ArrayList<>();
    
    //app IDs (will be initialized at start trading process
    public static String [] appIDs;
    
    //timers
    public static Timer aliveTimer;
    
    public static TimerTask stayAliveTimerTask;
    
    //counters
    public static int contractInfoIternalCounter;
    public static int dbTouchCounter;
    
    //flags
    public boolean firstRun = true;
    
   //Main
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String action = req.getParameter("action");
        
        switch(action){
            
            case "addTrader" :{
                //to db
                Trader trader = new Trader(req.getParameter("name"), req.getParameter("token"));
                HibernateUtil.saveTrader(trader);
                
                //to collection
                traders.add(trader);
                return;
            }
            
            //aka toString for all traders
            case "printAllTraders" :{
                String result = "Vsego trejderov : " + traders.size()+"\n";
                                
                for(Trader t : traders){
                    result+=t.toString()+"\n";
                }
                resp.getWriter().write(result);
                return;
            }
            
            //returns all traders as response to js file
            case "allTraders": {
                if(firstRun){
                    traders = HibernateUtil.getAllTraders();
                    firstRun = false;
                }
                
                //generate dto for transfering
                List<TraderDTO> tradersAsDTO = new ArrayList<>();
                for(Trader trader : traders){
                    TraderDTO traderDTO = trader.toDTO();
                    for(TradingSystem tradingSystem : trader.getTsList()){
                        traderDTO.getTsListDTO().add(tradingSystem.toDTO());
                    }
                    tradersAsDTO.add(traderDTO);
                }
                
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                
                resp.getWriter().write(new Gson().toJson(tradersAsDTO));
                return;
            }
            
            case "delTrader" : {
                String token = req.getParameter("token");
                
                //from collection avoiding concurrent modification  
                for(int i = 0; i < traders.size();i++){
                    Trader trader = traders.get(i);
                    if(trader.token.equals(token)){
                        traders.remove(i);
                    }
                }
                
                //from db
                String result = HibernateUtil.deleteTrader(token);
                
                resp.getWriter().write(result);
                return;
            }
            
            case ("test"):{
                traders.get(0).tsList.get(0).getSession().close();
                resp.getWriter().write("Session closed....");
                return;
            }
            
            //terminal signal process
            case "go":{
                String type = req.getParameter("type");
                String duration = req.getParameter("duration");
                String durationUnit = req.getParameter("duration_unit");
                String symbol = req.getParameter("symbol");
                String tsName = req.getParameter("tsName");
                
                
//              http://localhost:8084/BTR/AppServlet?action=go&type=PUT&duration=5&duration_unit=m&symbol=frxEURUSD&tsName=t1
//              http://127.0.0.2:8080/BTR/AppServlet?action=go&type=PUT&duration=5&duration_unit=m&symbol=frxEURUSD&tsName=t1 
//              localhost:8083/BTR/AppServlet?action=go&type=CALL&duration=3&duration_unit=m&symbol=R_50&tsName=t2
//              localhost:8083/BTR/AppServlet?action=go&type=CALL&duration=3&duration_unit=m&symbol=R_33&tsName=t1

                for(Trader trader : traders){
                    TradingSystem tradingSystem = trader.getTsByName(tsName);
                    if(tradingSystem != null && tradingSystem.isActive()){
                        
                        //object will store info about contract
                        ContractInfo contractInfo = new ContractInfo(contractInfoIternalCounter);
                        
                        //wtire send time
                        try {
                            Date sendTime = dateFormatter.parse(dateFormatter.format(new Date(System.currentTimeMillis())));
                            contractInfo.setSendTime(sendTime);
                        } catch (ParseException ex) {
                            Logger.getLogger(AppServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        //write ts name
                        contractInfo.setTs(tradingSystem.getName());
                        trader.contractsInfoList.add(contractInfo);
                        
                        //sending......
                        tradingSystem.getSession().getBasicRemote().sendText("{ \"buy\": \"1\",  \"price\": 1000,  \"parameters\":{  \"amount\":"+tradingSystem.getLot()+",  \"basis\":\"stake\",  \"contract_type\":\""+type+"\",  \"currency\":\"USD\", \"duration\":"+duration+",  \"duration_unit\":\""+durationUnit+"\", \"symbol\":\""+symbol+"\"}, \"passthrough\":{\"iternalId\":"+contractInfoIternalCounter+"}}");
                        
                        //this integer passed as unique identifier
                        contractInfoIternalCounter++;
                    }
                }
               
                //go back on main page
                resp.sendRedirect("/BTR");
                
                return;
            }
            
            //start trading process when button start clicked
            case "start": {
                if(dateCheck() == -1){
                    //getting app IDs
                    appIDs = req.getParameter("appIDs").split("-");

                    connectAndUthorize();

                    aliveTimer.schedule(stayAliveTimerTask, 33000, 44000);                
                }
                
                return;
            }
            
            case "stop": {
                
                return;
            }
            
            case "addTsToTrader":{
                String token = req.getParameter("token");
                String tsName = req.getParameter("tsName");
                
                TradingSystem ts = new TradingSystem(tsName);
                
                //add to db
                String result = HibernateUtil.addTsToTrader(token, ts);
                
                //add to collection
                for(Trader trader : traders){
                    if(trader.token.equals(token)){
                        ts.setSession(ts.getLot(), trader.getEndpoint());
                        trader.tsList.add(ts);
                    }
                }
                
                resp.getWriter().write(result);
                return;
            }
            
            case "updateTraderTs":{
                String token = req.getParameter("token");                
                String tsName = req.getParameter("tsName");
                float lot = Float.valueOf(req.getParameter("lot"));
                boolean active = Boolean.valueOf(req.getParameter("active"));
                
                System.out.println("Token " + token + " tsName :" + tsName + "lot : " + lot);
                
                //update in collection
                for(Trader trader : traders){
                    if(trader.token.equals(token)){
                        for(TradingSystem ts : trader.tsList){
                            if(ts.getName().equals(tsName)){
                                ts.setLot(lot);
                                ts.setActive(active);
                            }
                        }
                    }
                }
                
                //update in db
                String message = HibernateUtil.updateTraderTs(token, tsName, lot, active);
                
                resp.getWriter().write(message);
                return;
            }
            
            case "delTraderTs" : {
                String tokenParameter = req.getParameter("token");
                String tsNameParameter = req.getParameter("tsName");
                
                //del from collection
                for(int i = 0; i < traders.size(); i++){
                    Trader t = traders.get(i);
                    if(t.getToken().equals(tokenParameter)){
                        for(int k = 0; k < t.tsList.size(); k++){
                            TradingSystem ts = t.tsList.get(k);
                        if(ts.getName().equals(tsNameParameter)){
                            t.tsList.remove(k);
                        }
                    }
                    }
                }
                //del from db
                String message = HibernateUtil.deleteTraderTs(tokenParameter, tsNameParameter);
                
                resp.getWriter().write(message);
                return;
            }
            
            case "getAllBalances": {
                List<BalanceDTO> balances = getBalances();
                
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                
                resp.getWriter().write(new Gson().toJson(balances));
                
                return;
            }
            
            case "sortTraders":{
                Collections.sort(traders);
                return;
            }
        }
    }
    
    private static boolean netIsAvailable() {
    try {
        final URL url = new URL("http://www.google.com");
        final URLConnection conn = url.openConnection();
        conn.connect();
        conn.getInputStream().close();
        return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }
    
    @Override
    public void init(){
        //stay alive timer
        aliveTimer = new Timer();
        
        //stay alive timer task
        stayAliveTimerTask = new TimerTask() {
            @Override
            public void run() {
                int badSessionsCounter = 0;
                
                for(Trader trader : traders){
                    //check if trader was recently added and has no endpoint reference
                    if(trader.getEndpoint() == null){
                        trader.setEndpoint(new Endpoint(trader));
                    }
                    
                    for(TradingSystem ts : trader.getTsList()){
                        if(ts.getSession() == null){
                            ts.setSession(ts.getLot(), trader.getEndpoint());
                            badSessionsCounter++;
                            continue;
                        }
                        if(!ts.getSession().isOpen()){
                            ts.setSession(ts.getLot(), trader.getEndpoint());
                            badSessionsCounter++;
                            continue;
                        }
                        
                        //if everything is OK
                        try {
                            ts.getSession().getBasicRemote().sendText("{\"ping\": 1}");
                            ts.getSession().getBasicRemote().sendText("{\"balance\": 1}");
                        } catch (IOException ex) {
                            Logger.getLogger(AppServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } 
                }
                
                if(badSessionsCounter == 0){
                        System.out.println("...OK...");
                }else{
                    System.out.println("...Kolichestvo zakrbItbIx sessiy => " + badSessionsCounter);
                }
                
                //prevent database timeout problem
                dbTouchCounter++;
                if(dbTouchCounter == 30){
                    dbTouchCounter = 0;
                    HibernateUtil.getAllTradingSystems();
                }
            }
        };
    }

    @Override
    
    public void destroy() {
        System.out.println("+++Destroy method call");
        aliveTimer.cancel();
        HibernateUtil.getSessionFactory().close();
    }

    private static void connectAndUthorize() {
        //creating endpoint for every trader
        for(Trader trader : traders){
            Endpoint endpoint = new Endpoint(trader);
            trader.setEndpoint(endpoint);
            //create session for every trading system which has a trader
            for(TradingSystem tradingSystem : trader.getTsList()){
                tradingSystem.setSession(tradingSystem.getLot(), endpoint);
            }
        }
    }

    private List<BalanceDTO> getBalances() {
        List<BalanceDTO> balances = new ArrayList<BalanceDTO>();
        for(Trader trader : traders){
            balances.add(new BalanceDTO(trader.getToken(), trader.getBalance()));
        }
        return balances;
    }
    
    public static int dateCheck(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        Date date1 = null;
        try {
            date1 = sdf.parse("2018-11-11");
        } catch (ParseException ex) {
            Logger.getLogger(AppServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        Date date2 = new Date();
        
        return date2.compareTo(date1);
    }
    
}
