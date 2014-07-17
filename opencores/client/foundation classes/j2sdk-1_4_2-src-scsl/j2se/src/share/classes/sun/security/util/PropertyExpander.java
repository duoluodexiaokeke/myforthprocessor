/*
 * @(#)PropertyExpander.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.security.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

/**
 * A utility class to expand properties embedded in a string.
 * Strings of the form ${some.property.name} are expanded to
 * be the value of the property. Also, the special ${/} property
 * is expanded to be the same as file.separator. If a property
 * is not set, a GeneralSecurityException will be thrown.
 *
 * @version 1.12
 * @author Roland Schemers
 */
public class PropertyExpander {


    public static class ExpandException extends GeneralSecurityException {
	public ExpandException(String msg) {
	    super(msg);
	}
    }

    public static String expand(String value)
	throws ExpandException
    {
        return expand(value, false);
    }
 
     public static String expand(String value, boolean encodeURL)
         throws ExpandException
     {
	if (value == null)
	    return null;

	int p = value.indexOf("${", 0);

	// no special characters
	if (p == -1) return value;

	StringBuffer sb = new StringBuffer(value.length());
	int max = value.length();
	int i = 0;  // index of last character we copied

    scanner:
	while (p < max) {
	    if (p > i) {
		// copy in anything before the special stuff
		sb.append(value.substring(i, p));
		i = p;
	    }
	    int pe = p+2;

	    // do not expand ${{ ... }}
	    if (pe < max && value.charAt(pe) == '{') {
		pe = value.indexOf("}}", pe);
		if (pe == -1 || pe+2 == max) {
		    // append remaining chars
		    sb.append(value.substring(p));
		    break scanner;
		} else {
		    // append as normal text
		    pe++;
		    sb.append(value.substring(p, pe+1));
		}
	    } else {
		while ((pe < max) && (value.charAt(pe) != '}')) {
		    pe++;
		}
		if (pe == max) {
		    // no matching '}' found, just add in as normal text
		    sb.append(value.substring(p, pe));
		    break scanner;
		}
		String prop = value.substring(p+2, pe);
		if (prop.equals("/")) {
		    sb.append(java.io.File.separatorChar);
		} else {
		    String val = System.getProperty(prop);
		    if (val != null) {
			if (encodeURL) {
			    // encode 'val' unless it's an absolute URI
			    // at the beginning of the string buffer
			    try {
				if (sb.length() > 0 ||
				    !(new URI(val)).isAbsolute()) {
				    val = sun.net.www.ParseUtil.encodePath(val);
				}
			    } catch (URISyntaxException use) {
				val = sun.net.www.ParseUtil.encodePath(val);
			    }
			}
			sb.append(val);
		    } else {
			throw new ExpandException(
					     "unable to expand property " + 
					     prop);
		    }
		}
	    }
	    i = pe+1;
	    p = value.indexOf("${", i);
	    if (p == -1) {
		// no more to expand. copy in any extra
		if (i < max) {
		    sb.append(value.substring(i, max));
		} 
		// break out of loop
		break scanner;
	    }
	}
	return sb.toString();
    }

    public static void main(String args[]) throws Exception
    {
	System.out.println(expand(args[0]));
    }
}