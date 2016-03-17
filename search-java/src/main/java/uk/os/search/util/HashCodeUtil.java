/*
 * Copyright (c) 2002-2009, Hirondelle Systems
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *    * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    * Neither the name of Hirondelle Systems nor the
 *      names of its contributors may be used to endorse or promote products
 *      derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY HIRONDELLE SYSTEMS ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL HIRONDELLE SYSTEMS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package uk.os.search.util;

import java.lang.reflect.Array;

/**
 * Collected methods which allow easy implementation of <tt>hashCode</tt>.
 *
 * Example use case:
 * <pre>
 *  public int hashCode(){
 *    int result = HashCodeUtil.SEED;
 *    //collect the contributions of various fields
 *    result = HashCodeUtil.hash(result, fPrimitive);
 *    result = HashCodeUtil.hash(result, fObject);
 *    result = HashCodeUtil.hash(result, fArray);
 *    return result;
 *  }
 * </pre>
 * Source: http://www.javapractices.com/topic/TopicAction.do?Id=28
 */
public final class HashCodeUtil {

    /**
     * An initial value for a <tt>hashCode</tt>, to which is added contributions
     * from fields. Using a non-zero value decreases collisions of <tt>hashCode</tt>
     * values.
     */
    public static final int SEED = 23;

    // booleans
    public static int hash(int aSeed, boolean aBoolean) {
        log("boolean...");
        return firstTerm( aSeed ) + (aBoolean ? 1 : 0);
    }

    // chars
    public static int hash(int aSeed, char aChar) {
        log("char...");
        return firstTerm(aSeed) + (int)aChar;
    }

    // ints
    public static int hash(int aSeed , int aInt) {
    /*
    * Implementation Note
    * Note that byte and short are handled by this method, through
    * implicit conversion.
    */
        log("int...");
        return firstTerm(aSeed) + aInt;
    }

    // longs
    public static int hash(int aSeed , long aLong) {
        log("long...");
        return firstTerm(aSeed)  + (int)(aLong ^ (aLong >>> 32));
    }

    // floats
    public static int hash(int aSeed , float aFloat) {
        return hash(aSeed, Float.floatToIntBits(aFloat));
    }

    // doubles
    public static int hash(int aSeed , double aDouble) {
        return hash( aSeed, Double.doubleToLongBits(aDouble) );
    }

    /**
     * <tt>aObject</tt> is a possibly-null object field, and possibly an array.
     *
     * If <tt>aObject</tt> is an array, then each element may be a primitive
     * or a possibly-null object
     * @param aSeed value to be specified to better handle hash collisions
     * @param aObject to calculate the hash function to
     * @return the hash integer value
     */
    public static int hash(int aSeed , Object aObject) {
        int result = aSeed;
        if (aObject == null){
            result = hash(result, 0);
        }
        else if (!isArray(aObject)){
            result = hash(result, aObject.hashCode());
        }
        else {
            int length = Array.getLength(aObject);
            for (int idx = 0; idx < length; ++idx) {
                Object item = Array.get(aObject, idx);
                //if an item in the array references the array itself, prevent infinite looping
                if(! (item == aObject))
                    //recursive call!
                    result = hash(result, item);
            }
        }
        return result;
    }

    // PRIVATE
    private static final int fODD_PRIME_NUMBER = 37;

    private static int firstTerm(int aSeed){
        return fODD_PRIME_NUMBER * aSeed;
    }

    private static boolean isArray(Object aObject){
        return aObject.getClass().isArray();
    }

    private static void log(String aMessage){
        System.out.println(aMessage);
    }
}