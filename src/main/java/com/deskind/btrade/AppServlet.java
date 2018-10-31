
package com.deskind.btrade;

import com.deskind.btrade.dto.BalanceDTO;
import com.deskind.btrade.dto.TraderDTO;
import com.deskind.btrade.dto.TradingSystemDTO;
import com.deskind.btrade.entities.ContractInfo;
import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;
import com.deskind.btrade.tasks.LogWriterTimerTask;
import com.deskind.btrade.utils.Endpoint;
import com.deskind.btrade.utils.HibernateUtil;
import com.deskind.btrade.utils.PayoutInterestEndpoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;


@WebServlet(name = "AppServlet", urlPatterns = {"/AppServlet", "/app"})
public class AppServlet extends HttpServlet {
    /**
     * In seconds
     * Variable indicates how long to wait better proposal from binary service after signal was received
     * Default value if 5 seconds
     */
    public static int timeToWaitBetterProposal = 5;
        
    //date formatter
    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    //generating thread id based on this variable every time the signal was received 
    volatile public static int receivedSignalsCounter = 1;
    
    public static int numberOfBoughtContracts;
    
    //all traders (active and not active too)
    public static List<Trader> traders = new ArrayList<>();
    
    //application IDs (will be initialized at start trading process
    public static String [] appIDs;
    
    //minimal payout at which buy contracts
    public static int minimalPayout = 65;//65 is default value
    
    //timers
    public static Timer aliveTimer;
    
    //tasks for timers
    public static TimerTask stayAliveTimerTask;
    
    //counters
    public static int contractInfoIternalCounter;
    
    //utilize for 'touching' DB to prevent DB 'timeout'
    public static int dbTouchCounter;
    
    //flags
    public static boolean firstRun = true;
    
    //map with logs (every entry contains logs related to particular signal)
    public static HashMap<Integer, ArrayList<String>> logs = new HashMap<Integer, ArrayList<String>>();
    
    //main logic , some kind of request mapping
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String action = req.getParameter("action");
        
        switch(action){
            
            case "addTrader" :{
            	processAddTrader(req);
                return;
            }
            
            case "printAllTraders" :{
                processPrintAllTraders(resp);
                return;
            }
            
            //returns all traders as response to js file
            case "allTraders": {
            	processAllTraders(resp);
                return;
            }
            
            case "delTrader" : {
            	processDelitingTrader(req, resp);
                return;
            }
            
            case ("test"):{
                traders.get(0).tsList.get(0).getSession().close();
                resp.getWriter().write("Session closed....");
                return;
            }
            
            //terminal signal process
            case "go":{
                
                
//                System.out.println("YSTANOVLENNAJA MINIMAL'NAJA VbIPLATA = > " + minimalPayout);
                
//                http://localhost:8084/BTR/AppServlet?action=go&type=PUT&duration=5&duration_unit=m&symbol=frxEURUSD&tsName=t1
////              http://127.0.0.2:8080/BTR/AppServlet?action=go&type=PUT&duration=5&duration_unit=m&symbol=frxEURUSD&tsName=t1 
////              http://127.0.0.2:8080/BTR/AppServlet?action=go&type=PUT&duration=3&duration_unit=m&symbol=frxEURUSD&tsName=ttt
////              localhost:8083/BTR/AppServlet?action=go&type=CALL&duration=3&duration_unit=m&symbol=R_50&tsName=t2
////              localhost:8083/BTR/AppServlet?action=go&type=CALL&duration=3&duration_unit=m&symbol=R_33&tsName=t1
                
                //signal information
                final String type = req.getParameter("type");
                final String duration = req.getParameter("duration");
                final String durationUnit = req.getParameter("duration_unit");
                final String symbol = req.getParameter("symbol");
                final String tsName = req.getParameter("tsName");
                
                
                    
                    
                        //variables
                        int threadId = generateThreadId();
                        int intrestedTraders = 0;
                        
                        //timer for writing logs to file
                        new Timer().schedule(new LogWriterTimerTask(logs, threadId), timeToWaitBetterProposal*1000 + 5000);
                        
                        //add new Entry to 'logs' HashMap
                        addEntryToLogsMap(threadId);
                        
                        logs.get(threadId).add("YSTANOVLENNAJA MINIMAL'NAJA VbIPLATA = > " + minimalPayout);
                        logs.get(threadId).add("---"+threadId+"---POLUCHEN SIGNAL " + new Date().toString() + "TYPE => " + type + "  SYMBOL => " + symbol + "  TS_NAME => " + tsName);
                        
                        //finding interested traders (trading systems)
                        for(Trader trader : traders){                            
                            TradingSystem tradingSystem = trader.getTsByName(tsName);
                            if(tradingSystem != null && tradingSystem.isActive()){
                            	Session session = tradingSystem.getSession();
                            	if(session.isOpen() && session != null){
	                                //Outer json object
	                                JsonObject jsonToSend = new JsonObject();
	                                
	                                jsonToSend.addProperty("proposal", 1);
	                                jsonToSend.addProperty("subscribe", 1);
	                                jsonToSend.addProperty("amount", tradingSystem.getLot());
	                                jsonToSend.addProperty("basis", "stake");
	                                jsonToSend.addProperty("contract_type", type);
	                                jsonToSend.addProperty("currency", "USD");
	                                jsonToSend.addProperty("duration", duration);
	                                jsonToSend.addProperty("duration_unit", durationUnit);
	                                jsonToSend.addProperty("symbol", symbol);
	                                
	                                //inner passthrough json object
	                                JsonObject passthroughObject = new JsonObject();
	                                passthroughObject.addProperty("threadId", threadId);
	                                passthroughObject.addProperty("tsName", tsName);
	                                passthroughObject.addProperty("duration", duration);
	                                passthroughObject.addProperty("contractType", type);
	                                passthroughObject.addProperty("symbol", symbol);
	                                
	                                //adding inner object to outer
	                                jsonToSend.add("passthrough", passthroughObject);
	                                
	                                //sending............
	                                tradingSystem.getSession().getAsyncRemote().sendText(jsonToSend.toString());
                            	}
                                intrestedTraders++;
                            }
                        }
                        
                        logs.get(threadId).add("---"+threadId+"---KOLICHESTVO PODPISOK NA PRICE PROPOSAL = > " + intrestedTraders);
                    
                        
                
                
                resp.sendRedirect("/BTR");
                
                return;
            }
            
            //start trading process when button start clicked
            case "start": {
                if(dateCheck() == -1){
                    //getting app IDs
                    appIDs = req.getParameter("appIDs").split("-");

                    connectAndUthorize();

                    aliveTimer.schedule(stayAliveTimerTask, 33000, 33000);                
                }
                
                return;
            }
            
            case "setMinimalPayout": {
                minimalPayout = Integer.valueOf(req.getParameter("payoutValue"));
                System.out.println("+++Novoe znachenie vbIplaty = > " + minimalPayout);
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
                    if(trader.getToken().equals(token)){
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
                    if(trader.getToken().equals(token)){
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
    
    /**
     * 
     * @param req
     * @param resp
     */
    private void processDelitingTrader(HttpServletRequest req, HttpServletResponse resp) {
    	String token = req.getParameter("token");
        
        //deleting from collection avoiding concurrent modification  
        for(int i = 0; i < traders.size();i++){
            Trader trader = traders.get(i);
            if(trader.getToken().equals(token)){
                traders.remove(i);
            }
        }
        
        //deleting from DB
        String result = HibernateUtil.deleteTrader(token);
        
        try {
			resp.getWriter().write(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
     * Returns all traders in response as JSON string
     * @param resp
     * @note Not mess up with 'showAllTraders' method
     */
    private void processAllTraders(HttpServletResponse resp) {
    	if(firstRun){
            traders = HibernateUtil.getAllTraders();
            firstRun = false;
        }
        
        //generate DTO for transferring
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
        
        try {
			resp.getWriter().write(new Gson().toJson(tradersAsDTO));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
     * aka toString for all traders
     * @param resp
     */
    private void processPrintAllTraders(HttpServletResponse resp) {
    	 String result = "Vsego trejderov : " + traders.size()+"\n";
    	                                 
         for(Trader t : traders){
             result+=t.toString()+"\n";
	     }
         
	     try {
			resp.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
    
    /**
     * Add new trader to DB
     * @param req For getting information about new Trader
     */
	private void processAddTrader(HttpServletRequest req) {
    	Trader trader = new Trader(req.getParameter("name"), req.getParameter("token"));
        HibernateUtil.saveTrader(trader);
        
        //to collection
        traders.add(trader);
		
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
    
    private static synchronized int generateThreadId(){
        return receivedSignalsCounter++;
    }
    
    private static synchronized void addEntryToLogsMap(int threadId) {
    	logs.put(threadId, new ArrayList<String>());
    }
    
    private static String[] getAppIDs(){
        return appIDs;
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
                        Session session = ts.getSession();
                        
						if (session.isOpen() && session != null) {
							session.getAsyncRemote().sendText("{\"ping\": 1}");
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								System.out.println("+++Thread was interrupted at time of sleeping");
								e.printStackTrace();
							}
							session.getAsyncRemote().sendText("{\"balance\": 1}");
						}

                    } 
                }
                
                if(badSessionsCounter == 0){
                        System.out.print("...OK...");
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
