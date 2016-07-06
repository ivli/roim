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
public class BinaryOp implements IOperation {
    IOperand iLhs;
    IOperand iRhs;
    MathOp   iOp;
    
    public BinaryOp(IOperand aLhs, IOperand aRhs, String anOperation) {
        iLhs = aLhs;
        iRhs = aRhs;
        iOp  = MathOp.fromString(anOperation);
    }
    
    public IOperand getLhs() {return iLhs;}
    
    public IOperand getRhs() {return iRhs;}
    
    public MathOp getOp() {return iOp;}
    
    public IOperand value() {
        return iOp.product(iLhs, iRhs);
    }
    public String format(AbstractFormatter aF) {
        return aF.format(this);
    }
    /*
    public String getString() {
        return iLhs.getString() + " " 
                + iOp.getOperationChar() + " " 
                + iRhs.getString();
    }
    
    public String getCompleteString() {
        return this.getString() + "=" 
                + value().getString();
    }    
    */
}
