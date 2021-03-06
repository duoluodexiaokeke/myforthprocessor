/*
 * @(#)DefNewGeneration.java	1.3 03/01/23 11:40:42
 *
 * Copyright 2003 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package sun.jvm.hotspot.memory;

import java.io.*;
import java.util.*;

import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.runtime.*;
import sun.jvm.hotspot.types.*;

/** DefNewGeneration is a young generation containing eden, from- and
    to-space. */

public class DefNewGeneration extends Generation {
  private static AddressField edenSpaceField;
  private static AddressField fromSpaceField;
  private static AddressField toSpaceField;

  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }

  private static synchronized void initialize(TypeDataBase db) {
    Type type = db.lookupType("DefNewGeneration");
    
    edenSpaceField = type.getAddressField("_eden_space");
    fromSpaceField = type.getAddressField("_from_space");
    toSpaceField   = type.getAddressField("_to_space");
  }

  public DefNewGeneration(Address addr) {
    super(addr);
  }
  
  public GenerationName kind() {
    return GenerationName.DEF_NEW;
  }

  // Accessing spaces
  public EdenSpace eden() {
    return (EdenSpace) VMObjectFactory.newObject(EdenSpace.class, edenSpaceField.getValue(addr));
  }

  public ContiguousSpace from() {
    return (ContiguousSpace) VMObjectFactory.newObject(ContiguousSpace.class, fromSpaceField.getValue(addr));
  }

  public ContiguousSpace to() {
    return (ContiguousSpace) VMObjectFactory.newObject(ContiguousSpace.class, toSpaceField.getValue(addr));
  }

  public long capacity()            { return eden().capacity() + from().capacity(); /* to() is only used during scavenge */ }
  public long used()                { return eden().used()     + from().used();     /* to() is only used during scavenge */ }
  public long free()                { return eden().free()     + from().free();     /* to() is only used during scavenge */ }
  public long contiguousAvailable() { return eden().free(); }

  public String name() {
    return "default new generation";
  }

  public void spaceIterate(SpaceClosure blk, boolean usedOnly) {
    blk.doSpace(eden());
    blk.doSpace(from());
    if (!usedOnly) {
      blk.doSpace(to());
    }
  }

  public void printOn(PrintStream tty) {
    tty.print("  eden");
    eden().printOn(tty);
    tty.print("  from");
    from().printOn(tty);
    tty.print("  to  ");
    to().printOn(tty);
  }
}
