#ifdef USE_PRAGMA_IDENT_SRC
#pragma ident "@(#)IOBuf.cpp	1.6 03/01/23 11:06:24 JVM"
#endif
/*
 * Copyright 2003 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

#include <stdio.h>

// This file is currently used for os/solaris/agent too.  At some point in time
// the source will be reorganized to avoid these ifdefs.

#ifdef __sun
  #include <string.h>
  #include <inttypes.h>
  #include <sys/byteorder.h>
#endif

#include "IOBuf.hpp"

// Formats for printing pointers
#ifdef _LP64
#  define INTPTR_FORMAT "0x%016lx"
#else /* ! _LP64 */
#  define INTPTR_FORMAT "0x%08lx"
#endif /* _LP64 */

// Uncomment the #define below to get messages on stderr
// #define DEBUGGING

IOBuf::IOBuf(int inLen, int outLen) {
  inBuf = new Buffer(inLen);
  outBuf = new Buffer(outLen);
  fd = INVALID_SOCKET;
  outHandle = NULL;
  usingSocket = true;
  reset();
}

IOBuf::~IOBuf() {
  delete inBuf;
  delete outBuf;
}

void
IOBuf::setSocket(SOCKET sock) {
  fd = sock;
  usingSocket = true;
}

// Reading/writing files is only needed and used on windows.
#ifdef WIN32
void
IOBuf::setOutputFileHandle(HANDLE handle) {
  outHandle = handle;
  usingSocket = false;
}
#endif

void
IOBuf::reset() {
  gotDataLastTime = false;
  state          = TEXT_STATE;
  binPos         = 0;
  binLength      = 0;
}

IOBuf::ReadLineResult
IOBuf::tryReadLine() {
  return doReadLine(false);
}

char*
IOBuf::readLine() {
  ReadLineResult rr = doReadLine(true);
  if (rr != RL_GOT_DATA) {
    return NULL;
  }
  return getLine();
}

IOBuf::ReadLineResult
IOBuf::doReadLine(bool shouldWait) {

  if (!usingSocket) {
    return IOBuf::RL_ERROR;
  }

  if (gotDataLastTime) {
    curLine.clear();
  }

  int c;
  do {
    c = readChar(shouldWait);
    if (c >= 0) {
      Action act = processChar((char) c);
      if (act == GOT_LINE) {
        curLine.push_back('\0');
        gotDataLastTime = true;
        return IOBuf::RL_GOT_DATA;
      } else if (act == SKIP_EOL_CHAR) {
        // Do nothing
      } else {
        curLine.push_back((char) c);
      }
    }
  } while (shouldWait || c >= 0);

  gotDataLastTime = false;
  return IOBuf::RL_NO_DATA;
}

int
IOBuf::readChar(bool block) {
  do {
    int c = inBuf->readByte();
    if (c >= 0) {
      return c;
    }
    // See whether we need to compact the input buffer
    if (inBuf->remaining() < inBuf->size() / 2) {
      inBuf->compact();
    }
    // See whether socket is ready
    fd_set fds;
    FD_ZERO(&fds);
    FD_SET(fd, &fds);
    struct timeval timeout;
    timeout.tv_sec = 0;
    timeout.tv_usec = 0;
    if (block || select(1 + fd, &fds, NULL, NULL, &timeout) > 0) {
      if (block || FD_ISSET(fd, &fds)) {
#ifdef DEBUGGING
        int b = (block ? 1 : 0);
        fprintf(stderr, "calling recv: block = %d\n", b);
#endif
        // Read data from socket
        int numRead = recv(fd, inBuf->fillPos(), inBuf->remaining(), 0);
        if (numRead < 0) {
#ifdef DEBUGGING
          fprintf(stderr, "recv failed\n");
#endif
          return -1;
        }
        inBuf->incrFillPos(numRead);
      }
    }
  } while (block);

  return inBuf->readByte();
}

char*
IOBuf::getLine() {
#ifdef DEBUGGING
  fprintf(stderr, "Returning (first 10 chars) \"%.10s\"\n", curLine.begin());
#endif
  return curLine.begin();
}

bool
IOBuf::flush() {
  int numWritten = 0;

  while (outBuf->drainRemaining() != 0) {
#ifdef DEBUGGING
      fprintf(stderr, "Flushing %d bytes\n", outBuf->drainRemaining());
#endif
    if (usingSocket) {
      numWritten = send(fd, outBuf->drainPos(), outBuf->drainRemaining(), 0);
    } else {
      // this can only happen on windows.
#ifdef WIN32
      DWORD numBytesWritten;
      if (!WriteFile(outHandle, outBuf->drainPos(), outBuf->drainRemaining(), &numBytesWritten, NULL)) {
        numWritten = -1;
      } else {
        numWritten = numBytesWritten;
      }
#endif
    }
    if (numWritten != -1) {
#ifdef DEBUGGING
      fprintf(stderr, "Flushed %d bytes\n", numWritten);
#endif
      outBuf->incrDrainPos(numWritten);
    } else {
      return false;
    }
  }

  outBuf->compact();

  return true;
}

bool
IOBuf::writeString(const char* str) {
  int len = strlen(str);
  
  if (len > outBuf->size()) {
    return false;
  }

  if (len > outBuf->remaining()) {
    if (!flush()) {
      return false;
    }
  }
  
  // NOTE we do not copy the null terminator of the string.

  strncpy(outBuf->fillPos(), str, len);
  outBuf->incrFillPos(len);
  return true;
}

bool
IOBuf::writeInt(int val) {
  char buf[128];
  sprintf(buf, "%d", val);
  return writeString(buf);
}

bool
IOBuf::writeUnsignedInt(unsigned int val) {
  char buf[128];
  sprintf(buf, "%u", val);
  return writeString(buf);
}

bool
IOBuf::writeBoolAsInt(bool val) {
  if (val) {
    return writeString("1");
  } else {
    return writeString("0");
  }
}

bool
IOBuf::writeAddress(void* val) {
  char buf[128];
  sprintf(buf, INTPTR_FORMAT, val);
  return writeString(buf);
}

bool
IOBuf::writeSpace() {
  return writeString(" ");
}

bool
IOBuf::writeEOL() {
  return writeString("\n\r");
}

bool
IOBuf::writeBinChar(char c) {
  return writeBinBuf((char*) &c, sizeof(c));
}

bool
IOBuf::writeBinUnsignedShort(unsigned short i) {
  i = htons(i);
  return writeBinBuf((char*) &i, sizeof(i));
}

bool
IOBuf::writeBinUnsignedInt(unsigned int i) {
  i = htonl(i);
  return writeBinBuf((char*) &i, sizeof(i));
}

bool
IOBuf::writeBinBuf(char* buf, int size) {
  while (size > 0) {
    int spaceRemaining = outBuf->remaining();
    if (spaceRemaining == 0) {
      if (!flush()) {
        return false;
      }
      spaceRemaining = outBuf->remaining();
    }
    int toCopy = (size > spaceRemaining) ? spaceRemaining : size;
    memcpy(outBuf->fillPos(), buf, toCopy);
    outBuf->incrFillPos(toCopy);
    buf += toCopy;
    size -= toCopy;
    if (size > 0) {
      if (!flush()) {
        return false;
      }
    }
  }
  return true;
}

#ifdef WIN32
bool
IOBuf::fillFromFileHandle(HANDLE fh, DWORD* numBytesRead) {
  // Read as much as can fit in our buffer in one call to ReadFile
  DWORD numRead;
  if (!ReadFile(fh, outBuf->fillPos(), outBuf->remaining(), &numRead, NULL)) {
    return false;
  }
  outBuf->incrFillPos((int) numRead);
  *numBytesRead = numRead;
  return true;
}
#endif

bool
IOBuf::isBinEscapeChar(char c) {
  return (c == '|');
}

IOBuf::Action
IOBuf::processChar(char c) {
  Action action = NO_ACTION;
  switch (state) {
  case TEXT_STATE: {
    // Looking for text char, bin escape char, or EOL
    if (isBinEscapeChar(c)) {
#ifdef DEBUGGING
      fprintf(stderr, "[a: '%c'] ", inBuf[0]);
#endif
      binPos = 0;
#ifdef DEBUGGING
      fprintf(stderr, "[b: '%c'] ", inBuf[0]);
#endif
      binLength = 0;
#ifdef DEBUGGING
      fprintf(stderr, "[c: '%c'] ", inBuf[0]);
#endif
      state = BIN_STATE;
#ifdef DEBUGGING
      fprintf(stderr, "[d: '%c'] ", inBuf[0]);
#endif
#ifdef DEBUGGING
      fprintf(stderr, "\nSwitching to BIN_STATE\n");
#endif
    } else if (isEOL(c)) {
      state = EOL_STATE;
      action = GOT_LINE;
#ifdef DEBUGGING
      fprintf(stderr, "\nSwitching to EOL_STATE (GOT_LINE)\n");
#endif
    }
#ifdef DEBUGGING
    else {
      fprintf(stderr, "'%c' ", c);
      fflush(stderr);
    }
#endif
    break;
  }

  case BIN_STATE: {
    // Seeking to finish read of input
    if (binPos < 4) {
      int cur = c & 0xFF;
      binLength <<= 8;
      binLength |= cur;
      ++binPos;
    } else {
#ifdef DEBUGGING
      fprintf(stderr, "Reading binary byte %d of %d\n",
              binPos - 4, binLength);
#endif
      ++binPos;
      if (binPos == 4 + binLength) {
        state = TEXT_STATE;
#ifdef DEBUGGING
        fprintf(stderr, "Switching to TEXT_STATE\n");
#endif
      }
    }
    break;
  }

  case EOL_STATE: {
    // More EOL characters just cause us to re-enter this state
    if (isEOL(c)) {
      action = SKIP_EOL_CHAR;
    } else if (isBinEscapeChar(c)) {
      binPos = 0;
      binLength = 0;
      state = BIN_STATE;
    } else {
      state = TEXT_STATE;
#ifdef DEBUGGING
      fprintf(stderr, "'%c' ", c);
      fflush(stderr);
#endif
    }
    break;
  }

  } // switch

  return action;
}


bool
IOBuf::isEOL(char c) {
#ifdef WIN32
  return ((c == '\n') || (c == '\r'));
#elif defined(__sun)
  return c == '\n';
#else
  #error Please port isEOL() to your platform
  return false;
#endif
}
