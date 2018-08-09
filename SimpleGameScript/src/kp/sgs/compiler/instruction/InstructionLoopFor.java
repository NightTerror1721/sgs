/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler.instruction;

import java.util.List;
import java.util.Objects;
import kp.sgs.compiler.ScriptBuilder.LocalVariable;
import kp.sgs.compiler.ScriptBuilder.NamespaceScope;
import kp.sgs.compiler.ScriptBuilder.NamespaceScopeType;
import kp.sgs.compiler.StatementCompiler;
import kp.sgs.compiler.exception.CompilerError;
import kp.sgs.compiler.opcode.OpcodeList;
import kp.sgs.compiler.opcode.OpcodeList.OpcodeLocation;
import kp.sgs.compiler.opcode.Opcodes;
import kp.sgs.compiler.parser.CodeFragment;
import kp.sgs.compiler.parser.CodeFragmentList;
import kp.sgs.compiler.parser.CommandArguments;
import kp.sgs.compiler.parser.DataType;
import kp.sgs.compiler.parser.Identifier;
import kp.sgs.compiler.parser.Operation;
import kp.sgs.compiler.parser.Scope;
import kp.sgs.compiler.parser.Statement;
import kp.sgs.compiler.parser.StatementParser;
import kp.sgs.compiler.parser.Stopchar;
import kp.sgs.data.SGSValue.Type;

/**
 *
 * @author Asus
 */
public abstract class InstructionLoopFor extends Instruction
{
    final Statement action;
    
    private InstructionLoopFor(Statement action)
    {
        this.action = Objects.requireNonNull(action);
    }
    
    @Override
    public final InstructionId getInstructionId() { return InstructionId.LOOP_FOR; }
    
    public static final Instruction create(CodeFragmentList list) throws CompilerError
    {
        if(list.length() < 2)
            throw new CompilerError("Malformed \"for\" command. Expected valid statement and scope after command.");
        CodeFragment frag;
        
        frag = list.get(0);
        if(!frag.isCommandArguments())
            throw new CompilerError("Malformed \"for\" command. Expected valid statement and scope after command. But found: " + frag);
        CommandArguments args = (CommandArguments) frag;
        
        frag = list.get(1);
        Statement action = frag.isScope() ? (Scope) frag : StatementParser.parse(list.subList(1));
        
        switch (args.getArgumentCount())
        {
            case 3: return createNumeric(args.getArgument(0), args.getArgument(1), args.getArgument(2), action);
            case 1: {
                if(args.getArgument(0).isEmpty())
                    throw new CompilerError("Malformed generic \"for\" command. Expected for(<type|def>? <varname> : <iterable>){<scope>}. But found: " + frag);
                CodeFragmentList[] parts = args.getArgument(0).split(Stopchar.TWO_POINTS);
                if(parts.length != 2)
                    throw new CompilerError("Malformed generic \"for\" command. Expected for(<type|def>? <varname> : <iterable>){<scope>}. But found: " + frag);
                return createGeneric(parts[0], parts[1], action);
            }
            default: throw new CompilerError("Malformed \"for\" command. Expected valid statement and scope after command. But found: " + frag);
        }
    }
    
    
    
    
    private static final NumericFor createNumeric(CodeFragmentList initsList, CodeFragmentList conditionList, CodeFragmentList endsList, Statement action)
            throws CompilerError
    {
        /* Inits and type */
        Instruction inits;
        if(initsList != null && !initsList.isEmpty())
        {
            inits = InstructionParser.parseDeclarationInstructions(initsList);
        }
        else { inits = null; }
        
        /* Condition */
        Statement condition;
        if(conditionList != null && !conditionList.isEmpty())
        {
            condition = StatementParser.parse(conditionList);
        }
        else condition = null;
        
        /* Ends */
        Statement[] ends;
        if(endsList != null && !endsList.isEmpty())
        {
            CodeFragmentList[] parts = endsList.split(Stopchar.COMMA);
            if(parts == null || parts.length < 1 || (parts.length == 1 && parts[0].isEmpty()))
            {
                ends = null;
            }
            else
            {
                ends = new Statement[parts.length];
                for(int i=0;i<ends.length;i++)
                {
                    ends[i] = StatementParser.parse(parts[i]);
                }
            }
        }
        else ends = null;
        
        return new NumericFor(inits, condition, ends, action);
    }
    
    private static final GenericFor createGeneric(CodeFragmentList varnameList, CodeFragmentList iterableList, Statement action) throws CompilerError
    {
        DataType type;
        Identifier varname;
        switch(varnameList.length())
        {
            default: throw new CompilerError("Malformed generic \"for\" command. Expected for(<type|def>? <varname> : <iterable>){<scope>}.");
            case 2: {
                type = DataType.parse(varnameList.get(0));
                if(!varnameList.get(1).isIdentifier())
                    throw new CompilerError("Malformed generic \"for\" command. Expected for(<type|def>? <varname> : <iterable>){<scope>}.");
                varname = varnameList.get(1);
            } break;
        }
        Statement iteratorGetter = StatementParser.parse(iterableList);
        
        return new GenericFor(type, varname, iteratorGetter, action);
    }
    
    private static final class NumericFor extends InstructionLoopFor
    {
        private final Instruction inits;
        private final Statement condition;
        private final Statement[] ends;
        
        private NumericFor(Instruction inits, Statement condition, Statement[] ends, Statement action)
        {
            super(action);
            this.inits = inits;
            this.condition = condition;
            this.ends = ends;
        }
        
        @Override
        public final void compileConstantPart(NamespaceScope scope, List<Operation> functions) throws CompilerError
        {
            throw new CompilerError("Cannot compile for loop in constant mode");
        }

        @Override
        public final void compileFunctionPart(NamespaceScope scope, OpcodeList opcodes) throws CompilerError
        {
            NamespaceScope child = scope.createChildScope(NamespaceScopeType.LOOP);
            if(inits != null)
                inits.compileFunctionPart(child, opcodes);
            
            OpcodeLocation loopStart = opcodes.getBottomLocation();
            OpcodeLocation condFalse = null;
            if(condition != null)
                condFalse = StatementCompiler.compileDefaultIf(child, opcodes, condition);
            
            StatementCompiler.compileScope(child, opcodes, action);
            if(ends != null)
                for(Statement statement : ends)
                    StatementCompiler.compile(child, opcodes, statement, true);
            
            opcodes.append(Opcodes.goTo(loopStart));
            if(condFalse != null)
                opcodes.setJumpOpcodeLocationToBottom(condFalse);
            
            child.setBreakPointLocations(opcodes, opcodes.getBottomLocation());
            child.setContinuePointLocations(opcodes, loopStart);
        }
    }
    
    
    
    private static final class GenericFor extends InstructionLoopFor
    {
        private final DataType type;
        private final Identifier varname;
        private final Statement iteratorGetter;
        
        private GenericFor(DataType type, Identifier varname, Statement iteratorGetter, Statement action)
        {
            super(action);
            this.type = type == null ? DataType.ANY : type;
            this.varname = Objects.requireNonNull(varname);
            this.iteratorGetter = Objects.requireNonNull(iteratorGetter);
        }
        
        @Override
        public final void compileConstantPart(NamespaceScope scope, List<Operation> functions) throws CompilerError
        {
            throw new CompilerError("Cannot compile for loop in constant mode");
        }

        @Override
        public final void compileFunctionPart(NamespaceScope scope, OpcodeList opcodes) throws CompilerError
        {
            NamespaceScope child = scope.createChildScope(NamespaceScopeType.LOOP);
            LocalVariable iteratorVar = child.createHiddenLocalVariable("it", DataType.ANY);
            LocalVariable var = child.createLocalVariable(varname.toString(), type);
            
            /* Iterator creation */
            StatementCompiler.compile(child, opcodes, iteratorGetter, false);
            opcodes.append(Opcodes.ITERATOR);
            opcodes.append(Opcodes.storeVar(iteratorVar.getIndex()));
            
            /* loop condition */
            OpcodeLocation loopStart = opcodes.getBottomLocation();
            opcodes.append(Opcodes.loadVar(iteratorVar.getIndex()));
            opcodes.append(Opcodes.call(0, false));
            opcodes.append(Opcodes.DUP);
            OpcodeLocation endIt = opcodes.append(Opcodes.ifUndef());
            
            /* scope action */
            innerCast(opcodes);
            opcodes.append(Opcodes.storeVar(var.getIndex()));
            StatementCompiler.compileScope(child, opcodes, action);
            opcodes.append(Opcodes.goTo(loopStart));
            opcodes.setJumpOpcodeLocationToBottom(endIt);
            child.setBreakPointLocations(opcodes, opcodes.getBottomLocation());
            child.setContinuePointLocations(opcodes, loopStart);
            opcodes.append(Opcodes.POP);
        }
        
        private void innerCast(OpcodeList opcodes) throws CompilerError
        {
            switch(type.getTypeId())
            {
                case Type.INTEGER: opcodes.append(Opcodes.CAST_INT); break;
                case Type.FLOAT: opcodes.append(Opcodes.CAST_FLOAT); break;
                case Type.STRING: opcodes.append(Opcodes.CAST_STRING); break;
                case Type.ARRAY: opcodes.append(Opcodes.CAST_ARRAY); break;
                case Type.OBJECT: opcodes.append(Opcodes.CAST_OBJECT); break;
            }
        }
    }
}
