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

package org.apache.jena.query.text ;

import java.util.Collection ;
import java.util.Collections ;
import java.util.HashMap ;
import java.util.Map ;

import org.apache.jena.atlas.lib.MultiMap ;

import com.hp.hpl.jena.graph.Node ;

/**
 * Definition of a "document"
 */
public class EntityDefinition {
    private final Map<Node, String>      predicateToField = new HashMap<Node, String>() ;
    private final MultiMap<String, Node> fieldToPredicate = MultiMap.createMapList() ;
    private final Collection<String>     fields           = Collections.unmodifiableCollection(fieldToPredicate.keys()) ;
    // private final Collection<String> fields =
    // Collections.unmodifiableCollection(fieldToPredicate.keySet()) ;
    private final String                 entityField ;
    private final String                 primaryField ;
    private final Node                   primaryPredicate ;

    /**
     * @param entityField
     *            The entity being indexed (e.g. it's URI).
     * @param primaryField
     *            The primary/default field to search
     * @param primaryPredicate
     *            The property associated with the primary/default field
     */
    public EntityDefinition(String entityField, String primaryField, Node primaryPredicate) {
        this.entityField = entityField ;
        this.primaryField = primaryField ;
        this.primaryPredicate = primaryPredicate ;
        if (primaryField == null && primaryPredicate != null)
            throw new IllegalArgumentException("primaryField null but primaryPredicate not null") ;
        if (primaryField != null && primaryPredicate == null)
            throw new IllegalArgumentException("primaryField not null but primaryPredicate null") ;
        if (primaryField != null && primaryPredicate != null)
            set(primaryField, primaryPredicate) ;
    }

    public String getEntityField() {
        return entityField ;
    }

    public void set(String field, Node predicate) {
        predicateToField.put(predicate, field) ;
        // Add uniquely.
        Collection<Node> c = fieldToPredicate.get(field) ;
        if (c == null || !c.contains(predicate))
            fieldToPredicate.put(field, predicate) ;
    }

    public Collection<Node> getPredicates(String field) {
        return fieldToPredicate.get(field) ;
    }

    public String getField(Node predicate) {
        return predicateToField.get(predicate) ;
    }

    public Node getPrimaryPredicate() {
        return primaryPredicate ;
    }

    public String getPrimaryField() {
        return primaryField ;
    }

    public Collection<String> fields() {
        return fields ;
    }
}
