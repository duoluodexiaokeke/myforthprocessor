/**
 * @(#)Options.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.tools.javac.v8.util;

/**
 * A table of all command-line options.
 *  If an option has an argument, the option name is mapped to the argument.
 *  If a set option has no argument, it is mapped to itself.
 */
public class Options extends Hashtable {

    /**
     * The context key for the options.
     */
    private static final Context.Key optionsKey = new Context.Key();

    /**
     * Get the Options instance for this context.
     */
    public static Options instance(Context context) {
        Options instance = (Options) context.get(optionsKey);
        if (instance == null)
            instance = new Options(context);
        return instance;
    }

    protected Options(Context context) {
        super();
        context.put(optionsKey, this);
    }

    /*synthetic*/ public Object remove(Object x0) {
        return super.remove((String) x0);
    }

    /*synthetic*/ public Object put(Object x0, Object x1) {
        return super.put((String) x0, (String) x1);
    }

    /*synthetic*/ public Object get(Object x0) {
        return super.get((String) x0);
    }
}
