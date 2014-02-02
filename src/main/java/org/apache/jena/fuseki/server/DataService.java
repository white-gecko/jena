/**
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

package org.apache.jena.fuseki.server;

import static org.apache.jena.fuseki.server.DatasetStatus.CLOSING ;
import static org.apache.jena.fuseki.server.DatasetStatus.UNINITIALIZED ;

import java.util.Collection ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;
import java.util.concurrent.atomic.AtomicBoolean ;
import java.util.concurrent.atomic.AtomicLong ;

import org.apache.jena.atlas.lib.MultiMap ;
import org.apache.jena.atlas.lib.MultiMapToList ;
import org.apache.jena.fuseki.DEF ;
import org.apache.jena.fuseki.Fuseki ;
import org.apache.jena.fuseki.build.DataServiceDesc ;

import com.hp.hpl.jena.query.ReadWrite ;
import com.hp.hpl.jena.sparql.core.DatasetGraph ;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory ;
import com.hp.hpl.jena.sparql.core.DatasetGraphReadOnly ;
import com.hp.hpl.jena.tdb.StoreConnection ;
import com.hp.hpl.jena.tdb.transaction.DatasetGraphTransaction ;

public class DataService { //implements DatasetMXBean {
    // XXX Add a "null model assembler".
    
    public static DataService serviceOnlyDataService() {
        return dummy ; 
    }
    
    public static DataService dummy = new DataService(null, null) ;
    static {
        dummy.dataset = new DatasetGraphReadOnly(DatasetGraphFactory.createMemFixed()) ;
        dummy.addEndpoint(OperationName.Query, DEF.ServiceQuery) ;
        dummy.addEndpoint(OperationName.Query, DEF.ServiceQueryAlt) ;
    }
    
    private final DataServiceDesc svcDesc ;
    private DatasetGraph dataset = null ;              // Only valid if active.

    private MultiMapToList<OperationName, Operation> operations     = MultiMap.createMapList() ;
    private Map<String, Operation> endpoints                        = new HashMap<String, Operation>() ;
    
    private volatile DatasetStatus state = UNINITIALIZED ;

    // DataService-level counters.
    private final CounterSet counters                   = new CounterSet() ;
    private final AtomicLong    requestCounter          = new AtomicLong(0) ;   
    private final AtomicBoolean offlineInProgress       = new AtomicBoolean(false) ;
    private final AtomicBoolean acceptingRequests       = new AtomicBoolean(true) ;

    public DataService(DataServiceDesc desc, DatasetGraph dataset) {
        this.svcDesc = desc ;
        this.dataset = dataset ;
        counters.add(CounterName.Requests) ;
        counters.add(CounterName.RequestsGood) ;
        counters.add(CounterName.RequestsBad) ;
    }
    
    public DatasetGraph getDataset() {
        return dataset ; 
    }
    
    public void addEndpoint(OperationName operationName, String endpointName) {
        Operation oper = new Operation(operationName, endpointName) ;
        endpoints.put(endpointName, oper) ;
        operations.put(operationName, oper);
    }
    
    public Operation getOperation(String endpointName) {
        return endpoints.get(endpointName) ;
    }

    public List<Operation> getOperation(OperationName opName) {
        return operations.get(opName) ;
    }

    public Collection<Operation> getOperations() {
        return endpoints.values() ;
    }

    //@Override
    public boolean allowUpdate()    { return true ; }

    public void goOffline()         { 
        offlineInProgress.set(true) ;
        acceptingRequests.set(false) ;
        state = DatasetStatus.OFFLINE ; 
    }
    
    public void goActive()         { 
        offlineInProgress.set(false) ;
        acceptingRequests.set(true) ;
        state = DatasetStatus.ACTIVE ; 
    }

    public boolean isAcceptingRequests() {
        return acceptingRequests.get() ;
    }
    
    //@Override
    public  CounterSet getCounters() { return counters ; }
    
    //@Override 
    public long getRequests() { 
        return counters.value(CounterName.Requests) ;
    }

    //@Override
    public long getRequestsGood() {
        return counters.value(CounterName.RequestsGood) ;
    }
    //@Override
    public long getRequestsBad() {
        return counters.value(CounterName.RequestsBad) ;
    }

    /** Counter of active read transactions */
    public AtomicLong   activeReadTxn           = new AtomicLong(0) ;

    /** Counter of active write transactions */
    public AtomicLong   activeWriteTxn          = new AtomicLong(0) ;

    /** Cumulative counter of read transactions */
    public AtomicLong   totalReadTxn            = new AtomicLong(0) ;

    /** Cumulative counter of writer transactions */
    public AtomicLong   totalWriteTxn           = new AtomicLong(0) ;

    public void startTxn(ReadWrite mode)
    {
        switch(mode)
        {
            case READ:  
                activeReadTxn.getAndIncrement() ;
                totalReadTxn.getAndIncrement() ;
                break ;
            case WRITE:
                activeWriteTxn.getAndIncrement() ;
                totalWriteTxn.getAndIncrement() ;
                break ;
        }
    }

    public void finishTxn(ReadWrite mode)
    {
        switch(mode)
        {
            case READ:  
                activeReadTxn.decrementAndGet() ;
                break ;
            case WRITE:
                activeWriteTxn.decrementAndGet() ;
                break ;
        }
        checkShutdown() ;
    }

    private void checkShutdown() {
        if ( state == CLOSING ) {
            if ( activeReadTxn.get() == 0 && activeWriteTxn.get() == 0 )
                shutdown() ;
        }
    }

    private void shutdown() {
        Fuseki.serverLog.info("Shutting down dataset") ;
        dataset.close() ;
        if ( dataset instanceof DatasetGraphTransaction ) {
            DatasetGraphTransaction dsgtxn = (DatasetGraphTransaction)dataset ;
            StoreConnection.release(dsgtxn.getLocation()) ;
        }
        dataset = null ; 
    }
}

