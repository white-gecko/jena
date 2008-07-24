/*
 * (c) Copyright 2008 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package lib;

/** A cache.  But we already use that name (and this is a replacement for that) */ 
// Rename later.
public class Pool<Key, T>
{
    // SoftReference<T>?
    private int max ;
    private int min ;
    CacheLRU<Key, PoolEntry<T>> objects ;
    
    // Statistics
    private long cacheEntries ;
    private long cacheHits ;
    private long cacheMisses ; 
    
    public Pool(int num)            { this(0, num) ; }
    private Pool(int min, int max)
    { 
        this.min = min ;
        this.max = max ;
        objects = new CacheLRU<Key, PoolEntry<T>>(max) ;
        cacheEntries = 0 ;
        cacheHits = 0 ;
        cacheMisses = 0 ;
    }
    
    synchronized
    public T getObject(Key key, boolean exclusive)
    { 
        PoolEntry<T> entry = objects.get(key) ;
        if ( entry == null )
        {
            cacheMisses++ ;
            return null ;
        }
        
        if ( exclusive )
        {
            if ( entry.refCount > 0 )
                ; // Problems
            entry.refCount = -1 ;
            cacheHits++ ;
            return entry.thing ; 
        }
        else
        {
            if ( entry.refCount < 0 )
                ;// Problems
            entry.refCount++ ;
            cacheHits++ ;
            return entry.thing ;
        }
    }
    
    synchronized
    public void returnObject(Key key)
    {
        PoolEntry<T> entry = objects.get(key) ;
        if ( entry == null )
            return ;
        
        if ( entry.refCount < 0 )
        {
            entry.refCount = 0 ;
            return ;
        }
        
        entry.refCount -- ;
        if ( entry.refCount == 0 )
            ; //??
    }
    
    synchronized
    public void putObject(Key key, T t)
    {
        PoolEntry<T> entry = objects.get(key) ;
        if ( entry != null )
        {
            if ( entry.thing.equals(t) )
                ; //WARN
        }
        cacheEntries++ ;
        objects.put(key, new PoolEntry<T>(t)) ;
    }
        
    synchronized
    public void removeObject(Key key)
    {
        PoolEntry<T> entry = objects.get(key) ;
        if ( entry == null )
            return ;
        
        if ( entry.refCount != 0 )
            ; // Problems
        objects.remove(key) ;
        cacheEntries-- ;
    }
    
    static class Handler<Key, T> implements ActionKeyValue<Key, PoolEntry<T>>
    {

        @Override
        public void apply(Key key, PoolEntry<T> entry)
        {
            if ( entry.refCount != 0 )
                ;
            entry.refCount = 0 ;
        }
        
    }

    // Hmm - bet there is an existing class to do all this. 
    static class PoolEntry<T>
    {
        // Ref count = -1 ==> exclusive lock.
        int refCount = 0 ;
//        enum Status { FREE, ALLOCATED, INVALID } ;
//        Status status = Status.INVALID ;
        T thing ;
        PoolEntry(T thing) { this.thing = thing ; } 
    }
    
}

/*
 * (c) Copyright 2008 Hewlett-Packard Development Company, LP
 * All rights reserved.
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
 *
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
 */