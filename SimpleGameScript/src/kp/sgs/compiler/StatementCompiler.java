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
import kp.sgs.compiler.parser.Arguments;
import kp.sgs.compiler.parser.DataType;
import kp.sgs.compiler.parser.Identifier;
import kp.sgs.compiler.parser.Literal;
import kp.sgs.compiler.parser.Mutable;
import kp.sgs.compiler.parser.Mutable.MutableEntry;
import kp.sgs.compiler.parser.Operation;
import kp.sgs.compiler.parser.Operator;
import kp.sgs.compiler.parser.Scope;
import kp.sgs.compiler.parser.Statement;
import kp.sgs.data.SGSImmutableValue;

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
            case OPERATION: resultType = compileOperation(scope, opcodes, statement, pop); break;
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
            case LIBRARY_ELEMENT:
                opcodes.append(Opcodes.libeLoad(id.getIndex()));
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
    
    public static final DataType compileOperation(NamespaceScope scope, OpcodeList opcodes, Statement operation, boolean pop) throws CompilerError
    {
        Operation op = (Operation) operation;
        switch(op.getOperator().getOperatorType())
        {
            case UNARY: return compileUnary(scope, opcodes, op.getOperator(), op.getOperand(0));
            case BINARY: return compileBinary(scope, opcodes, op.getOperator(), op.getOperand(0), op.getOperand(1));
            case ARRAY_GET: return compileArrayGet(scope, opcodes, op.getOperand(0), op.getOperand(1));
            case PROPERTY_GET: return compilePropertyGet(scope, opcodes, op.getOperand(0), op.getOperand(1));
            case CALL: return compileCall(scope, opcodes, op.getOperand(0), op.getOperand(1), pop);
            case INVOKE: return compileInvoke(scope, opcodes, op.getOperand(0), op.getOperand(1), op.getOperand(2), pop);
            case TERNARY_CONDITIONAL: return compileTernaryCondition(scope, opcodes, op.getOperand(0), op.getOperand(1), op.getOperand(2), pop);
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
        if(operator == Operator.INDIRECTION)
        {
            NamespaceIdentifier id;
            if(op.isIdentifier() && (id = scope.getIdentifier(op.toString())).isLibraryElement())
            {
                opcodes.append(Opcodes.libeRefGet(id.getIndex()));
                return DataType.ANY;
            }
            else
            {
                DataType type = compile(scope, opcodes, op, false);
                opcodes.append(Opcodes.REF_GET);
                return type;
            }
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
    
    private static DataType compileArrayGet(NamespaceScope scope, OpcodeList opcodes, Statement arrayOp, Statement indexOp) throws CompilerError
    {
        NamespaceIdentifier id;
        if(arrayOp.isIdentifier() && (id = scope.getIdentifier(arrayOp.toString())).isLibraryElement());
        else
        {
            id = null;
            compile(scope, opcodes, arrayOp, false);
        }
        if(indexOp.isLiteral())
        {
            SGSImmutableValue value = ((Literal) indexOp).getSGSValue();
            if(value.isInteger() && value.toInt() >= 0 && value.toInt() <= 0xff)
                if(id != null)
                    opcodes.append(Opcodes.libeArrayIntGet(id.getIndex(), value.toInt()));
                else opcodes.append(Opcodes.arrayIntGet(value.toInt()));
            else
            {
                compileLiteral(scope, opcodes, indexOp);
                if(id != null)
                    opcodes.append(Opcodes.libeArrayGet(id.getIndex()));
                else opcodes.append(Opcodes.ARRAY_GET);
            }
        }
        else
        {
            compile(scope, opcodes, indexOp, false);
            if(id != null)
                opcodes.append(Opcodes.libeArrayGet(id.getIndex()));
            else opcodes.append(Opcodes.ARRAY_GET);
        }
        return DataType.ANY;
    }
    
    private static DataType compilePropertyGet(NamespaceScope scope, OpcodeList opcodes, Statement objectOp, Identifier identifier) throws CompilerError
    {
        NamespaceIdentifier id;
        if(objectOp.isIdentifier() && (id = scope.getIdentifier(objectOp.toString())).isLibraryElement())
            opcodes.append(Opcodes.libePGet(id.getIndex(), scope.registerIdentifier(identifier.toString())));
        else
        {
            compile(scope, opcodes, objectOp, false);
            opcodes.append(Opcodes.objPGet(scope.registerIdentifier(identifier.toString())));
        }
        return DataType.ANY;
    }
    
    private static DataType compileCall(NamespaceScope scope, OpcodeList opcodes, Statement funcOp, Arguments args, boolean popReturn) throws CompilerError
    {
        if(funcOp.isIdentifier())
        {
            NamespaceIdentifier id = scope.getIdentifier(funcOp.toString());
            if(id.isFunction() || id.isLibraryElement())
            {
                int count = compileArguments(scope, opcodes, args);
                opcodes.append(Opcodes.localCall(id.getIndex(), count, popReturn));
                return id.getReturnType();
            }
            else if(id.isLibraryElement())
            {
                int count = compileArguments(scope, opcodes, args);
                opcodes.append(Opcodes.libeCall(id.getIndex(), count, popReturn));
                return id.getReturnType();
            }
        }
        compile(scope, opcodes, funcOp, false);
        int count = compileArguments(scope, opcodes, args);
        opcodes.append(Opcodes.call(count, popReturn));
        return DataType.ANY;
    }
    
    private static DataType compileInvoke(NamespaceScope scope, OpcodeList opcodes, Statement objOp, Identifier property, Arguments args, boolean popReturn) throws CompilerError
    {
        compile(scope, opcodes, objOp, false);
        int count = compileArguments(scope, opcodes, args);
        NamespaceIdentifier id = scope.getIdentifier(property.toString());
        opcodes.append(Opcodes.invoke(id.getIndex(), count, popReturn));
        return DataType.ANY;
    }
    
    private static int compileArguments(NamespaceScope scope, OpcodeList opcodes, Arguments args) throws CompilerError
    {
        for(Statement arg : args)
            compile(scope, opcodes, arg, false);
        return args.getArgumentCount();
    }
    
    private static DataType compileTernaryCondition(NamespaceScope scope, OpcodeList opcodes, Statement condOp, Statement trueOp, Statement falseOp, boolean pop) throws CompilerError
    {
        OpcodeLocation falseJump = compileDefaultIf(scope, opcodes, falseOp);
        DataType trueType = compile(scope, opcodes, trueOp, pop);
        OpcodeLocation endJump = opcodes.append(Opcodes.goTo());
        opcodes.setJumpOpcodeLocationToBottom(falseJump);
        DataType falseType = compile(scope, opcodes, trueOp, pop);
        opcodes.setJumpOpcodeLocationToBottom(endJump);
        if(!pop)
            scope.getRuntimeStack().pop();
        return DataType.canUseImplicitCast(trueType, falseType)
                ? trueType : DataType.canUseImplicitCast(falseType, trueType)
                ? falseType : DataType.ANY;
    }
    
    private static DataType compileNewFunction(NamespaceScope scope, OpcodeList opcodes, Operation op, boolean pop) throws CompilerError
    {
        if(op.getOperandCount() == 2)
            return compileNewFunction(scope, opcodes, null, op.getOperand(0), op.getOperand(1), pop);
        return compileNewFunction(scope, opcodes, op.getOperand(0), op.getOperand(1), op.getOperand(2), pop);
    }
    public static final DataType compileNewFunction(NamespaceScope scope, OpcodeList opcodes, Identifier name, Arguments pars, Scope funcScope, boolean pop) throws CompilerError
    {
        
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
