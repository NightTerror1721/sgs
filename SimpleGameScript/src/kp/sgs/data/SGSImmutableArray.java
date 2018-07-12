/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import kp.sgs.SGSGlobals;
import static kp.sgs.data.SGSValue.FALSE;
import static kp.sgs.data.SGSValue.TRUE;
import kp.sgs.data.utils.SGSIterator;

/**
 *
 * @author Asus
 */
public class SGSImmutableArray extends SGSImmutableValue implements SGSArray
{
    private final SGSValue[] array;
    
    public SGSImmutableArray(SGSValue... array)
    {
        if(array == null)
            throw new NullPointerException();
        this.array = new SGSValue[array.length];
        System.arraycopy(array, 0, this.array, 0, array.length);
    }
    
    @Override
    public final int getDataType() { return Type.CONST_ARRAY; }

    @Override
    public final int toInt() { return array.length; }

    @Override
    public final long toLong() { return array.length; }

    @Override
    public final float toFloat() { return array.length; }

    @Override
    public final double toDouble() { return array.length; }

    @Override
    public final boolean toBoolean() { return array.length > 0; }

    @Override
    public final String toString() { return Arrays.toString(array); }

    @Override
    public final SGSArray toArray() { return this; }
    
    @Override
    public final SGSObject toObject() { return SGSObject.of(true, (SGSArray) this); }
    
    @Override public final SGSValue operatorGreater(SGSValue value) { throw new UnsupportedOperationException("Array cannot use operatorGreater"); }
    @Override public final SGSValue operatorSmaller(SGSValue value) { throw new UnsupportedOperationException("Array cannot use operatorSmaller"); }
    @Override public final SGSValue operatorGreaterEquals(SGSValue value) { throw new UnsupportedOperationException("Array cannot use operatorGreaterEquals"); }
    
    /* Comparison operators */
    @Override public final SGSValue operatorEquals(SGSValue value) { return Arrays.equals(array, value.toArray().array()) ? TRUE : FALSE; }
    @Override public final SGSValue operatorNotEquals(SGSValue value) { return Arrays.equals(array, value.toArray().array()) ? FALSE : TRUE; }
    @Override public final SGSValue operatorSmallerEquals(SGSValue value) { throw new UnsupportedOperationException("Array cannot use operatorSmallerEquals"); }
    @Override public final SGSValue operatorNegate() { return array.length <= 0 ?  TRUE : FALSE; }
    @Override public final SGSValue operatorConcat(SGSValue value) { return new SGSString(Arrays.toString(array).concat(value.toString())); }
    @Override public final int      operatorLength() { return array.length; }
    
    
    /* Math operators */
    @Override public final SGSValue operatorPlus(SGSValue value)
    {
        SGSValue[] result;
        if(value.isArray())
        {
            SGSValue[] other = value.toArray().array();
            result = new SGSValue[array.length + other.length];
            System.arraycopy(array, 0, result, 0, array.length);
            System.arraycopy(other, 0, result, array.length, other.length);
        }
        else
        {
            result = new SGSValue[array.length + 1];
            result[array.length] = value;
            System.arraycopy(array, 0, result, 0, array.length);
        }
        return new SGSMutableArray(result);
    }
    @Override public final SGSValue operatorMinus(SGSValue value)
    {
        List<SGSValue> other = Arrays.asList(value.toArray().array());
        return new SGSMutableArray(Arrays.stream(array).filter(e -> other.contains(e)).toArray(size -> new SGSValue[size]));
    }
    @Override public final SGSValue operatorMultiply(SGSValue value) { throw new UnsupportedOperationException("const Array cannot use operatorMultiply"); }
    @Override public final SGSValue operatorDivide(SGSValue value) { throw new UnsupportedOperationException("const Array cannot use operatorDivide"); }
    @Override public final SGSValue operatorRemainder(SGSValue value) { throw new UnsupportedOperationException("const Array cannot use operatorRemainder"); }
    @Override public final SGSValue operatorIncrease() { throw new UnsupportedOperationException("const Array cannot use operatorIncrease"); }
    @Override public final SGSValue operatorDecrease() { throw new UnsupportedOperationException("const Array cannot use operatorDecrease"); }
    @Override public final SGSValue operatorNegative() { throw new UnsupportedOperationException("const Array cannot use operatorNegative"); }
    
    
    /* Bit operators */
    @Override public final SGSValue operatorBitwiseShiftLeft(SGSValue value) { throw new UnsupportedOperationException("const Array cannot use operatorShiftLeft"); }
    @Override public final SGSValue operatorBitwiseShiftRight(SGSValue value) { throw new UnsupportedOperationException("const Array cannot use operatorShiftRight"); }
    @Override public final SGSValue operatorBitwiseAnd(SGSValue value) { throw new UnsupportedOperationException("const Array cannot use operatorLogicAnd"); }
    @Override public final SGSValue operatorBitwiseOr(SGSValue value) { throw new UnsupportedOperationException("const Array cannot use operatorLogicOr"); }
    @Override public final SGSValue operatorBitwiseXor(SGSValue value) { throw new UnsupportedOperationException("const Array cannot use operatorLogicXor"); }
    @Override public final SGSValue operatorBitwiseNot() { throw new UnsupportedOperationException("const Array cannot use operatorLogicNot"); }
    
    
    /* Array operators */
    @Override public final SGSValue operatorGet(SGSValue index) { return array[index.toInt()]; }
    @Override public final SGSValue operatorGet(int index) { return array[index]; }
    
    
    /* Object operators */
    @Override public final SGSValue operatorGetProperty(String name) { throw new UnsupportedOperationException("const Array cannot use operatorGetProperty"); }
    @Override public final SGSValue operatorCall(SGSGlobals globals, SGSValue[] args) { throw new UnsupportedOperationException("const Array cannot use operatorCall"); }
    
    
    /* Pointer operators */
    @Override public final SGSValue operatorReferenceGet() { throw new UnsupportedOperationException("const Array cannot use operatorPointerGet"); }
    @Override public final SGSValue operatorReferenceSet(SGSValue value) { throw new UnsupportedOperationException("const Array cannot use operatorPointerSet"); }
    
    
    /* Iterator operators */
    @Override public final SGSValue operatorIterator() { return new SGSIterator(this); }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof SGSMutableArray && Arrays.equals(array, ((SGSMutableArray) o).array);
    }

    @Override
    public final int hashCode()
    {
        int hash = 7;
        hash = 67 * hash + Arrays.deepHashCode(this.array);
        return hash;
    }
    
    @Override
    public final boolean isMutable() { return false; }
    
    @Override
    public final int arrayLength() { return array.length; }

    @Override
    public final SGSValue arrayGet(int index) { return array[index]; }

    @Override
    public final SGSValue arraySet(int index, SGSValue value)
    {
        if(value == null)
            throw new NullPointerException();
        return array[index] = value;
    }
    
    @Override
    public final SGSValue[] array() { return array; }
    
    @Override
    public final SGSValue toSGSValue() { return this; }
    
    @Override
    public final Iterator<SGSValue> iterator()
    {
        return new Iterator<SGSValue>()
        {
            private int it = 0;
            
            @Override public boolean hasNext() { return it < array.length; }

            @Override public SGSValue next() { return array[it++]; }
        };
    }
}
