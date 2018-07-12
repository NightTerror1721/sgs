/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data;

import kp.sgs.SGSGlobals;
import kp.sgs.data.utils.SGSIterator;

/**
 *
 * @author Asus
 */
public final class SGSInteger extends SGSImmutableValue
{
    public final int value;
    
    public SGSInteger(int value) { this.value = value; }
    
    @Override
    public final int getDataType() { return Type.INTEGER; }

    @Override
    public final int toInt() { return value; }

    @Override
    public final long toLong() { return value; }

    @Override
    public final float toFloat() { return value; }

    @Override
    public final double toDouble() { return value; }

    @Override
    public final boolean toBoolean() { return value != 0; }

    @Override
    public final String toString() { return String.valueOf(value); }

    @Override
    public final SGSArray toArray() { return SGSArray.of(true, this); }
    
    @Override
    public final SGSObject toObject() { return SGSObject.of(true, this); }
    
    
    
    /* Common operators */
    @Override public final SGSValue operatorEquals(SGSValue value) { return this.value == value.toDouble() ? TRUE : FALSE; }
    @Override public final SGSValue operatorNotEquals(SGSValue value) { return this.value != value.toDouble() ? TRUE : FALSE; }
    @Override public final SGSValue operatorGreater(SGSValue value) { return this.value > value.toDouble() ? TRUE : FALSE; }
    @Override public final SGSValue operatorSmaller(SGSValue value) { return this.value < value.toDouble() ? TRUE : FALSE; }
    @Override public final SGSValue operatorGreaterEquals(SGSValue value) { return this.value >= value.toDouble() ? TRUE : FALSE; }
    @Override public final SGSValue operatorSmallerEquals(SGSValue value) { return this.value <= value.toDouble() ? TRUE : FALSE; }
    @Override public final SGSValue operatorNegate() { return this.value != 0 ? TRUE : FALSE; }
    @Override public final SGSValue operatorConcat(SGSValue value) { return new SGSString(String.valueOf(this.value).concat(value.toString())); }
    @Override public final int      operatorLength() { return 1; }
    
    
    /* Math operators */
    @Override public final SGSValue operatorPlus(SGSValue value) { return new SGSFloat(this.value + value.toDouble()); }
    @Override public final SGSValue operatorMinus(SGSValue value) { return new SGSFloat(this.value - value.toDouble()); }
    @Override public final SGSValue operatorMultiply(SGSValue value) { return new SGSFloat(this.value * value.toDouble()); }
    @Override public final SGSValue operatorDivide(SGSValue value) { return new SGSFloat(this.value / value.toDouble()); }
    @Override public final SGSValue operatorRemainder(SGSValue value) { return new SGSInteger((int) (this.value % value.toDouble())); }
    @Override public final SGSValue operatorIncrease() { return new SGSInteger((int) this.value + 1); }
    @Override public final SGSValue operatorDecrease() { return new SGSInteger((int) this.value - 1); }
    @Override public final SGSValue operatorNegative() { return new SGSInteger(-this.value); }
    
    
    /* Bit operators */
    @Override public final SGSValue operatorBitwiseShiftLeft(SGSValue value) { return new SGSInteger(this.value << value.toInt()); }
    @Override public final SGSValue operatorBitwiseShiftRight(SGSValue value) { return new SGSInteger(this.value >>> value.toInt()); }
    @Override public final SGSValue operatorBitwiseAnd(SGSValue value) { return new SGSInteger(this.value & value.toInt()); }
    @Override public final SGSValue operatorBitwiseOr(SGSValue value) { return new SGSInteger(this.value | value.toInt()); }
    @Override public final SGSValue operatorBitwiseXor(SGSValue value) { return new SGSInteger(this.value ^ value.toInt()); }
    @Override public final SGSValue operatorBitwiseNot() { return new SGSInteger(~this.value); }
    
    
    /* Array operators */
    @Override public final SGSValue operatorGet(SGSValue index) { throw new UnsupportedOperationException("Integer cannot use operatorGet"); }
    
    
    /* Object operators */
    @Override public final SGSValue operatorGetProperty(String name) { throw new UnsupportedOperationException("Integer cannot use operatorGetProperty"); }
    @Override public final SGSValue operatorCall(SGSGlobals globals, SGSValue[] args) { throw new UnsupportedOperationException("Integer cannot use operatorCall"); }
    
    
    /* Pointer operators */
    @Override public final SGSValue operatorReferenceGet() { throw new UnsupportedOperationException("Integer cannot use operatorPointerGet"); }
    @Override public final SGSValue operatorReferenceSet(SGSValue value) { throw new UnsupportedOperationException("Integer cannot use operatorPointerSet"); }
    
    
    /* Iterator operators */
    @Override public final SGSValue operatorIterator() { return SGSIterator.scalarIterator(this); }

    @Override
    public final boolean equals(Object o)
    {
        return o instanceof SGSImmutableValue && value == ((SGSImmutableValue) o).toDouble();
    }

    @Override
    public final int hashCode()
    {
        int hash = 7;
        hash = 67 * hash + this.value;
        return hash;
    }
}
