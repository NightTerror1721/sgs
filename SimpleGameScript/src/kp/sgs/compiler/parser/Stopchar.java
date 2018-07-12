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
public class Stopchar extends CodeFragment
{
    private final String symbol;
    
    private Stopchar(String symbol) { this.symbol = symbol; }
    
    @Override
    public final boolean isValidOperand() { return false; }
    
    @Override
    public final CodeFragmentType getFragmentType() { return CodeFragmentType.STOPCHAR; }
    
    @Override
    public final String toString() { return symbol; }
    
    
    public static final Stopchar SEMICOLON = new Stopchar(";");
    public static final Stopchar COMMA = new Stopchar(",");
    public static final Stopchar TWO_POINTS = new Stopchar(":");
    public static final Stopchar THREE_POINTS = new Stopchar("...");
}
