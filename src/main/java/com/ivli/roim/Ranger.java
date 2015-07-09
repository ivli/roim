/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

/**
 *
 * @author likhachev
 */
class Ranger {
    static final <T extends Comparable <T>> T range (T val, T min, T max) {
        if (val.compareTo(min) < 0)  return min;
        else if (val.compareTo(max) > 0) return max;
        return val;
    }
}

