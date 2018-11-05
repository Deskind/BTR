package com.deskind.btrade;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.deskind.btrade.entities.SignalManager;
import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.tasks.ConnectionPointsDestroyer;
import com.deskind.btrade.tasks.ConnectionPointsInitializer;
import com.deskind.btrade.tasks.SignalsConsumer;
import com.deskind.btrade.tasks.StayAlive;

import logs.MyFormatter;

/**
 * Servlet manages start/stop trading process
 */
@WebServlet(name = "ManagerServlet", urlPatterns = { "/manager" })
public class ManagerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static boolean isWorking = false;

	private static Logger logger;
	private FileHandler handler = null;
	
	//contains 3 id for different lots
	private static String[] binaryIDs;
	
	//Thread which consumes(process) signals when they come
	private Thread signalsConsumer;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String answer = "";

		switch (request.getParameter("action")) {

		case "start": {
			answer = processStartTrading(request.getParameter("appIDs"));
			response.getWriter().println(answer);
			return;
		}
		
		case "stop":{
			answer = processStopTrading();
			response.getWriter().println(answer);
			return;
		}

		}
	}
	
	/**
	 * Method for stopping trading process
	 */
	private String processStopTrading() {
		if(!isWorking) return "Trading process is not started ...";
		
		//flag
		isWorking = false;
		
		signalsConsumer.interrupt();
		
		//closing sessions
		Thread connectionPointsDestroyer = new ConnectionPointsDestroyer(TraderLifecycle.getTraders());
		connectionPointsDestroyer.setName("+++Connection points destroyer thread");
		connectionPointsDestroyer.start();
		
		return "Trading process stopped ...";
	}

	/**
	 * Method starts trading process
	 * 
	 * @param parameter
	 */
	private String processStartTrading(String ids) {
		//getting traders
		List<Trader> tradersList = TraderLifecycle.getTraders();
		
		//if trading process already started
		if(isWorking) return "Trading process already started ...";
		//if there are no traders
		if(tradersList == null || tradersList.isEmpty()) return "No traders. (Hint: Reload web browser page)";
		
		//normal flow 
		//getting app id
		binaryIDs = ids.split("-");
		
		//touch flag
		isWorking = true;
		
		//running signals consumer thread
		signalsConsumer = new SignalsConsumer(SignalManager.getSignalsQueue(), tradersList);
		signalsConsumer.setName("+++SignalsConsumerThread");
		signalsConsumer.start();

		//run ConnectionPointsInitializer thread
		Thread pointsInitializer = new ConnectionPointsInitializer(tradersList);
		pointsInitializer.setName("Points Initializer");
		pointsInitializer.start();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//run 'stay alive' thread
		Thread stayAliveThread = new StayAlive(tradersList);
		stayAliveThread.setName("+++StayAlive");
		stayAliveThread.start();
		
		return "Trading process started ...";
	}
	
	@Override
	public void init() {
		
		//logging stuff
		logger = Logger.getLogger(ManagerServlet.class.getName());

		try {
			handler = new FileHandler("c:\\btr_logs\\ManagerLogMessages.txt", false);
			logger.addHandler(handler);
			handler.setFormatter(new MyFormatter());
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public void destroy() {
		handler.close();
	}


	
	//GETTERS
	public static boolean isWorking() {
		return isWorking;
	}
	
	public static String[] getBinaryIDs() {
		return binaryIDs;
	}

	
	public static Logger getLogger() {
		return logger;
	}

	
	
	
}
