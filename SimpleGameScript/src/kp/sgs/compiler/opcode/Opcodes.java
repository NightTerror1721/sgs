/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.opcode;

import kp.sgs.SGSConstants.Instruction;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.opcode.OpcodeList.OpcodeLocation;

/**
 *
 * @author Asus
 */
public final class Opcodes
{
    private Opcodes() {}
    
    private static Opcode opcode(int opcode, int stackPush, int stackPop) { return new Opcode(opcode, 1, stackPush, stackPop); }
    private static Opcode opcode(int opcode, int stackPush, int stackPop, byte idx0) { return new MonoOpcode(opcode, stackPush, stackPop, idx0); }
    private static Opcode opcode(int opcode, int stackPush, int stackPop, byte idx0, byte idx1) { return new BiOpcode(opcode, stackPush, stackPop, idx0, idx1); }
    private static Opcode opcode(int opcode, int stackPush, int stackPop, byte idx0, byte idx1, byte idx2) { return new TriOpcode(opcode, stackPush, stackPop, idx0, idx1, idx2); }
    private static Opcode opcode(int opcode, int stackPush, int stackPop, byte idx0, byte idx1, byte idx2, byte idx3) {
       return new TetraOpcode(opcode, stackPush, stackPop, idx0, idx1, idx2, idx3);
    }
    
    public static final Opcode NOP = opcode(Instruction.NOP, 0, 0); 
    
    public static final Opcode loadConst(int index) throws CompilerError
    {
        if(index > 0xffff)
            throw new CompilerError("constant index overflow");
        if(index > 0xff)
            return opcode(Instruction.LOAD_CONST16, 1, 0, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
        return opcode(Instruction.LOAD_CONST, 1, 0, (byte) (index & 0xff));
    }
    public static final Opcode loadVar(int index) throws CompilerError
    {
        if(index > 0xff)
            throw new CompilerError("variable index overflow");
        return opcode(Instruction.LOAD_VAR, 1, 0, (byte) (index & 0xff));
    }
    public static final Opcode loadArg(int index) throws CompilerError
    {
        if(index > 0xff)
            throw new CompilerError("argument index overflow");
        return opcode(Instruction.LOAD_ARG, 1, 0, (byte) (index & 0xff));
    }
    public static final Opcode loadFunction(int index) throws CompilerError
    {
        if(index > 0xffff)
            throw new CompilerError("function index overflow");
        if(index > 0xff)
            return opcode(Instruction.LOAD_FUNCTION16, 1, 0, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
        return opcode(Instruction.LOAD_FUNCTION, 1, 0, (byte) (index & 0xff));
    }
    public static final Opcode loadClosure(int index, int parameterCount) throws CompilerError
    {
        if(index > 0xffff)
            throw new CompilerError("closure index overflow");
        if(parameterCount > 0xff)
            throw new CompilerError("parameter count overflow");
        if(index > 0xff)
            return opcode(Instruction.LOAD_CLOSURE16, 1, 0, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff), (byte) (parameterCount & 0xff));
        return opcode(Instruction.LOAD_CLOSURE, 1, 0, (byte) (index & 0xff), (byte) 0, (byte) (parameterCount & 0xff));
    }
    public static final Opcode loadGlobal(int index) throws CompilerError
    {
        if(index > 0xffff)
            throw new CompilerError("global index overflow");
        if(index > 0xff)
            return opcode(Instruction.LOAD_GLOBAL16, 1, 0, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
        return opcode(Instruction.LOAD_GLOBAL, 1, 0, (byte) (index & 0xff));
    }
    public static final Opcode LOAD_UNDEF = opcode(Instruction.LOAD_UNDEF, 1, 0);
    
    public static final Opcode storeVar(int index) throws CompilerError
    {
        if(index > 0xff)
            throw new CompilerError("variable index overflow");
        return opcode(Instruction.STORE_VAR, 0, 1, (byte) (index & 0xff));
    }
    public static final Opcode storeArg(int index) throws CompilerError
    {
        if(index > 0xff)
            throw new CompilerError("argument index overflow");
        return opcode(Instruction.STORE_ARG, 0, 1, (byte) (index & 0xff));
    }
    public static final Opcode storeGlobal(int index) throws CompilerError
    {
        if(index > 0xffff)
            throw new CompilerError("global index overflow");
        if(index > 0xff)
            return opcode(Instruction.STORE_GLOBAL16, 0, 1, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
        return opcode(Instruction.STORE_GLOBAL, 0, 1, (byte) (index & 0xff));
    }
    public static final Opcode storeVarUndef(int index) throws CompilerError
    {
        if(index > 0xff)
            throw new CompilerError("variable index overflow");
        return opcode(Instruction.STORE_VAR_UNDEF, 0, 0, (byte) (index & 0xff));
    }
    
    public static final Opcode ARRAY_NEW = opcode(Instruction.ARRAY_NEW, 1, 1);
    public static final Opcode ARRAY_GET = opcode(Instruction.ARRAY_GET, 1, 2);
    public static final Opcode ARRAY_SET = opcode(Instruction.ARRAY_SET, 1, 3);
    public static final Opcode arrayIntGet(int index) throws CompilerError
    {
        if(index > 0xff)
            throw new CompilerError("array int index overflow");
        return opcode(Instruction.ARRAY_INT_GET, 1, 1, (byte) (index & 0xff));
    }
    public static final Opcode arrayIntSet(int index) throws CompilerError
    {
        if(index > 0xff)
            throw new CompilerError("array int index overflow");
        return opcode(Instruction.ARRAY_INT_SET, 1, 2, (byte) (index & 0xff));
    }
    
    public static final Opcode OBJ_NEW = opcode(Instruction.OBJ_NEW, 1, 0);
    public static final Opcode objPGet(int index) throws CompilerError
    {
        if(index > 0xffff)
            throw new CompilerError("identifier index overflow");
        if(index > 0xff)
            return opcode(Instruction.OBJ_PGET16, 1, 1, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
        return opcode(Instruction.OBJ_PGET, 1, 1, (byte) (index & 0xff));
    }
    public static final Opcode objPSet(int index) throws CompilerError
    {
        if(index > 0xffff)
            throw new CompilerError("identifier index overflow");
        if(index > 0xff)
            return opcode(Instruction.OBJ_PSET16, 1, 2, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
        return opcode(Instruction.OBJ_PSET, 1, 2, (byte) (index & 0xff));
    }
    
    public static final Opcode REF_HEAP = opcode(Instruction.REF_HEAP, 1, 0);
    public static final Opcode refLocal(int index) throws CompilerError
    {
        if(index > 0xff)
            throw new CompilerError("variable index overflow");
        return opcode(Instruction.REF_LOCAL, 1, 0, (byte) (index & 0xff));
    }
    public static final Opcode REF_GET = opcode(Instruction.REF_GET, 1, 1);
    public static final Opcode REF_SET = opcode(Instruction.REF_SET, 1, 2);
    
    public static final Opcode POP = opcode(Instruction.POP, 0, 1);
    public static final Opcode SWAP = opcode(Instruction.SWAP, 0, 0);
    public static final Opcode SWAP2 = opcode(Instruction.SWAP2, 0, 0);
    public static final Opcode DUP = opcode(Instruction.DUP, 1, 0);
    
    public static final Opcode CAST_INT = opcode(Instruction.CAST_INT, 1, 1);
    public static final Opcode CAST_FLOAT = opcode(Instruction.CAST_FLOAT, 1, 1);
    public static final Opcode CAST_STRING = opcode(Instruction.CAST_STRING, 1, 1);
    public static final Opcode CAST_ARRAY = opcode(Instruction.CAST_ARRAY, 1, 1);
    public static final Opcode CAST_OBJECT = opcode(Instruction.CAST_OBJECT, 1, 1);
    
    public static final JumpOpcode goTo(OpcodeLocation target) throws CompilerError
    {
        return new JumpOpcode(Instruction.GOTO, Instruction.GOTO16, 0, target);
    }
    public static final JumpOpcode goTo() throws CompilerError { return goTo(null); }
    
    public static final Opcode RETURN_NONE = opcode(Instruction.RETURN_NONE, 0, 0);
    public static final Opcode RETURN = opcode(Instruction.RETURN, 0, 1);
    
    public static final Opcode ADD = opcode(Instruction.ADD, 1, 2);
    public static final Opcode SUB = opcode(Instruction.SUB, 1, 2);
    public static final Opcode MUL = opcode(Instruction.MUL, 1, 2);
    public static final Opcode DIV = opcode(Instruction.DIV, 1, 2);
    public static final Opcode REM = opcode(Instruction.REM, 1, 2);
    public static final Opcode NEG = opcode(Instruction.NEG, 1, 1);
    public static final Opcode INC = opcode(Instruction.INC, 1, 1);
    public static final Opcode DEC = opcode(Instruction.DEC, 1, 1);
    
    public static final Opcode BW_SFH_L = opcode(Instruction.ADD, 1, 2);
    public static final Opcode BW_SFH_R = opcode(Instruction.SUB, 1, 2);
    public static final Opcode BW_AND = opcode(Instruction.MUL, 1, 2);
    public static final Opcode BW_OR = opcode(Instruction.DIV, 1, 2);
    public static final Opcode BW_XOR = opcode(Instruction.REM, 1, 2);
    public static final Opcode BW_NOT = opcode(Instruction.NEG, 1, 1);
    
    public static final Opcode EQ = opcode(Instruction.EQ, 1, 2);
    public static final Opcode NEQ = opcode(Instruction.NEQ, 1, 2);
    public static final Opcode TEQ = opcode(Instruction.TEQ, 1, 2);
    public static final Opcode TNEQ = opcode(Instruction.TNEQ, 1, 2);
    public static final Opcode GR = opcode(Instruction.GR, 1, 2);
    public static final Opcode SM = opcode(Instruction.SM, 1, 2);
    public static final Opcode GREQ = opcode(Instruction.GREQ, 1, 2);
    public static final Opcode SMEQ = opcode(Instruction.SMEQ, 1, 2);
    public static final Opcode ISDEF = opcode(Instruction.ISDEF, 1, 1);
    public static final Opcode ISUNDEF = opcode(Instruction.ISUNDEF, 1, 1);
    public static final Opcode INV = opcode(Instruction.INV, 1, 1);
    public static final Opcode CONCAT = opcode(Instruction.CONCAT, 1, 2);
    public static final Opcode LEN = opcode(Instruction.LEN, 1, 1);
    public static final Opcode TYPEID = opcode(Instruction.TYPEID, 1, 1);
    public static final Opcode ITERATOR = opcode(Instruction.ITERATOR, 1, 1);
    
    public static final JumpOpcode IF(OpcodeLocation target) throws CompilerError
    {
        return new JumpOpcode(Instruction.IF, Instruction.IF16, 1, target);
    }
    public static final JumpOpcode IF() throws CompilerError { return IF(null); }
    
    public static final JumpOpcode ifEq(OpcodeLocation target) throws CompilerError
    {
        return new JumpOpcode(Instruction.IF_EQ, Instruction.IF_EQ16, 2, target);
    }
    public static final JumpOpcode ifEq() throws CompilerError { return ifEq(null); }
    public static final JumpOpcode ifNEq(OpcodeLocation target) throws CompilerError
    {
        return new JumpOpcode(Instruction.IF_NEQ, Instruction.IF_NEQ16, 2, target);
    }
    public static final JumpOpcode ifNEq() throws CompilerError { return ifNEq(null); }
    public static final JumpOpcode ifTEq(OpcodeLocation target) throws CompilerError
    {
        return new JumpOpcode(Instruction.IF_TEQ, Instruction.IF_TEQ16, 2, target);
    }
    public static final JumpOpcode ifTEq() throws CompilerError { return ifTEq(null); }
    public static final JumpOpcode ifTNEq(OpcodeLocation target) throws CompilerError
    {
        return new JumpOpcode(Instruction.IF_TNEQ, Instruction.IF_TNEQ16, 2, target);
    }
    public static final JumpOpcode ifTNEq() throws CompilerError { return ifTNEq(null); }
    public static final JumpOpcode ifGr(OpcodeLocation target) throws CompilerError
    {
        return new JumpOpcode(Instruction.IF_GR, Instruction.IF_GR16, 2, target);
    }
    public static final JumpOpcode ifGr() throws CompilerError { return ifGr(null); }
    public static final JumpOpcode ifSm(OpcodeLocation target) throws CompilerError
    {
        return new JumpOpcode(Instruction.IF_SM, Instruction.IF_SM16, 2, target);
    }
    public static final JumpOpcode ifSm() throws CompilerError { return ifSm(null); }
    public static final JumpOpcode ifGrEq(OpcodeLocation target) throws CompilerError
    {
        return new JumpOpcode(Instruction.IF_GREQ, Instruction.IF_GREQ16, 2, target);
    }
    public static final JumpOpcode ifGrEq() throws CompilerError { return ifGrEq(null); }
    public static final JumpOpcode ifSmEq(OpcodeLocation target) throws CompilerError
    {
        return new JumpOpcode(Instruction.IF_SMEQ, Instruction.IF_SMEQ16, 2, target);
    }
    public static final JumpOpcode ifSmEq() throws CompilerError { return ifSmEq(null); }
    public static final JumpOpcode ifInv(OpcodeLocation target) throws CompilerError
    {
        return new JumpOpcode(Instruction.IF_INV, Instruction.IF_INV16, 1, target);
    }
    public static final JumpOpcode ifInv() throws CompilerError { return ifDef(null); }
    
    public static final JumpOpcode ifDef(OpcodeLocation target) throws CompilerError
    {
        return new JumpOpcode(Instruction.IF_DEF, Instruction.IF_DEF16, 1, target);
    }
    public static final JumpOpcode ifDef() throws CompilerError { return ifDef(null); }
    public static final JumpOpcode ifUndef(OpcodeLocation target) throws CompilerError
    {
        return new JumpOpcode(Instruction.IF_UNDEF, Instruction.IF_UNDEF16, 1, target);
    }
    public static final JumpOpcode ifUndef() throws CompilerError { return ifUndef(null); }
    
    public static final Opcode localCall(int index, int argumentCount, boolean popReturn) throws CompilerError
    {
        if(index > 0xffff)
            throw new CompilerError("identifier index overflow");
        if(argumentCount > 0xff)
            throw new CompilerError("argument count overflow");
        switch((index > 0xff ? 0x1 : 0x0) | (argumentCount > 0 ? 0x2 : 0x0) | (popReturn ? 0x4 : 0x0))
        {
            default: throw new IllegalStateException();
            case 0x0: return opcode(Instruction.LOCAL_VCALL_NA, 0, 0, (byte) (index & 0xff));
            case 0x1: return opcode(Instruction.LOCAL_VCALL_NA16, 0, 0, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
            case 0x2: return opcode(Instruction.LOCAL_VCALL, 0, argumentCount, (byte) argumentCount, (byte) (index & 0xff));
            case 0x4: return opcode(Instruction.LOCAL_CALL_NA, 1, 0, (byte) (index & 0xff));
            case 0x1 | 0x2: return opcode(Instruction.LOCAL_VCALL16, 0, argumentCount, (byte) argumentCount, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
            case 0x1 | 0x4: return opcode(Instruction.LOCAL_CALL_NA16, 1, 0, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
            case 0x2 | 0x4: return opcode(Instruction.LOCAL_CALL, 1, argumentCount, (byte) argumentCount, (byte) (index & 0xff));
            case 0x1 | 0x2 | 0x4: return opcode(Instruction.LOCAL_CALL16, 1, argumentCount, (byte) argumentCount, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
        }
    }
    
    public static final Opcode call(int argumentCount, boolean popReturn) throws CompilerError
    {
        if(argumentCount > 0xff)
            throw new CompilerError("argument count overflow");
        switch((argumentCount > 0 ? 0x1 : 0x0) | (popReturn ? 0x2 : 0x0))
        {
            default: throw new IllegalStateException();
            case 0x0: return opcode(Instruction.VCALL_NA, 0, 1);
            case 0x1: return opcode(Instruction.VCALL, 0, (argumentCount + 1), (byte) argumentCount);
            case 0x2: return opcode(Instruction.CALL_NA, 1, 1);
            case 0x1 | 0x2: return opcode(Instruction.CALL, 1, (argumentCount + 1), (byte) argumentCount);
        }
    }
    
    public static final Opcode invoke(int index, int argumentCount, boolean popReturn) throws CompilerError
    {
        if(index > 0xffff)
            throw new CompilerError("identifier index overflow");
        if(argumentCount > 0xff)
            throw new CompilerError("argument count overflow");
        switch((index > 0xff ? 0x1 : 0x0) | (argumentCount > 0 ? 0x2 : 0x0) | (popReturn ? 0x4 : 0x0))
        {
            default: throw new IllegalStateException();
            case 0x0: return opcode(Instruction.VINVOKE_NA, 0, 1, (byte) (index & 0xff));
            case 0x1: return opcode(Instruction.VINVOKE_NA16, 0, 1, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
            case 0x2: return opcode(Instruction.VINVOKE, 0, (argumentCount + 1), (byte) argumentCount, (byte) (index & 0xff));
            case 0x4: return opcode(Instruction.INVOKE_NA, 1, 1, (byte) (index & 0xff));
            case 0x1 | 0x2: return opcode(Instruction.VINVOKE16, 0, (argumentCount + 1), (byte) argumentCount, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
            case 0x1 | 0x4: return opcode(Instruction.INVOKE_NA16, 1, 1, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
            case 0x2 | 0x4: return opcode(Instruction.INVOKE, 1, (argumentCount + 1), (byte) argumentCount, (byte) (index & 0xff));
            case 0x1 | 0x2 | 0x4: return opcode(Instruction.INVOKE16, 1, (argumentCount + 1), (byte) argumentCount, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
        }
    }
    
    public static final Opcode libeLoad(int index) throws CompilerError
    {
        if(index > 0xffff)
            throw new CompilerError("Library Element index overflow");
        if(index > 0xff)
            return opcode(Instruction.LIBE_LOAD16, 1, 0, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
        return opcode(Instruction.LIBE_LOAD, 1, 0, (byte) (index & 0xff));
    }
    
    public static final Opcode libeArrayGet(int elementIndex) throws CompilerError
    {
        if(elementIndex > 0xffff)
            throw new CompilerError("Library Element index overflow");
        if(elementIndex > 0xff)
            return opcode(Instruction.LIBE_A_GET16, 1, 1, (byte) (elementIndex & 0xff), (byte) ((elementIndex >>> 8) & 0xff));
        return opcode(Instruction.LIBE_A_GET, 1, 1, (byte) (elementIndex & 0xff));
    }
    
    public static final Opcode libeArrayIntGet(int elementIndex, int arrayIntIndex) throws CompilerError
    {
        if(elementIndex > 0xffff)
            throw new CompilerError("Library Element index overflow");
        if(arrayIntIndex > 0xff)
            throw new CompilerError("array int index overflow");
        if(elementIndex > 0xff)
            return opcode(Instruction.LIBE_AINT_GET16, 1, 0, (byte) (elementIndex & 0xff), (byte) ((elementIndex >>> 8) & 0xff), (byte) (arrayIntIndex & 0xff));
        return opcode(Instruction.LIBE_AINT_GET, 1, 0, (byte) (elementIndex & 0xff), (byte) (arrayIntIndex & 0xff));
    }
    
    public static final Opcode libePGet(int elementIndex, int identifierIndex) throws CompilerError
    {
        if(elementIndex > 0xffff)
            throw new CompilerError("Library Element index overflow");
        if(identifierIndex > 0xffff)
            throw new CompilerError("identifier index overflow");
        if(elementIndex > 0xff)
            if(identifierIndex > 0xff)
                return opcode(Instruction.LIBE_P16_GET16, 1, 0,
                        (byte) (elementIndex & 0xff), (byte) ((elementIndex >>> 8) & 0xff), (byte) (identifierIndex & 0xff), (byte) ((identifierIndex >>> 8) & 0xff));
            else return opcode(Instruction.LIBE_P_GET16, 1, 0, (byte) (elementIndex & 0xff), (byte) ((elementIndex >>> 8) & 0xff), (byte) (identifierIndex & 0xff));
        else if(identifierIndex > 0xff)
            return opcode(Instruction.LIBE_P16_GET, 1, 0, (byte) (elementIndex & 0xff), (byte) (identifierIndex & 0xff), (byte) ((identifierIndex >>> 8) & 0xff));
        else return opcode(Instruction.LIBE_P_GET, 1, 0, (byte) (elementIndex & 0xff), (byte) (identifierIndex & 0xff));
    }
    
    public static final Opcode libeRefGet(int index) throws CompilerError
    {
        if(index > 0xffff)
            throw new CompilerError("Library Element index overflow");
        if(index > 0xff)
            return opcode(Instruction.LIBE_REF_GET16, 1, 0, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
        return opcode(Instruction.LIBE_REF_GET, 1, 0, (byte) (index & 0xff));
    }
    
    public static final Opcode libeCall(int index, int argumentCount, boolean popReturn) throws CompilerError
    {
        if(index > 0xffff)
            throw new CompilerError("identifier index overflow");
        if(argumentCount > 0xff)
            throw new CompilerError("argument count overflow");
        switch((index > 0xff ? 0x1 : 0x0) | (argumentCount > 0 ? 0x2 : 0x0) | (popReturn ? 0x4 : 0x0))
        {
            default: throw new IllegalStateException();
            case 0x0: return opcode(Instruction.LIBE_VCALL_NA, 0, 0, (byte) (index & 0xff));
            case 0x1: return opcode(Instruction.LIBE_VCALL_NA16, 0, 0, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
            case 0x2: return opcode(Instruction.LIBE_VCALL, 0, argumentCount, (byte) argumentCount, (byte) (index & 0xff));
            case 0x4: return opcode(Instruction.LIBE_CALL_NA, 1, 0, (byte) (index & 0xff));
            case 0x1 | 0x2: return opcode(Instruction.LIBE_VCALL16, 0, argumentCount, (byte) argumentCount, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
            case 0x1 | 0x4: return opcode(Instruction.LIBE_CALL_NA16, 1, 0, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
            case 0x2 | 0x4: return opcode(Instruction.LIBE_CALL, 1, argumentCount, (byte) argumentCount, (byte) (index & 0xff));
            case 0x1 | 0x2 | 0x4: return opcode(Instruction.LIBE_CALL16, 1, argumentCount, (byte) argumentCount, (byte) (index & 0xff), (byte) ((index >>> 8) & 0xff));
        }
    }
    
    
    
    
    
    
    
    
    
    private static final class MonoOpcode extends Opcode
    {
        private final byte idx0;
        
        private MonoOpcode(int opcode, int stackPush, int stackPop, byte idx)
        {
            super(opcode, 2, stackPush, stackPop);
            this.idx0 = idx;
        }
        
        @Override
        public final void build(byte[] bytecode, int offset)
        {
            super.build(bytecode, offset);
            bytecode[offset + 1] = idx0;
        }
    }
    
    private static final class BiOpcode extends Opcode
    {
        private final byte idx0;
        private final byte idx1;
        
        private BiOpcode(int opcode, int stackPush, int stackPop, byte idx0, byte idx1)
        {
            super(opcode, 3, stackPush, stackPop);
            this.idx0 = idx0;
            this.idx1 = idx1;
        }
        
        @Override
        public final void build(byte[] bytecode, int offset)
        {
            super.build(bytecode, offset);
            bytecode[offset + 1] = idx0;
            bytecode[offset + 2] = idx1;
        }
    }
    
    private static final class TriOpcode extends Opcode
    {
        private final byte idx0;
        private final byte idx1;
        private final byte idx2;
        
        private TriOpcode(int opcode, int stackPush, int stackPop, byte idx0, byte idx1, byte idx2)
        {
            super(opcode, 4, stackPush, stackPop);
            this.idx0 = idx0;
            this.idx1 = idx1;
            this.idx2 = idx2;
        }
        
        @Override
        public final void build(byte[] bytecode, int offset)
        {
            super.build(bytecode, offset);
            bytecode[offset + 1] = idx0;
            bytecode[offset + 2] = idx1;
            bytecode[offset + 3] = idx2;
        }
    }
    
    private static final class TetraOpcode extends Opcode
    {
        private final byte idx0;
        private final byte idx1;
        private final byte idx2;
        private final byte idx3;
        
        private TetraOpcode(int opcode, int stackPush, int stackPop, byte idx0, byte idx1, byte idx2, byte idx3)
        {
            super(opcode, 4, stackPush, stackPop);
            this.idx0 = idx0;
            this.idx1 = idx1;
            this.idx2 = idx2;
            this.idx3 = idx3;
        }
        
        @Override
        public final void build(byte[] bytecode, int offset)
        {
            super.build(bytecode, offset);
            bytecode[offset + 1] = idx0;
            bytecode[offset + 2] = idx1;
            bytecode[offset + 3] = idx2;
            bytecode[offset + 4] = idx3;
        }
    }
}
