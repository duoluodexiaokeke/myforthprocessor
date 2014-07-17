/*
 * @(#)ExecOptionPermission.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.rmi.rmid;

import java.security.*;
import java.io.*;
import java.util.*;

/**
 * The ExecOptionPermission class represents permission for rmid to use
 * a specific command-line option when launching an activation group.
 * <P>
 * @version 1.5, 01/23/03
 *
 * @author Ann Wollrath
 *
 * @serial exclude
 */
public final class ExecOptionPermission extends Permission
{
    /**
     * does this permission have a wildcard at the end?
     */
    private transient boolean wildcard;

    /**
     * the name without the wildcard on the end
     */
    private transient String name;

    /**
     * UID for serialization
     */
    private static final long serialVersionUID = 5842294756823092756L;
    
    public ExecOptionPermission(String name) {
	super(name);
	init(name);
    }

    public ExecOptionPermission(String name, String actions) {
	this(name);
    }

    /**
     * Checks if the specified permission is "implied" by
     * this object.
     * <P>
     * More specifically, this method returns true if:<p>
     * <ul>
     * <li> <i>p</i>'s class is the same as this object's class, and<p>
     * <li> <i>p</i>'s name equals or (in the case of wildcards)
     *      is implied by this object's
     *      name. For example, "a.b.*" implies "a.b.c", and
     *      "a.b=*" implies "a.b=c"
     * </ul>
     *
     * @param p the permission to check against.
     *
     * @return true if the passed permission is equal to or
     * implied by this permission, false otherwise.
     */
    public boolean implies(Permission p) {
	if (!(p instanceof ExecOptionPermission))
	    return false;

	ExecOptionPermission that = (ExecOptionPermission) p;

	if (this.wildcard) {
	    if (that.wildcard) {
		// one wildcard can imply another
		return that.name.startsWith(name);
	    } else {
		// make sure p.name is longer so a.b.* doesn't imply a.b
		return (that.name.length() > this.name.length()) &&
		    that.name.startsWith(this.name);
	    }
	} else {
	    if (that.wildcard) {
		// a non-wildcard can't imply a wildcard
		return false;
	    } else {
		return this.name.equals(that.name);
	    }
	}
    }

    /**
     * Checks two ExecOptionPermission objects for equality.
     * Checks that <i>obj</i>'s class is the same as this object's class
     * and has the same name as this object.
     * <P>
     * @param obj the object we are testing for equality with this object.
     * @return true if <i>obj</i> is an ExecOptionPermission, and has the same
     * name as this ExecOptionPermission object, false otherwise.
     */
    public boolean equals(Object obj) {
	if (obj == this)
	    return true;

	if ((obj == null) || (obj.getClass() != getClass()))
	    return false;

	ExecOptionPermission that = (ExecOptionPermission) obj;

	return this.getName().equals(that.getName());
    }


    /**
     * Returns the hash code value for this object.
     * The hash code used is the hash code of the name, that is,
     * <code>getName().hashCode()</code>, where <code>getName</code> is
     * from the Permission superclass.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return this.getName().hashCode();
    }

    /**
     * Returns the canonical string representation of the actions.
     *
     * @return the canonical string representation of the actions.
     */
    public String getActions() {
	return "";
    }
    
    /**
     * Returns a new PermissionCollection object for storing
     * ExecOptionPermission objects.
     * <p>
     * A ExecOptionPermissionCollection stores a collection of
     * ExecOptionPermission permissions.
     *
     * <p>ExecOptionPermission objects must be stored in a manner that allows
     * them to be inserted in any order, but that also enables the
     * PermissionCollection <code>implies</code> method
     * to be implemented in an efficient (and consistent) manner.
     *
     * @return a new PermissionCollection object suitable for
     * storing ExecOptionPermissions.
     */
    public PermissionCollection newPermissionCollection() {
	return new ExecOptionPermissionCollection();
    }

    /**
     * readObject is called to restore the state of the ExecOptionPermission
     * from a stream. 
     */
    private synchronized void readObject(java.io.ObjectInputStream s)
         throws IOException, ClassNotFoundException
    {
	s.defaultReadObject();
	// init is called to initialize the rest of the values.
	init(getName());
    }
    
    /**
     * Initialize a ExecOptionPermission object. Common to all constructors.
     * Also called during de-serialization.
     */
    private void init(String name) 
    {
	if (name == null)
	    throw new NullPointerException("name can't be null");

	if (name.equals("")) {
	    throw new IllegalArgumentException("name can't be empty");
	}

	if (name.endsWith(".*") || name.endsWith("=*") || name.equals("*")) {
	    wildcard = true;
	    if (name.length() == 1) {
		this.name = "";
	    } else {
		this.name = name.substring(0, name.length()-1);
	    }
	} else {
	    this.name = name;
	}
    }

    /**
     * A ExecOptionPermissionCollection stores a collection
     * of ExecOptionPermission permissions. ExecOptionPermission objects
     * must be stored in a manner that allows them to be inserted in any
     * order, but enable the implies function to evaluate the implies
     * method in an efficient (and consistent) manner.
     *
     * A ExecOptionPermissionCollection handles comparing a permission like
     * "a.b.c.d.e" * with a Permission such as "a.b.*", or "*".
     *
     * @serial include
     */
    private static class ExecOptionPermissionCollection
        extends PermissionCollection
	implements java.io.Serializable
    {

	private Hashtable permissions;
	private boolean all_allowed; // true if "*" is in the collection
	private static final long serialVersionUID = -1242475729790124375L;
	
	/**
	 * Create an empty ExecOptionPermissionCollection.
	 */
	public ExecOptionPermissionCollection() {
	    permissions = new Hashtable(11);
	    all_allowed = false;
	}

	/**
	 * Adds a permission to the collection. The key for the hash is
	 * permission.name.
	 *
	 * @param permission the Permission object to add.
	 *
	 * @exception IllegalArgumentException - if the permission is not a
	 *                                       ExecOptionPermission
	 *
	 * @exception SecurityException - if this ExecOptionPermissionCollection 
	 *                                object has been marked readonly
	 */

	public void add(Permission permission)
	{
	    if (! (permission instanceof ExecOptionPermission))
		throw new IllegalArgumentException("invalid permission: "+
						   permission);
	    if (isReadOnly())
		throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");

	    ExecOptionPermission p = (ExecOptionPermission) permission;

	    permissions.put(p.getName(), permission);
	    if (!all_allowed) {
		if (p.getName().equals("*"))
		    all_allowed = true;
	    }
	}

	/**
	 * Check and see if this set of permissions implies the permissions
	 * expressed in "permission".
	 *
	 * @param p the Permission object to compare
	 *
	 * @return true if "permission" is a proper subset of a permission in
	 * the set, false if not.
	 */
	public boolean implies(Permission permission)
	{
	    if (! (permission instanceof ExecOptionPermission))
   		return false;

	    ExecOptionPermission p = (ExecOptionPermission) permission;

	    // short circuit if the "*" Permission was added
	    if (all_allowed)
		return true;

	    // strategy:
	    // Check for full match first. Then work our way up the
	    // name looking for matches on a.b.*

	    String pname = p.getName();

	    Permission x = (Permission) permissions.get(pname);

	    if (x != null)
		// we have a direct hit!
		return x.implies(permission);


	    // work our way up the tree...
	    int last, offset;

	    offset = pname.length() - 1;

	    while ((last = pname.lastIndexOf(".", offset)) != -1) {

		pname = pname.substring(0, last+1) + "*";
		x = (Permission) permissions.get(pname);

		if (x != null) {
		    return x.implies(permission);
		}
		offset = last - 1;
	    }

	    // check for "=*" wildcard match
	    pname = p.getName();
	    offset = pname.length() - 1;
	    
	    while ((last = pname.lastIndexOf("=", offset)) != -1) {
		
		pname = pname.substring(0, last+1) + "*";
		x = (Permission) permissions.get(pname);

		if (x != null) {
		    return x.implies(permission);
		}
		offset = last - 1;
	    }

	    // we don't have to check for "*" as it was already checked
	    // at the top (all_allowed), so we just return false
	    return false;
	}

	/**
	 * Returns an enumeration of all the ExecOptionPermission objects in the
	 * container.
	 *
	 * @return an enumeration of all the ExecOptionPermission objects.
	 */

	public Enumeration elements()
	{
	    return permissions.elements();
	}
    }
}