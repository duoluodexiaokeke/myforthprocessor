/*
 * @(#)FP2RegisterDecoder.java	1.2 03/01/23 11:15:58
 *
 * Copyright 2003 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package sun.jvm.hotspot.asm.sparc;

import sun.jvm.hotspot.asm.*;
import sun.jvm.hotspot.utilities.Assert;

class FP2RegisterDecoder extends FloatDecoder {

    FP2RegisterDecoder(int opf, String name, int srcType, int resultType) {
        super(opf, name, srcType, resultType);
    }

    Instruction decodeFloatInstruction(int instruction,
                                       SPARCRegister rs1, SPARCRegister rs2,
                                       SPARCRegister rd,
                                       SPARCInstructionFactory factory) {
        if (Assert.ASSERTS_ENABLED)
            Assert.that(rs2.isFloat() && rd.isFloat(), "rs2, rd have to be float registers");

        return factory.newFP2RegisterInstruction(name, opf, (SPARCFloatRegister)rs2, (SPARCFloatRegister)rd);
    }
}
