/*
 * (c) Copyright 2008 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.tdb.pgraph.assembler;

import static com.hp.hpl.jena.sparql.util.graph.GraphUtils.exactlyOneProperty;
import static com.hp.hpl.jena.sparql.util.graph.GraphUtils.getAsStringValue;
import static com.hp.hpl.jena.tdb.pgraph.assembler.PGraphAssemblerVocab.pDescription;
import static com.hp.hpl.jena.tdb.pgraph.assembler.PGraphAssemblerVocab.pFile;

import com.hp.hpl.jena.rdf.model.Resource;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.assembler.Mode;
import com.hp.hpl.jena.assembler.assemblers.AssemblerBase;

import com.hp.hpl.jena.tdb.base.file.Location;
import com.hp.hpl.jena.tdb.index.IndexBuilder;
import com.hp.hpl.jena.tdb.index.RangeIndex;
import com.hp.hpl.jena.tdb.pgraph.GraphTDB;
import com.hp.hpl.jena.tdb.pgraph.TripleIndex;

public class IndexAssembler extends AssemblerBase //implements Assembler
{
    // Subassembler of a PGraph assembler and is called directly.
    /* 
     * [ :description "SPO" ; :file "SPO.idx" ]
     */
    
    private Location location = null ;
    public IndexAssembler()                   { this.location = null ; }
    public IndexAssembler(Location location)  { this.location = location ; }
    
    @Override
    public TripleIndex open(Assembler a, Resource root, Mode mode)
    {
        exactlyOneProperty(root, pDescription) ;
        String desc = getAsStringValue(root, pDescription).toUpperCase() ;
        exactlyOneProperty(root, pFile) ;
        String filename = getAsStringValue(root, pFile) ;
        
        // Need to get location from the enclosing PGraphAssembler
        if ( location != null )
            filename = location.absolute(filename) ;
        
        RangeIndex rIndex = IndexBuilder.createRangeIndex(new Location(filename), 
                                                          desc, 
                                                          GraphTDB.indexRecordFactory) ;
        return new TripleIndex(desc, rIndex) ;
    }

    public static RangeIndex rangeIndex(String filename, String name)
    {
        return IndexBuilder.createRangeIndex(new Location(filename), name, GraphTDB.indexRecordFactory) ;
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