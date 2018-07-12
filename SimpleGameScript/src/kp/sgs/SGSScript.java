/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs;

import java.util.Objects;
import static kp.sgs.SGSConstants.Instruction.*;
import kp.sgs.data.SGSFloat;
import kp.sgs.data.SGSFunction;
import kp.sgs.data.SGSImmutableValue;
import kp.sgs.data.SGSInteger;
import kp.sgs.data.SGSMutableArray;
import kp.sgs.data.SGSMutableObject;
import kp.sgs.data.SGSReference;
import kp.sgs.data.SGSString;
import kp.sgs.data.SGSValue;
import kp.sgs.data.utils.SGSHeapReference;

/**
 *
 * @author Asus
 */
public final class SGSScript
{
    final SGSImmutableValue[] constants;
    final String[] identifiers;
    final byte[][] functions;
    private final SGSFunction[] functionCache;
    
    public SGSScript(
            SGSImmutableValue[] constants,
            String[] identifiers,
            byte[][] functions)
    {
        this.constants = Objects.requireNonNull(constants);
        this.identifiers = Objects.requireNonNull(identifiers);
        this.functions = Objects.requireNonNull(functions);
        this.functionCache = new SGSFunction[functions.length];
    }
    
    public final SGSValue execute(SGSGlobals globals, SGSValue... args) { return executeFunction(globals, functions[0], args); }
    public final SGSValue execute(SGSGlobals globals) { return executeFunction(globals, functions[0], SGSConstants.EMPTY_ARGS); }
    
    private SGSValue executeFunction(SGSGlobals globals, byte[] code, SGSValue[] args)
    {
        int sit;
        SGSValue[] stack = new SGSValue[(code[SGSConstants.CODE_STACK_LEN] & 0xff) + (sit = (code[SGSConstants.CODE_VARS_LEN] & 0xff))];
        int inst = SGSConstants.CODE_INIT;
        
        for(;;)
        {
            switch(code[inst++] & 0xff)
            {
                default: throw new RuntimeException("Invalid instruction: " + Integer.toHexString(code[inst++ - 1] & 0xff));
                case NOP: break;
                
                case LOAD_CONST: stack[sit++] = constants[code[inst++] & 0xff]; break;
                case LOAD_CONST16: stack[sit++] = constants[(code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)]; break;
                case LOAD_VAR: stack[sit++] = stack[code[inst++] & 0xff]; break;
                case LOAD_ARG: stack[sit++] = args[code[inst++] & 0xff]; break;
                case LOAD_FUNCTION: stack[sit++] = loadFunctionFromCache(code[inst++ & 0xff]); break;
                case LOAD_FUNCTION16: stack[sit++] = loadFunctionFromCache((code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)); break;
                case LOAD_CLOSURE: {
                    int fidx = code[inst++];
                    int len = code[inst++] & 0xff;
                    SGSValue[] a = new SGSValue[len];
                    System.arraycopy(stack, sit -= len, a, 0, a.length);
                    stack[sit++] = new SGSScriptClosure(fidx, a);
                } break;
                case LOAD_CLOSURE16: {
                    int fidx = code[inst++];
                    int len = (code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8);
                    SGSValue[] a = new SGSValue[len];
                    System.arraycopy(stack, sit -= len, a, 0, a.length);
                    stack[sit++] = new SGSScriptClosure(fidx, a);
                } break;
                case LOAD_GLOBAL: stack[sit++] = globals.getGlobalValue(identifiers[code[inst++] & 0xff]); break;
                case LOAD_GLOBAL16: stack[sit++] = globals.getGlobalValue(identifiers[(code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)]); break;
                case LOAD_UNDEF: stack[sit++] = SGSValue.UNDEFINED;
                
                case STORE_VAR: stack[code[inst++] & 0xff] = stack[--sit]; break;
                case STORE_GLOBAL: globals.setGlobalValue(identifiers[code[inst++] & 0xff], stack[--sit]); break;
                case STORE_GLOBAL16: globals.setGlobalValue(identifiers[(code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)], stack[--sit]); break;
                case STORE_VAR_UNDEF: stack[code[inst++] & 0xff] = SGSValue.UNDEFINED;
                
                case ARRAY_NEW: stack[sit - 1] = new SGSMutableArray(stack[sit - 1].toInt()); break;
                case ARRAY_GET: stack[sit - 2] = stack[sit - 2].operatorGet(stack[sit - 1]); sit--; break;
                case ARRAY_SET: stack[sit - 3] = stack[sit - 3].operatorSet(stack[sit - 2], stack[sit - 1]); sit -= 2; break;
                case ARRAY_INT_GET: stack[sit - 1] = stack[sit - 1].operatorGet(code[inst++] & 0xff); break;
                case ARRAY_INT_SET: stack[sit - 2] = stack[sit - 2].operatorSet(code[inst++] & 0xff, stack[sit - 1]); sit--; break;
                
                case OBJ_NEW: stack[sit++] = new SGSMutableObject(); break;
                case OBJ_PGET: stack[sit - 1] = stack[sit - 1].operatorGetProperty(identifiers[code[inst++] & 0xff]); break;
                case OBJ_PGET16: stack[sit - 1] = stack[sit - 1].operatorGetProperty(identifiers[(code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)]); break;
                case OBJ_PSET: stack[sit - 2] = stack[sit - 2].operatorSetProperty(identifiers[code[inst++] & 0xff], stack[sit - 1]); sit--; break;
                case OBJ_PSET16: stack[sit - 2] = stack[sit - 2].operatorSetProperty(identifiers[(code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)], stack[sit - 1]); sit--; break;
                
                case REF_HEAP: stack[sit++] = new SGSHeapReference(); break;
                case REF_LOCAL: stack[sit++] = new SGSLocalReference(stack, code[inst++]);
                case REF_GET: stack[sit - 1] = stack[sit - 1].operatorReferenceGet(); break;
                case REF_SET: stack[sit - 2] = stack[sit - 2].operatorReferenceSet(stack[sit - 1]); sit--; break;
                
                case POP: sit--; break;
                case SWAP: {
                    SGSValue aux = stack[sit - 1];
                    stack[sit - 1] = stack[sit - 2];
                    stack[sit - 2] = aux;
                } break;
                case SWAP2: {
                    SGSValue aux = stack[sit - 1];
                    stack[sit - 1] = stack[sit - 3];
                    stack[sit - 3] = aux;
                } break;
                case DUP: stack[sit++] = stack[sit - 2]; break;
                
                case CAST_INT: stack[sit - 1] = new SGSInteger(stack[sit - 1].toInt()); break;
                case CAST_FLOAT: stack[sit - 1] = new SGSFloat(stack[sit - 1].toDouble()); break;
                case CAST_STRING: stack[sit - 1] = new SGSString(stack[sit - 1].toString()); break;
                case CAST_ARRAY: stack[sit - 1] = stack[sit - 1].toArray().toSGSValue(); break;
                case CAST_OBJECT: stack[sit - 1] = stack[sit - 1].toObject().toSGSValue(); break;
                
                case GOTO: inst = (code[inst++] & 0xff) - 1; break;
                case GOTO16: inst = ((code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)) - 1; break;
                
                case RETURN_NONE: return SGSValue.UNDEFINED;
                case RETURN: return stack[--sit];
                
                case ADD: stack[sit - 2] = stack[sit - 2].operatorPlus(stack[sit - 1]); sit--; break;
                case SUB: stack[sit - 2] = stack[sit - 2].operatorMinus(stack[sit - 1]); sit--; break;
                case MUL: stack[sit - 2] = stack[sit - 2].operatorMultiply(stack[sit - 1]); sit--; break;
                case DIV: stack[sit - 2] = stack[sit - 2].operatorDivide(stack[sit - 1]); sit--; break;
                case REM: stack[sit - 2] = stack[sit - 2].operatorRemainder(stack[sit - 1]); sit--; break;
                case NEG: stack[sit - 1] = stack[sit - 1].operatorNegative(); break;
                case INC: stack[sit - 1] = stack[sit - 1].operatorIncrease(); break;
                case DEC: stack[sit - 1] = stack[sit - 1].operatorDecrease(); break;
                
                case BW_SFH_L: stack[sit - 2] = stack[sit - 2].operatorBitwiseShiftLeft(stack[sit - 1]); sit--; break;
                case BW_SFH_R: stack[sit - 2] = stack[sit - 2].operatorBitwiseShiftRight(stack[sit - 1]); sit--; break;
                case BW_AND: stack[sit - 2] = stack[sit - 2].operatorBitwiseAnd(stack[sit - 1]); sit--; break;
                case BW_OR: stack[sit - 2] = stack[sit - 2].operatorBitwiseOr(stack[sit - 1]); sit--; break;
                case BW_XOR: stack[sit - 2] = stack[sit - 2].operatorBitwiseXor(stack[sit - 1]); sit--; break;
                case BW_NOT: stack[sit - 1] = stack[sit - 1].operatorBitwiseNot(); break;
                
                case EQ: stack[sit - 2] = stack[sit - 2].operatorEquals(stack[sit - 1]); sit--; break;
                case NEQ: stack[sit - 2] = stack[sit - 2].operatorNotEquals(stack[sit - 1]); sit--; break;
                case TEQ: stack[sit - 2] = stack[sit - 2].operatorTypedEquals(stack[sit - 1]); sit--; break;
                case TNEQ: stack[sit - 2] = stack[sit - 2].operatorTypedNotEquals(stack[sit - 1]); sit--; break;
                case GR: stack[sit - 2] = stack[sit - 2].operatorGreater(stack[sit - 1]); sit--; break;
                case SM: stack[sit - 2] = stack[sit - 2].operatorSmaller(stack[sit - 1]); sit--; break;
                case GREQ: stack[sit - 2] = stack[sit - 2].operatorGreaterEquals(stack[sit - 1]); sit--; break;
                case SMEQ: stack[sit - 2] = stack[sit - 2].operatorSmallerEquals(stack[sit - 1]); sit--; break;
                case ISDEF: stack[sit - 1] = stack[sit - 1].isUndefined() ? SGSValue.FALSE : SGSValue.TRUE; break;
                case ISUNDEF: stack[sit - 1] = stack[sit - 1].isUndefined() ? SGSValue.TRUE : SGSValue.FALSE; break;
                case INV: stack[sit - 1] = stack[sit - 1].operatorNegate(); break;
                case CONCAT: stack[sit - 2] = stack[sit - 2].operatorConcat(stack[sit - 1]); sit--; break;
                case LEN: stack[sit - 1] = new SGSInteger(stack[sit - 1].operatorLength()); break;
                case TYPEID: stack[sit - 1] = new SGSInteger(stack[sit - 1].getDataType()); break;
                case ITERATOR: stack[sit - 1] = stack[sit - 1].operatorIterator(); break;
                
                case IF: if(stack[--sit].toBoolean()) inst = (code[inst++] & 0xff) - 1; else inst++; break;
                case IF16: if(stack[--sit].toBoolean()) inst = ((code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)) - 1; break;
                
                case IF_EQ: if(stack[--sit - 1].operatorEquals(stack[sit--]).toBoolean()) inst = (code[inst++] & 0xff) - 1; else inst++; break;
                case IF_EQ16: if(stack[--sit - 1].operatorEquals(stack[sit--]).toBoolean()) inst = ((code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)) - 1; break;
                case IF_NEQ: if(stack[--sit - 1].operatorNotEquals(stack[sit--]).toBoolean()) inst = (code[inst++] & 0xff) - 1; else inst++; break;
                case IF_NEQ16: if(stack[--sit - 1].operatorNotEquals(stack[sit--]).toBoolean()) inst = ((code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)) - 1; break;
                case IF_TEQ: if(stack[--sit - 1].operatorTypedEquals(stack[sit--]).toBoolean()) inst = (code[inst++] & 0xff) - 1; else inst++; break;
                case IF_TEQ16: if(stack[--sit - 1].operatorTypedEquals(stack[sit--]).toBoolean()) inst = ((code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)) - 1; break;
                case IF_TNEQ: if(stack[--sit - 1].operatorTypedNotEquals(stack[sit--]).toBoolean()) inst = (code[inst++] & 0xff) - 1; else inst++; break;
                case IF_TNEQ16: if(stack[--sit - 1].operatorTypedNotEquals(stack[sit--]).toBoolean()) inst = ((code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)) - 1; break;
                case IF_GR: if(stack[--sit - 1].operatorGreater(stack[sit--]).toBoolean()) inst = (code[inst++] & 0xff) - 1; else inst++; break;
                case IF_GR16: if(stack[--sit - 1].operatorGreater(stack[sit--]).toBoolean()) inst = ((code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)) - 1; break;
                case IF_SM: if(stack[--sit - 1].operatorSmaller(stack[sit--]).toBoolean()) inst = (code[inst++] & 0xff) - 1; else inst++; break;
                case IF_SM16: if(stack[--sit - 1].operatorSmaller(stack[sit--]).toBoolean()) inst = ((code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)) - 1; break;
                case IF_GREQ: if(stack[--sit - 1].operatorGreaterEquals(stack[sit--]).toBoolean()) inst = (code[inst++] & 0xff) - 1; else inst++; break;
                case IF_GREQ16: if(stack[--sit - 1].operatorGreaterEquals(stack[sit--]).toBoolean()) inst = ((code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)) - 1; break;
                case IF_SMEQ: if(stack[--sit - 1].operatorSmallerEquals(stack[sit--]).toBoolean()) inst = (code[inst++] & 0xff) - 1; else inst++; break;
                case IF_SMEQ16: if(stack[--sit - 1].operatorSmallerEquals(stack[sit--]).toBoolean()) inst = ((code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)) - 1; break;
                case IF_INV: if(stack[sit--].operatorNegate().toBoolean()) inst = (code[inst++] & 0xff) - 1; else inst++; break;
                case IF_INV16: if(stack[sit--].operatorNegate().toBoolean()) inst = ((code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)) - 1; break;
                
                case IF_DEF: if(stack[--sit] != SGSValue.UNDEFINED) inst = (code[inst++] & 0xff) - 1; else inst++; break;
                case IF_DEF16: if(stack[--sit] != SGSValue.UNDEFINED) inst = ((code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)) - 1; break;
                case IF_UNDEF: if(stack[--sit] == SGSValue.UNDEFINED) inst = (code[inst++] & 0xff) - 1; else inst++; break;
                case IF_UNDEF16: if(stack[--sit] == SGSValue.UNDEFINED) inst = ((code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)) - 1; break;
                
                case LOCAL_CALL: {
                    SGSValue[] fargs = new SGSValue[code[inst++] & 0xff];
                    System.arraycopy(stack, sit -= fargs.length, fargs, 0, fargs.length);
                    stack[sit++] = executeFunction(globals, functions[code[inst++] & 0xff], fargs);
                } break;
                case LOCAL_CALL16: {
                    SGSValue[] fargs = new SGSValue[code[inst++] & 0xff];
                    System.arraycopy(stack, sit -= fargs.length, fargs, 0, fargs.length);
                    stack[sit++] = executeFunction(globals, functions[(code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)], fargs);
                } break;
                case LOCAL_CALL_NA: stack[sit++] = executeFunction(globals, functions[code[inst++] & 0xff], SGSConstants.EMPTY_ARGS); break;
                case LOCAL_CALL_NA16: stack[sit++] = executeFunction(globals, functions[(code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)], SGSConstants.EMPTY_ARGS); break;
                case LOCAL_VCALL: {
                    SGSValue[] fargs = new SGSValue[code[inst++] & 0xff];
                    System.arraycopy(stack, sit -= fargs.length, fargs, 0, fargs.length);
                    executeFunction(globals, functions[code[inst++] & 0xff], fargs);
                } break;
                case LOCAL_VCALL16: {
                    SGSValue[] fargs = new SGSValue[code[inst++] & 0xff];
                    System.arraycopy(stack, sit -= fargs.length, fargs, 0, fargs.length);
                    executeFunction(globals, functions[(code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)], fargs);
                } break;
                case LOCAL_VCALL_NA: executeFunction(globals, functions[code[inst++] & 0xff], SGSConstants.EMPTY_ARGS); break;
                case LOCAL_VCALL_NA16: executeFunction(globals, functions[(code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)], SGSConstants.EMPTY_ARGS); break;
                
                case CALL: {
                    SGSValue[] fargs = new SGSValue[code[inst++] & 0xff];
                    System.arraycopy(stack, sit -= fargs.length, fargs, 0, fargs.length);
                    stack[sit - 1] = stack[sit - 1].operatorCall(globals, fargs);
                } break;
                case CALL_NA: stack[sit - 1] = stack[sit - 1].operatorCall(globals, SGSConstants.EMPTY_ARGS); break;
                case VCALL: {
                    SGSValue[] fargs = new SGSValue[code[inst++] & 0xff];
                    System.arraycopy(stack, sit -= fargs.length, fargs, 0, fargs.length);
                    stack[--sit].operatorCall(globals, fargs);
                } break;
                case VCALL_NA: stack[--sit].operatorCall(globals, SGSConstants.EMPTY_ARGS); break;
                
                case INVOKE: {
                    SGSValue[] fargs = new SGSValue[(code[inst++] & 0xff) + 1];
                    System.arraycopy(stack, sit -= fargs.length, fargs, 0, fargs.length);
                    stack[sit++] = fargs[0].operatorGetProperty(identifiers[code[inst++] & 0xff]).operatorCall(globals, fargs);
                } break;
                case INVOKE16: {
                    SGSValue[] fargs = new SGSValue[(code[inst++] & 0xff) + 1];
                    System.arraycopy(stack, sit -= fargs.length, fargs, 0, fargs.length);
                    stack[sit++] = fargs[0].operatorGetProperty(identifiers[(code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)]).operatorCall(globals, fargs);
                } break;
                case INVOKE_NA: stack[sit - 1] = stack[sit - 1].operatorGetProperty(identifiers[code[inst++] & 0xff])
                        .operatorCall(globals, new SGSValue[] { stack[sit - 1] }); break;
                case INVOKE_NA16: stack[sit - 1] = stack[sit - 1].operatorGetProperty(identifiers[(code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)])
                        .operatorCall(globals, new SGSValue[] { stack[sit - 1] }); break;
                case VINVOKE: {
                    SGSValue[] fargs = new SGSValue[(code[inst++] & 0xff) + 1];
                    System.arraycopy(stack, sit -= fargs.length, fargs, 0, fargs.length);
                    fargs[0].operatorGetProperty(identifiers[code[inst++] & 0xff]).operatorCall(globals, fargs);
                } break;
                case VINVOKE16: {
                    SGSValue[] fargs = new SGSValue[(code[inst++] & 0xff) + 1];
                    System.arraycopy(stack, sit -= fargs.length, fargs, 0, fargs.length);
                    fargs[0].operatorGetProperty(identifiers[(code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)]).operatorCall(globals, fargs);
                } break;
                case VINVOKE_NA: sit--; stack[sit].operatorGetProperty(identifiers[code[inst++] & 0xff])
                        .operatorCall(globals, new SGSValue[] { stack[sit] }); break;
                case VINVOKE_NA16: sit--; stack[sit].operatorGetProperty(identifiers[(code[inst++] & 0xff) | ((code[inst++] & 0xff) << 8)])
                        .operatorCall(globals, new SGSValue[] { stack[sit] }); break;
            }
        }
    }
    
    private SGSFunction loadFunctionFromCache(int index)
    {
        return functionCache[index] != null ? functionCache[index] : (functionCache[index] = new SGSScriptFunction(index));
    }
    
    
    
    public class SGSScriptFunction extends SGSFunction
    {
        final int functionIdx;
        
        private SGSScriptFunction(int functionIdx) { this.functionIdx = functionIdx; }
        
        @Override public final SGSValue operatorEquals(SGSValue value)
        {
            if(value instanceof SGSScriptFunction)
            {
                SGSScriptFunction f = (SGSScriptFunction) value;
                return SGSScript.this == f.script() && functionIdx == f.functionIdx ? TRUE : FALSE;
            }
            return FALSE;
        }
        @Override public final SGSValue operatorNotEquals(SGSValue value)
        {
            if(value instanceof SGSScriptFunction)
            {
                SGSScriptFunction f = (SGSScriptFunction) value;
                return SGSScript.this == f.script() && functionIdx == f.functionIdx ? FALSE : TRUE;
            }
            return TRUE;
        }
        private SGSScript script() { return SGSScript.this; }
        
        @Override public SGSValue operatorCall(SGSGlobals globals, SGSValue[] args) { return executeFunction(globals, functions[functionIdx], args); }
    }
    
    private final class SGSScriptClosure extends SGSScriptFunction
    {
        private final SGSValue[] inners;
        public SGSScriptClosure(int functionIdx, SGSValue[] inners)
        {
            super(functionIdx);
            this.inners = inners;
        }
        
        @Override
        public final SGSValue operatorCall(SGSGlobals globals, SGSValue[] args)
        {
            if(inners.length > 0)
            {
                SGSValue[] newArgs = new SGSValue[inners.length + args.length];
                System.arraycopy(inners, 0, newArgs, 0, inners.length);
                System.arraycopy(args, 0, newArgs, inners.length, args.length);
                return executeFunction(globals, functions[functionIdx], newArgs);
            }
            else return executeFunction(globals, functions[functionIdx], args);
        }
    }
    
    private final class SGSLocalReference extends SGSReference
    {
        private final SGSValue[] stack;
        private final int varIdx;
        
        private SGSLocalReference(SGSValue[] stack, int varIdx)
        {
            this.stack = stack;
            this.varIdx = varIdx;
        }
        
        /* Pointer operators */
        @Override public final SGSValue operatorReferenceGet() { return stack[varIdx] == null ? UNDEFINED : stack[varIdx]; }
        @Override public final SGSValue operatorReferenceSet(SGSValue value)
        {
            stack[varIdx] = value;
            return value == null ? UNDEFINED : value;
        }
    }
}