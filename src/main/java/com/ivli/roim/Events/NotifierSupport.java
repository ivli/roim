
package com.ivli.roim.Events;


/**
 *
 * @author likhachev
 */

public class NotifierSupport <T extends java.util.EventListener>{
    
    private final java.util.HashSet<T> iList = new java.util.HashSet<>(); 
    
    void add(T aL) {
        iList.add(aL);    
    }
    
    void remove(T aL) {
        iList.remove(aL);
    } 
    
    void mazltow(java.util.EventObject aMsg) {
        //iList.stream().forEach((n) -> {n.});
    }
            
}
