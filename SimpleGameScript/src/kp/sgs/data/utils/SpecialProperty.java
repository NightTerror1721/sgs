/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.data.utils;

/**
 *
 * @author Asus
 */
public interface SpecialProperty
{
    String CAST_INT             = "(int)";
    String CAST_FLOAT           = "(float)";
    String CAST_STRING          = "(string)";
    String CAST_ARRAY           = "(array)";
    String CAST_OBJECT          = "(object)";
    
    String OP_EQUALS            = "==";
    String OP_NOT_EQUALS        = "!=";
    String OP_GREATER           = ">";
    String OP_SMALLER           = "<";
    String OP_GREATER_EQUALS    = ">=";
    String OP_SMALLER_EQUALS    = "<=";
    String OP_NEGATE            = "!";
    
    String OP_PLUS              = "+";
    String OP_MINUS             = "-";
    String OP_MULTIPLY          = "*";
    String OP_DIVIDE            = "/";
    String OP_REMAINDER         = "%";
    String OP_INCREASE          = "++";
    String OP_DECREASE          = "--";
    
    String OP_BTW_SLEFT         = "<<";
    String OP_BTW_SRIGHT        = ">>";
    String OP_BTW_AND           = "&";
    String OP_BTW_XOR           = "^";
    String OP_BTW_OR            = "|";
    String OP_BTW_NOT           = "~";
    
    String OP_GET               = "[]";
    String OP_SET               = "[]=";
    
    String OP_CALL              = "()";
    
    String OP_ITERATOR          = "iterator";
    
    String OP_LENGTH            = "length";
    
    String CONSTRUCTOR          = "new";
}
