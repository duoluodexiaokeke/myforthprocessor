/*
 * Copyright 2003 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

/*
 * @(#)AuthTime.java	1.5 03/06/24
 *
 * Portions Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * ===========================================================================
 *  IBM Confidential
 *  OCO Source Materials
 *  Licensed Materials - Property of IBM
 * 
 *  (C) Copyright IBM Corp. 1999 All Rights Reserved.
 * 
 *  The source code for this program is not published or otherwise divested of
 *  its trade secrets, irrespective of what has been deposited with the U.S.
 *  Copyright Office.
 * 
 *  Copyright 1997 The Open Group Research Institute.  All rights reserved.
 * ===========================================================================
 * 
 */
package sun.security.krb5.internal.rcache;

import sun.security.krb5.internal.KerberosTime;

/**
 * The class represents the timestamp in authenticator.
 *
 * @author Yanni Zhang
 * @version 1.00 10 Jul 00
 */
public class AuthTime {
    long kerberosTime;
    int cusec;

    /**
     * Constructs a new <code>AuthTime</code>.
     * @param time time from the <code>Authenticator</code>.
     * @param cusec microsecond field from the <code>Authenticator</code>.
     */
    public AuthTime(long time, int c) {
        kerberosTime = time;
        cusec = c;
    }

    /**
     * Compares if an object equals to an <code>AuthTime</code> object.
     * @param o an object.
     * @return true if two objects are equivalent, otherwise, return false.
     */
    public boolean equals(Object o) {
        if (o instanceof AuthTime) {
            if ((((AuthTime)o).kerberosTime == kerberosTime) 
                && (((AuthTime)o).cusec == cusec)) {
                return true;
            }
        }
        return false;
    }
}
