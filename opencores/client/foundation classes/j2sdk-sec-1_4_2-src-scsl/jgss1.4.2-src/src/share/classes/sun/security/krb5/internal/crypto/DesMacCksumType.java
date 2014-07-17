/*
 * Copyright 2003 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

/*
 * @(#)DesMacCksumType.java	1.7 03/06/24
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

package sun.security.krb5.internal.crypto;

import sun.security.krb5.Checksum;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.*;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;

public class DesMacCksumType extends CksumType {

    public DesMacCksumType() {
    }

    public int confounderSize() {
	return 8;
    }

    public int cksumType() {
	return Checksum.CKSUMTYPE_DES_MAC;
    }

    public boolean isSafe() {
	return true;
    }

    public int cksumSize() {
	return 16;
    }

    public int keyType() {
	return Krb5.KEYTYPE_DES;
    }

    public int keySize() {
	return 8;
    }

    public byte[] calculateChecksum(byte[] data, int size) {
	return null;
    }

    /**
     * Calculates keyed checksum.
     * @param data the data used to generate the checksum.
     * @param size length of the data.
     * @param key the key used to encrypt the checksum.
     * @return keyed checksum.
     *
     * @modified by Yanni Zhang, 12/08/99. 
     */
    public byte[] calculateKeyedChecksum(byte[] data, int size, byte[] key) throws KrbCryptoException {  
	byte[] new_data = new byte[size + confounderSize()];
	byte[] conf = Confounder.bytes(confounderSize());
	System.arraycopy(conf, 0, new_data, 0, confounderSize());
	System.arraycopy(data, 0, new_data, confounderSize(), size);

	//check for weak keys
	try {
	    if (DESKeySpec.isWeak(key, 0)) {
		key[7] = (byte)(key[7] ^ 0xF0);
	    }	
	} catch (InvalidKeyException ex) {
	    // swallow, since it should never happen
	}
	byte[] residue_ivec = new byte[key.length];
	byte[] residue = Des.des_cksum(residue_ivec, new_data, key);
	byte[] cksum = new byte[cksumSize()];
	System.arraycopy(conf, 0, cksum, 0, confounderSize());
	System.arraycopy(residue, 0, cksum, confounderSize(),
			 cksumSize() - confounderSize());

	byte[] new_key = new byte[keySize()];
	System.arraycopy(key, 0, new_key, 0, key.length);
	for (int i = 0; i < new_key.length; i++)
	new_key[i] = (byte)(new_key[i] ^ 0xf0); 
	//check for weak keys
	try {
	    if (DESKeySpec.isWeak(new_key, 0)) {
		new_key[7] = (byte)(new_key[7] ^ 0xF0);
	    }	
	} catch (InvalidKeyException ex) {
	    // swallow, since it should never happen
	}
	byte[] ivec = new byte[new_key.length];

	//des-cbc encrypt
	byte[] enc_cksum = new byte[cksum.length];
	Des.cbc_encrypt(cksum, enc_cksum, new_key, ivec, true);
	return enc_cksum;
    }

    /**
     * Verifies keyed checksum.
     * @param data the data.
     * @param size the length of data.
     * @param key the key used to encrypt the checksum.
     * @param checksum
     * @return true if verification is successful.
     *
     * @modified by Yanni Zhang, 12/08/99. 
     */
    public boolean verifyKeyedChecksum(byte[] data, int size,
				       byte[] key, byte[] checksum) throws KrbCryptoException {
	byte[] cksum = decryptKeyedChecksum(checksum, key);

	byte[] new_data = new byte[size + confounderSize()];
	System.arraycopy(cksum, 0, new_data, 0, confounderSize());
	System.arraycopy(data, 0, new_data, confounderSize(), size);

	//check for weak keys
	try {
	    if (DESKeySpec.isWeak(key, 0)) {			   
		key[7] = (byte)(key[7] ^ 0xF0);
	    }
	} catch (InvalidKeyException ex) {
	    // swallow, since it should never happen
	}
	byte[] ivec = new byte[key.length];
	byte[] new_cksum = Des.des_cksum(ivec, new_data, key);
	byte[] orig_cksum = new byte[cksumSize() - confounderSize()];
	System.arraycopy(cksum,  confounderSize(), orig_cksum, 0,
			 cksumSize() - confounderSize());
	return isChecksumEqual(orig_cksum, new_cksum);
    }

    /**
     * Decrypts keyed checksum.
     * @param enc_cksum the buffer for encrypted checksum.
     * @param key the key.
     * @return the checksum.
     *
     * @modified by Yanni Zhang, 12/08/99.
     */
    private byte[] decryptKeyedChecksum(byte[] enc_cksum, byte[] key) throws KrbCryptoException {	 
	byte[] new_key = new byte[keySize()];
	System.arraycopy(key, 0, new_key, 0, key.length);
	for (int i = 0; i < new_key.length; i++)
	new_key[i] = (byte)(new_key[i] ^ 0xf0); 
	//check for weak keys
	try {
	    if (DESKeySpec.isWeak(new_key, 0)) {
		new_key[7] = (byte)(new_key[7] ^ 0xF0);
	    }
	} catch (InvalidKeyException ex) {
	    // swallow, since it should never happen
	}
	byte[] ivec = new byte[new_key.length];
	byte[] cksum = new byte[enc_cksum.length];
	Des.cbc_encrypt(enc_cksum, cksum, new_key, ivec, false);  
	return cksum;
    }

}