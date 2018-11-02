package com.deskind.btrade;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.deskind.btrade.entities.Signal;
import com.deskind.btrade.entities.SignalManager;
import com.deskind.btrade.tasks.SignalsConsumer;

/**
 * Servlet process all signals from terminal
 */
@WebServlet(name ="SignalsServlet", urlPatterns= {"/ss"})
public class SignalServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Signal s = new Signal(request.getParameter("type"),
				request.getParameter("duration"),
				request.getParameter("duration_unit"),
				request.getParameter("symbol"),
				request.getParameter("symbol"));
		
		//add signal to queue only if 'manager' working now
		if(ManagerServlet.isWorking()) {
			SignalManager.addNewSignal(s);
		}
		
	}
}
