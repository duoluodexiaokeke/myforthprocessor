/**
 * @(#)Context.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.tools.javac.v8.util;

/**
 *
 * Support for an abstract context, modelled loosely after ThreadLocal
 * but using a user-provided context instead of the current thread.
 *
 * Within the compiler, a single Context is used for each invocation of
 * the compiler.  The context is then used to ensure a single copy of
 * each compiler phase exists per compiler invocation.
 *
 * <p>Typical usage pattern is:
 * <pre>
 * public class Phase {
 *     private static final Context.Key<Phase> phaseKey = new Context.Key<Phase>();
 *
 *     public static Phase instance(Context context) {
 *	   Phase instance = context.get(phaseKey);
 *	   if (instance == null)
 *	       instance = new Phase(context);
 *	   return instance;
 *     }
 *
 *     protected Phase(Context context) {
 *	   context.put(thaseKey, this);
 *	   // other intitialization follows...
 *     }
 * }
 * </pre>
 */
public class Context {

    /**
     * The client creates an instance of this class for each key.
     */
    public static class Key {

        public Key() {
            super();
        }
    }

    /**
      *
      * The underlying map storing the data.
      * We maintain the invariant that this table contains only
      * mappings of the form
      *     Key<T> -> T
      */
    private Hashtable ht = Hashtable.make();

    /**
     * Set the value for the key in this context.
     */
    public void put(Key key, Object data) {
        if (ht.put(key, data) != null)
            throw new AssertionError("duplicate context value");
    }

    /**
      * Get the value for the key in this context.
      */
    public Object get(Key key) {
        return (Object) ht.get(key);
    }

    public Context() {
        super();
    }
}
