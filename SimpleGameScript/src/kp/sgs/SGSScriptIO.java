/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import kp.sgs.data.SGSArray;
import kp.sgs.data.SGSFloat;
import kp.sgs.data.SGSImmutableArray;
import kp.sgs.data.SGSImmutableObject;
import kp.sgs.data.SGSImmutableValue;
import kp.sgs.data.SGSInteger;
import kp.sgs.data.SGSObject;
import kp.sgs.data.SGSString;
import kp.sgs.data.SGSValue;
import kp.sgs.data.SGSValue.Type.ImmutableType;
import kp.sgs.lib.SGSLibrary;
import kp.sgs.lib.SGSLibraryElement;
import kp.sgs.lib.SGSLibraryRepository;

/**
 *
 * @author Asus
 */
public final class SGSScriptIO
{
    private SGSScriptIO() {}
    
    public static final void write(SGSScript script, OutputStream out) throws IOException
    {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeInt(SGSConstants.MAGIC_NUMBER);
        
        /* Constants */
        dos.writeInt(script.constants.length);
        for(SGSImmutableValue c : script.constants)
            writeValue(dos, c);
        
        /* Identifiers */
        dos.writeInt(script.identifiers.length);
        for(String id : script.identifiers)
            dos.writeUTF(id);
        
        /* Functions */
        dos.writeInt(script.functions.length);
        for(byte[] f : script.functions)
        {
            dos.writeInt(f.length);
            dos.write(f);
        }
        
        /* Library elements */
        dos.writeInt(script.libelements.length);
        for(SGSLibraryElement e : script.libelements)
        {
            dos.writeUTF(e.getLibrary().getLibraryName());
            dos.writeUTF(e.getElementName());
        }
    }
    public static final void write(SGSScript script, File file) throws IOException
    {
        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file)))
        {
            write(script, bos);
        }
    }
    
    
    public static final SGSScript read(InputStream in, SGSLibraryRepository libs) throws IOException
    {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(in, 65536));
        if(dis.readInt() != SGSConstants.MAGIC_NUMBER)
            throw new IOException("Wrong " + SGSConstants.COMPILED_FILE_EXTENSION + " file format");
        
        /* Constants */
        SGSImmutableValue[] constants = new SGSImmutableValue[dis.readInt()];
        for(int i=0;i<constants.length;i++)
            constants[i] = readValue(dis);
        
        /* Identifiers */
        String[] identifiers = new String[dis.readInt()];
        for(int i=0;i<identifiers.length;i++)
            identifiers[i] = dis.readUTF();
        
        /* Functions */
        byte[][] functions = new byte[dis.readInt()][];
        for(int i=0;i<functions.length;i++)
        {
            functions[i] = new byte[dis.readInt()];
            dis.read(functions[i]);
        }
        
        /* Library elements */
        SGSLibraryElement[] libelements = new SGSLibraryElement[dis.readInt()];
        for(int i=0;i<libelements.length;i++)
        {
            String libname = dis.readUTF();
            SGSLibrary lib = libs.getLibrary(libname);
            if(lib == null)
                throw new IOException("Library " + libname + " not found.");
            String elementName = dis.readUTF();
            if(!lib.hasLibraryElement(elementName))
                throw new IOException("Cannot found " + elementName + " element in " + libname + " library.");
            libelements[i] = lib.getLibraryElement(elementName);
        }
        
        return new SGSScript(constants, identifiers, functions, libelements);
    }
    public static final SGSScript read(File file, SGSLibraryRepository libs) throws IOException
    {
        try(FileInputStream fis = new FileInputStream(file))
        {
            return read(fis, libs);
        }
    }
    
    
    private static void writeValue(DataOutputStream dos, SGSValue value) throws IOException
    {
        dos.writeByte(value.getDataType());
        switch(value.getDataType())
        {
            case ImmutableType.UNDEFINED: break;
            case ImmutableType.INTEGER: dos.writeInt(value.toInt()); break;
            case ImmutableType.FLOAT: dos.writeDouble(value.toDouble()); break;
            case ImmutableType.STRING: dos.writeUTF(value.toString()); break;
            case ImmutableType.CONST_ARRAY: writeArray(dos, value.toArray()); break;
            case ImmutableType.CONST_OBJECT: writeObject(dos, value.toObject()); break;
            default: throw new IllegalArgumentException("Expected a immutable value in script constant pool");
        }
    }
    
    private static void writeArray(DataOutputStream dos, SGSArray array) throws IOException
    {
        if(array.isMutable())
            throw new IllegalArgumentException("Expected a const array in script constant pool");
        dos.writeInt(array.arrayLength());
        for(SGSValue value : array)
            writeValue(dos, value);
    }
    
    private static void writeObject(DataOutputStream dos, SGSObject object) throws IOException
    {
        if(object.isMutable())
            throw new IllegalArgumentException("Expected a const object in script constant pool");
        dos.writeInt(object.objectSize());
        for(Map.Entry<String, SGSValue> e : object)
        {
            dos.writeUTF(e.getKey());
            writeValue(dos, e.getValue());
        }
    }
    
    
    private static SGSImmutableValue readValue(DataInputStream dis) throws IOException
    {
        switch(dis.readInt())
        {
            case ImmutableType.UNDEFINED: return SGSValue.UNDEFINED;
            case ImmutableType.INTEGER:return new SGSInteger(dis.readInt());
            case ImmutableType.FLOAT: return new SGSFloat(dis.readDouble());
            case ImmutableType.STRING: return new SGSString(dis.readUTF());
            case ImmutableType.CONST_ARRAY: return readArray(dis);
            case ImmutableType.CONST_OBJECT: return readObject(dis);
            default: throw new IllegalArgumentException("Expected a immutable value in script constant pool");
        }
    }
    
    private static SGSImmutableValue readArray(DataInputStream dis) throws IOException
    {
        SGSValue[] array = new SGSValue[dis.readInt()];
        for(int i=0;i<array.length;i++)
            array[i] = readValue(dis);
        return new SGSImmutableArray(array);
    }
    
    private static SGSImmutableValue readObject(DataInputStream dis) throws IOException
    {
        int len = dis.readInt();
        HashMap<String, SGSValue> map = new HashMap<>(len);
        for(int i=0;i<len;i++)
        {
            String name = dis.readUTF();
            SGSValue value = readValue(dis);
            map.put(name, value);
        }
        return new SGSImmutableObject(map);
    }
    
    
    public static final int getConstantCount(SGSScript script) { return script.constants.length; }
    public static final int getIdentifierCount(SGSScript script) { return script.identifiers.length; }
    public static final int getFunctionCount(SGSScript script) { return script.functions.length; }
    public static final int getLibraryElementCount(SGSScript script) { return script.libelements.length; }
    
    public static final void forEachConstant(SGSScript script, Consumer<SGSImmutableValue> consumer)
    {
        for(SGSImmutableValue value : script.constants)
            consumer.accept(value);
    }
    public static final void forEachIdentifier(SGSScript script, Consumer<String> consumer)
    {
        for(String value : script.identifiers)
            consumer.accept(value);
    }
    public static final void forEachFunction(SGSScript script, Consumer<byte[]> consumer)
    {
        for(byte[] value : script.functions)
            consumer.accept(value);
    }
    public static final void forEachLibraryElement(SGSScript script, Consumer<SGSLibraryElement> consumer)
    {
        for(SGSLibraryElement value : script.libelements)
            consumer.accept(value);
    }
    
    public static final void forEachConstant(SGSScript script, IntBiConsumer<SGSImmutableValue> consumer)
    {
        for(int i=0;i<script.constants.length;i++)
            consumer.accept(script.constants[i], i);
    }
    public static final void forEachIdentifier(SGSScript script, IntBiConsumer<String> consumer)
    {
        for(int i=0;i<script.identifiers.length;i++)
            consumer.accept(script.identifiers[i], i);
    }
    public static final void forEachFunction(SGSScript script, IntBiConsumer<byte[]> consumer)
    {
        for(int i=0;i<script.functions.length;i++)
            consumer.accept(script.functions[i], i);
    }
    public static final void forEachLibraryElement(SGSScript script, IntBiConsumer<SGSLibraryElement> consumer)
    {
        for(int i=0;i<script.libelements.length;i++)
            consumer.accept(script.libelements[i], i);
    }
    
    public static final void forEachConstantExcept(SGSScript script, ConsumerWithException<SGSImmutableValue> consumer) throws Throwable
    {
        for(int i=0;i<script.constants.length;i++)
            consumer.accept(script.constants[i], i);
    }
    public static final void forEachIdentifierExcept(SGSScript script, ConsumerWithException<String> consumer) throws Throwable
    {
        for(int i=0;i<script.identifiers.length;i++)
            consumer.accept(script.identifiers[i], i);
    }
    public static final void forEachFunctionExcept(SGSScript script, ConsumerWithException<byte[]> consumer) throws Throwable
    {
        for(int i=0;i<script.functions.length;i++)
            consumer.accept(script.functions[i], i);
    }
    public static final void forEachLibraryElementExcept(SGSScript script, ConsumerWithException<SGSLibraryElement> consumer) throws Throwable
    {
        for(int i=0;i<script.libelements.length;i++)
            consumer.accept(script.libelements[i], i);
    }
    
    @FunctionalInterface
    public static interface IntBiConsumer<E>
    {
        void accept(E element, int index);
    }
    
    @FunctionalInterface
    public static interface ConsumerWithException<E>
    {
        void accept(E element, int index) throws Throwable;
    }
}
