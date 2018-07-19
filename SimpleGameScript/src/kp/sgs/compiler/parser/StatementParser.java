/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.parser;

import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.parser.CodeFragmentList.Pointer;

/**
 *
 * @author Asus
 */
public final class StatementParser
{
    private StatementParser() {}
    
    public static final Statement parse(CodeFragmentList list) throws CompilerError
    {
        Pointer it = list.counter();
        Statement operand = packPart(it);
        if(it.end())
            return operand;
        return packOperation(it, operand);
    }
    
    private static Statement packPart(Pointer it) throws CompilerError
    {
        if(it.end())
            throw new CompilerError("unexpected ned of instruction");
        return packPostUnary(it, packPreUnary(it));
    }
    
    private static Statement packPreUnary(Pointer it) throws CompilerError
    {
        CodeFragment part = it.listValue();
        it.increase();
        if(part.isOperator())
        {
            if(it.end())
                throw new CompilerError("unexpected end of instruction");
            Operator prefix = (Operator) part;
            if(!prefix.isUnary())
                throw new CompilerError("Operator " + prefix + " cannot be a non unary prefix operator");
            part = packNextOperatorPart(it, (Operator) part);
            if(!part.isValidOperand())
                throw new CompilerError("Expected valid operand. But found: " + part);
            return Operation.unary(prefix, (Statement) part);
        }
        if(part == Command.DEF)
        {
            if(it.end())
                throw new CompilerError("Expected a valid identifier in new function.");
            CodeFragment identifier = it.listValue();
            it.increase();
            if(identifier.isIdentifier())
            {
                if(it.end() || it.listValue() != Operator.CALL)
                    throw new CompilerError("Expected a valid arguments list in new function.");
                it.increase();
            }
            else
            {
                if(identifier != Operator.CALL)
                    throw new CompilerError("Expected a valid arguments list in new function.");
                //it.increase();
                identifier = null;
            }
            if(it.end())
                throw new CompilerError("Expected a valid arguments list in new function.");
            CodeFragment args = it.listValue();
            it.increase();
            if(it.end())
                throw new CompilerError("Expected a valid arguments list in new function.");
            CodeFragment scope = it.listValue();
            it.increase();
            return Operation.newFunction(identifier, args, scope);
        }
        if(!part.isStatement())
            throw new CompilerError("Expected valid operand. But found: " + part);
        return (Statement) part;
    }
    
    private static Statement packPostUnary(Pointer it, Statement pre) throws CompilerError
    {
        if(it.end())
            return pre;
        CodeFragment part = it.listValue();
        
        if(part.is(CodeFragmentType.OPERATOR))
        {
            Operator sufix = (Operator) part;
            if(!sufix.isUnary())
                return pre;
            it.increase();
            if(sufix.hasRightToLeftOrder())
                throw new CompilerError("Operator " + sufix + " cannot be an unary sufix operator");
            if(!pre.isValidOperand())
                throw new CompilerError("Expected valid operand. But found: " + part);
            return packPostUnary(it, Operation.unary(sufix, pre));
        }
        return pre;
    }
    
    private static Operator findNextOperatorSymbol(CodeFragmentList list, int index)
    {
        int len = list.length();
        for(int i=index;i<len;i++)
            if(list.get(i).is(CodeFragmentType.OPERATOR))
                return list.get(i);
        return null;
    }
    
    private static Statement getSuperOperatorScope(Pointer it, Operator opBase) throws CompilerError
    {
        int start = it.value();
        for(;!it.end();it.increase())
        {
            if(!it.listValue().is(CodeFragmentType.OPERATOR))
                continue;
            Operator op = (Operator) it.listValue();
            if(opBase.comparePriority(op) > 0)
            {
                //it.decrease();
                return parse(it.list().subList(start, it.value() - start));
            }
        }
        return parse(it.list().subList(start));
    }
    
    private static Statement packOperation(Pointer it, Statement operand1) throws CompilerError
    {
        if(!it.listValue().is(CodeFragmentType.OPERATOR))
            throw new CompilerError("Expected a valid operator between operands. \"" + it.listValue() + "\"");
        Operator operator = (Operator) it.listValue();
        it.increase();
        Statement operation;
        
        if(operator.isTernaryConditional())
        {
            int start = it.value();
            int terOp = 0;
            for(;!it.end();it.increase())
            {
                CodeFragment c = it.listValue();
                if(c == Operator.TERNARY_CONDITIONAL)
                    terOp++;
                else if(c == Stopchar.TWO_POINTS)
                {
                    if(terOp == 0)
                        break;
                    terOp--;
                }
            }
            if(it.end())
                throw new CompilerError("Expected a : in ternary operator");
            Statement response1 = parse(it.list().subList(start, it.value()  - start));
            it.increase();
            Statement response2 = parse(it.list().subList(it.value()));
            it.finish();
            return Operation.ternaryConditional(operand1, response1, response2);
        }
        else if(operator.isBinary())
        {
            Statement operand2 = packNextOperatorPart(it, operator);
            operation = Operation.binary(operator, operand1, operand2);
        }
        else if(operator.isAssignment())
        {
            Statement operand2 = packNextOperatorPart(it, operator);
            operation = Operation.assignment(operator, operand1, operand2);
        }
        else if(operator.isCall())
        {
            if(it.end())
                throw new CompilerError("Expected a valid arguments list in call operator.");
            CodeFragment args = it.listValue();
            it.increase();
            operation = Operation.call(operand1, args);
        }
        else if(operator.isInvoke())
        {
            if(it.end())
                throw new CompilerError("Expected a valid identifier in call operator.");
            CodeFragment identifier = it.listValue();
            it.increase();
            if(it.end() || it.listValue() != Operator.CALL)
                throw new CompilerError("Expected a valid arguments list in call operator.");
            it.increase();
            if(it.end())
                throw new CompilerError("Expected a valid arguments list in call operator.");
            CodeFragment args = it.listValue();
            it.increase();
            operation = Operation.invoke(operand1, identifier, args);
        }
        else if(operator.isArrayGet())
        {
            Statement index = packNextOperatorPart(it, operator);
            operation = Operation.arrayGet(operand1, index);
        }
        else if(operator.isPropertyGet())
        {
            if(it.end())
                throw new CompilerError("Expected a valid identifier in property get operator.");
            CodeFragment identifier = it.listValue();
            it.increase();
            operation = Operation.propertyGet(operand1, identifier);
        }
        else throw new CompilerError("Invalid operator type: " + operator);
        
        
        if(it.end())
            return operation;
        return packOperation(it, operation);
    }
    
    private static Statement packNextOperatorPart(Pointer it, Operator operator) throws CompilerError
    {
        Operator nextOperator = findNextOperatorSymbol(it.list(), it.value());
        if(nextOperator != null && operator.comparePriority(nextOperator) >= 0)
            nextOperator = null;

        Statement operand2;
        if(nextOperator != null)
            operand2 = getSuperOperatorScope(it, operator);
        else operand2 = packPart(it);
        
        if(operator == Operator.PROPERTY_GET &&
                !operand2.is(CodeFragmentType.IDENTIFIER))
            throw new CompilerError("Expected a valid identifier in PropertyAccess operator: " + operand2);
        return operand2;
    }
    
    public static final Operation tryParseAssignmentNewFunction(CodeFragmentList list) throws CompilerError
    {
        Pointer it = list.counter();
        if(it.end())
            return null;
        CodeFragment identifier = it.listValue();
        it.increase();
        if(identifier.isIdentifier())
        {
            if(it.end() || it.listValue() != Operator.CALL)
                return null;
            it.increase();
        }
        else return null;
        if(it.end())
            throw new CompilerError("Expected a valid arguments list in function declaration.");
        CodeFragment args = it.listValue();
        it.increase();
        if(it.end())
            throw new CompilerError("Expected a valid arguments list in function declaration.");
        CodeFragment scope = it.listValue();
        it.increase();
        return Operation.newFunction(identifier, args, scope);
    }
}
