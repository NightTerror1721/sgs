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
        int LOAD_ARG        = 0x04; // <arg_idx>
        int LOAD_FUNCTION   = 0x05; // <func_idx>
        int LOAD_FUNCTION16 = 0x06; // <func_idx|0-7> <func_idx|8-15>
        int LOAD_CLOSURE    = 0x07; // <func_idx> <pars_count>
        int LOAD_CLOSURE16  = 0x08; // <func_idx|0-7> <func_idx|8-15> <pars_count>
        int LOAD_GLOBAL     = 0x09; // <identifier_idx>
        int LOAD_GLOBAL16   = 0x0A; // <identifier_idx|0-7> <identifier_idx|8-15>
        int LOAD_UNDEF      = 0x0B; //
        
        int STORE_VAR       = 0x0C; // <var_idx>
        int STORE_ARG       = 0x0D; // <arg_idx>
        int STORE_GLOBAL    = 0x0E; // <identifier_idx>
        int STORE_GLOBAL16  = 0x0F; // <identifier_idx|0-7> <identifier_idx|8-15>
        int STORE_VAR_UNDEF = 0x10; // <var_idx>
        
        int ARRAY_NEW       = 0x11; //
        int ARRAY_GET       = 0x12; //
        int ARRAY_SET       = 0x13; //
        int ARRAY_INT_GET   = 0x14; // <local_value>
        int ARRAY_INT_SET   = 0x15; // <local_value>
        
        int OBJ_NEW         = 0x16; //
        int OBJ_PGET        = 0x17; // <identifier_idx>
        int OBJ_PGET16      = 0x18; // <identifier_idx|0-7> <identifier_idx|8-15>
        int OBJ_PSET        = 0x19; // <identifier_idx>
        int OBJ_PSET16      = 0x1A; // <identifier_idx|0-7> <identifier_idx|8-15>
        
        int REF_HEAP        = 0x1B; // 
        int REF_LOCAL       = 0x1C; // <var_idx>
        int REF_GET         = 0x1D; // 
        int REF_SET         = 0x1E; // 
        
        int POP             = 0x1F; // 
        int SWAP            = 0x20; // 
        int SWAP2           = 0x21; // 
        int DUP             = 0x22; // 
        
        int CAST_INT        = 0x23; //
        int CAST_FLOAT      = 0x24; //
        int CAST_STRING     = 0x25; //
        int CAST_ARRAY      = 0x26; //
        int CAST_OBJECT     = 0x27; //
        
        int GOTO            = 0x28; // <instruction_idx> <IGNORED>
        int GOTO16          = 0x29; // <instruction_idx|0-7> <instruction_idx|8-15>
        
        int RETURN_NONE     = 0x2A; //
        int RETURN          = 0x2B; //
        
        int ADD             = 0x2C; //
        int SUB             = 0x2D; //
        int MUL             = 0x2E; //
        int DIV             = 0x2F; //
        int REM             = 0x30; //
        int NEG             = 0x31; //
        int INC             = 0x32; //
        int DEC             = 0x33; //
        
        int BW_SFH_L        = 0x34; //
        int BW_SFH_R        = 0x35; //
        int BW_AND          = 0x36; //
        int BW_OR           = 0x37; //
        int BW_XOR          = 0x38; //
        int BW_NOT          = 0x39; //
        
        int EQ              = 0x3A; //
        int NEQ             = 0x3B; //
        int TEQ             = 0x3C; //
        int TNEQ            = 0x3D; //
        int GR              = 0x3E; //
        int SM              = 0x3F; //
        int GREQ            = 0x40; //
        int SMEQ            = 0x41; //
        int ISDEF           = 0x42; //
        int ISUNDEF         = 0x43; //
        int INV             = 0x44; //
        int CONCAT          = 0x45; //
        int LEN             = 0x46; //
        int TYPEID          = 0x47; //
        int ITERATOR        = 0x48; //
        
        int IF              = 0x49; // <instruction_idx> <IGNORED>
        int IF16            = 0x4A; // <instruction_idx|0-7> <instruction_idx|8-15>
        
        int IF_EQ           = 0x4B; // <instruction_idx> <IGNORED>
        int IF_EQ16         = 0x4C; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_NEQ          = 0x4D; // <instruction_idx> <IGNORED>
        int IF_NEQ16        = 0x4E; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_TEQ          = 0x4F; // <instruction_idx> <IGNORED>
        int IF_TEQ16        = 0x50; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_TNEQ         = 0x51; // <instruction_idx> <IGNORED>
        int IF_TNEQ16       = 0x52; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_GR           = 0x53; // <instruction_idx> <IGNORED>
        int IF_GR16         = 0x54; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_SM           = 0x55; // <instruction_idx> <IGNORED>
        int IF_SM16         = 0x56; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_GREQ         = 0x57; // <instruction_idx> <IGNORED>
        int IF_GREQ16       = 0x58; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_SMEQ         = 0x59; // <instruction_idx> <IGNORED>
        int IF_SMEQ16       = 0x5A; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_INV          = 0x5B; // <instruction_idx> <IGNORED>
        int IF_INV16        = 0x5C; // <instruction_idx|0-7> <instruction_idx|8-15>
        
        int IF_DEF          = 0x5D; // <instruction_idx> <IGNORED>
        int IF_DEF16        = 0x5E; // <instruction_idx|0-7> <instruction_idx|8-15>
        int IF_UNDEF        = 0x5F; // <instruction_idx> <IGNORED>
        int IF_UNDEF16      = 0x60; // <instruction_idx|0-7> <instruction_idx|8-15>
        
        int LOCAL_CALL      = 0x61; // <args_len> <func_idx>
        int LOCAL_CALL16    = 0x62; // <args_len> <func_idx|0-7> <func_idx|8-15>
        int LOCAL_CALL_NA   = 0x63; // <func_idx>
        int LOCAL_CALL_NA16 = 0x64; // <func_idx|0-7> <func_idx|8-15>
        int LOCAL_VCALL     = 0x65; // <args_len> <func_idx>
        int LOCAL_VCALL16   = 0x66; // <args_len> <func_idx|0-7> <func_idx|8-15>
        int LOCAL_VCALL_NA  = 0x67; // <func_idx>
        int LOCAL_VCALL_NA16= 0x68; // <func_idx|0-7> <func_idx|8-15>
        
        int CALL            = 0x69; // <args_len>
        int CALL_NA         = 0x6A; //
        int VCALL           = 0x6B; // <args_len>
        int VCALL_NA        = 0x6C; //
        
        int INVOKE          = 0x6D; // <args_len> <identifier_idx>
        int INVOKE16        = 0x6E; // <args_len> <instruction_idx|0-7> <instruction_idx|8-15>
        int INVOKE_NA       = 0x6F; // <identifier_idx>
        int INVOKE_NA16     = 0x70; // <instruction_idx|0-7> <instruction_idx|8-15>
        int VINVOKE         = 0x71; // <args_len> <identifier_idx>
        int VINVOKE16       = 0x72; // <args_len> <instruction_idx|0-7> <instruction_idx|8-15>
        int VINVOKE_NA      = 0x73; // <identifier_idx>
        int VINVOKE_NA16    = 0x74; // <instruction_idx|0-7> <instruction_idx|8-15>
        
        int LIBE_LOAD       = 0x75; // <libelement_idx>
        int LIBE_LOAD16     = 0x76; // <libelement_idx|0-7> <libelement_idx|8-15>
        int LIBE_A_GET      = 0x77; // <libelement_idx>
        int LIBE_A_GET16    = 0x78; // <libelement_idx|0-7> <libelement_idx|8-15>
        int LIBE_AINT_GET   = 0x79; // <libelement_idx> <local_value>
        int LIBE_AINT_GET16 = 0x7A; // <libelement_idx|0-7> <libelement_idx|8-15> <local_value>
        int LIBE_P_GET      = 0x7B; // <libelement_idx> <identifier_idx>
        int LIBE_P16_GET    = 0x7C; // <libelement_idx> <identifier_idx|0-7> <identifier_idx|8-15>
        int LIBE_P_GET16    = 0x7D; // <libelement_idx|0-7> <libelement_idx|8-15> <identifier_idx>
        int LIBE_P16_GET16  = 0x7E; // <libelement_idx|0-7> <libelement_idx|8-15> <identifier_idx|0-7> <identifier_idx|8-15>
        int LIBE_REF_GET    = 0x7F; // <libelement_idx>
        int LIBE_REF_GET16  = 0x80; // <libelement_idx|0-7> <libelement_idx|8-15>
        int LIBE_CALL       = 0x81; // <args_len> <libelement_idx>
        int LIBE_CALL16     = 0x82; // <args_len> <libelement_idx|0-7> <libelement_idx|8-15>
        int LIBE_CALL_NA    = 0x83; // <libelement_idx>
        int LIBE_CALL_NA16  = 0x84; // <libelement_idx|0-7> <libelement_idx|8-15>
        int LIBE_VCALL      = 0x85; // <args_len> <libelement_idx>
        int LIBE_VCALL16    = 0x86; // <args_len> <libelement_idx|0-7> <libelement_idx|8-15>
        int LIBE_VCALL_NA   = 0x87; // <libelement_idx>
        int LIBE_VCALL_NA16 = 0x88; // <libelement_idx|0-7> <libelement_idx|8-15>
        
        int ARGS_TO_ARRAY   = 0x89; // <var_idx> <offset_idx>
        
    }
}
