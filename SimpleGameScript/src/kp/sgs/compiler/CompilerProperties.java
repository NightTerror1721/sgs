/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.sgs.compiler;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import kp.sgs.compiler.parser.Identifier;
import kp.sgs.lib.SGSLibraryRepository;

/**
 *
 * @author Asus
 */
public final class CompilerProperties
{
    public static final String DEFAULT_MAIN_FUNCTION_NAME = "main";
    
    private String mainFunctionName = DEFAULT_MAIN_FUNCTION_NAME;
    private SGSLibraryRepository libs;
    private LinkedList<File> dirs = new LinkedList<>();
    
    public final void setMainFunctionName(String name)
    {
        if(!Identifier.isValidIdentifier(Objects.requireNonNull(name)))
            throw new IllegalArgumentException("Invalid function name");
        this.mainFunctionName = name;
    }
    public final String getFunctionName() { return mainFunctionName; }
    
    public final void setLibraryRepository(SGSLibraryRepository libraryRepository)
    {
        this.libs = libraryRepository;
    }
    public final SGSLibraryRepository getLibraryRepository() { return libs; }
    
    public final void setDirectories(File... dirs)
    {
        this.dirs = new LinkedList<>(Arrays.asList(dirs));
    }
    public final void setDirectories(String... dirs)
    {
        this.dirs = new LinkedList<>(Arrays.stream(dirs).map(s -> new File(s)).collect(Collectors.toList()));
    }
    public final void setDirectories(Collection<File> dirs)
    {
        this.dirs = new LinkedList<>(dirs);
    }
    public final void setDirectoriesFromString(Collection<String> dirs)
    {
        this.dirs = new LinkedList<>(dirs.stream().map(s -> new File(s)).collect(Collectors.toList()));
    }
    
    public final void addDirectories(File... dirs)
    {
        this.dirs.addAll(Arrays.asList(dirs));
    }
    public final void addDirectories(String... dirs)
    {
        this.dirs.addAll(Arrays.stream(dirs).map(s -> new File(s)).collect(Collectors.toList()));
    }
    public final void addDirectories(Collection<File> dirs)
    {
        this.dirs.addAll(dirs);
    }
    public final void addDirectoriesFromString(Collection<String> dirs)
    {
        this.dirs.addAll(dirs.stream().map(s -> new File(s)).collect(Collectors.toList()));
    }
    
    public final List<File> getDirectories() { return Collections.unmodifiableList(dirs); }
}
