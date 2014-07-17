/*
 * @(#)BatchEnvironment.java	1.19 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package sun.rmi.rmic.iiop;

import sun.rmi.rmic.Main;
import sun.tools.java.ClassPath;
import java.io.OutputStream;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.Identifier;
import sun.tools.java.ClassNotFound;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * BatchEnvironment for iiop extends rmic's version to add
 * parse state.
 */
public class BatchEnvironment extends sun.rmi.rmic.BatchEnvironment implements Constants {

    /*
     * If the following flag is true, then the IDL generator can map
     * the methods and constants of non-conforming types. However,
     * this is very expensive, so the default should be false.
     */
    private boolean parseNonConforming = false;
    
    /* Common objects used within package */
    
    HashSet alreadyChecked = new HashSet();
    Hashtable allTypes = new Hashtable(3001, 0.5f);
    Hashtable invalidTypes = new Hashtable(256, 0.5f);
    DirectoryLoader loader = null;
    ClassPathLoader classPathLoader = null;
    Hashtable nameContexts = null;
    Hashtable namesCache = new Hashtable();
    NameContext modulesContext = new NameContext(false);    
    
    ClassDefinition defRemote = null;
    ClassDefinition defError = null;
    ClassDefinition defException = null;
    ClassDefinition defRemoteException = null;
    ClassDefinition defCorbaObject = null;
    ClassDefinition defSerializable = null;
    ClassDefinition defExternalizable = null;
    ClassDefinition defThrowable = null;
    ClassDefinition defRuntimeException = null;
    ClassDefinition defIDLEntity = null;
    ClassDefinition defValueBase = null;
    
    sun.tools.java.Type typeRemoteException = null;
    sun.tools.java.Type typeIOException = null;
    sun.tools.java.Type typeException = null;
    sun.tools.java.Type typeThrowable = null;
    
    ContextStack contextStack = null;
    
    /**
     * Create a BatchEnvironment for rmic with the given class path,
     * stream for messages and Main.
     */
    public BatchEnvironment(OutputStream out, ClassPath path, Main main) {
    	
    	super(out,path,main);

        // Make sure we have our definitions...

    	try {
	    defRemote =
		getClassDeclaration(idRemote).getClassDefinition(this);
    	    defError =
    	        getClassDeclaration(idJavaLangError).getClassDefinition(this);
    	    defException =
    	        getClassDeclaration(idJavaLangException).getClassDefinition(this);
    	    defRemoteException =
    	        getClassDeclaration(idRemoteException).getClassDefinition(this);
    	    defCorbaObject =
    	        getClassDeclaration(idCorbaObject).getClassDefinition(this);
    	    defSerializable =
    	        getClassDeclaration(idJavaIoSerializable).getClassDefinition(this);
    	    defRuntimeException =
    	        getClassDeclaration(idJavaLangRuntimeException).getClassDefinition(this);    
    	    defExternalizable =
    	        getClassDeclaration(idJavaIoExternalizable).getClassDefinition(this);    
    	    defThrowable=
    	        getClassDeclaration(idJavaLangThrowable).getClassDefinition(this);    
    	    defIDLEntity=
    	        getClassDeclaration(idIDLEntity).getClassDefinition(this);    
    	    defValueBase=
    	        getClassDeclaration(idValueBase).getClassDefinition(this);    
    	    typeRemoteException = defRemoteException.getClassDeclaration().getType();
    	    typeException = defException.getClassDeclaration().getType();
            typeIOException = getClassDeclaration(idJavaIoIOException).getType();
            typeThrowable = getClassDeclaration(idJavaLangThrowable).getType();

            classPathLoader = new ClassPathLoader(path);
    	    
    	} catch (ClassNotFound e) {
    	    error(0, "rmic.class.not.found", e.name);
    	    throw new Error();
    	}
    }

    /**
     * Return whether or not to parse non-conforming types.
     */
    public boolean getParseNonConforming () {
	return parseNonConforming;
    }
    
    /**
     * Set whether or not to parse non-conforming types.
     */
    public void setParseNonConforming (boolean parseEm) {
	    
	// If we are transitioning from not parsing to
	// parsing, we need to throw out any previously
	// parsed types...
	    
	if (parseEm && !parseNonConforming) {
	    reset();    
	}
	    
	parseNonConforming = parseEm;
    }
    
    /**
     * Clear out any data from previous executions.
     */
    public void reset () {
        
        // First, find all Type instances and call destroy()
        // on them...

        for (Enumeration e = allTypes.elements() ; e.hasMoreElements() ;) {
            Type type = (Type) e.nextElement();
            type.destroy();
        }

        for (Enumeration e = invalidTypes.keys() ; e.hasMoreElements() ;) {
            Type type = (Type) e.nextElement();
            type.destroy();
        }

        for (Iterator e = alreadyChecked.iterator() ; e.hasNext() ;) {
            Type type = (Type) e.next();
            type.destroy();
        }
        
        if (contextStack != null) contextStack.clear();
        
        // Remove and clear all NameContexts in the
        // nameContexts cache...
            
        if (nameContexts != null) {
            for (Enumeration e = nameContexts.elements() ; e.hasMoreElements() ;) {
                NameContext context = (NameContext) e.nextElement();
                context.clear();
            }
            nameContexts.clear();
        }

        // Now remove all table entries...

	allTypes.clear();
	invalidTypes.clear();
        alreadyChecked.clear();
        namesCache.clear();
        modulesContext.clear();
        
        // Clean up remaining...
        loader = null;
	parseNonConforming = false;

        // REVISIT - can't clean up classPathLoader here
    }

    /**
     * Release resources, if any.
     */
    public void shutdown() {
        if (alreadyChecked != null) {
	    //System.out.println();
	    //System.out.println("allTypes.size() = "+ allTypes.size());
	    //System.out.println("    InstanceCount before reset = "+Type.instanceCount);
            reset();
	    //System.out.println("    InstanceCount AFTER reset = "+Type.instanceCount);

            alreadyChecked = null;
	    allTypes = null;
	    invalidTypes = null;
            nameContexts = null;
            namesCache = null;
            modulesContext = null;
            defRemote = null;
            defError = null;
            defException = null;
            defRemoteException = null;
            defCorbaObject = null;
            defSerializable = null;
            defExternalizable = null;
            defThrowable = null;
            defRuntimeException = null;
            defIDLEntity = null;
            defValueBase = null;
            typeRemoteException = null;
            typeIOException = null;
            typeException = null;
            typeThrowable = null;
            
            super.shutdown();
        }
    }
}