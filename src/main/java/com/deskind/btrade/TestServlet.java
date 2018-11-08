package com.deskind.btrade;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.deskind.btrade.binary.objects.ProfitTableEntry;
import com.deskind.btrade.entities.SignalManager;
import com.deskind.btrade.tasks.SignalsConsumer;
import com.deskind.btrade.utils.HibernateUtil;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet(urlPatterns = {"/testservlet"})
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
	}
	
	

}
