package com.deskind.btrade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.deskind.btrade.dto.TraderDTO;
import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;
import com.deskind.btrade.utils.Endpoint;
import com.deskind.btrade.utils.HibernateUtil;
import com.google.gson.Gson;

/**
 * Servlet implementation class TraderLifecycle
 */
@WebServlet("/TraderLifecycle")
public class TraderLifecycle extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private Set<Trader> traders = new HashSet<Trader>();

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
		}
	}
	
	/**
     * Add new trader to DB
     * @param req For getting information about new Trader
     */
	private void addTrader(HttpServletRequest req) {
    	Trader trader = new Trader(req.getParameter("name"), req.getParameter("token"));
    	trader.setEndpoint(new Endpoint(trader));
    	
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
        traders = (Set<Trader>) HibernateUtil.getAllTraders();
        
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
	
}
