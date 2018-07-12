/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.parser;

/**
 *
 * @author Asus
 */
public abstract class Operator extends CodeFragment
{
    private final OperatorSymbol symbol;
    
    private Operator(OperatorSymbol symbol)
    {
        this.symbol = symbol;
    }
    
    public final OperatorSymbol getSymbol() { return symbol; }
    public final String getSymbolName() { return symbol.getSymbolName(); }
    public final int getPriority() { return symbol.getPriority(); }
    
    public boolean hasInnerOperator() { return false; }
    public Operator getInnerOperator() { return null; }
    
    public final boolean hasRightToLeftOrder() { return symbol.hasRightToLeftOrder(); }
    
    public final boolean isConditional() { return symbol.isConditional(); }
    
    public abstract OperatorType getOperatorType();
    public final boolean isUnary() { return getOperatorType() == OperatorType.UNARY; }
    public final boolean isBinary() { return getOperatorType() == OperatorType.BINARY; }
    public final boolean isTernaryConditional() { return getOperatorType() == OperatorType.TERNARY_CONDITIONAL; }
    public final boolean isAssignment() { return getOperatorType() == OperatorType.ASSIGNMENT; }
    public final boolean isArrayGet() { return getOperatorType() == OperatorType.ARRAY_GET; }
    public final boolean isPropertyGet() { return getOperatorType() == OperatorType.ARRAY_GET; }
    public final boolean isCall() { return getOperatorType() == OperatorType.CALL; }
    public final boolean isInvoke() { return getOperatorType() == OperatorType.INVOKE; }
    public final boolean isNewFunction() { return getOperatorType() == OperatorType.NEW_FUNCTION; }
    
    public final int comparePriority(Operator op) { return symbol.comparePriority(op.symbol); }
    
    @Override
    public final boolean isValidOperand() { return false; }

    @Override
    public final CodeFragmentType getFragmentType() { return CodeFragmentType.OPERATOR; }
    
    
    
    public static final Operator
            SUFIX_INCREMENT = new UnaryOperator(OperatorSymbol.SUFIX_INCREMENT),
            SUFIX_DECREMENT = new UnaryOperator(OperatorSymbol.SUFIX_DECREMENT),
            CALL = new CallOperator(),
            INVOKE = new InvokeOperator(),
            ARRAY_GET = new ArrayGetOperator(),
            PROPERTY_GET = new PropertyGetOperator(),
            
            PREFIX_INCREMENT = new UnaryOperator(OperatorSymbol.PREFIX_INCREMENT),
            PREFIX_DECREMENT = new UnaryOperator(OperatorSymbol.PREFIX_DECREMENT),
            UNARY_PLUS = new UnaryOperator(OperatorSymbol.UNARY_PLUS),
            UNARY_MINUS = new UnaryOperator(OperatorSymbol.UNARY_MINUS),
            NOT = new UnaryOperator(OperatorSymbol.NOT),
            BITWISE_NOT = new UnaryOperator(OperatorSymbol.BITWISE_NOT),
            INDIRECTION = new UnaryOperator(OperatorSymbol.INDIRECTION),
            ADDRESS_OF = new UnaryOperator(OperatorSymbol.ADDRESS_OF),
            CAST_INT = new UnaryOperator(OperatorSymbol.CAST_INT),
            CAST_FLOAT = new UnaryOperator(OperatorSymbol.CAST_FLOAT),
            CAST_STRING = new UnaryOperator(OperatorSymbol.CAST_STRING),
            CAST_ARRAY = new UnaryOperator(OperatorSymbol.CAST_ARRAY),
            CAST_OBJECT = new UnaryOperator(OperatorSymbol.CAST_OBJECT),
            LENGTH = new UnaryOperator(OperatorSymbol.LENGTH),
            ISDEF = new UnaryOperator(OperatorSymbol.ISDEF),
            ISUNDEF = new UnaryOperator(OperatorSymbol.ISUNDEF),
            TYPEID = new UnaryOperator(OperatorSymbol.TYPEID),
            ITERATOR = new UnaryOperator(OperatorSymbol.ITERATOR),
            NEW_FUNCTION = new NewFunctionOperator(),
            
            MULTIPLICATION = new BinaryOperator(OperatorSymbol.MULTIPLICATION),
            DIVISION = new BinaryOperator(OperatorSymbol.DIVISION),
            REMAINDER = new BinaryOperator(OperatorSymbol.REMAINDER),
            
            ADDITION = new BinaryOperator(OperatorSymbol.ADDITION),
            SUBTRACTION = new BinaryOperator(OperatorSymbol.SUBTRACTION),
            
            BITWISE_LEFT_SHIFT = new BinaryOperator(OperatorSymbol.BITWISE_LEFT_SHIFT),
            BITWISE_RIGHT_SHIFT = new BinaryOperator(OperatorSymbol.BITWISE_RIGHT_SHIFT),
            
            GREATER_THAN = new BinaryOperator(OperatorSymbol.GREATER_THAN),
            SMALLER_THAN = new BinaryOperator(OperatorSymbol.SMALLER_THAN),
            GREATER_EQUALS_THAN = new BinaryOperator(OperatorSymbol.GREATER_EQUALS_THAN),
            SMALLER_EQUALS_THAN = new BinaryOperator(OperatorSymbol.SMALLER_EQUALS_THAN),
            
            EQUALS = new BinaryOperator(OperatorSymbol.EQUALS),
            NOT_EQUALS = new BinaryOperator(OperatorSymbol.NOT_EQUALS),
            TYPED_EQUALS = new BinaryOperator(OperatorSymbol.TYPED_EQUALS),
            TYPED_NOT_EQUALS = new BinaryOperator(OperatorSymbol.TYPED_NOT_EQUALS),
            
            BITWISE_AND = new BinaryOperator(OperatorSymbol.BITWISE_AND),
            
            BITWISE_XOR = new BinaryOperator(OperatorSymbol.BITWISE_XOR),
            
            BITWISE_OR = new BinaryOperator(OperatorSymbol.BITWISE_OR),
            
            LOGICAL_AND = new BinaryOperator(OperatorSymbol.LOGICAL_AND),
            
            LOGICAL_OR = new BinaryOperator(OperatorSymbol.LOGICAL_OR),
            
            CONCAT = new BinaryOperator(OperatorSymbol.CONCAT),
            
            TERNARY_CONDITIONAL = new TernaryConditionalOperator(),
            
            ASSIGNMENT = new AssignmentOperator(OperatorSymbol.ASSIGNMENT),
            ASSIGNMENT_ADDITION = new AssignmentOperator(OperatorSymbol.ASSIGNMENT_ADDITION, ADDITION),
            ASSIGNMENT_SUBTRACTION = new AssignmentOperator(OperatorSymbol.ASSIGNMENT_SUBTRACTION, SUBTRACTION),
            ASSIGNMENT_MULTIPLICATION = new AssignmentOperator(OperatorSymbol.ASSIGNMENT_MULTIPLICATION, MULTIPLICATION),
            ASSIGNMENT_DIVISION = new AssignmentOperator(OperatorSymbol.ASSIGNMENT_DIVISION, DIVISION),
            ASSIGNMENT_REMAINDER = new AssignmentOperator(OperatorSymbol.ASSIGNMENT_REMAINDER, REMAINDER),
            ASSIFNMENT_BITWISE_LEFT_SHIFT = new AssignmentOperator(OperatorSymbol.ASSIFNMENT_BITWISE_LEFT_SHIFT, BITWISE_LEFT_SHIFT),
            ASSIFNMENT_BITWISE_RIGHT_SHIFT = new AssignmentOperator(OperatorSymbol.ASSIFNMENT_BITWISE_RIGHT_SHIFT, BITWISE_RIGHT_SHIFT),
            ASSIFNMENT_BITWISE_AND = new AssignmentOperator(OperatorSymbol.ASSIFNMENT_BITWISE_AND, BITWISE_AND),
            ASSIFNMENT_BITWISE_XOR = new AssignmentOperator(OperatorSymbol.ASSIFNMENT_BITWISE_XOR, BITWISE_XOR),
            ASSIFNMENT_BITWISE_OR = new AssignmentOperator(OperatorSymbol.ASSIFNMENT_BITWISE_OR, BITWISE_OR);
    
    
    
    public static final Operator getInverseOperator(Operator op)
    {
        switch(op.symbol)
        {
            case ISDEF: return ISUNDEF;
            case ISUNDEF: return ISDEF;
            default: return null;
        }
    }
    
    
    
    private static class UnaryOperator extends Operator
    {
        public UnaryOperator(OperatorSymbol symbol)
        {
            super(symbol);
        }
        
        @Override
        public final OperatorType getOperatorType() { return OperatorType.UNARY; }
    }
    
    private static class BinaryOperator extends Operator
    {
        public BinaryOperator(OperatorSymbol symbol)
        {
            super(symbol);
        }
        
        @Override
        public final OperatorType getOperatorType() { return OperatorType.BINARY; }
    }
    
    private static class TernaryConditionalOperator extends Operator
    {
        public TernaryConditionalOperator()
        {
            super(OperatorSymbol.TERNARY_CONDITIONAL);
        }
        
        @Override
        public final OperatorType getOperatorType() { return OperatorType.TERNARY_CONDITIONAL; }
    }
    
    private static class AssignmentOperator extends Operator
    {
        private final Operator innerOperator;
        
        public AssignmentOperator(OperatorSymbol symbol, Operator innerOperator)
        {
            super(symbol);
            this.innerOperator = innerOperator;
        }
        public AssignmentOperator(OperatorSymbol symbol) { this(symbol, null); }
        
        @Override
        public final OperatorType getOperatorType() { return OperatorType.ASSIGNMENT; }
        
        @Override
        public final boolean hasInnerOperator() { return innerOperator != null; }
        
        @Override
        public final Operator getInnerOperator() { return innerOperator; }
    }
    
    private static class ArrayGetOperator extends Operator
    {
        public ArrayGetOperator()
        {
            super(OperatorSymbol.ARRAY_GET);
        }
        
        @Override
        public final OperatorType getOperatorType() { return OperatorType.ARRAY_GET; }
    }
    
    private static class PropertyGetOperator extends Operator
    {
        public PropertyGetOperator()
        {
            super(OperatorSymbol.PROPERTY_GET);
        }
        
        @Override
        public final OperatorType getOperatorType() { return OperatorType.PROPERTY_GET; }
    }
    
    private static class CallOperator extends Operator
    {
        public CallOperator()
        {
            super(OperatorSymbol.CALL);
        }
        
        @Override
        public final OperatorType getOperatorType() { return OperatorType.CALL; }
    }
    
    private static class InvokeOperator extends Operator
    {
        public InvokeOperator()
        {
            super(OperatorSymbol.INVOKE);
        }
        
        @Override
        public final OperatorType getOperatorType() { return OperatorType.CALL; }
    }
    
    private static class NewFunctionOperator extends Operator
    {
        public NewFunctionOperator()
        {
            super(OperatorSymbol.NEW_FUNCTION);
        }
        
        @Override
        public final OperatorType getOperatorType() { return OperatorType.NEW_FUNCTION; }
    }
    
    public enum OperatorType { UNARY, BINARY, TERNARY_CONDITIONAL, ASSIGNMENT, ARRAY_GET, PROPERTY_GET, CALL, INVOKE, NEW_FUNCTION }
}
