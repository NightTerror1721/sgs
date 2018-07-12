/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler;

import kp.sgs.compiler.ScriptBuilder.Constant;
import kp.sgs.compiler.ScriptBuilder.NamespaceIdentifier;
import kp.sgs.compiler.ScriptBuilder.NamespaceScope;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.opcode.OpcodeList;
import kp.sgs.compiler.opcode.OpcodeList.OpcodeLocation;
import kp.sgs.compiler.opcode.Opcodes;
import kp.sgs.compiler.parser.DataType;
import kp.sgs.compiler.parser.Literal;
import kp.sgs.compiler.parser.Mutable;
import kp.sgs.compiler.parser.Mutable.MutableEntry;
import kp.sgs.compiler.parser.Operation;
import kp.sgs.compiler.parser.Operator;
import kp.sgs.compiler.parser.Statement;

/**
 *
 * @author Asus
 */
public final class StatementCompiler
{
    private StatementCompiler() {}
    
    public static final DataType compile(NamespaceScope scope, OpcodeList opcodes, Statement statement, boolean pop) throws CompilerError
    {
        DataType resultType;
        switch(statement.getFragmentType())
        {
            default: throw new IllegalStateException();
            case IDENTIFIER: resultType = compileIdentifier(scope, opcodes, statement); break;
            case LITERAL: resultType = compileLiteral(scope, opcodes, statement); break;
            case MUTABLE: resultType = compileMutable(scope, opcodes, statement); break;
            case OPERATION: resultType = compileOperation(scope, opcodes, statement); break;
        }
        if(pop)
            opcodes.append(Opcodes.POP);
        return resultType;
    }
    
    public static final DataType compileIdentifier(NamespaceScope scope, OpcodeList opcodes, Statement identifier) throws CompilerError
    {
        NamespaceIdentifier id = scope.getIdentifier(identifier.toString());
        switch(id.getIdentifierType())
        {
            case LOCAL_VARIABLE:
                opcodes.append(Opcodes.loadVar(id.getIndex()));
                break;
            case GLOBAL_VARIABLE:
                opcodes.append(Opcodes.loadGlobal(id.getIndex()));
                break;
            case ARGUMENT:
                opcodes.append(Opcodes.loadArg(id.getIndex()));
                break;
            case CONSTANT:
                opcodes.append(Opcodes.loadConst(id.getIndex()));
                break;
            case FUNCTION:
                opcodes.append(Opcodes.loadFunction(id.getIndex()));
                break;
            default: throw new IllegalStateException();
        }
        return id.getType();
    }
    
    public static final DataType compileLiteral(NamespaceScope scope, OpcodeList opcodes, Statement literal) throws CompilerError
    {
        Literal lit = (Literal) literal;
        Constant c = scope.registerLiteral(lit);
        opcodes.append(Opcodes.loadConst(c.getIndex()));
        return c.getType();
    }
    
    public static final DataType compileMutable(NamespaceScope scope, OpcodeList opcodes, Statement mutable) throws CompilerError
    {
        Mutable mut = (Mutable) mutable;
        int len = mut.getEntryCount();
        if(mut.isArray())
        {
            compileLiteral(scope, opcodes, Literal.valueOf(len));
            opcodes.append(Opcodes.ARRAY_NEW);
            for(int i=0;i<len;i++)
            {
                opcodes.append(Opcodes.DUP);
                if(i > 0xff)
                    compileLiteral(scope, opcodes, Literal.valueOf(i));
                compile(scope, opcodes, mut.getEntry(i).getValue(), false);
                opcodes.append(i > 0xff ? Opcodes.ARRAY_SET : Opcodes.arrayIntSet(i));
            }
            return DataType.ARRAY;
        }
        else // Object
        {
            opcodes.append(Opcodes.OBJ_NEW);
            for(int i=0;i<len;i++)
            {
                opcodes.append(Opcodes.DUP);
                MutableEntry e = mut.getEntry(i);
                int nameIndex = scope.registerIdentifier(e.getKey());
                compile(scope, opcodes, e.getValue(), false);
                opcodes.append(Opcodes.objPSet(nameIndex));
            }
            return DataType.OBJECT;
        }
    }
    
    public static final DataType compileOperation(NamespaceScope scope, OpcodeList opcodes, Statement operation) throws CompilerError
    {
        Operation op = (Operation) operation;
        switch(op.getOperator().getOperatorType())
        {
            case UNARY: return compileUnary(scope, opcodes, op.getOperator(), op.getOperand(0));
            case BINARY: return compileBinary(scope, opcodes, op.getOperator(), op.getOperand(0), op.getOperand(1));
        }
    }
    
    private static DataType compileUnary(NamespaceScope scope, OpcodeList opcodes, Operator operator, Statement op) throws CompilerError
    {
        if(operator == Operator.ADDRESS_OF)
        {
            if(!op.isIdentifier())
                throw new CompilerError("Expected valid local variable to \"&\" operator.");
            NamespaceIdentifier id = scope.getIdentifier(op.toString());
            if(!id.isLocalVariable())
                throw new CompilerError("Expected valid local variable to \"&\" operator.");
            opcodes.append(Opcodes.refLocal(id.getIndex()));
            return DataType.ANY;
        }
        DataType type = compile(scope, opcodes, op, false);
        switch(operator.getSymbol())
        {
            case SUFIX_INCREMENT:
                opcodes.append(Opcodes.INC, Opcodes.DUP);
                store(scope, opcodes, op, type);
                return type;
            case SUFIX_DECREMENT:
                opcodes.append(Opcodes.DEC, Opcodes.DUP);
                store(scope, opcodes, op, type);
                return type;
            case PREFIX_INCREMENT:
                opcodes.append(Opcodes.DUP, Opcodes.INC);
                store(scope, opcodes, op, type);
                return type;
            case PREFIX_DECREMENT:
                opcodes.append(Opcodes.DUP, Opcodes.DEC);
                store(scope, opcodes, op, type);
                return type;
            case UNARY_PLUS:
                return type;
            case UNARY_MINUS:
                opcodes.append(Opcodes.NEG);
                return type;
            case NOT:
                opcodes.append(Opcodes.INV);
                return type;
            case BITWISE_NOT:
                opcodes.append(Opcodes.BW_NOT);
                return type;
            case INDIRECTION:
                opcodes.append(Opcodes.REF_GET);
                return type;
            case CAST_INT:
                opcodes.append(Opcodes.CAST_INT);
                return DataType.INTEGER;
            case CAST_FLOAT:
                opcodes.append(Opcodes.CAST_FLOAT);
                return DataType.FLOAT;
            case CAST_STRING:
                opcodes.append(Opcodes.CAST_STRING);
                return DataType.STRING;
            case CAST_ARRAY:
                opcodes.append(Opcodes.CAST_ARRAY);
                return DataType.ARRAY;
            case CAST_OBJECT:
                opcodes.append(Opcodes.CAST_OBJECT);
                return DataType.OBJECT;
            case LENGTH:
                opcodes.append(Opcodes.LEN);
                return DataType.INTEGER;
            case ISDEF:
                opcodes.append(Opcodes.ISDEF);
                return DataType.INTEGER;
            case ISUNDEF:
                opcodes.append(Opcodes.ISUNDEF);
                return DataType.INTEGER;
            case TYPEID:
                opcodes.append(Opcodes.TYPEID);
                return DataType.INTEGER;
            case ITERATOR:
                opcodes.append(Opcodes.ITERATOR);
                return DataType.ANY;
            default: throw new IllegalStateException();
        }
    }
    
    private static DataType compileBinary(NamespaceScope scope, OpcodeList opcodes, Operator operator, Statement left, Statement right) throws CompilerError
    {
        if(operator == Operator.LOGICAL_AND || operator == Operator.LOGICAL_OR)
        {
            OpcodeLocation ifJump = compileCondition(scope, opcodes, left);
            if(operator == Operator.LOGICAL_AND)
            {
                OpcodeLocation gotoJumo = opcodes.append(Opcodes.goTo());
                opcodes.setJumpOpcodeLocationToBottom(ifJump);
                ifJump = compileCondition(scope, opcodes, right);
                compileLiteral(scope, opcodes, Literal.FALSE);
                OpcodeLocation lastGoto = opcodes.append(Opcodes.goTo());
                opcodes.setJumpOpcodeLocationTarget(gotoJumo, ifJump.next());
                compileLiteral(scope, opcodes, Literal.TRUE);
                opcodes.setJumpOpcodeLocationToBottom(lastGoto);
            }
            else
            {
                OpcodeLocation rightJump = compileCondition(scope, opcodes, right);
                compileLiteral(scope, opcodes, Literal.FALSE);
                OpcodeLocation lastGoto = opcodes.append(Opcodes.goTo());
                opcodes.setJumpOpcodeLocationToBottom(ifJump);
                opcodes.setJumpOpcodeLocationToBottom(rightJump);
                compileLiteral(scope, opcodes, Literal.TRUE);
                opcodes.setJumpOpcodeLocationToBottom(lastGoto);
                
            }
            scope.getRuntimeStack().pop();
            return DataType.INTEGER;
        }
        DataType tleft = compile(scope, opcodes, left, false);
        DataType tright = compile(scope, opcodes, right, false);
        DataType type = binaryDataCheck(scope, opcodes, tleft, tright);
        
        switch(operator.getSymbol())
        {
            case MULTIPLICATION:
                opcodes.append(Opcodes.MUL);
                return type;
            case DIVISION:
                opcodes.append(Opcodes.DIV);
                return type;
            case REMAINDER:
                opcodes.append(Opcodes.REM);
                return type;
            case ADDITION:
                opcodes.append(Opcodes.ADD);
                return type;
            case SUBTRACTION:
                opcodes.append(Opcodes.SUB);
                return type;
            case BITWISE_LEFT_SHIFT:
                opcodes.append(Opcodes.BW_SFH_L);
                return type;
            case BITWISE_RIGHT_SHIFT:
                opcodes.append(Opcodes.BW_SFH_R);
                return type;
            case GREATER_THAN:
                opcodes.append(Opcodes.GR);
                return type;
            case SMALLER_THAN:
                opcodes.append(Opcodes.SM);
                return type;
            case GREATER_EQUALS_THAN:
                opcodes.append(Opcodes.GREQ);
                return type;
            case SMALLER_EQUALS_THAN:
                opcodes.append(Opcodes.SMEQ);
                return type;
            case EQUALS:
                opcodes.append(Opcodes.EQ);
                return type;
            case NOT_EQUALS:
                opcodes.append(Opcodes.NEQ);
                return type;
            case TYPED_EQUALS:
                opcodes.append(Opcodes.TEQ);
                return type;
            case TYPED_NOT_EQUALS:
                opcodes.append(Opcodes.TNEQ);
                return type;
            case BITWISE_AND:
                opcodes.append(Opcodes.BW_AND);
                return type;
            case BITWISE_XOR:
                opcodes.append(Opcodes.BW_XOR);
                return type;
            case BITWISE_OR:
                opcodes.append(Opcodes.BW_OR);
                return type;
            default: throw new IllegalStateException();
        }
    }
    private static DataType binaryDataCheck(NamespaceScope scope, OpcodeList opcodes, DataType tleft, DataType tright) throws CompilerError
    {
        if(DataType.canUseImplicitCast(tleft, tright))
            return tleft;
        if(tleft == DataType.INTEGER && tright == DataType.FLOAT)
            return DataType.FLOAT;
        throw new CompilerError("Cannot cast " + tleft + " to " + tright);
    }
    
    private static DataType compileAssignment(NamespaceScope scope, OpcodeList opcodes, Operator operator, Statement dest, Statement source)
    {
        
    }
    
    private static void store(NamespaceScope scope, OpcodeList opcodes, Statement dest, DataType sourceType)
    {
        
    }
    
    public static final OpcodeLocation compileCondition(NamespaceScope scope, OpcodeList opcodes, Statement statement) throws CompilerError
    {
        Operation op;
        if(statement.isOperation() && (op = (Operation) statement).getOperatorSymbol().isConditional())
        {
            switch(op.getOperatorSymbol())
            {
                case NOT:
                    compile(scope, opcodes, op.getOperand(0), false);
                    return opcodes.append(Opcodes.ifInv());
                case ISDEF:
                    compile(scope, opcodes, op.getOperand(0), false);
                    return opcodes.append(Opcodes.ifDef());
                case ISUNDEF:
                    compile(scope, opcodes, op.getOperand(0), false);
                    return opcodes.append(Opcodes.ifUndef());
                case GREATER_THAN:
                    compile(scope, opcodes, op.getOperand(0), false);
                    compile(scope, opcodes, op.getOperand(1), false);
                    return opcodes.append(Opcodes.ifGr());
                case SMALLER_THAN:
                    compile(scope, opcodes, op.getOperand(0), false);
                    compile(scope, opcodes, op.getOperand(1), false);
                    return opcodes.append(Opcodes.ifSm());
                case GREATER_EQUALS_THAN:
                    compile(scope, opcodes, op.getOperand(0), false);
                    compile(scope, opcodes, op.getOperand(1), false);
                    return opcodes.append(Opcodes.ifGrEq());
                case SMALLER_EQUALS_THAN:
                    compile(scope, opcodes, op.getOperand(0), false);
                    compile(scope, opcodes, op.getOperand(1), false);
                    return opcodes.append(Opcodes.ifSmEq());
                case EQUALS:
                    compile(scope, opcodes, op.getOperand(0), false);
                    compile(scope, opcodes, op.getOperand(1), false);
                    return opcodes.append(Opcodes.ifEq());
                case NOT_EQUALS:
                    compile(scope, opcodes, op.getOperand(0), false);
                    compile(scope, opcodes, op.getOperand(1), false);
                    return opcodes.append(Opcodes.ifNEq());
                case TYPED_EQUALS:
                    compile(scope, opcodes, op.getOperand(0), false);
                    compile(scope, opcodes, op.getOperand(1), false);
                    return opcodes.append(Opcodes.ifTEq());
                case TYPED_NOT_EQUALS:
                    compile(scope, opcodes, op.getOperand(0), false);
                    compile(scope, opcodes, op.getOperand(1), false);
                    return opcodes.append(Opcodes.ifTNEq());
                default: throw new IllegalStateException();
            }
        }
        else
        {
            compile(scope, opcodes, statement, false);
            return opcodes.append(Opcodes.IF());
        }
    }
    public static final OpcodeLocation compileDefaultIf(NamespaceScope scope, OpcodeList opcodes, Statement statement) throws CompilerError
    {
        OpcodeLocation loc = compileCondition(scope, opcodes, statement);
        OpcodeLocation locJump = opcodes.append(Opcodes.goTo());
        opcodes.setJumpOpcodeLocationToBottom(loc);
        return locJump;
    }
}
