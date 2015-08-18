/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.Events;

///import java.util.EventListener;
import java.util.EventObject;
import java.util.HashSet;

/**
 *
 * @author likhachev
 */
public class EventNotifier <T extends MyEventListener> {
    HashSet<T> iList = new HashSet();
    void register(T aT) {
        iList.add(aT);
    }
    
    void orphan(T aT) {
        iList.remove(aT);
    }        
    
    void broadcast(EventObject aE) {
        iList.stream().forEach((t) -> {
            t.mazltow(aE);
        });
    } 
}
