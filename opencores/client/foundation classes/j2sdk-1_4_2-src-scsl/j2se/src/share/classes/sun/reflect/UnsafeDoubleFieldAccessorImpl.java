/*
 * @(#)UnsafeDoubleFieldAccessorImpl.java	1.6 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.reflect;

import java.lang.reflect.Field;

class UnsafeDoubleFieldAccessorImpl extends UnsafeFieldAccessorImpl {
    UnsafeDoubleFieldAccessorImpl(Field field) {
        super(field);
    }

    public Object get(Object obj) throws IllegalArgumentException {
        return new Double(getDouble(obj));
    }

    public boolean getBoolean(Object obj) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    public byte getByte(Object obj) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    public char getChar(Object obj) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    public short getShort(Object obj) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    public int getInt(Object obj) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    public long getLong(Object obj) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    public float getFloat(Object obj) throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    public double getDouble(Object obj) throws IllegalArgumentException {
        ensureObj(obj);
        return unsafe.getDouble(obj, fieldOffset);
    }

    public void set(Object obj, Object value)
        throws IllegalArgumentException, IllegalAccessException
    {
        ensureObj(obj);
        if (isFinal) {
            throw new IllegalAccessException("Field is final");
        }
        if (value == null) {
            throw new IllegalArgumentException();
        }
        if (value instanceof Byte) {
            unsafe.putDouble(obj, fieldOffset, ((Byte) value).byteValue());
            return;
        }
        if (value instanceof Short) {
            unsafe.putDouble(obj, fieldOffset, ((Short) value).shortValue());
            return;
        }
        if (value instanceof Character) {
            unsafe.putDouble(obj, fieldOffset, ((Character) value).charValue());
            return;
        }
        if (value instanceof Integer) {
            unsafe.putDouble(obj, fieldOffset, ((Integer) value).intValue());
            return;
        }
        if (value instanceof Long) {
            unsafe.putDouble(obj, fieldOffset, ((Long) value).longValue());
            return;
        }
        if (value instanceof Float) {
            unsafe.putDouble(obj, fieldOffset, ((Float) value).floatValue());
            return;
        }
        if (value instanceof Double) {
            unsafe.putDouble(obj, fieldOffset, ((Double) value).doubleValue());
            return;
        }
        throw new IllegalArgumentException();
    }

    public void setBoolean(Object obj, boolean z)
        throws IllegalArgumentException, IllegalAccessException
    {
        throw new IllegalArgumentException();
    }

    public void setByte(Object obj, byte b)
        throws IllegalArgumentException, IllegalAccessException
    {
        setDouble(obj, b);
    }

    public void setChar(Object obj, char c)
        throws IllegalArgumentException, IllegalAccessException
    {
        setDouble(obj, c);
    }

    public void setShort(Object obj, short s)
        throws IllegalArgumentException, IllegalAccessException
    {
        setDouble(obj, s);
    }

    public void setInt(Object obj, int i)
        throws IllegalArgumentException, IllegalAccessException
    {
        setDouble(obj, i);
    }

    public void setLong(Object obj, long l)
        throws IllegalArgumentException, IllegalAccessException
    {
        setDouble(obj, l);
    }

    public void setFloat(Object obj, float f)
        throws IllegalArgumentException, IllegalAccessException
    {
        setDouble(obj, f);
    }

    public void setDouble(Object obj, double d)
        throws IllegalArgumentException, IllegalAccessException
    {
        ensureObj(obj);
        if (isFinal) {
            throw new IllegalAccessException("Field is final");
        }
        unsafe.putDouble(obj, fieldOffset, d);
    }
}