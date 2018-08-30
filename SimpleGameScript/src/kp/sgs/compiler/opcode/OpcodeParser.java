/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.opcode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import kp.sgs.SGSConstants;
import static kp.sgs.SGSConstants.Instruction.*;
import kp.sgs.SGSScript;
import kp.sgs.SGSScriptIO;
import kp.sgs.compiler.parser.DataType;
import kp.sgs.data.SGSValue;

/**
 *
 * @author Asus
 */
public final class OpcodeParser
{
    private OpcodeParser() {}
    
    public static final void parseTo(SGSScript script, Writer writer) throws IOException { parseScript(new Output(writer), script); }
    public static final void parseTo(SGSScript script, OutputStream os) throws IOException { parseScript(new Output(os), script); }
    
    public static final void parseTo(SGSScript script, File file) throws IOException
    {
        try(FileOutputStream fos = new FileOutputStream(file)) { parseTo(script, fos); }
    }
    
    private static void parseScript(Output output, SGSScript script) throws IOException
    {
        try
        {
            output.append("#CONSTANTS: ").append(Integer.toString(SGSScriptIO.getConstantCount(script)));
            SGSScriptIO.forEachConstantExcept(script, (c, i) -> output.append('\n').appendElementReference(i)
                    .append(SGSValue.Type.toString(c.getDataType())).append(": ")
                    .append(c.toString().replace("\n", "\\n").replace("\t", "\\t")));
            output.append("\n\n\n");
            
            output.append("#IDENTIFIERS: ").append(Integer.toString(SGSScriptIO.getIdentifierCount(script)));
            SGSScriptIO.forEachIdentifierExcept(script, (id, i) -> output.append('\n').appendElementReference(i).append(id));
            output.append("\n\n\n");
            
            output.append("#LIBRARY IMPORTS: ").append(Integer.toString(SGSScriptIO.getLibraryElementCount(script)));
            SGSScriptIO.forEachLibraryElementExcept(script, (libe, i) -> {
                output.append('\n').appendElementReference(i).append("from ").append(libe.getLibrary().getLibraryName())
                        .append(" import ").append(libe.getElementName());
            });
            output.append("\n\n\n");
            
            output.append("#FUNCTIONS: ").append(Integer.toString(SGSScriptIO.getFunctionCount(script)));
            SGSScriptIO.forEachFunctionExcept(script, (func, index) -> {
                output.append('\n').appendElementReference(index).append('\n')
                        .append("    stack_len: ").append(Integer.toString(func[SGSConstants.CODE_STACK_LEN] & 0xff))
                        .append("\n    vars_len: ").append(Integer.toString(func[SGSConstants.CODE_VARS_LEN] & 0xff))
                        .append("\n    return_type: ").append(DataType.fromTypeidToString(func[SGSConstants.CODE_RETURN_TYPE] & 0xff))
                        .append("\n    code:");
                for(int offset = SGSConstants.CODE_INIT; offset < func.length; offset++)
                {
                    output.append("\n    ").appendOpcodePosition(offset);
                    offset += parseOpcode(output, func, offset);
                }
            });
            output.flush();
        }
        catch(Throwable ex) { throw new IOException(ex); }
    }
    
    static final String opcodeToString(Opcode opcode)
    {
        byte[] code = new byte[opcode.length];
        opcode.build(code, 0);
        StringWriter w;
        Output out = new Output(w = new StringWriter());
        try
        {
            parseOpcode(out, code, 0);
            out.close();
            return w.toString();
        }
        catch(IOException ex) { return "<<OPCODE NAME ERROR>>"; }
    }
    
    private static int parseOpcode(Output output, byte[] code, int offset) throws IOException
    {
        switch(code[offset] & 0xff)
        {
            case NOP: output.appendOpname("NOP"); return 0;
            
            case LOAD_CONST: output.appendOpname("LOAD_CONST").appendByteRef(code[offset + 1]); return 1;
            case LOAD_CONST16: output.appendOpname("LOAD_CONST16").appendWordRef(code[offset + 1], code[offset + 2]); return 2;
            case LOAD_VAR: output.appendOpname("LOAD_VAR").appendByteRef(code[offset + 1]); return 1;
            case LOAD_FUNCTION: output.appendOpname("LOAD_FUNCTION").appendByteRef(code[offset + 1]); return 1;
            case LOAD_FUNCTION16: output.appendOpname("LOAD_FUNCTION16").appendWordRef(code[offset + 1], code[offset + 2]); return 2;
            case LOAD_CLOSURE: output.appendOpname("LOAD_CLOSURE").appendByteRef(code[offset + 1]).appendByte(code[offset + 2]); return 2;
            case LOAD_CLOSURE16: output.appendOpname("LOAD_CLOSURE16").appendWordRef(code[offset + 1], code[offset + 2]).appendByte(code[offset + 3]); return 3;
            case LOAD_GLOBAL: output.appendOpname("LOAD_GLOBAL").appendByteRef(code[offset + 1]); return 1;
            case LOAD_GLOBAL16: output.appendOpname("LOAD_GLOBAL16").appendWordRef(code[offset + 1], code[offset + 2]); return 2;
            case LOAD_UNDEF: output.appendOpname("LOAD_UNDEF"); return 0;
            
            case STORE_VAR: output.appendOpname("STORE_VAR").appendByteRef(code[offset + 1]); return 1;
            case STORE_GLOBAL: output.appendOpname("STORE_GLOBAL").appendByteRef(code[offset + 1]); return 1;
            case STORE_GLOBAL16: output.appendOpname("STORE_GLOBAL16").appendWordRef(code[offset + 1], code[offset + 2]); return 2;
            case STORE_VAR_UNDEF: output.appendOpname("STORE_VAR_UNDEF").appendByteRef(code[offset + 1]); return 1;
            
            case ARRAY_NEW: output.appendOpname("ARRAY_NEW"); return 0;
            case ARRAY_GET: output.appendOpname("ARRAY_GET"); return 0;
            case ARRAY_SET: output.appendOpname("ARRAY_SET"); return 0;
            case ARRAY_INT_GET: output.appendOpname("ARRAY_INT_GET").appendByte(code[offset + 1]); return 1;
            case ARRAY_INT_SET: output.appendOpname("ARRAY_INT_SET").appendByte(code[offset + 1]); return 1;
            
            case OBJ_NEW: output.appendOpname("OBJ_NEW"); return 0;
            case OBJ_PGET: output.appendOpname("OBJ_PGET").appendByteRef(code[offset + 1]); return 1;
            case OBJ_PGET16: output.appendOpname("OBJ_PGET16").appendWordRef(code[offset + 1], code[offset + 2]); return 2;
            case OBJ_PSET: output.appendOpname("OBJ_PSET").appendByteRef(code[offset + 1]); return 1;
            case OBJ_PSET16: output.appendOpname("OBJ_PSET16").appendWordRef(code[offset + 1], code[offset + 2]); return 2;
            
            case REF_HEAP: output.appendOpname("REF_HEAP"); return 0;
            case REF_LOCAL: output.appendOpname("REF_LOCAL").appendByteRef(code[offset + 1]).appendTypeid(code[offset + 2]); return 2;
            case REF_GET: output.appendOpname("REF_GET"); return 0;
            case REF_SET: output.appendOpname("REF_SET"); return 0;
            
            case POP: output.appendOpname("POP"); return 0;
            case SWAP: output.appendOpname("SWAP"); return 0;
            case SWAP2: output.appendOpname("SWAP2"); return 0;
            case DUP: output.appendOpname("DUP"); return 0;
            case DUP2: output.appendOpname("DUP2"); return 0;
            
            case CAST_INT: output.appendOpname("CAST_INT"); return 0;
            case CAST_FLOAT: output.appendOpname("CAST_FLOAT"); return 0;
            case CAST_STRING: output.appendOpname("CAST_STRING"); return 0;
            case CAST_ARRAY: output.appendOpname("CAST_ARRAY"); return 0;
            case CAST_OBJECT: output.appendOpname("CAST_OBJECT"); return 0;
            
            case GOTO: output.appendOpname("GOTO").appendBytePos(code[offset + 1]); return 2;
            case GOTO16: output.appendOpname("GOTO16").appendWordPos(code[offset + 1], code[offset + 2]); return 2;
            
            case RETURN_NONE: output.appendOpname("RETURN_NONE"); return 0;
            case RETURN: output.appendOpname("RETURN"); return 0;
            
            case ADD: output.appendOpname("ADD"); return 0;
            case SUB: output.appendOpname("SUB"); return 0;
            case MUL: output.appendOpname("MUL"); return 0;
            case DIV: output.appendOpname("DIV"); return 0;
            case REM: output.appendOpname("REM"); return 0;
            case NEG: output.appendOpname("NEG"); return 0;
            case INC: output.appendOpname("INC"); return 0;
            case DEC: output.appendOpname("DEC"); return 0;
            
            case BW_SFH_L: output.appendOpname("BW_SFH_L"); return 0;
            case BW_SFH_R: output.appendOpname("BW_SFH_R"); return 0;
            case BW_AND: output.appendOpname("BW_AND"); return 0;
            case BW_OR: output.appendOpname("BW_OR"); return 0;
            case BW_XOR: output.appendOpname("BW_XOR"); return 0;
            case BW_NOT: output.appendOpname("BW_NOT"); return 0;
            
            case EQ: output.appendOpname("EQ"); return 0;
            case NEQ: output.appendOpname("NEQ"); return 0;
            case TEQ: output.appendOpname("TEQ"); return 0;
            case TNEQ: output.appendOpname("TNEQ"); return 0;
            case GR: output.appendOpname("GR"); return 0;
            case SM: output.appendOpname("SM"); return 0;
            case GREQ: output.appendOpname("GREQ"); return 0;
            case SMEQ: output.appendOpname("SMEQ"); return 0;
            case ISDEF: output.appendOpname("ISDEF"); return 0;
            case ISUNDEF: output.appendOpname("ISUNDEF"); return 0;
            case INV: output.appendOpname("INV"); return 0;
            case CONCAT: output.appendOpname("CONCAT"); return 0;
            case LEN: output.appendOpname("LEN"); return 0;
            case TYPEID: output.appendOpname("TYPEID"); return 0;
            case ITERATOR: output.appendOpname("ITERATOR"); return 0;
            
            case IF: output.appendOpname("IF").appendBytePos(code[offset + 1]); return 2;
            case IF16: output.appendOpname("IF16").appendWordPos(code[offset + 1], code[offset + 2]); return 2;
            
            case IF_EQ: output.appendOpname("IF_EQ").appendBytePos(code[offset + 1]); return 2;
            case IF_EQ16: output.appendOpname("IF_EQ16").appendWordPos(code[offset + 1], code[offset + 2]); return 2;
            case IF_NEQ: output.appendOpname("IF_NEQ").appendBytePos(code[offset + 1]); return 2;
            case IF_NEQ16: output.appendOpname("IF_NEQ16").appendWordPos(code[offset + 1], code[offset + 2]); return 2;
            case IF_TEQ: output.appendOpname("IF_TEQ").appendBytePos(code[offset + 1]); return 2;
            case IF_TEQ16: output.appendOpname("IF_TEQ16").appendWordPos(code[offset + 1], code[offset + 2]); return 2;
            case IF_TNEQ: output.appendOpname("IF_TNEQ").appendBytePos(code[offset + 1]); return 2;
            case IF_TNEQ16: output.appendOpname("IF_TNEQ16").appendWordPos(code[offset + 1], code[offset + 2]); return 2;
            case IF_GR: output.appendOpname("IF_GR").appendBytePos(code[offset + 1]); return 2;
            case IF_GR16: output.appendOpname("IF_GR16").appendWordPos(code[offset + 1], code[offset + 2]); return 2;
            case IF_SM: output.appendOpname("IF_SM").appendBytePos(code[offset + 1]); return 2;
            case IF_SM16: output.appendOpname("IF_SM16").appendWordPos(code[offset + 1], code[offset + 2]); return 2;
            case IF_GREQ: output.appendOpname("IF_GREQ").appendBytePos(code[offset + 1]); return 2;
            case IF_GREQ16: output.appendOpname("IF_GREQ16").appendWordPos(code[offset + 1], code[offset + 2]); return 2;
            case IF_SMEQ: output.appendOpname("IF_SMEQ").appendBytePos(code[offset + 1]); return 2;
            case IF_SMEQ16: output.appendOpname("IF_SMEQ16").appendWordPos(code[offset + 1], code[offset + 2]); return 2;
            case IF_INV: output.appendOpname("IF_INV").appendBytePos(code[offset + 1]); return 2;
            case IF_INV16: output.appendOpname("IF_INV16").appendWordPos(code[offset + 1], code[offset + 2]); return 2;
            
            case IF_DEF: output.appendOpname("IF_DEF").appendBytePos(code[offset + 1]); return 2;
            case IF_DEF16: output.appendOpname("IF_DEF16").appendWordPos(code[offset + 1], code[offset + 2]); return 2;
            case IF_UNDEF: output.appendOpname("IF_UNDEF").appendBytePos(code[offset + 1]); return 2;
            case IF_UNDEF16: output.appendOpname("IF_UNDEF16").appendWordPos(code[offset + 1], code[offset + 2]); return 2;
            
            case LOCAL_CALL: output.appendOpname("LOCAL_CALL").appendByte(code[offset + 1]).appendByteRef(code[offset + 2]); return 2;
            case LOCAL_CALL16: output.appendOpname("LOCAL_CALL16").appendByte(code[offset + 1]).appendWordRef(code[offset + 2], code[offset + 3]); return 3;
            case LOCAL_CALL_NA: output.appendOpname("LOCAL_CALL_NA").appendByteRef(code[offset + 2]); return 1;
            case LOCAL_CALL_NA16: output.appendOpname("LOCAL_CALL_NA16").appendWordRef(code[offset + 2], code[offset + 3]); return 2;
            case LOCAL_VCALL: output.appendOpname("LOCAL_VCALL").appendByte(code[offset + 1]).appendByteRef(code[offset + 2]); return 2;
            case LOCAL_VCALL16: output.appendOpname("LOCAL_VCALL16").appendByte(code[offset + 1]).appendWordRef(code[offset + 2], code[offset + 3]); return 3;
            case LOCAL_VCALL_NA: output.appendOpname("LOCAL_VCALL_NA").appendByteRef(code[offset + 2]); return 1;
            case LOCAL_VCALL_NA16: output.appendOpname("LOCAL_VCALL_NA16").appendWordRef(code[offset + 2], code[offset + 3]); return 2;
            
            case CALL: output.appendOpname("CALL").appendByte(code[offset + 1]); return 1;
            case CALL_NA: output.appendOpname("CALL_NA"); return 0;
            case VCALL: output.appendOpname("VCALL").appendByte(code[offset + 1]); return 1;
            case VCALL_NA: output.appendOpname("VCALL_NA"); return 0;
            
            case INVOKE: output.appendOpname("INVOKE").appendByte(code[offset + 1]).appendByteRef(code[offset + 2]); return 2;
            case INVOKE16: output.appendOpname("INVOKE16").appendByte(code[offset + 1]).appendWordRef(code[offset + 2], code[offset + 3]); return 3;
            case INVOKE_NA: output.appendOpname("INVOKE_NA").appendByteRef(code[offset + 2]); return 1;
            case INVOKE_NA16: output.appendOpname("INVOKE_NA16").appendWordRef(code[offset + 2], code[offset + 3]); return 2;
            case VINVOKE: output.appendOpname("VINVOKE").appendByte(code[offset + 1]).appendByteRef(code[offset + 2]); return 2;
            case VINVOKE16: output.appendOpname("VINVOKE16").appendByte(code[offset + 1]).appendWordRef(code[offset + 2], code[offset + 3]); return 3;
            case VINVOKE_NA: output.appendOpname("VINVOKE_NA").appendByteRef(code[offset + 2]); return 1;
            case VINVOKE_NA16: output.appendOpname("VINVOKE_NA16").appendWordRef(code[offset + 2], code[offset + 3]); return 2;
            
            case LIBE_LOAD: output.appendOpname("LIBE_LOAD").appendByteRef(code[offset + 1]); return 1;
            case LIBE_LOAD16: output.appendOpname("LIBE_LOAD16").appendWordRef(code[offset + 1], code[offset + 2]); return 2;
            case LIBE_A_GET: output.appendOpname("LIBE_A_GET").appendByteRef(code[offset + 1]); return 1;
            case LIBE_A_GET16: output.appendOpname("LIBE_A_GET16").appendWordRef(code[offset + 1], code[offset + 2]); return 2;
            case LIBE_AINT_GET: output.appendOpname("LIBE_AINT_GET").appendByteRef(code[offset + 1]).appendByte(code[offset + 2]); return 2;
            case LIBE_AINT_GET16: output.appendOpname("LIBE_AINT_GET16").appendWordRef(code[offset + 1], code[offset + 2]).appendByte(code[offset + 3]); return 3;
            case LIBE_P_GET: output.appendOpname("LIBE_P16_GET16").appendByteRef(code[offset + 1]).appendByteRef(code[offset + 2]); return 2;
            case LIBE_P16_GET: output.appendOpname("LIBE_P16_GET").appendWordRef(code[offset + 1], code[offset + 2]).appendByteRef(code[offset + 3]); return 3;
            case LIBE_P_GET16: output.appendOpname("LIBE_P_GET16").appendByteRef(code[offset + 1]).appendWordRef(code[offset + 2], code[offset + 3]); return 3;
            case LIBE_P16_GET16: output.appendOpname("LIBE_P16_GET16").appendWordRef(code[offset + 1], code[offset + 2]).appendWordRef(code[offset + 3], code[offset + 4]); return 4;
            case LIBE_REF_GET: output.appendOpname("LIBE_REF_GET").appendByteRef(code[offset + 1]); return 1;
            case LIBE_REF_GET16: output.appendOpname("LIBE_REF_GET16").appendWordRef(code[offset + 1], code[offset + 2]); return 2;
            case LIBE_CALL: output.appendOpname("LIBE_CALL").appendByte(code[offset + 1]).appendByteRef(code[offset + 2]); return 2;
            case LIBE_CALL16: output.appendOpname("LIBE_CALL16").appendByte(code[offset + 1]).appendWordRef(code[offset + 2], code[offset + 3]); return 3;
            case LIBE_CALL_NA: output.appendOpname("LIBE_CALL_NA").appendByteRef(code[offset + 1]); return 1;
            case LIBE_CALL_NA16: output.appendOpname("LIBE_CALL_NA16").appendWordRef(code[offset + 1], code[offset + 3]); return 2;
            case LIBE_VCALL: output.appendOpname("LIBE_VCALL").appendByte(code[offset + 1]).appendByteRef(code[offset + 2]); return 2;
            case LIBE_VCALL16: output.appendOpname("LIBE_VCALL16").appendByte(code[offset + 1]).appendWordRef(code[offset + 2], code[offset + 3]); return 3;
            case LIBE_VCALL_NA: output.appendOpname("LIBE_VCALL_NA").appendByteRef(code[offset + 1]); return 1;
            case LIBE_VCALL_NA16: output.appendOpname("LIBE_VCALL_NA16").appendWordRef(code[offset + 1], code[offset + 3]); return 2;
            case LIBE_NEW: output.appendOpname("LIBE_NEW").appendByte(code[offset + 1]).appendByteRef(code[offset + 2]); return 2;
            case LIBE_NEW16: output.appendOpname("LIBE_NEW16").appendByte(code[offset + 1]).appendWordRef(code[offset + 2], code[offset + 3]); return 3;
            case LIBE_NEW_NA: output.appendOpname("LIBE_NEW_NA").appendByteRef(code[offset + 1]); return 1;
            case LIBE_NEW_NA16: output.appendOpname("LIBE_NEW_NA16").appendWordRef(code[offset + 1], code[offset + 3]); return 2;
            case LIBE_VNEW: output.appendOpname("LIBE_VNEW").appendByte(code[offset + 1]).appendByteRef(code[offset + 2]); return 2;
            case LIBE_VNEW16: output.appendOpname("LIBE_VNEW16").appendByte(code[offset + 1]).appendWordRef(code[offset + 2], code[offset + 3]); return 3;
            case LIBE_VNEW_NA: output.appendOpname("LIBE_VNEW_NA").appendByteRef(code[offset + 1]); return 1;
            case LIBE_VNEW_NA16: output.appendOpname("LIBE_VNEW_NA16").appendWordRef(code[offset + 1], code[offset + 3]); return 2;
            
            case ARGS_TO_ARRAY: output.appendOpname("ARGS_TO_ARRAY").appendByteRef(code[offset + 1]).appendByte(code[offset + 2]); return 2;
            case ARG_TO_VAR: output.appendOpname("ARG_TO_VAR").appendByteRef(code[offset + 1]).appendByteRef(code[offset + 2]); return 2;
            
            case NEW: output.appendOpname("NEW").appendByte(code[offset + 1]); return 1;
            case NEW_NA: output.appendOpname("NEW_NA"); return 0;
            case VNEW: output.appendOpname("VNEW").appendByte(code[offset + 1]); return 1;
            case VNEW_NA: output.appendOpname("VNEW_NA"); return 0;
            case BASE: output.appendOpname("BASE"); return 1;
            
            default: output.append("<<UNKNOWN_OPCODE>>"); return 0;
        }
    }
    
    private static final class Output extends BufferedWriter
    {
        
        public Output(Writer out) { super(out); }
        public Output(OutputStream out) { super(new OutputStreamWriter(out)); }
        
        public final Output appendOpname(String name) throws IOException { super.append(name).append(' '); return this; }
        public final Output appendByte(int b) throws IOException { super.append(Integer.toString(b & 0xff)).append(' '); return this; }
        public final Output appendByteRef(int bref) throws IOException { super.append('#').append(Integer.toString(bref & 0xff)).append(' '); return this; }
        public final Output appendBytePos(int bpos) throws IOException { super.append("0x").append(Integer.toHexString(bpos & 0xff)).append(' '); return this; }
        public final Output appendWord(int b0, int b1) throws IOException
        {
            super.append(Integer.toString((b0 & 0xff) | ((b0 & 0xff) << 8))).append(' ');
            return this;
        }
        public final Output appendWordRef(int bref0, int bref1) throws IOException
        {
            super.append('#').append(Integer.toString((bref0 & 0xff) | ((bref1 & 0xff) << 8))).append(' ');
            return this;
        }
        public final Output appendWordPos(int bpos0, int bpos1) throws IOException
        {
            super.append("0x").append(Integer.toHexString((bpos0 & 0xff) | ((bpos1 & 0xff) << 8))).append(' ');
            return this;
        }
        
        public final Output append(String value) throws IOException { super.append(value); return this; }
        public final Output append(char value) throws IOException { super.append(value); return this; }
        
        public final Output appendElementReference(int reference) throws IOException { super.append('#').append(Integer.toString(reference)).append(": "); return this; }
        public final Output appendOpcodePosition(int position) throws IOException { super.append("0x").append(Integer.toHexString(position)).append(": "); return this; }
        
        public final Output appendTypeid(int typeid) throws IOException { super.append(DataType.fromTypeidToString(typeid)).append(' '); return this; }
    }
}
