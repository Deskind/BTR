package com.deskind.btrade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;

import com.deskind.btrade.dto.TraderDTO;
import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;
import com.deskind.btrade.utils.ConnectionPoint;
import com.deskind.btrade.utils.HibernateUtil;
import com.google.gson.Gson;

/**
 * Operates over trader lifecycle 
 */
@WebServlet(name="TraderServlet", urlPatterns = {"/trader"})
public class TraderLifecycle extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	//app traders
	private static List<Trader> traders;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		switch(request.getParameter("action")){
			case "addTrader" :{
	        	addTrader(request);
	            return;
	        }
			
            case "allTraders": {
            	allTraders(response);
                return;
            }
            
            case "delTrader" : {
            	processDelitingTrader(request, response);
                return;
            }
            
            case "delTraderTs" : {
            	String token = request.getParameter("token");
                String tsName = request.getParameter("tsName");
                
            	processDeleteTs(token, tsName, response);
                
                return;
            }
            
            case "addTsToTrader":{
                String token = request.getParameter("token");
                String tsName = request.getParameter("tsName");
                
                processAddTs(token, tsName, response);
                
                return;
            }
            
            case "updateTraderTs":{
                String token = request.getParameter("token");                
                String tsName = request.getParameter("tsName");
                float lot = Float.valueOf(request.getParameter("lot"));
                boolean active = Boolean.valueOf(request.getParameter("active"));
                
                processTsUpdate(token, tsName, lot, active, response);
                
                return;
            }
		}
	}
	
	/**
	 * Method updates trading system state
	 * @param token
	 * @param tsName
	 * @param lot
	 * @param active
	 */
	private void processTsUpdate(String token, String tsName, float lot, boolean active, HttpServletResponse response) {
        
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
        
        //update in DB
        String message = HibernateUtil.updateTraderTs(token, tsName, lot, active);
        
        try {
			response.getWriter().write(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adding trading system to trader
	 * @param token
	 * @param tsName
	 * @param response
	 */
	private void processAddTs(String token, String tsName, HttpServletResponse response) {
		TradingSystem ts = new TradingSystem(tsName);
        
        //add to DB
        String result = HibernateUtil.addTsToTrader(token, ts);
        
        //add to collection
        for(Trader trader : traders){
            if(trader.getToken().equals(token)){
                trader.tsList.add(ts);
                ts.setSession(ts.getLot(), new ConnectionPoint(trader));
            }
        }
        
        //answer
        try {
			response.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deletes trader's trading system by name
	 * @param token 
	 * @param tsName
	 */
	private void processDeleteTs(String token, String tsName, HttpServletResponse response) {
		//delete from collection
        for(int i = 0; i < traders.size(); i++){
            Trader t = traders.get(i);
            if(t.getToken().equals(token)){
                for(int k = 0; k < t.tsList.size(); k++){
                    TradingSystem ts = t.getTsList().get(k);
	                if(ts.getName().equals(tsName)){
	                    t.getTsList().remove(k);
	                }
                }
            }
        }
        
        //delete from DB
        String message = HibernateUtil.deleteTraderTs(token, tsName);
        
        //answer
        try {
			response.getWriter().write(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
     * Add new trader to DB
     * @param req For getting information about new Trader
     */
	private void addTrader(HttpServletRequest req) {
    	Trader trader = new Trader(req.getParameter("name"), req.getParameter("token"));
    	
    	//save to DB
        HibernateUtil.saveTrader(trader);
        
        //to collection
        traders.add(trader);
		
	}
	
	/**
     * Returns all traders in response as JSON string
     * @param resp
     * @note Not mess up with 'showAllTraders' method
     */
    private void allTraders(HttpServletResponse resp) {
    	//remember all traders
        traders = HibernateUtil.getAllTraders();
        
        //generate DTO for transferring
        List<TraderDTO> tradersAsDTO = new ArrayList<>();
        for(Trader trader : traders){
            TraderDTO traderDTO = trader.toDTO();
            for(TradingSystem tradingSystem : trader.getTsList()){
                traderDTO.getTsListDTO().add(tradingSystem.toDTO());
            }
            tradersAsDTO.add(traderDTO);
        }
        
        //content configuration
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        //answer
        try {
			resp.getWriter().write(new Gson().toJson(tradersAsDTO));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    /**
     * Method for deleting trader
     * @param req
     * @param resp
     */
    private void processDelitingTrader(HttpServletRequest req, HttpServletResponse resp) {
    	//delete by token
    	String token = req.getParameter("token");
        
        //deleting from collection avoiding concurrent modification (in case 'for-each') 
        for(int i = 0; i < traders.size();i++){
            Trader trader = traders.get(i);
            if(trader.getToken().equals(token)){
                traders.remove(i);
            }
        }
        
        //deleting from DB
        String result = HibernateUtil.deleteTrader(token);
        
        //answer
        try {
			resp.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//GETTERS
    public static List<Trader> getTraders() {
		return traders;
	}
    
}
