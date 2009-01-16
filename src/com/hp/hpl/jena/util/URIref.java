/*
 *  (c) Copyright 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 * * $Id: URIref.java,v 1.10 2009-01-16 17:23:56 andy_seaborne Exp $
   
   AUTHOR:  Jeremy J. Carroll
*//*
 * URIref.java
 *
 * Created on September 20, 2001, 12:04 PM
 */

package com.hp.hpl.jena.util;


/**
 * This class provides methods to encode and decode URI References
 * in accordance with http://www.w3.org/TR/charmod/#sec-URIs .
 * The details of how the algorithms handle '%' are captured in
 * http://lists.w3.org/Archives/Public/uri/2001Sep/0009.html
 * @author  jjc
 */
public class URIref extends Object {
    
    /** Convert a Unicode string, first to UTF-8 and then to
     * an RFC 2396 compliant URI with optional fragment identifier
     * using %NN escape mechanism as appropriate.
     * The '%' character is assumed to already indicated an escape byte.
     * The '%' character must be followed by two hexadecimal digits.
     * @param unicode The uri, in characters specified by RFC 2396 + '#'
     * @return The corresponding Unicode String
     */ 
    static public String encode(String unicode) {
        try {
        byte utf8[] = unicode.getBytes("UTF-8");
        byte rsltAscii[] = new byte[utf8.length*6];
        int in = 0;
        int out = 0;
        while ( in < utf8.length ) {
            switch ( utf8[in] ) {
                case (byte)'a': case (byte)'b': case (byte)'c': case (byte)'d': case (byte)'e': case (byte)'f': case (byte)'g': case (byte)'h': case (byte)'i': case (byte)'j': case (byte)'k': case (byte)'l': case (byte)'m': case (byte)'n': case (byte)'o': case (byte)'p': case (byte)'q': case (byte)'r': case (byte)'s': case (byte)'t': case (byte)'u': case (byte)'v': case (byte)'w': case (byte)'x': case (byte)'y': case (byte)'z':
                case (byte)'A': case (byte)'B': case (byte)'C': case (byte)'D': case (byte)'E': case (byte)'F': case (byte)'G': case (byte)'H': case (byte)'I': case (byte)'J': case (byte)'K': case (byte)'L': case (byte)'M': case (byte)'N': case (byte)'O': case (byte)'P': case (byte)'Q': case (byte)'R': case (byte)'S': case (byte)'T': case (byte)'U': case (byte)'V': case (byte)'W': case (byte)'X': case (byte)'Y': case (byte)'Z':
                case (byte)'0': case (byte)'1': case (byte)'2': case (byte)'3': case (byte)'4': case (byte)'5': case (byte)'6': case (byte)'7': case (byte)'8': case (byte)'9':
                case (byte)';': case (byte)'/': case (byte)'?': case (byte)':': case (byte)'@': case (byte)'&': case (byte)'=': case (byte)'+': case (byte)'$': case (byte)',':
                case (byte)'-': case (byte)'_': case (byte)'.': case (byte)'!': case (byte)'~': case (byte)'*': case (byte)'\'': case (byte)'(': case (byte)')':
                case (byte)'#': 
                case (byte)'[': case (byte)']':
                    rsltAscii[out] = utf8[in];
                    out++;
                    in++;
                    break;
                default : // case (byte) '%':
                    if (utf8[in] == '%')
                    {

                        try
                        {
                            if (in + 2 < utf8.length)
                            {
                                byte first = hexEncode(hexDecode(utf8[in + 1])) ;
                                byte second = hexEncode(hexDecode(utf8[in + 2])) ;
                                rsltAscii[out++] = (byte)'%' ;
                                rsltAscii[out++] = first ;
                                rsltAscii[out++] = second ;
                                in += 3 ;
                                break ;
                            }
                        } catch (IllegalArgumentException e)
                        {
                            // Illformed - should issue message ....
                            System.err.println("Confusing IRI to encode - contains literal '%': " + unicode) ;
                            // Fall through.
                        }
                    }
                    rsltAscii[out++] = (byte)'%' ;
                    // Get rid of sign ...
                    int c = (utf8[in]) & 255 ;
                    rsltAscii[out++] = hexEncode(c / 16) ;
                    rsltAscii[out++] = hexEncode(c % 16) ;
                    in++ ;
                    break ;
                }
        }
        return new String(rsltAscii,0,out,"US-ASCII");
        }
        catch ( java.io.UnsupportedEncodingException e ) {
            throw new Error( "The JVM is required to support UTF-8 and US-ASCII encodings.");
        }
    }
    
    /** Convert a URI, in US-ASCII, with escaped characters taken from UTF-8, 
     * to the corresponding Unicode string.
     * On ill-formed input the results are undefined, specifically if
     * the unescaped version is not a UTF-8 String, some String will be
     * returned.
     * Escaped '%' characters (i.e. "%25") are left unchanged.
     * @param uri The uri, in characters specified by RFC 2396 + '#'.
     * @return The corresponding Unicode String.
     * @exception IllegalArgumentException If a % hex sequence is ill-formed.
     */
    static public String decode(String uri) {
        try {
            byte ascii[] = uri.getBytes("US-ASCII");
            byte utf8[] = new byte[ascii.length];
            int in = 0;
            int out = 0;
            while ( in < ascii.length ) {
                if ( ascii[in] == (byte)'%' 
                     && ( ascii[in+1] != '2'
                       || ascii[in+2] != '5' ) ) {
                    in++;
                    utf8[out++] = (byte)(hexDecode(ascii[in])*16 | hexDecode(ascii[in+1]));
                    in += 2;
                } else {
                    utf8[out++] = ascii[in++];
                }
            }
            return new String(utf8,0,out,"UTF-8");
        }
        catch ( java.io.UnsupportedEncodingException e ) {
            throw new Error( "The JVM is required to support UTF-8 and US-ASCII encodings.");
        }
        catch ( ArrayIndexOutOfBoundsException ee ) {
            throw new IllegalArgumentException("Incomplete Hex escape sequence in " + uri );
        }
    }
    
    static private byte hexEncode(int i ) {
        if (i<10)
            return (byte) ('0' + i);
        else
            return (byte)('A' + i - 10);
    }
    
    static private int hexDecode(byte b ) {
        switch (b) { 
            case (byte)'a': case (byte)'b': case (byte)'c': case (byte)'d': case (byte)'e': case (byte)'f':
             return ((b)&255)-'a'+10;
            case (byte)'A': case (byte)'B': case (byte)'C': case (byte)'D': case (byte)'E': case (byte)'F': 
            return b - (byte)'A' + 10;
            case (byte)'0': case (byte)'1': case (byte)'2': case (byte)'3': case (byte)'4': case (byte)'5': case (byte)'6': case (byte)'7': case (byte)'8': case (byte)'9':
                return b - (byte)'0';
                default:
                    throw new IllegalArgumentException("Bad Hex escape character: " + ((b)&255) );
        }
    }
    
    /** For simple testing ...
     */
    static public void main(String args[]) {
        for (int i=0; i<args.length; i++) {
            System.out.println(args[i] + " => " + decode(args[i]) + " => " + encode(decode(args[i])));
        }
    }

    

}
