/*
 * @(#)ArrayObjectTypeNode.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.tools.jdwpgen;

import java.util.*;

class ArrayObjectTypeNode extends ObjectTypeNode {

    String docType() {
        return "arrayID";
    }

    String javaType() {
        return "ArrayReferenceImpl";
    }

    String javaRead() {
        return "ps.readArrayReference()";
    }
}