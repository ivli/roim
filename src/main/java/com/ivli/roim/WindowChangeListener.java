/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.util.EventListener;
/**
 *
 * @author likhachev
 */
public interface WindowChangeListener extends EventListener {
   public abstract void windowChanged(WindowChangeEvent anEvt);   
}
