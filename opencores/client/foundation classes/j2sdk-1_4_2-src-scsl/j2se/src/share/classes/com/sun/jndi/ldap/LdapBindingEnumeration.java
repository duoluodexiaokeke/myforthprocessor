/*
 * @(#)LdapBindingEnumeration.java	1.6 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jndi.ldap;

import java.util.Vector;
import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.spi.*;

import com.sun.jndi.toolkit.ctx.Continuation;

final class LdapBindingEnumeration extends LdapNamingEnumeration {

    LdapBindingEnumeration(LdapCtx homeCtx, LdapResult answer, Name remain,
	Continuation cont) throws NamingException
    {
	super(homeCtx, answer, remain, cont);
    }

    protected NameClassPair 
      createItem(String dn, Attributes attrs, Vector respCtls)
	throws NamingException {

	Object obj = null;
	String atom = getAtom(dn);

	if (attrs.get(Obj.JAVA_ATTRIBUTES[Obj.CLASSNAME]) != null) {
	    // serialized object or object reference
	    obj = Obj.decodeObject(attrs);
	} 
	if (obj == null) {
	    // DirContext object
	    obj = new LdapCtx(homeCtx, dn);
	}

	CompositeName cn = new CompositeName();
	cn.add(atom);

	try {
	    obj = DirectoryManager.getObjectInstance(obj, cn, homeCtx, 
		homeCtx.envprops, attrs);

	} catch (NamingException e) {
	    throw e;

	} catch (Exception e) {
	    NamingException ne = 
		new NamingException(
			"problem generating object using object factory");
	    ne.setRootCause(e);
	    throw ne;
	}

	if (respCtls != null) {
	    return new BindingWithControls(cn.toString(), obj,
		homeCtx.convertControls(respCtls));
	} else {
	    return new Binding(cn.toString(), obj);
	}
    }

    protected LdapNamingEnumeration
    getReferredResults(LdapReferralContext refCtx) throws NamingException{
	// repeat the original operation at the new context
	return (LdapNamingEnumeration) refCtx.listBindings(listArg);
    }
}