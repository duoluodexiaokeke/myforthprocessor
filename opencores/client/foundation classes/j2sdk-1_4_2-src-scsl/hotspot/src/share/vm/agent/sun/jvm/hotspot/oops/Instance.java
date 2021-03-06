/*
 * @(#)Instance.java	1.6 03/01/23 11:42:49
 *
 * Copyright 2003 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

package sun.jvm.hotspot.oops;

import java.io.*;
import java.util.*;
import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.runtime.*;
import sun.jvm.hotspot.types.*;

// An Instance is an instance of a Java Class

public class Instance extends Oop {
  static {
    VM.registerVMInitializedObserver(new Observer() {
        public void update(Observable o, Object data) {
          initialize(VM.getVM().getTypeDataBase());
        }
      });
  }
  
  private static synchronized void initialize(TypeDataBase db) throws WrongTypeException {
    Type type = db.lookupType("instanceOopDesc");
  }

  public Instance(OopHandle handle, ObjectHeap heap) {
    super(handle, heap);
  }

  public boolean isInstance()          { return true; }

  public void iterateFields(OopVisitor visitor, boolean doVMFields) {
    super.iterateFields(visitor, doVMFields);
    ((InstanceKlass) getKlass()).iterateNonStaticFields(visitor);
  }

  public void printValueOn(PrintStream tty) {
    // Special-case strings.
    // FIXME: would like to do this in more type-safe fashion (need
    // SystemDictionary analogue)
    if (getKlass().getName().asString().equals("java/lang/String")) {
      tty.print("\"" + OopUtilities.stringOopToString(this) + "\"");
    } else {
      super.printValueOn(tty);
    }
  }
}
