/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hp.hpl.jena.tdb.store.bulkloader2;

import static com.hp.hpl.jena.sparql.util.Utils.nowAsString ;

import java.util.Iterator ;

import org.apache.jena.atlas.lib.Bytes ;
import org.apache.jena.atlas.lib.Tuple ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.atlas.logging.ProgressLogger ;
import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import tdb.cmdline.CmdTDB ;

import com.hp.hpl.jena.tdb.base.file.Location ;
import com.hp.hpl.jena.tdb.base.record.Record ;
import com.hp.hpl.jena.tdb.base.record.RecordFactory ;
import com.hp.hpl.jena.tdb.index.RangeIndex ;
import com.hp.hpl.jena.tdb.index.TupleIndex ;
import com.hp.hpl.jena.tdb.store.NodeId ;
import com.hp.hpl.jena.tdb.sys.SystemTDB ;

/** Copy one index to another, probably with a different key order */
public class CmdIndexCopy
{
    static { LogCtl.setLog4j() ; }
    private static Logger log = LoggerFactory.getLogger(CmdIndexCopy.class) ;
    
    static long tickQuantum = 100*1000 ;
    static int superTick = 10 ;
    
    public static void main(String...argv)
    {
        CmdTDB.init() ;
        
        // Ideas:
        // Copy to buffer, sort, write in sequential clumps.
        // Profile code for hotspots
        
        // Maybe be worth opening the data file (the leaves) as a regular, 
        // non-memory mapped file as we read it through once, in natural order,
        // and it may be laid out in increasing block order on-disk, e.g. repacked
        // and in increasing order with occassional oddities if SPO from the bulk loader.
        
        if ( argv.length != 4 )
        {
            System.err.println("Usage: Location1 Index1 Location2 Index2") ;
            System.exit(1) ;
        }
        
        String locationStr1 = argv[0] ;
        String indexName1 = argv[1] ;
        String locationStr2 = argv[2] ;
        String indexName2 = argv[3] ;
        
        // Argument processing
        
        Location location1 = new Location(locationStr1) ;
        Location location2 = new Location(locationStr2) ;
        
        int keyLength = SystemTDB.SizeOfNodeId * indexName1.length() ;
        int valueLength = 0 ;
        
        // The name is the order.
        String primary = "SPO" ;
        
        String indexOrder = indexName2 ;
        String label = indexName1+" => "+indexName2 ;
        
        if ( true )
        {
            TupleIndex index1 = IndexFactory.openTupleIndex(location1, indexName1, primary, indexName1, 10, 10, keyLength, valueLength) ;      
            TupleIndex index2 = IndexFactory.openTupleIndex(location2, indexName2, primary, indexOrder, 10, 10, keyLength, valueLength) ;

            tupleIndexCopy(index1, index2, label) ;

            index1.close() ;
            index2.close() ;
        }
        else
        {
            // BPT -> BPT copy with reordering (hardcoded)
            RangeIndex index1 = IndexFactory.openBPT(location1, indexName1, 10, 10, keyLength, valueLength) ;  
            // Unlikely to be metafile for destination.
            keyLength = index1.getRecordFactory().keyLength() ;
            valueLength = index1.getRecordFactory().valueLength() ;
            RangeIndex index2 = IndexFactory.openBPT(location2, indexName2, 10000, 10000, keyLength, valueLength) ;
            bptCopy(index1, index2, label) ;
            index1.close() ;
            index2.close() ;
        }
    }

    private static void tupleIndexCopy(TupleIndex index1, TupleIndex index2, String label)
    {
        ProgressLogger monitor = new ProgressLogger(log, label, tickQuantum, superTick) ;
        monitor.start() ;
        
        Iterator<Tuple<NodeId>> iter1 = index1.all() ;
        
        long counter = 0 ;
        for ( ; iter1.hasNext(); )
        {
            counter++ ;
            Tuple<NodeId> tuple = iter1.next() ;
            index2.add(tuple) ;
            monitor.tick() ;
        }
        
        index2.sync() ;
        long time = monitor.finish() ;
        float elapsedSecs = time/1000F ;
        
        float rate = (elapsedSecs!=0) ? counter/elapsedSecs : 0 ;
        
        print("Total: %,d records : %,.2f seconds : %,.2f records/sec [%s]", counter, elapsedSecs, rate, nowAsString()) ;
    }
    

    // ??Fast copy by record.
    
    private static void bptCopy(RangeIndex bpt1, RangeIndex bpt2, String label)
    {
        ProgressLogger monitor = new ProgressLogger(log, label, tickQuantum, superTick) ;
        monitor.start() ;
        RecordFactory rf = bpt1.getRecordFactory() ;
        
        Iterator<Record> iter1 = bpt1.iterator() ;
        long counter = 0 ;
        // Use the same slot each time.
        Record r2 = rf.create() ;
        
        for ( ; iter1.hasNext(); )
        {
            counter++ ;
            Record r = iter1.next() ;
            byte[] key1 = r.getKey() ;
            
            long i1 = Bytes.getLong(key1, 0 ) ;
            long i2 = Bytes.getLong(key1, 8 ) ;
            long i3 = Bytes.getLong(key1, 16 ) ;
            
            //r2 = rf.create() ;
            //bpt2.add(r) ;
            // TESTING: SPO => OSP
            byte[] key2 = r2.getKey() ;
            Bytes.setLong(i3, key2, 0) ;
            Bytes.setLong(i1, key2, 8) ;
            Bytes.setLong(i2, key2, 16) ;
            bpt2.add(r2) ;
            monitor.tick() ;
        }
        
        bpt2.sync() ;
        long time = monitor.finish() ;
        float elapsedSecs = time/1000F ;
        
        float rate = (elapsedSecs!=0) ? counter/elapsedSecs : 0 ;
        
        String str =  String.format("Total: %,d records : %,.2f seconds : %,.2f records/sec [%s]", counter, elapsedSecs, rate, nowAsString()) ;
        log.info(str) ;
    }
    
    static private void print(String fmt, Object...args)
    {
        if ( log != null && log.isInfoEnabled() )
        {
            String str = String.format(fmt, args) ;
            log.info(str) ;
        }
    }
}
