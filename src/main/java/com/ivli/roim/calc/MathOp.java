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
        NOP (TYPE.UNARY,             ""),
        CHSIGN(TYPE.UNARY,        "+/-"),
        ADDITION(TYPE.BINARY,       "+"),
        SUBTRACTION(TYPE.BINARY,    "-"),
        MULTIPLICATION(TYPE.BINARY, "*"),
        DIVISION(TYPE.BINARY,       "/");
        
        TYPE  iType;
        String iStr;
       
        
        OP(TYPE aT, String aS) {
            iType = aT; 
            iStr = aS;           
        }
    }   
   
    final OP iOp;
       
    protected MathOp(OP aO) {        
        iOp = aO;
    }
       
    public OP getOperation() {return iOp;}
    
    public static final String[] getOpListString() {
        java.util.Set<OP> so = java.util.EnumSet.allOf(OP.class);
        
        String[] ret = new String[so.size()]; //String();
        int n = 0;
        for (OP o : OP.values())
            ret[n++] = o.iStr;
        return ret;        
    }
    
    public String getOperationChar() {
        return getOperation().iStr;           
    }
    
    
    public abstract IOperand product(IOperand aLhs, IOperand aRhs);
       
    static final MathOp getNop() {
        return getOP(OP.NOP);
    }
    
    static final MathOp getChSign() {
        return getOP(OP.CHSIGN);
    }
    
    static final MathOp getAddition() {
        return getOP(OP.ADDITION);
    }
     
    static final MathOp getSubtraction() {
        return getOP(OP.SUBTRACTION);
    }
    
    static final MathOp fromString(String aStr) {
        for (OP o : OP.values()) {
            if (o.iStr == aStr)
                return getOP(o);
        }
        return getNop(); //wouldn't it better raise an exception
    }
        
    static final MathOp getOP(OP aOrdinal) {
        switch (aOrdinal) {
            case CHSIGN: 
                return new MathOp(OP.NOP) {            
                    public IOperand product(IOperand aLhs, IOperand aRhs) {
                        return new Operand((-1.0) * aLhs.value());
                }};                                 
            case ADDITION: 
                return new MathOp(OP.ADDITION) {
                    public IOperand product(IOperand aLhs, IOperand aRhs) {
                        return new Operand(aLhs.value() + aRhs.value());
                }};                    
            case SUBTRACTION: 
                return new MathOp(OP.SUBTRACTION) {            
                    public IOperand product(IOperand aLhs, IOperand aRhs) {
                        return new Operand(aLhs.value() - aRhs.value());
                }};                    
            case MULTIPLICATION:  
                return new MathOp(OP.SUBTRACTION) {            
                    public IOperand product(IOperand aLhs, IOperand aRhs) {
                        return new Operand(aLhs.value() * aRhs.value());
                }};                   
            case DIVISION:  
                return new MathOp(OP.SUBTRACTION) {            
                    public IOperand product(IOperand aLhs, IOperand aRhs) {
                        return new Operand(aLhs.value() / aRhs.value());
                }};
            case NOP: //fall thru
            default: return new MathOp(OP.NOP) {            
                    public IOperand product(IOperand aLhs, IOperand aRhs) {
                        return new Operand(aLhs.value() / aRhs.value());
                }};
        }
    }
    
}
