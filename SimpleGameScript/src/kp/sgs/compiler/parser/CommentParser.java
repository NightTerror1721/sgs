/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.parser;

import java.io.EOFException;

/**
 *
 * @author Asus
 */
public final class CommentParser
{
    private CommentParser() {}
    
    public static final CodeReader parse(CodeReader source)
    {
        boolean write = true;
        CodeWriter writer = new CodeWriter();
        try
        {
            for(;;)
            {
                char c = source.next();
                switch(c)
                {
                    case '/': {
                        if(write)
                        {
                            if(source.canPeek(1))
                            {
                                switch(source.peek(1))
                                {
                                    case '/':
                                        if(source.seek('\n'))
                                            writer.closeLine();
                                        continue;
                                    case '*':
                                        write = false;
                                        source.next();
                                        continue;
                                }
                            }
                            writer.append(c);
                        }
                    } break;
                    
                    case '*': {
                        if(!write)
                        {
                            if(source.canPeek(1) && source.peek(1) == '/')
                            {
                                write = true;
                                source.next();
                                continue;
                            }
                        }
                        else writer.append(c);
                    } break;
                    
                    case '\n': {
                        writer.closeLine();
                    } break;
                }
            }
        }
        catch(EOFException ex)
        {
            if(!writer.isLastLineEmpty())
                writer.closeLine();
            return writer.toCodeReader();
        }
    }
}
