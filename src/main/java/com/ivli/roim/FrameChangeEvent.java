/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.util.EventListener;
import java.util.EventObject;
/**
 *
 * @author likhachev
 */
public final class FrameChangeEvent extends EventObject {
    private final int iFrameNo; 
    public FrameChangeEvent(Object aO, int aFrameNo) {
        super(aO); 
        iFrameNo=aFrameNo;
    }  
}

interface FrameChangeListener extends EventListener {
    public abstract void windowChanged(WindowChangeEvent anEvt);   
}
