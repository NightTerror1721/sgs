/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.parser;

import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.data.SGSValue.Type;

/**
 *
 * @author Asus
 */
public final class DataType extends CodeFragment
{
    private final int sgsType;
    private final Operator castOperator;
    
    private DataType(int type, Operator castOperator)
    {
        this.sgsType = type;
        this.castOperator = castOperator;
    }
    
    public final int getTypeId() { return sgsType; }
    
    public final boolean isAny() { return this == ANY; }
    
    public final Operator getCastOperator()
    {
        if(castOperator == null)
            throw new IllegalStateException();
        return castOperator;
    }
    
    @Override
    public final CodeFragmentType getFragmentType() { return CodeFragmentType.DATA_TYPE; }

    @Override
    public final boolean isValidOperand() { return false; }
    
    @Override
    public final String toString() { return castOperator == null ? "def" : castOperator.toString(); }
    
    public static final boolean canUseImplicitCast(DataType to, DataType from)
    {
        if(to.isAny())
            return true;
        switch(to.sgsType)
        {
            case Type.INTEGER: switch(from.sgsType) {
                case Type.INTEGER: return true;
                default: return false;
            }
            case Type.FLOAT: switch(from.sgsType) {
                case Type.INTEGER:
                case Type.FLOAT: return true;
                default: return false;
            }
            case Type.STRING: switch(from.sgsType) {
                case Type.STRING: return true;
                default: return false;
            }
            case Type.ARRAY: switch(from.sgsType) {
                case Type.ARRAY: return true;
                default: return false;
            }
            case Type.OBJECT: switch(from.sgsType) {
                case Type.OBJECT: return true;
                default: return false;
            }
            default: return false;
        }
    }
    
    public static final DataType parse(CodeFragment frag) throws CompilerError
    {
        if(frag == Command.DEF)
            return ANY;
        if(frag.isDataType())
            return (DataType) frag;
        throw new CompilerError("Expected valid data type or \"def\". But found " + frag);
    }
    
    public static final boolean isValid(CodeFragment frag)
    {
        return frag == Command.DEF || frag.isDataType();
    }
    
    
    public static final DataType
            INTEGER = new DataType(Type.INTEGER, Operator.CAST_INT),
            FLOAT = new DataType(Type.FLOAT, Operator.CAST_FLOAT),
            STRING = new DataType(Type.STRING, Operator.CAST_STRING),
            ARRAY = new DataType(Type.ARRAY, Operator.CAST_ARRAY),
            OBJECT = new DataType(Type.OBJECT, Operator.CAST_OBJECT),
            ANY = new DataType(-1, null);
}
