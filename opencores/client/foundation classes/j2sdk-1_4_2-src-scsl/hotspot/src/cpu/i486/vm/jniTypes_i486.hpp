#ifdef USE_PRAGMA_IDENT_HDR
#pragma ident "@(#)jniTypes_i486.hpp	1.12 03/01/23 10:55:06 JVM"
#endif
/*
 * Copyright 2003 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

// This file holds platform-dependent routines used to write primitive jni 
// types to the array of arguments passed into JavaCalls::call

class JNITypes : AllStatic {
  // These functions write a java primitive type (in native format)
  // to a java stack slot array to be passed as an argument to JavaCalls:calls.
  // I.e., they are functionally 'push' operations if they have a 'pos'
  // formal parameter.  Note that jlong's and jdouble's are written
  // _in reverse_ of the order in which they appear in the interpreter
  // stack.  This is because call stubs (see stubGenerator_sparc.cpp)
  // reverse the argument list constructed by JavaCallArguments (see
  // javaCalls.hpp).

private:
  // Helper routines.
  static inline void    put_int2r(jint *from, intptr_t *to)	      { *(jint *)(to++) = from[1];
                                                                        *(jint *)(to  ) = from[0]; }
  static inline void    put_int2r(jint *from, intptr_t *to, int& pos) { put_int2r(from, to + pos); pos += 2; }

public:
  // Ints are stored in native format in one JavaCallArgument slot at *to.
  static inline void    put_int(jint  from, intptr_t *to)	    { *(jint *)(to +   0  ) =  from; }
  static inline void    put_int(jint  from, intptr_t *to, int& pos) { *(jint *)(to + pos++) =  from; }
  static inline void    put_int(jint *from, intptr_t *to, int& pos) { *(jint *)(to + pos++) = *from; }

  // Longs are stored in big-endian word format in two JavaCallArgument slots at *to.
  // The high half is in *to and the low half in *(to+1).
  static inline void    put_long(jlong  from, intptr_t *to)	      { put_int2r((jint *)&from, to); }
  static inline void    put_long(jlong  from, intptr_t *to, int& pos) { put_int2r((jint *)&from, to, pos); }
  static inline void    put_long(jlong *from, intptr_t *to, int& pos) { put_int2r((jint *) from, to, pos); }

  // Oops are stored in native format in one JavaCallArgument slot at *to.
  static inline void    put_obj(oop  from, intptr_t *to)	   { *(oop *)(to +   0  ) =  from; }
  static inline void    put_obj(oop  from, intptr_t *to, int& pos) { *(oop *)(to + pos++) =  from; }
  static inline void    put_obj(oop *from, intptr_t *to, int& pos) { *(oop *)(to + pos++) = *from; }

  // Floats are stored in native format in one JavaCallArgument slot at *to.
  static inline void    put_float(jfloat  from, intptr_t *to)   	{ *(jfloat *)(to +   0  ) =  from;  }
  static inline void    put_float(jfloat  from, intptr_t *to, int& pos) { *(jfloat *)(to + pos++) =  from; }
  static inline void    put_float(jfloat *from, intptr_t *to, int& pos) { *(jfloat *)(to + pos++) = *from; }

  // Doubles are stored in big-endian word format in two JavaCallArgument slots at *to.
  // The high half is in *to and the low half in *(to+1).
  static inline void    put_double(jdouble  from, intptr_t *to)	          { put_int2r((jint *)&from, to); }
  static inline void    put_double(jdouble  from, intptr_t *to, int& pos) { put_int2r((jint *)&from, to, pos); }
  static inline void    put_double(jdouble *from, intptr_t *to, int& pos) { put_int2r((jint *) from, to, pos); }


  // The get_xxx routines, on the other hand, actually _do_ fetch
  // java primitive types from the interpreter stack.
  // No need to worry about alignment on Intel.
  static inline jint    get_int   (intptr_t *from) { return *(jint *)   from; }
  static inline jlong   get_long  (intptr_t *from) { return *(jlong *)  from; }
  static inline oop     get_obj   (intptr_t *from) { return *(oop *)    from; }
  static inline jfloat  get_float (intptr_t *from) { return *(jfloat *) from; }
  static inline jdouble get_double(intptr_t *from) { return *(jdouble *)from; }


  // And last, but certainly not least, we have the put_stack_xxx routines, which
  // actually _do_ store java primitive types into the interpreter stack.
  static inline void    put_stack_int   (jint    from, intptr_t *to) { *(jint *)   to = from; }
  static inline void    put_stack_long  (jlong   from, intptr_t *to) { *(jlong *)  to = from; }
  static inline void    put_stack_obj   (oop     from, intptr_t *to) { *(oop *)    to = from; }
  static inline void    put_stack_float (jfloat  from, intptr_t *to) { *(jfloat *) to = from; }
  static inline void    put_stack_double(jdouble from, intptr_t *to) { *(jdouble *)to = from; }


  // Well, not quite last: the "raw" functions below do not swap words.
  // I can't imagine what they're used for, since they're processor dependent.
  // Here on Intel, they're equivalent to get_xxx and put_stack_xxx.
  static inline void    put_raw_int   (jint *   from, intptr_t *to) { *(jint *)   to = *from; }
  static inline void    put_raw_long  (jlong *  from, intptr_t *to) { *(jlong *)  to = *from; }
  static inline void    put_raw_obj   (oop *    from, intptr_t *to) { *(oop *)    to = *from; }
  static inline void    put_raw_float (jfloat * from, intptr_t *to) { *(jfloat *) to = *from; }
  static inline void    put_raw_double(jdouble *from, intptr_t *to) { *(jdouble *)to = *from; }

  static inline jint    get_raw_int   (intptr_t *from)	{ return *(jint *)   from; }
  static inline jlong   get_raw_long  (intptr_t *from)  { return *(jlong *)  from; }
  static inline oop     get_raw_obj   (intptr_t *from)  { return *(oop *)    from; }
  static inline jfloat  get_raw_float (intptr_t *from)  { return *(jfloat *) from; }
  static inline jdouble get_raw_double(intptr_t *from)  { return *(jdouble *)from; }
};
