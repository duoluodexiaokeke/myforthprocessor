#ifdef USE_PRAGMA_IDENT_SRC
#pragma ident "@(#)port.cpp	1.14 03/01/23 12:06:13 JVM"
#endif
/*
 * Copyright 2003 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

// Code for portable compiling

#ifdef __GNUC__
#pragma implementation
#endif

#include "incls/_precompiled.incl"
#include "incls/_port.cpp.incl"

// %%%%% includes not needed with AVM framework - Ungar
// #include "port.hpp"

// This is only used if turboc is used and it causes problems with
// gcc.
#ifdef __TURBOC__
#include <iostream.h>
#endif

#include <stdio.h>

//------------------------------gcd--------------------------------------------
// Greatest common divisor
uint32 gcd( register uint32 x, register uint32 y )
{
  register uint32 tmp;
  while( x ) {                  // While not zero
    tmp = x;			// Hold onto smaller x value
    x = y % x;			// Compute modulus; since y>=x, 0 <= mod < x
    y = tmp;			// y = old x
  }
  return y;
}

//-----------------------------------------------------------------------------
// Find first 1, or return 32 if empty
int ff1( uint32 mask )
{
  unsigned i, n = 0;

  for( i=1, n=0; i; i<<=1, n++)
    if( mask&i ) return n;
  return 32;
}

//-----------------------------------------------------------------------------
// Find highest 1, or return 32 if empty
int fh1( uint32 mask )
{
  unsigned i, n = 0;

  for( i=((uint32)1<<31), n=31; i; i>>=1, n--)
    if( mask&i ) return n;
  return 32;
}

//------------------------------rotate32---------------------------------------
// Rotate 32bits.  Postive rotates left (bits move toward high-order bit),
// negative rotates right.
uint32 rotate32( register uint32 x, register int32 cnt )
{
  if( cnt >= 0 ) {		// Positive rotates left
    cnt &= 31;			// Mask off extra shift bits
  } else {			// Negative rotates right
    cnt = (-cnt)&31;		// Flip sign; mask extra shift bits
    cnt = 32-cnt;		// Rotate right by big left rotation
  }
  return (x << cnt) | (x >> (32-cnt));
}

/* Disabled - we have another log2 in the system.
   This function doesn't work if used as substitute
   for the existing log2. Keep around until we have
   verified all uses of log2 do the correct thing!
//------------------------------log2-------------------------------------------
// Log base 2.  Might also be called 'count leading zeros'.  Log2(x) returns 
// an l such that (1L<<l) <= x < (2L<<l).  log2(x) returns 32.
uint log2( uint32 x )
{
  register uint l = 32;		// Log bits
  register int32 sx = x;	// Treat as signed number
  while( sx >= 0 )		// While high bit is clear
    sx <<= 1, l--;		// Shift bits left, count down log2
  return l;
}
*/

//------------------------------print------------------------------------------
// Print a pointer without modifying the contents
#ifdef __TURBOC__
ostream &ostream::operator << (const void *ptr)
{
  return (*this) << "0x" << hex << (uint)ptr << dec;
}
#else
/*ostream &operator << (ostream &os, const void *ptr)
{
  return os << "0x" << hex << (uint)ptr << dec;
}*/
#endif