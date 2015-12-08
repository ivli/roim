/*
 * Copyright (C) 2015 likhachev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.ivli.roim.calc;

/**
 *
 * @author likhachev
 */
public abstract class MathOp {
    public enum TYPE {
        UNARY,
        BINARY
    }
    
    public enum OP {
        CHANGESIGN,
        SUMMATION,
        SUBTRACTION,
        MULTIPLICATION,
        DIVISION    
    }   
    
    private static final String[] OLS = {
        "+/-", "+", "-", "*", "/"
    };
    
    TYPE iType;
    OP   iOp;
    
    protected MathOp(TYPE aT, OP aO) {
        iType = aT;
        iOp = aO;
    }
    
    public TYPE getType() {return iType;}
    public OP getOperation() {return iOp;}
    
    public static final String[] getOpListString() {
        return OLS;        
    }
    
    public String getOperationChar() {
        switch (getOperation()) {
            case CHANGESIGN: return "+/-"; 
            case SUMMATION: return "+";
            case SUBTRACTION: return "-";
            case MULTIPLICATION: return "*";
            case DIVISION:  return "/";             
            default: throw new IllegalArgumentException();
        }
    }
    
    /*
    public Operand product(Operand anOp, OP anO) {
        switch (anO) {
            case CHANGESIGN: break;
            default:     ///throw an exception
                break;
        }
        return     

    } 
    */        
    /*
    public abstract Operand product(Operand anOp) ; {
        return new Operand(Double.NaN); ///todo: either extend chierarcy or raise an exception
    }
    */
    public abstract Operand product(Operand aLhs, Operand aRhs);/* {
        return new Operand(Double.NaN); ///todo: either extend chierarcy or raise an exception
    }*/
    
    static final MathOp getSubtraction() {
        return new MathOp(TYPE.BINARY, OP.SUBTRACTION) {
            
            public Operand product(Operand aLhs, Operand aRhs) {
                return new Operand(aLhs.value() - aRhs.value());
            }    
        };
    }
    
    static final MathOp getSummation() {
        return new MathOp(TYPE.BINARY, OP.SUMMATION) {
            public Operand product(Operand aLhs, Operand aRhs) {
                return new Operand(aLhs.value() + aRhs.value());
            }    
        };
    }
    
    
    
}
