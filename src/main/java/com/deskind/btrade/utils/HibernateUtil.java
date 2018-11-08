
package com.deskind.btrade.utils;

import com.deskind.btrade.binary.objects.ProfitTableEntry;
import com.deskind.btrade.entities.ContractInfo;
import com.deskind.btrade.entities.Trader;
import com.deskind.btrade.entities.TradingSystem;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    
    //STATIC INIT
    static {
        
        Configuration configObj = new Configuration();
        configObj.configure("hibernate.cfg.xml");

        ServiceRegistry serviceRegistryObj = new StandardServiceRegistryBuilder().applySettings(configObj.getProperties()).build(); 
        sessionFactory = configObj.buildSessionFactory(serviceRegistryObj);
        
    }
    
    //STATIC METHODS
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static Session getSession(){
        return sessionFactory.openSession();
    }

    public static void saveTrader(Trader trader) {
        Session s = getSession();
        Transaction t = s.beginTransaction();
        s.save(trader);
        t.commit();
        s.close();
    }

    public static List<Trader> getAllTraders() {
        Session s = getSession();
        Transaction t = s.beginTransaction();
        List<Trader> list = s.createQuery("from trader").list();
        t.commit();
        s.close();
        
        return list;
    }

public static String addTsToTrader(String token, TradingSystem ts) {
        
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        Query traderQuery = session.createQuery("from trader where token = :token");
        traderQuery.setParameter("token", token);
        
        //ts adding 
        Trader t = (Trader)traderQuery.uniqueResult();
        if(t != null){
            //save new ts instance
            session.save(ts);
            t.tsList.add(ts);
            session.saveOrUpdate(t);
        }else{
        	//clean up
            transaction.commit();
            session.close();
            return "fail";
        }
        
        //clean up
        transaction.commit();
        session.close();
        
        return "success";
    }

    public static String addNewTs(String name) {
        
        Session s = getSession();
        Transaction t = s.beginTransaction();
        TradingSystem ts = new TradingSystem(name);
        s.save(ts);
        t.commit();
        s.close();
        
        return "success";
    }

    public static String updateTraderTs(String token, String tsName, float lot, boolean active) {
        String result = "";
        
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        Query traderQuery = session.createQuery("from trader where token = :token");
        traderQuery.setParameter("token", token);
        
        Trader t = (Trader)traderQuery.uniqueResult();
        if(t != null){
            for(TradingSystem ts : t.tsList){
                if(ts.getName().equals(tsName)){
                    ts.setLot(lot);
                    ts.setActive(active);
                    session.saveOrUpdate(ts);
                    session.saveOrUpdate(t);
                    result = "success";
                }
            }
        }else{
            result = "fail";
        }
        
        //clean up
        transaction.commit();
        session.close();
        
        return result;
    }

    public static String deleteTrader(String token) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        Query traderQuery = session.createQuery("from trader where token = :token");
        traderQuery.setParameter("token", token);
        
        Trader t = (Trader)traderQuery.uniqueResult();
        if(t != null){
            session.delete(t);
            transaction.commit();
            session.close();
            return "success";
        }
        
        transaction.commit();
        session.close();
        return "fail";
    }

    public static String deleteTraderTs(String tokenParameter, String tsNameParameter) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        Query traderQuery = session.createQuery("from trader where token = :token");
        traderQuery.setParameter("token", tokenParameter);
        
        Trader trader = (Trader)traderQuery.uniqueResult();
        for(int i = 0; i < trader.tsList.size();i++){
            TradingSystem ts = trader.tsList.get(i);
            if(ts.getName().equals(tsNameParameter)){
                
                trader.tsList.remove(i);
                session.saveOrUpdate(trader);
                
                transaction.commit();
                session.close();
                return "success";
            }
        }
        
        transaction.commit();
        session.close();
        
        return "fail";
    }


    public static void getAllTradingSystems() {
        Session s = getSession();
        Transaction t = s.beginTransaction();
        List<TradingSystem> list = s.createQuery("from trading_system").list();
        t.commit();
        s.close();
    }

	public static void saveContract(ProfitTableEntry entry) {
		Session s = getSession();
        Transaction t = s.beginTransaction();
        s.save(entry);
        t.commit();
        s.close();
	}
    
    
}

