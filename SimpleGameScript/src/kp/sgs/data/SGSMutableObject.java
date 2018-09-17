/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import static kp.sgs.data.SGSValue.FALSE;
import static kp.sgs.data.SGSValue.TRUE;
import kp.sgs.data.utils.SGSIterator;
import kp.sgs.data.utils.SpecialProperty;

/**
 *
 * @author Asus
 */
public final class SGSMutableObject extends SGSValue implements SGSObject
{
    private final SGSValue base;
    private final Map<String, SGSValue> properties;
    
    public SGSMutableObject(Map<String, SGSValue> properties, SGSValue base)
    {
        if(properties == null)
            throw new NullPointerException();
        this.base = base;
        this.properties = properties;
    }
    public SGSMutableObject(Map<String, SGSValue> properties) { this(properties, null); }
    public SGSMutableObject(SGSValue base) { this(null, base); }
    public SGSMutableObject() { this(new HashMap<>(), null); }
    
    @Override
    public final int getDataType() { return Type.OBJECT; }

    @Override
    public final int toInt()
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.CAST_INT)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this }).toInt();
        throw new UnsupportedOperationException("operator cast int not implemented");
    }

    @Override
    public final long toLong()
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.CAST_INT)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this }).toLong();
        throw new UnsupportedOperationException("operator cast int not implemented");
    }

    @Override
    public final float toFloat()
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.CAST_FLOAT)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this }).toFloat();
        throw new UnsupportedOperationException("operator cast float not implemented");
    }

    @Override
    public final double toDouble()
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.CAST_FLOAT)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this }).toDouble();
        throw new UnsupportedOperationException("operator cast float not implemented");
    }

    @Override
    public final boolean toBoolean()
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.CAST_INT)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this }).toBoolean();
        throw new UnsupportedOperationException("operator cast int not implemented");
    }

    @Override
    public final String toString()
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.CAST_STRING)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this }).toString();
        return objectToString(this);
    }

    @Override
    public final SGSArray toArray()
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.CAST_ARRAY)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this }).toArray();
        return SGSArray.of(true, this);
    }
    
    @Override
    public final SGSObject toObject()
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.CAST_OBJECT)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this }).toObject();
        return this;
    }
    
    
    /* Comparison operators */
    @Override public final SGSValue operatorEquals(SGSValue value)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_EQUALS)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, value });
        return properties.equals(value.toObject().map()) ? TRUE : FALSE;
    }
    @Override public final SGSValue operatorNotEquals(SGSValue value)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_NOT_EQUALS)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, value });
        return properties.equals(value.toObject().map()) ? FALSE : TRUE;
    }
    @Override public final SGSValue operatorGreater(SGSValue value)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_GREATER)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, value });
        throw new UnsupportedOperationException("> operator not implemented");
    }
    @Override public final SGSValue operatorSmaller(SGSValue value)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_SMALLER)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, value });
        throw new UnsupportedOperationException("< operator not implemented");
    }
    @Override public final SGSValue operatorGreaterEquals(SGSValue value)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_GREATER_EQUALS)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, value });
        throw new UnsupportedOperationException(">= operator not implemented");
    }
    @Override public final SGSValue operatorSmallerEquals(SGSValue value)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_SMALLER_EQUALS)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, value });
        throw new UnsupportedOperationException("<= operator not implemented");
    }
    @Override public final SGSValue operatorNegate()
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_NEGATE)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this });
        return properties.isEmpty() ? TRUE : FALSE;
    }
    @Override public final SGSValue operatorConcat(SGSValue value) { return new SGSString(toString().concat(value.toString())); }
    @Override public final int      operatorLength()
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_LENGTH)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this }).toInt();
        return properties.size();
    }
    
    
    /* Math operators */
    @Override public final SGSValue operatorPlus(SGSValue value)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_PLUS)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, value });
        throw new UnsupportedOperationException("+ operator not implemented");
    }
    @Override public final SGSValue operatorMinus(SGSValue value)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_MINUS)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, value });
        throw new UnsupportedOperationException("- operator not implemented");
    }
    @Override public final SGSValue operatorMultiply(SGSValue value)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_MULTIPLY)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, value });
        throw new UnsupportedOperationException("* operator not implemented");
    }
    @Override public final SGSValue operatorDivide(SGSValue value)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_DIVIDE)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, value });
        throw new UnsupportedOperationException("/ operator not implemented");
    }
    @Override public final SGSValue operatorRemainder(SGSValue value)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_REMAINDER)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, value });
        throw new UnsupportedOperationException("% operator not implemented");
    }
    @Override public final SGSValue operatorIncrease()
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_INCREASE)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this });
        throw new UnsupportedOperationException("++ operator not implemented");
    }
    @Override public final SGSValue operatorDecrease()
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_DECREASE)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this });
        throw new UnsupportedOperationException("-- operator not implemented");
    }
    @Override public final SGSValue operatorNegative() { throw new UnsupportedOperationException("Object cannot use operatorNegative"); }
    
    
    /* Bit operators */
    @Override public final SGSValue operatorBitwiseShiftLeft(SGSValue value)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_BTW_SLEFT)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, value });
        throw new UnsupportedOperationException("<< operator not implemented");
    }
    @Override public final SGSValue operatorBitwiseShiftRight(SGSValue value)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_BTW_SRIGHT)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, value });
        throw new UnsupportedOperationException(">> operator not implemented");
    }
    @Override public final SGSValue operatorBitwiseAnd(SGSValue value)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_BTW_AND)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, value });
        throw new UnsupportedOperationException("& operator not implemented");
    }
    @Override public final SGSValue operatorBitwiseOr(SGSValue value)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_BTW_OR)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, value });
        throw new UnsupportedOperationException("| operator not implemented");
    }
    @Override public final SGSValue operatorBitwiseXor(SGSValue value)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_BTW_XOR)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, value });
        throw new UnsupportedOperationException("^ operator not implemented");
    }
    @Override public final SGSValue operatorBitwiseNot()
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_BTW_NOT)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this });
        throw new UnsupportedOperationException("~ operator not implemented");
    }
    

    /* Array operators */
    @Override public SGSValue operatorGet(SGSValue index)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_GET)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, index });
        return properties.getOrDefault(index, UNDEFINED);
    }
    @Override public void     operatorSet(SGSValue index, SGSValue value)
    {
        if(value == null)
            throw new NullPointerException();
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_SET)) != UNDEFINED)
            prop.operatorCall(new SGSValue[] { this, index, value });
        properties.put(index.toString(), value);
    }
    @Override public SGSValue operatorGet(int index)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_GET)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this, new SGSInteger(index) });
        return properties.getOrDefault(index, UNDEFINED);
    }
    @Override public void     operatorSet(int index, SGSValue value)
    {
        if(value == null)
            throw new NullPointerException();
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_SET)) != UNDEFINED)
            prop.operatorCall(new SGSValue[] { this, new SGSInteger(index), value });
        properties.put(Integer.toString(index), value);
    }
    
    
    /* Object operators */
    @Override public SGSValue operatorGetProperty(String name)
    {
        SGSValue prop = properties.getOrDefault(name, UNDEFINED);
        return prop == UNDEFINED ? base == null ? UNDEFINED : base.operatorGetProperty(name) : prop;
    }
    @Override public void     operatorSetProperty(String name, SGSValue value)
    {
        if(value == null)
            throw new NullPointerException();
        properties.put(name, value);
    }
    @Override public SGSValue operatorCall(SGSValue[] args)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_CALL)) != UNDEFINED)
        {
            SGSValue[] fargs = new SGSValue[args.length + 1];
            System.arraycopy(args, 0, fargs, 1, args.length);
            fargs[0] = this;
            return prop.operatorCall(fargs);
        }
        throw new UnsupportedOperationException("() operator not implemented");
    }
    
    
    /* Pointer operators */
    @Override public final SGSValue operatorReferenceGet() { throw new UnsupportedOperationException("Object cannot use operatorPointerGet"); }
    @Override public final void     operatorReferenceSet(SGSValue value) { throw new UnsupportedOperationException("Object cannot use operatorPointerSet"); }
    
    
    /* Iterator operators */
    @Override public final SGSValue operatorIterator()
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.OP_ITERATOR)) != UNDEFINED)
            return prop.operatorCall(new SGSValue[] { this });
        return new SGSIterator(properties.values());
    }
    
    @Override
    public final void constructor(SGSValue object, SGSValue[] args)
    {
        SGSValue prop;
        if((prop = operatorGetProperty(SpecialProperty.CONSTRUCTOR)) != UNDEFINED)
        {
            SGSValue[] fargs = new SGSValue[args.length + 1];
            System.arraycopy(args, 0, fargs, 1, args.length);
            fargs[0] = object;
            prop.operatorCall(fargs);
        }
    }
    
    @Override
    public final boolean equals(Object o)
    {
        return o instanceof SGSMutableObject && properties.equals(((SGSMutableObject) o).properties);
    }

    @Override
    public final int hashCode() { return properties.hashCode(); }
    
    
    public static final String objectToString(SGSObject obj)
    {
        if(obj.objectIsEmpty())
            return "{}";
        
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        
        for(Map.Entry<String, SGSValue> e : obj)
        {
            sb.append('\t').append(e.getKey()).append(": ")
                    .append(e.getValue().toString().replace("\n", "\n\t")).append('\n');
        }
        sb.append('}');
        
        return sb.toString();
    }

    @Override
    public final SGSImmutableValue operatorConst() { return new SGSImmutableObject(properties); }

    @Override
    public final boolean isMutable() { return true; }

    @Override
    public final int objectSize() { return properties.size(); }
    
    @Override
    public final boolean objectIsEmpty() { return properties.isEmpty(); }
    
    @Override
    public final boolean objectHasProperty(String name) { return properties.containsKey(name); }

    @Override
    public final SGSValue objectGetProperty(String name) { return properties.getOrDefault(name, UNDEFINED); }

    @Override
    public final SGSValue objectSetProperty(String name, SGSValue value)
    {
        if(value == null)
            throw new NullPointerException();
        properties.put(name, value);
        return value;
    }
    
    @Override
    public final SGSValue objectGetBase() { return base == null ? UNDEFINED : base; }
    
    @Override
    public final Map<String, SGSValue> map() { return properties; }
    
    @Override
    public final SGSValue toSGSValue() { return this; }

    @Override
    public final Iterator<Map.Entry<String, SGSValue>> iterator() { return properties.entrySet().iterator(); }

    @Override
    public final SGSValue getGlobalValue(String name) { return properties.getOrDefault(name, UNDEFINED); }

    @Override
    public final SGSValue setGlobalValue(String name, SGSValue value)
    {
        properties.put(name, value == null ? UNDEFINED : value);
        return value;
    }

    @Override
    public final boolean hasGlobalValue(String name) { return properties.containsKey(name); }
    
}
