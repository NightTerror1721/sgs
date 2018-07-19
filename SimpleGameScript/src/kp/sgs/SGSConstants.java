/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs;

import kp.sgs.data.SGSValue;

/**
 *
 * @author Asus
 */
public interface SGSConstants
{
    int MAGIC_NUMBER = ('S' & 0xff) | (('S' & 0xff) << 8) | (('G' & 0xff) << 16) | (('S' & 0xff) << 24);
    
    String SOURCE_FILE_EXTENSION = "sgs";
    String COMPILED_FILE_EXTENSION = "csgs";
    
    int MAX_STACK_LENGTH = 256;
    int MAX_VARS = 256;
    
    int CODE_STACK_LEN      = 0;
    int CODE_VARS_LEN       = 1;
    int CODE_RETURN_TYPE    = 2;
    int CODE_INIT           = 3;
    
    SGSValue[] EMPTY_ARGS = {};
    
    interface Instruction
    {
        int NOP             = 0x00; //
        
        int LOAD_CONST      = 0x01; // <const_idx>
        int LOAD_CONST16    = 0x02; // <const_idx|0-7> <const_idx|8-15>
        int LOAD_VAR        = 0x03; // <var_idx>
        int LOAD_FUNCTION   = 0x04; // <func_idx>
        int LOAD_FUNCTION16 = 0x05; // <func_idx|0-7> <func_idx|8-15>
        int LOAD_CLOSURE    = 0x06; // <func_idx> <pars_count>
        int LOAD_CLOSURE16  = 0x07; // <func_idx|0-7> <func_idx|8-15> <pars_count>
        int LOAD_GLOBAL     = 0x08; // <identifier_idx>
        int LOAD_GLOBAL16   = 0x09; // <identifier_idx|0-7> <identifier_idx|8-15>
        int LOAD_UNDEF      = 0x0A; //
        
        int STORE_VAR       = 0x0B; // <var_idx>
        int STORE_GLOBAL    = 0x0C; // <identifier_idx>
        int STORE_GLOBAL16  = 0x0D; // <identifier_idx|0-7> <identifier_idx|8-15>
        int STORE_VAR_UNDEF = 0x0E; // <var_idx>
        
        int ARRAY_NEW       = 0x0F; //
        int ARRAY_GET       = 0x10; //
        int ARRAY_SET       = 0x11; //
        int ARRAY_INT_GET   = 0x12; // <local_value>
        int ARRAY_INT_SET   = 0x13; // <local_value>
        
        int OBJ_NEW         = 0x14; //
        int OBJ_PGET        = 0x15; // <identifier_idx>
        int OBJ_PGET16      = 0x16; // <identifier_idx|0-7> <identifier_idx|8-15>
        int OBJ_PSET        = 0x17; // <identifier_idx>
        int OBJ_PSET16      = 0x18; // <identifier_idx|0-7> <identifier_idx|8-15>
        
        int REF_HEAP        = 0x19; // 
        int REF_LOCAL       = 0x1A; // <var_idx>
        int REF_GET         = 0x1B; // 
        int REF_SET         = 0x1C; // 
        
        int POP             = 0x1D; // 
        int SWAP            = 0x1E; // 
        int SWAP2           = 0x1F; // 
        int DUP             = 0x20; // 
        
        int CAST_INT        = 0x21; //
        int CAST_FLOAT      = 0x22; //
        int CAST_STRING     = 0x23; //
        int CAST_ARRAY      = 0x24; //
        int CAST_OBJECT     = 0x25; //
        
        int GOTO            = 0x26; // <instruction_idx> <IGNORED>
        int GOTO16          = 0x27; // <instruction_idx|0-7> <instruction_idx|8-15>
        
        int RETURN_NONE     = 0x28; //
        int RETURN          = 0x29; //
        
        int ADD             = 0x2A; //
        int SUB             = 0x2B; //
        int MUL             = 0x2C; //
        int DIV             = 0x2D; //
        int REM             = 0x2E; //
        int NEG             = 0x2F; //
        int INC             = 0x30; //
        int DEC             = 0x31; //
        
        int BW_SFH_L        = 0x32; //
        int BW_SFH_R        = 0x33; //
        int BW_AND          = 0x34; //
        int BW_OR           = 0x35; //
        int BW_XOR          = 0x36; //
        int BW_NOT          = 0x37; //
        
        int EQ              = 0x38; //
        int NEQ             = 0x39; //
        int TEQ             = 0x3A; //
        int TNEQ            = 0x3B; //
        int GR              = 0x3C; //
        int SM              = 0x3D; //
        int GREQ            = 0x3E; //
        int SMEQ            = 0x3F; //
        int ISDEF           = 0x40; //
        int ISUNDEF         = 0x41; //
        int INV             = 0x42; //
        int CONCAT          = 0x43; //
        int LEN             = 0x44; //
        int TYPEID          = 0x45; //
        int ITERATOR        = 0x46; //
        
        int IF              = 0x47; // <instruction_idx> <IGNORED>
        int IF16            = 0x48; // <instruction_idx|0-7> <instruction_idx|8-15>
        
        int IF_EQ           = 0x49; // <instruction_idx> <IGNORED>
        int IF_EQ16         = 0x4A; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_NEQ          = 0x4B; // <instruction_idx> <IGNORED>
        int IF_NEQ16        = 0x4C; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_TEQ          = 0x4D; // <instruction_idx> <IGNORED>
        int IF_TEQ16        = 0x4E; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_TNEQ         = 0x4F; // <instruction_idx> <IGNORED>
        int IF_TNEQ16       = 0x50; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_GR           = 0x51; // <instruction_idx> <IGNORED>
        int IF_GR16         = 0x52; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_SM           = 0x53; // <instruction_idx> <IGNORED>
        int IF_SM16         = 0x54; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_GREQ         = 0x55; // <instruction_idx> <IGNORED>
        int IF_GREQ16       = 0x56; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_SMEQ         = 0x57; // <instruction_idx> <IGNORED>
        int IF_SMEQ16       = 0x58; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_INV          = 0x59; // <instruction_idx> <IGNORED>
        int IF_INV16        = 0x5A; // <instruction_idx|0-7> <instruction_idx|8-15>
        
        int IF_DEF          = 0x5B; // <instruction_idx> <IGNORED>
        int IF_DEF16        = 0x5C; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_UNDEF        = 0x5D; // <instruction_idx> <IGNORED>
        int IF_UNDEF16      = 0x5E; // <instruction_idx|0-7> <instruction_idx|8-15>
        
        int LOCAL_CALL      = 0x5F; // <args_len> <func_idx>
        int LOCAL_CALL16    = 0x60; // <args_len> <func_idx|0-7> <func_idx|8-15>
        int LOCAL_CALL_NA   = 0x61; // <func_idx>
        int LOCAL_CALL_NA16 = 0x62; // <func_idx|0-7> <func_idx|8-15>
        int LOCAL_VCALL     = 0x63; // <args_len> <func_idx>
        int LOCAL_VCALL16   = 0x64; // <args_len> <func_idx|0-7> <func_idx|8-15>
        int LOCAL_VCALL_NA  = 0x65; // <func_idx>
        int LOCAL_VCALL_NA16= 0x66; // <func_idx|0-7> <func_idx|8-15>
        
        int CALL            = 0x67; // <args_len>
        int CALL_NA         = 0x68; //
        int VCALL           = 0x69; // <args_len>
        int VCALL_NA        = 0x6A; //
        
        int INVOKE          = 0x6B; // <args_len> <identifier_idx>
        int INVOKE16        = 0x6C; // <args_len> <identifier_idx|0-7> <identifier_idx|8-15>
        int INVOKE_NA       = 0x6D; // <identifier_idx>
        int INVOKE_NA16     = 0x6E; // <identifier_idx|0-7> <identifier_idx|8-15>
        int VINVOKE         = 0x6F; // <args_len> <identifier_idx>
        int VINVOKE16       = 0x70; // <args_len> <identifier_idx|0-7> <identifier_idx|8-15>
        int VINVOKE_NA      = 0x71; // <identifier_idx>
        int VINVOKE_NA16    = 0x72; // <identifier_idx|0-7> <identifier_idx|8-15>
        
        int LIBE_LOAD       = 0x73; // <libelement_idx>
        int LIBE_LOAD16     = 0x74; // <libelement_idx|0-7> <libelement_idx|8-15>
        int LIBE_A_GET      = 0x75; // <libelement_idx>
        int LIBE_A_GET16    = 0x76; // <libelement_idx|0-7> <libelement_idx|8-15>
        int LIBE_AINT_GET   = 0x77; // <libelement_idx> <local_value>
        int LIBE_AINT_GET16 = 0x78; // <libelement_idx|0-7> <libelement_idx|8-15> <local_value>
        int LIBE_P_GET      = 0x79; // <libelement_idx> <identifier_idx>
        int LIBE_P16_GET    = 0x7A; // <libelement_idx> <identifier_idx|0-7> <identifier_idx|8-15>
        int LIBE_P_GET16    = 0x7B; // <libelement_idx|0-7> <libelement_idx|8-15> <identifier_idx>
        int LIBE_P16_GET16  = 0x7C; // <libelement_idx|0-7> <libelement_idx|8-15> <identifier_idx|0-7> <identifier_idx|8-15>
        int LIBE_REF_GET    = 0x7D; // <libelement_idx>
        int LIBE_REF_GET16  = 0x7E; // <libelement_idx|0-7> <libelement_idx|8-15>
        int LIBE_CALL       = 0x7F; // <args_len> <libelement_idx>
        int LIBE_CALL16     = 0x80; // <args_len> <libelement_idx|0-7> <libelement_idx|8-15>
        int LIBE_CALL_NA    = 0x81; // <libelement_idx>
        int LIBE_CALL_NA16  = 0x82; // <libelement_idx|0-7> <libelement_idx|8-15>
        int LIBE_VCALL      = 0x83; // <args_len> <libelement_idx>
        int LIBE_VCALL16    = 0x84; // <args_len> <libelement_idx|0-7> <libelement_idx|8-15>
        int LIBE_VCALL_NA   = 0x85; // <libelement_idx>
        int LIBE_VCALL_NA16 = 0x86; // <libelement_idx|0-7> <libelement_idx|8-15>
        
        int ARGS_TO_ARRAY   = 0x87; // <var_idx> <offset_idx>
        int ARG_TO_VAR      = 0x88; // <var_idx> <arg_idx>
        
    }
}
