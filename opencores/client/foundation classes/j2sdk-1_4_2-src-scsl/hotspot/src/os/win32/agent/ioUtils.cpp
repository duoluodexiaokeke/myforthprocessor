#ifdef USE_PRAGMA_IDENT_SRC
#pragma ident "@(#)ioUtils.cpp	1.5 03/01/23 11:06:59 JVM"
#endif
/*
 * Copyright 2003 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

#include <ctype.h>
#include <string.h>
#include "ioUtils.hpp"
#include "IOBuf.hpp"

bool
scanInt(char** data, int* num) {
  *num = 0;

  // Skip whitespace
  while ((**data != 0) && (isspace(**data))) {
    ++*data;
  }

  if (**data == 0) {
    return false;
  }

  while ((**data != 0) && (!isspace(**data))) {
    char cur = **data;
    if ((cur < '0') || (cur > '9')) {
      return false;
    }
    *num *= 10;
    *num += cur - '0';
    ++*data;
  }  

  return true;
}

bool
scanUnsignedLong(char** data, unsigned long* num) {
  *num = 0;

  // Skip whitespace
  while ((**data != 0) && (isspace(**data))) {
    ++*data;
  }

  if (**data == 0) {
    return false;
  }

  while ((**data != 0) && (!isspace(**data))) {
    char cur = **data;
    if ((cur < '0') || (cur > '9')) {
      return false;
    }
    *num *= 10;
    *num += cur - '0';
    ++*data;
  }  

  return true;
}

bool
charToNibble(char ascii, int* value) {
  if (ascii >= '0' && ascii <= '9') {
    *value = ascii - '0';
    return true;
  } else if (ascii >= 'A' && ascii <= 'F') {
    *value = 10 + ascii - 'A';
    return true;
  } else if (ascii >= 'a' && ascii <= 'f') {
    *value = 10 + ascii - 'a';
    return true;
  }

  return false;
}

bool
scanAddress(char** data, unsigned long* addr) {
  *addr = 0;

  // Skip whitespace
  while ((**data != 0) && (isspace(**data))) {
    ++*data;
  }

  if (**data == 0) {
    return false;
  }

  if (strncmp(*data, "0x", 2) != 0) {
    return false;
  }

  *data += 2;

  while ((**data != 0) && (!isspace(**data))) {
    int val;
    bool res = charToNibble(**data, &val);
    if (!res) {
      return false;
    }
    *addr <<= 4;
    *addr |= val;
    ++*data;
  }

  return true;
}

bool
scanAndSkipBinEscapeChar(char** data) {
  // Skip whitespace
  while ((**data != 0) && (isspace(**data))) {
    ++*data;
  }

  if (!IOBuf::isBinEscapeChar(**data)) {
    return false;
  }

  ++*data;

  return true;
}

bool
scanBinUnsignedLong(char** data, unsigned long* num) {
  *num = 0;
  for (int i = 0; i < 4; i++) {
    unsigned char val = (unsigned char) **data;
    *num = (*num << 8) | val;
    ++*data;
  }
  return true;
}
