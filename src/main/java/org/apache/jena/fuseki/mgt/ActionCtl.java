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

package org.apache.jena.fuseki.mgt;

import org.apache.jena.fuseki.Fuseki ;
import org.apache.jena.fuseki.server.DatasetRef ;
import org.apache.jena.fuseki.server.DatasetRegistry ;
import org.apache.jena.fuseki.servlets.ActionBase ;
import org.apache.jena.fuseki.servlets.HttpAction ;

/** Control/admin request lifecycle */
public abstract class ActionCtl extends ActionBase
{
    protected ActionCtl() { super(Fuseki.adminLog) ; }
    
    @Override
    final
    protected void execCommonWorker(HttpAction action)
    {
        DatasetRef dsRef = null ;
        String name = mapRequestToDatasetName(action) ;
        if ( name != null )
            dsRef = DatasetRegistry.get().get(name) ;
        else {
            // This is a placeholder when creating new DatasetRefs
            // and also if addressig a container, not a dataset
            dsRef = new DatasetRef() ;
            dsRef.name = name ;
        }
        action.setDataset(dsRef) ;
        executeAction(action) ;
    }

    protected String mapRequestToDatasetName(HttpAction action) {
//        action.log.info("context path  = "+action.request.getContextPath()) ;
//        action.log.info("pathinfo      = "+action.request.getPathInfo()) ;
//        action.log.info("servlet path  = "+action.request.getServletPath()) ;
        // if /name
        //    request.getServletPath() otherwise it's null
        // if /*
        //    request.getPathInfo() ; otherwise it's null.
        
        String pathInfo = action.request.getPathInfo() ;
        if ( pathInfo == null || pathInfo.isEmpty() || pathInfo.equals("/") )
            // Includes calling as a container. 
            return null ;
        String name = pathInfo ;
        // pathInfo starts with a "/"
        int idx = pathInfo.lastIndexOf('/') ;
        if ( idx > 0 )
            name = name.substring(idx) ;
        // Returns "/name"
        return name ; 
    }

    // Execute - no stats.
    // Intercept point for the UberServlet 
    protected void executeAction(HttpAction action) {
        executeLifecycle(action) ;
    }
    
    // This is the service request lifecycle.
    final
    protected void executeLifecycle(HttpAction action)
    {
        startRequest(action) ;
        perform(action) ;
        finishRequest(action) ;
    }
    
    protected abstract void perform(HttpAction action) ;

//    /** Map request to uri in the registry.
//     *  null means no mapping done (passthrough). 
//     */
//    protected String mapRequestToDataset(HttpAction action) 
//    {
//        return ActionLib.mapRequestToDataset(action.request.getRequestURI()) ;
//    }
}