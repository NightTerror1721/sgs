/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.parser;

import java.util.regex.Pattern;
import kp.sgs.compiler.exception.CompilerError;

/**
 *
 * @author Asus
 */
public class Identifier extends Statement
{
    private final String identifier;
    
    private Identifier(String identifier)
    {
        this.identifier = identifier;
    }

    @Override
    public final CodeFragmentType getFragmentType() { return CodeFragmentType.IDENTIFIER; }
    
    @Override
    public final String toString() { return identifier; }
    
    
    private static final Pattern ID_PAT = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]*");
    
    public static final boolean isValidIdentifier(String str)
    {
        return ID_PAT.matcher(str).matches();
    }
    
    public static final Identifier valueOf(String str) throws CompilerError
    {
        if(!isValidIdentifier(str))
            throw new CompilerError("Invalid identifier: " + str);
        return new Identifier(str);
    }
}
