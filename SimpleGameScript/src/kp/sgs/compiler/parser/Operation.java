/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.parser;

import kp.sgs.compiler.exception.CompilerError;

/**
 *
 * @author Asus
 */
public final class Operation extends Statement
{
    private Operator operator;
    private final Statement[] operands;

    private Operation(Operator operator, Statement... operands)
    {
        if(operator == null)
            throw new NullPointerException();
        if(operands == null)
            throw new NullPointerException();
        for(int i=0;i<operands.length;i++)
            if(operands[i] == null)
                throw new NullPointerException();
        this.operator = operator;
        this.operands = operands;
    }

    private boolean invertOperator()
    {
        Operator op = Operator.getInverseOperator(operator);
        if(op != null)
        {
            operator = op;
            return true;
        }
        return false;
    }
    
    public final int getOperandCount() { return operands.length; }

    public final Operator getOperator() { return operator; }

    public final <S extends Statement> S getOperand(int index) { return (S) operands[index]; }
    
    public final OperatorSymbol getOperatorSymbol() { return getOperator().getSymbol(); }
    
    public final boolean isUnary() { return operator.isUnary(); }
    public final boolean isBinary() { return operator.isBinary(); }
    public final boolean isTernaryConditional() { return operator.isTernaryConditional(); }
    public final boolean isAssignment() { return operator.isAssignment(); }
    public final boolean isArrayGet() { return operator.isArrayGet(); }
    public final boolean isPropertyGet() { return operator.isPropertyGet(); }
    public final boolean isCall() { return operator.isCall(); }
    public final boolean isInvoke() { return operator.isInvoke(); }
    public final boolean isNewFunction() { return operator.isNewFunction(); }
    
    @Override
    public final CodeFragmentType getFragmentType() { return CodeFragmentType.OPERATION; }
    
    
    public static final Statement unary(Operator operator, Statement op)
    {
        if(!operator.isUnary())
            throw new IllegalArgumentException();
        if(operator.getSymbol() == OperatorSymbol.NOT && op.is(CodeFragmentType.OPERATION))
        {
            Operation operation = (Operation) op;
            if(operation.invertOperator())
                return operation;
        }
        if(op.is(CodeFragmentType.LITERAL))
            return literalUnary(operator, (Literal) op);
        return new Operation(operator, op);
    }
    
    public static final Statement binary(Operator operator, Statement left, Statement right)
    {
        if(!operator.isBinary())
            throw new IllegalArgumentException();
        if(left.is(CodeFragmentType.LITERAL) && right.is(CodeFragmentType.LITERAL))
            return literalBinary(operator, (Literal) left, (Literal) right);
        return new Operation(operator, left, right);
    }
    
    public static final Statement ternaryConditional(Statement cond, Statement trueOp, Statement falseOp)
    {
        return new Operation(Operator.TERNARY_CONDITIONAL, cond, trueOp, falseOp);
    }
    
    public static final Statement assignment(Operator operator, Statement location, Statement op) throws CompilerError
    {
        if(!operator.isAssignment())
            throw new IllegalArgumentException();
        if(!location.isIdentifier())
        {
            if(!location.isOperation())
                throw new CompilerError("Expected valid identifier, array access or property access in left assignment operator. But found: " + location);
            Operation lop = (Operation) location;
            switch(lop.getOperatorSymbol())
            {
                case ARRAY_GET: case PROPERTY_GET: case INDIRECTION: break;
                default:
                    throw new CompilerError("Expected valid identifier, array access or property access in left assignment operator. But found: " + location);
            }
        }
        return new Operation(operator, location, op);
    }
    
    public static final Statement arrayGet(Statement array, Statement indexOp)
    {
        return new Operation(Operator.ARRAY_GET, array, indexOp);
    }
    
    public static final Statement propertyGet(Statement object, CodeFragment identifier) throws CompilerError
    {
        if(!identifier.isIdentifier())
            throw new CompilerError("Expected valid identifier in right property access operator. But found: " + identifier);
        return new Operation(Operator.PROPERTY_GET, object, (Identifier) identifier);
    }
    
    public static final Statement call(Statement function, CodeFragment arguments) throws CompilerError
    {
        if(!arguments.isArguments())
            throw new CompilerError("Expected a valid arguments list in call operator. But found: " + arguments);
        return new Operation(Operator.CALL, function, (Arguments) arguments);
    }
    
    public static final Statement invoke(Statement object, CodeFragment identifier, CodeFragment arguments) throws CompilerError
    {
        if(!identifier.isIdentifier())
            throw new CompilerError("Expected a valid identifier in invoke operator <object>-><identifier>(<args>). But found: " + identifier);
        if(!arguments.isArguments())
            throw new CompilerError("Expected a valid arguments list in invoke operator. But found: " + arguments);
        return new Operation(Operator.INVOKE, object, (Identifier) identifier, (Arguments) arguments);
    }
    
    
    public static final Operation newFunction(CodeFragment identifier, CodeFragment arguments, CodeFragment scope) throws CompilerError
    {
        if(identifier != null && !identifier.isIdentifier())
            throw new CompilerError("Expected a valid identifier in new function \"def <identifier?>(<arguments...>)\". But found: " + identifier);
        if(!arguments.isArguments())
            throw new CompilerError("Expected a valid arguments list in new function. But found: " + arguments);
        if(!scope.isScope())
            throw new CompilerError("Expected a valid scope in new function. But found: " + scope);
        if(identifier == null)
            return new Operation(Operator.NEW_FUNCTION, (Arguments) arguments, (Scope) scope);
        return new Operation(Operator.NEW_FUNCTION, (Identifier) identifier, (Arguments) arguments, (Scope) scope);
    }
    
    
    
    
    private static Statement literalUnary(Operator operator, Literal value)
    {
        switch(operator.getSymbol())
        {
            case UNARY_PLUS: return value.operatorUnaryPlus();
            case UNARY_MINUS: return value.operatorUnaryMinus();
            case NOT: return value.operatorNot();
            case BITWISE_NOT: return value.operatorBitwiseNot();
            case CAST_INT: return value.operatorCastInt();
            case CAST_FLOAT: return value.operatorCastFloat();
            case CAST_STRING: return value.operatorCastString();
            case CAST_ARRAY: return value.operatorCastArray();
            case CAST_OBJECT: return value.operatorCastObject();
            case LENGTH: return value.operatorLength();
            case ISDEF: return value.operatorIsdef();
            case ISUNDEF: return value.operatorIsundef();
            case TYPEID: return value.operatorTypeid();
            default: return new Operation(operator, value);
        }
    }
    
    private static Statement literalBinary(Operator operator, Literal left, Literal right)
    {
        switch(operator.getSymbol())
        {
            case MULTIPLICATION: return left.operatorMultiply(right);
            case DIVISION: return left.operatorDivision(right);
            case REMAINDER: return left.operatorRemainder(right);
            case ADDITION: return left.operatorPlus(right);
            case SUBTRACTION: return left.operatorMinus(right);
            case BITWISE_LEFT_SHIFT: return left.operatorBitwiseShiftLeft(right);
            case BITWISE_RIGHT_SHIFT: return left.operatorBitwiseShiftRight(right);
            case GREATER_THAN: return left.operatorGreater(right);
            case SMALLER_THAN: return left.operatorSmaller(right);
            case GREATER_EQUALS_THAN: return left.operatorGreaterEquals(right);
            case SMALLER_EQUALS_THAN: return left.operatorSmallerEquals(right);
            case EQUALS: return left.operatorEquals(right);
            case NOT_EQUALS: return left.operatorNotEquals(right);
            case TYPED_EQUALS: return left.operatorTypedEquals(right);
            case TYPED_NOT_EQUALS: return left.operatorTypedNotEquals(right);
            case BITWISE_AND: return left.operatorBitwiseAnd(right);
            case BITWISE_XOR: return left.operatorBitwiseXor(right);
            case BITWISE_OR: return left.operatorBitwiseOr(right);
            case CONCAT: return left.operatorConcat(right);
            default: return new Operation(operator, left, right);
        }
    }
}
