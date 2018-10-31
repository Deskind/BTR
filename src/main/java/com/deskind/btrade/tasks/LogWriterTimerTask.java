/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.deskind.btrade.tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author deski
 */
public class LogWriterTimerTask extends TimerTask{
    private int threadID;
    private Map<Integer, ArrayList<String>> logs;

    public LogWriterTimerTask(Map<Integer, ArrayList<String>> logs, int threadId) {
        this.logs = logs;
        this.threadID = threadId;
    }

    @Override
    public void run() {
        ArrayList<String> l = logs.get(threadID);
        File file = new File("c:\\logs\\"+threadID+".txt");
        
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            for(String s : l){
                bw.write(s);
                bw.newLine();
            }
            logs.remove(threadID);
        } catch (IOException ex) {
            Logger.getLogger(LogWriterTimerTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
