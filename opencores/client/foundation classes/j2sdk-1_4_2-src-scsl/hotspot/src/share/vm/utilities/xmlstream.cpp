#ifdef USE_PRAGMA_IDENT_SRC
#pragma ident "@(#)xmlstream.cpp	1.2 03/01/23 12:29:19 JVM"
#endif
/*
 * Copyright 2003 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

#include "incls/_precompiled.incl"
#include "incls/_xmlstream.cpp.incl"

void xmlStream::initialize(outputStream* out) {
  _out = out;
  _last_flush = 0;
  _markup_state = BODY;
  _text_init._outer_xmlStream = this;
  _text = &_text_init;

#ifdef ASSERT
  _element_depth = 0;
  int   init_len = 100;
  char* init_buf = NEW_C_HEAP_ARRAY(char, init_len);
  _element_close_stack_low  = init_buf;
  _element_close_stack_high = init_buf + init_len;
  _element_close_stack_ptr  = init_buf + init_len - 1;
  _element_close_stack_ptr[0] = '\0';
#endif

  // Make sure each log uses the same base for time stamps.
  if (is_open()) {
    _out->time_stamp().update_to(1);
  }
}

#ifdef ASSERT
xmlStream::~xmlStream() {
  FREE_C_HEAP_ARRAY(char, _element_close_stack_low);
}
#endif

// Pass the given chars directly to _out.
void xmlStream::write(const char* s, size_t len) {
  if (!is_open())  return;

  out()->write(s, len);
}


// Pass the given chars directly to _out, except that
// we watch for special "<&>" chars.
// This is suitable for either attribute text or for body text.
// We don't fool with "<![CDATA[" quotes, just single-character entities.
// This makes it easier for dumb tools to parse the output.
void xmlStream::write_text(const char* s, size_t len) {
  if (!is_open())  return;

  size_t written = 0;
  // All normally printed material goes inside XML quotes.
  // This leaves the output free to include markup also.
  // Scan the string looking for inadvertant "<&>" chars
  for (size_t i = 0; i < len; i++) {
    char ch = s[i];
    // Escape special chars.
    const char* esc = NULL;
    switch (ch) {
      // These are important only in attrs, but we do them always:
    case '\'': esc = "&apos;"; break;
    case '"':  esc = "&quot;"; break;
    case '<':  esc = "&lt;";   break;
    case '&':  esc = "&amp;";  break;
      // This is a freebie.
    case '>':  esc = "&gt;";   break;
    }
    if (esc != NULL) {
      if (written < i) {
        out()->write(&s[written], i - written);
        written = i;
      }
      out()->print_raw(esc);
      written++;
    }
  }

  // Print the clean remainder.  Usually, it is all of s.
  if (written < len) {
    out()->write(&s[written], len - written);
  }
}

// ------------------------------------------------------------------
// Outputs XML text, with special characters quoted.
void xmlStream::text(const char* format, ...) {
  va_list ap;
  va_start(ap, format);
  va_text(format, ap);
  va_end(ap);
}

#define BUFLEN 2*K   /* max size of output of individual print methods */

// ------------------------------------------------------------------
void xmlStream::va_tag(bool push, const char* format, va_list ap) {
  assert(!inside_attrs(), "cannot print tag inside attrs");
  char buffer[BUFLEN];
  size_t len;
  const char* kind = do_vsnprintf(buffer, BUFLEN, format, ap, false, len);
  see_tag(kind, push);
  print_raw("<");
  write(kind, len);
  _markup_state = (push ? HEAD : ELEM);
}

#ifdef ASSERT
/// Debugging goo to make sure element tags nest properly.

// ------------------------------------------------------------------
void xmlStream::see_tag(const char* tag, bool push) {
  assert(!inside_attrs(), "cannot start new element inside attrs");
  if (!push)  return;

  // tag goes up until either null or space:
  const char* tag_end = strchr(tag, ' ');
  size_t tag_len = (tag_end == NULL) ? strlen(tag) : tag_end - tag;
  assert(tag_len > 0, "tag must not be empty");
  // push the tag onto the stack, pulling down the pointer
  char* old_ptr  = _element_close_stack_ptr;
  char* old_low  = _element_close_stack_low;
  char* push_ptr = old_ptr - (tag_len+1);
  if (push_ptr < old_low) {
    int old_len = _element_close_stack_high - old_ptr;
    int new_len = old_len * 2;
    if (new_len < 100)  new_len = 100;
    char* new_low  = NEW_C_HEAP_ARRAY(char, new_len);
    char* new_high = new_low + new_len;
    char* new_ptr  = new_high - old_len;
    memcpy(new_ptr, old_ptr, old_len);
    _element_close_stack_high = new_high;
    _element_close_stack_low  = new_low;
    _element_close_stack_ptr  = new_ptr;
    FREE_C_HEAP_ARRAY(char, old_low);
    push_ptr = new_ptr - (tag_len+1);
  }
  assert(push_ptr >= _element_close_stack_low, "in range");
  memcpy(push_ptr, tag, tag_len);
  push_ptr[tag_len] = 0;
  _element_close_stack_ptr = push_ptr;
  _element_depth += 1;
}

// ------------------------------------------------------------------
void xmlStream::pop_tag(const char* tag) {
  assert(!inside_attrs(), "cannot close element inside attrs");
  assert(_element_depth > 0, "must be in an element to close");
  assert(*tag != 0, "tag must not be empty");
  char*  want_tag = _element_close_stack_ptr;
  if (strcmp(tag, want_tag) != 0) {
    tty->print("closing tag </%s>, pending tags: ", tag);
    for (char* cp = want_tag; *cp; cp += 1+strlen(cp)) {
      tty->print(" <%s>", cp);
    }
    tty->cr();
    ShouldNotReachHere();
  }
  // Pop the stack, by skipping over the tag and its null.
  _element_close_stack_ptr = want_tag + strlen(want_tag) + 1;
  _element_depth -= 1;
}
#endif


// ------------------------------------------------------------------
// First word in formatted string is element kind, and any subsequent
// words must be XML attributes.  Outputs "<kind .../>".
void xmlStream::elem(const char* format, ...) {
  va_list ap;
  va_start(ap, format);
  va_elem(format, ap);
  va_end(ap);
}

// ------------------------------------------------------------------
void xmlStream::va_elem(const char* format, va_list ap) {
  va_begin_elem(format, ap);
  end_elem();
}


// ------------------------------------------------------------------
// First word in formatted string is element kind, and any subsequent
// words must be XML attributes.  Outputs "<kind ...", not including "/>".
void xmlStream::begin_elem(const char* format, ...) {
  va_list ap;
  va_start(ap, format);
  va_tag(false, format, ap);
  va_end(ap);
}

// ------------------------------------------------------------------
void xmlStream::va_begin_elem(const char* format, va_list ap) {
  va_tag(false, format, ap);
}

// ------------------------------------------------------------------
// Outputs "/>".
void xmlStream::end_elem() {
  assert(_markup_state == ELEM, "misplaced end_elem");
  print_raw("/>\n");
  _markup_state = BODY;
}

// ------------------------------------------------------------------
// Outputs formatted text, followed by "/>".
void xmlStream::end_elem(const char* format, ...) {
  va_list ap;
  va_start(ap, format);
  out()->vprint(format, ap);
  va_end(ap);
  end_elem();
}


// ------------------------------------------------------------------
// First word in formatted string is element kind, and any subsequent
// words must be XML attributes.  Outputs "<kind ...>".
void xmlStream::head(const char* format, ...) {
  va_list ap;
  va_start(ap, format);
  va_head(format, ap);
  va_end(ap);
}

// ------------------------------------------------------------------
void xmlStream::va_head(const char* format, va_list ap) {
  va_begin_head(format, ap);
  end_head();
}

// ------------------------------------------------------------------
// First word in formatted string is element kind, and any subsequent
// words must be XML attributes.  Outputs "<kind ...", not including ">".
void xmlStream::begin_head(const char* format, ...) {
  va_list ap;
  va_start(ap, format);
  va_tag(true, format, ap);
  va_end(ap);
}

// ------------------------------------------------------------------
void xmlStream::va_begin_head(const char* format, va_list ap) {
  va_tag(true, format, ap);
}

// ------------------------------------------------------------------
// Outputs ">".
void xmlStream::end_head() {
  assert(_markup_state == HEAD, "misplaced end_head");
  print_raw(">\n");
  _markup_state = BODY;
}


// ------------------------------------------------------------------
// Outputs formatted text, followed by ">".
void xmlStream::end_head(const char* format, ...) {
  va_list ap;
  va_start(ap, format);
  out()->vprint(format, ap);
  va_end(ap);
  end_head();
}


// ------------------------------------------------------------------
// Outputs "</kind>".
void xmlStream::tail(const char* kind) {
  pop_tag(kind);
  print_raw("</");
  print_raw(kind);
  print_raw(">\n");
}

// ------------------------------------------------------------------
// Outputs "<kind_done ... stamp='D.DD'/> </kind>".
void xmlStream::done(const char* format, ...) {
  va_list ap;
  va_start(ap, format);
  va_done(format, ap);
  va_end(ap);
}

// ------------------------------------------------------------------
void xmlStream::va_done(const char* format, va_list ap) {
  char buffer[200];
  guarantee(strlen(format) + 10 < sizeof(buffer), "bigger format buffer")
  const char* kind = format;
  const char* kind_end = strchr(kind, ' ');
  size_t kind_len = (kind_end != NULL) ? (kind_end - kind) : strlen(kind);
  strncpy(buffer, kind, kind_len);
  strcpy(buffer + kind_len, "_done");
  strcat(buffer, format + kind_len);
  // Output the trailing event with the timestamp.
  va_begin_elem(buffer, ap);
  stamp();
  end_elem();
  // Output the tail-tag of the enclosing element.
  buffer[kind_len] = 0;
  tail(buffer);
}

// Output a timestamp attribute.
void xmlStream::stamp() {
  assert(inside_attrs(), "stamp must be an attribute");
  print_raw(" stamp='");
  out()->stamp();
  print_raw("'");
}


// ------------------------------------------------------------------
// Output a method attribute, in the form " method='pkg/cls name sig'".
// This is used only when there is no ciMethod available.
void xmlStream::method(methodHandle method) {
  assert(inside_attrs(), "printing attributes");
  if (method.is_null())  return;
  print_raw(" method='");
  //method->print_short_name(text());
  method->method_holder()->klass_part()->name()->print_symbol_on(text());
  print_raw(" ");  // " " is easier for tools to parse than "::"
  method->name()->print_symbol_on(text());
  print_raw(" ");  // separator
  method->signature()->print_symbol_on(text());
  print("' bytes='%d'", method->code_size());
#ifndef CORE
  nmethod* nm = method->code();
  int      nmsize = (nm == NULL) ? 0 : nm->instructions_size();
  if (nmsize != 0)  print(" nmsize='%d'", nmsize);
  print(" count='%d'", method->invocation_count());
  int bec = method->backedge_count();
  if (bec != 0)  print(" backedge_count='%d'", bec);
#ifdef COMPILER2
  print(" iicount='%d'", method->interpreter_invocation_count());
#endif
#endif
}


// ------------------------------------------------------------------
// Output a klass attribute, in the form " klass='pkg/cls'".
// This is used only when there is no ciKlass available.
void xmlStream::klass(KlassHandle klass) {
  assert(inside_attrs(), "printing attributes");
  if (klass.is_null())  return;
  print_raw(" klass='");
  //klass->print_short_name(log->out());
  klass->name()->print_symbol_on(out());
}

void xmlStream::name(symbolHandle name) {
  assert(inside_attrs(), "printing attributes");
  if (name.is_null())  return;
  print_raw(" name='");
  //name->print_short_name(text());
  name->print_symbol_on(text());
  print_raw("'");
}


void xmlStream::flush() {
  out()->flush();
  _last_flush = count();
}

void xmlTextStream::flush() {
  if (_outer_xmlStream == NULL)  return;
  _outer_xmlStream->flush();
}

void xmlTextStream::write(const char* str, size_t len) {
  if (_outer_xmlStream == NULL)  return;
  _outer_xmlStream->write_text(str, len);
  update_position(str, len);
}