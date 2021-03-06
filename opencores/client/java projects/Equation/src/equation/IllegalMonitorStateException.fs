MODULENAME equation.IllegalMonitorStateException
(
* @(#)IllegalMonitorStateException.java	1.11 03/01/23
*
* Copyright 2003 Sun Microsystems, Inc. All rights reserved.
* SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
)  ( *
* Thrown to indicate that a thread has attempted to wait on an
* object's monitor or to notify other threads waiting on an object's
* monitor without owning the specified monitor.
*
* @author  unascribed
* @version 1.11, 01/23/03
* @see     java.lang.Object#notify()
* @see     java.lang.Object#notifyAll()
* @see     java.lang.Object#wait()
* @see     java.lang.Object#wait(long)
* @see     java.lang.Object#wait(long, int)
* @since   JDK1.0
)
( *
* Constructs an <code>IllegalMonitorStateException</code> with no
* detail message.
)

:LOCAL IllegalMonitorStateException.IllegalMonitorStateException§46614784
   1 VALLOCATE LOCAL §base0
   
   \ new statement
   0
   LOCALS §this |
   
   \ new statement
   RuntimeException§-1151249920.table 1000231424 EXECUTE-NEW
   
   \ new statement
   28 MALLOC DROP DUP DUP §base0 V! TO §this OVER
   IF
      OVER CELL+ @ 4 + OVER OVER ! DROP
   ENDIF
   CELL+ @ OVER
   OVER ! NIP
   IllegalMonitorStateException§-52541184.table OVER 12 + !
   -52559872 OVER 20 + !
   " IllegalMonitorStateException " OVER 16 + !
   0 OVER 24 + ! DROP
   
   \ new statement
   §this DUP 0 V! ( return object )    
   \ new statement
   0 §break32830 LABEL
   
   \ new statement
   §base0 SETVTOP
   PURGE 2
   
   \ new statement
   DROP
;
( *
* Constructs an <code>IllegalMonitorStateException</code> with the
* specified detail message.
*
* @param   s   the detail message.
)

:LOCAL IllegalMonitorStateException.IllegalMonitorStateException§894191872
   2 VALLOCATE LOCAL §base0
   DUP 4 §base0 + V! LOCAL s
   
   \ new statement
   0
   LOCALS §this |
   
   \ new statement
   s
   RuntimeException§-1151249920.table -745188864 EXECUTE-NEW
   
   \ new statement
   28 MALLOC DROP DUP DUP §base0 V! TO §this OVER
   IF
      OVER CELL+ @ 4 + OVER OVER ! DROP
   ENDIF
   CELL+ @ OVER
   OVER ! NIP
   IllegalMonitorStateException§-52541184.table OVER 12 + !
   -52559872 OVER 20 + !
   " IllegalMonitorStateException " OVER 16 + !
   0 OVER 24 + ! DROP
   
   \ new statement
   §this DUP 0 V! ( return object )    
   \ new statement
   0 §break32831 LABEL
   
   \ new statement
   
   §base0 SETVTOP
   PURGE 3
   
   \ new statement
   DROP
;
VARIABLE IllegalMonitorStateException._staticBlocking
VARIABLE IllegalMonitorStateException._staticThread  HERE 4 - SALLOCATE

A:HERE VARIABLE IllegalMonitorStateException§-52541184.table 2 DUP 2* CELLS ALLOT R@ ! A:CELL+
46614784 R@ ! A:CELL+ IllegalMonitorStateException.IllegalMonitorStateException§46614784 VAL R@ ! A:CELL+
894191872 R@ ! A:CELL+ IllegalMonitorStateException.IllegalMonitorStateException§894191872 VAL R@ ! A:CELL+
A:DROP
