/*
 * Copyright (c) 2007-2011 Madhav Vaidyanathan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 */


package com.ecml;

import java.util.Arrays;

/** @class ListInt
 * <br>
 * An ArrayList of int types. (Actually an Array)
 */

public class ListInt {
    private int[] data;    /* The list of ints */
    private int count;     /* The size of the list */

    /** Create a new ListInt with 11 ints */
    public ListInt() {
        data = new int[11];
        count = 0;
    }

    /** Create a new ListInt with the specified capacity
     * 
     * @param capacity
     */
    public ListInt(int capacity) {
        data = new int[capacity];
        count = 0;
    }

    /** Get the size of the list */
    public int size() {
        return count;
    }
    
    /** Add a new int in the list */
    public void add(int x) {
        if (data.length == count) {
            int[] newdata = new int[count * 2];
            for (int i = 0; i < count; i++) {
                newdata[i] = data[i];
            }
            data = newdata;
        }
        data[count] = x;
        count++;
    }

    /** Get the int of this ListInt at index
     * 
     * @param index
     */
    public int get(int index) {
        return data[index];
    }

    /** Set the int x at index of this ListInt
     * 
     * @param index
     * @param x
     */
    public void set(int index, int x) {
        data[index] = x;
    }

    /** Return true if x belongs to this ListInt
     * 
     * @param x
     */
    public boolean contains(int x) {
        for (int i = 0; i < count; i++) {
            if (data[i] == x) {
                return true;
            }
        }
        return false;
    }

    public void sort() {
        Arrays.sort(data, 0, count);
    }
}

