/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler;

import java.util.Objects;
import kp.sgs.compiler.parser.Identifier;

/**
 *
 * @author Asus
 */
public final class CompilerProperties
{
    public static final String DEFAULT_MAIN_FUNCTION_NAME = "main";
    
    private String mainFunctionName = DEFAULT_MAIN_FUNCTION_NAME;
    
    public final void setMainFunctionName(String name)
    {
        if(!Identifier.isValidIdentifier(Objects.requireNonNull(name)))
            throw new IllegalArgumentException("Invalid function name");
        this.mainFunctionName = name;
    }
    public final String getFunctionName() { return mainFunctionName; }
}
