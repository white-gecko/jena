/*
 * (c) Copyright 2001, 2002, 2003, 2004, 2005, 2006 Hewlett-Packard Development Company, LP
 * [See end of file]
 */

/* Generated By:JJTree: Do not edit this line. Q_Query.java */

package com.hp.hpl.jena.rdql.parser;


import com.hp.hpl.jena.datatypes.xsd.*;
import com.hp.hpl.jena.rdql.*;

import java.util.* ;

/** Concrete result of parsing a query.
 *  This is the top node inth eabstract syntax tree generated by the jjtree/javacc grammar.
 *  After being created this class builds a Query suitable for execution.  After that,
 *  this is not used, although many of the syntax tree nodes are used as they implement
 *  the interfaces needed by the abstarct query model.
 */

public class Q_Query extends SimpleNode
{
    
    public Q_Query(int id) { super(id); }

    public Q_Query(RDQLParser p, int id) { super(p, id); }

    private Query query = null ;

    // --------------------------------------------------------------------

    boolean selectAllVars = false ;

    // Post parsing fixups.
    // This is to rearrange the parse tree (pull up the structural nodes
    // and remove the need for later casting).
    // It separates the parse tree from the query processor.
    // By the end, the only classes of relevance should be:
    //      Var
    //      Expr
    //      Value
    //      TriplePattern
    // all nicely inserted into the Query object.

    // This code could live in the parser object itself but then it is stored in the
    // .jjt file, making development harder.

    public void phase2(Query q)
    {
        query = q ;
        try {
            int numQueryChildren = jjtGetNumChildren() ;
            // Firstly , fix up URIs and qnames.
            // Do this always because of default prefixes.
            
            for ( int j = 0 ; j < numQueryChildren ; j++ )
            {
                Node n = jjtGetChild(j) ;
                if ( n instanceof Q_PrefixesClause )
                {
                    extractPrefixes(q, (Q_PrefixesClause)n) ; 
                }
            }
            this.postParse(q) ;
            
            int i = 0 ;
            // Select
            if ( jjtGetChild(i) instanceof Q_SelectClause )
            {
                extractVarList(q, jjtGetChild(i)) ;
                i++ ;
            }
            else
                throw new RDQL_InternalErrorException("Parser didn't catch absense of select clause") ;

            // Source
            if ( jjtGetChild(i) instanceof Q_SourceClause )
            {
                // SourceClause -> SourceSelector -> URL
                int numSources = jjtGetChild(i).jjtGetNumChildren() ;
                if ( numSources > 1 )
                {
                    throw new QueryException("Error: Multiple sources in FROM clause") ;  
                }
                
                // This coide is waiting to be fixed for multiple sources
                // That requires an interface change to Query   
                for ( int j = 0 ; j < numSources ; j++ )
                {                
                    Node n = jjtGetChild(i).jjtGetChild(j).jjtGetChild(0) ;
                    String source = ((Q_URL)n).urlString ;
                    // Just the first
                    if ( j == 0 )
                        q.setSourceURL(source) ;
                }
                i++ ;
            }

            // Triple patterns

            if ( jjtGetChild(i) instanceof Q_TriplePatternClause )
            {
                // Convert to graph-level query.
                extractTriplePatternsFP(q, jjtGetChild(i)) ;
                i++ ;
            }
            else
                throw new RDQL_InternalErrorException("Parser didn't catch absense of triple patterns") ;

            // Constraints

            if ( i < numQueryChildren )
            {
	            if ( jjtGetChild(i) instanceof Q_ConstraintClause )
	            {
	                extractConstraints(q, jjtGetChild(i)) ;
	                i++ ;
	            }
            }
        }
        catch (RDQL_InternalErrorException e) { throw e ; }
        catch (QueryException qEx) { throw qEx; } 
        catch (ClassCastException e) { throw new RDQL_InternalErrorException("Parser generated illegal parse tree: "+e) ; }
        catch (Exception e)
        {
            e.printStackTrace(System.err) ;
            throw new RDQL_InternalErrorException("Unknown exception: "+e) ;
        }
    }


    /** Formats the query from phase 2 in a style that is acceptable to the
     *  parser.  Note this is NOT guaranteed to be the same as the original string
     *  because we may have done optimizations or other rearranging.
     *  It should give the same answers on the same dataset.
     */

    public String toString()
    {
    	throw new UnsupportedOperationException ("Q_Query.toString()") ;
        /*
        StringBuff sbuff = new StringBuffer(1024) ;
        sbuff.append(RDQLParserConstants.tokenImage[RDQLParserConstants.SELECT]) ;
        for ( for i = 0 ; i < query.resultVars.size() ; i++ )
            sbuff.append(" ?").append(((Var)query.resultVars.get(i)).getVarName()) ;

        sbuff.append(" ").append(RDQLParserConstants.tokenImage[RDQLParserConstants.WHERE]) ;
        for ( for i = 0 ; i < query.constraints ; i++ )
        {
            TriplePattern tp = (TriplePattern)query.constraints.get(i) ;
            sbuff.append(tp.toString()) ;
        }
        */
    }

    private void extractVarList(Query q, Node node)
    {
        int n = node.jjtGetNumChildren() ;
        selectAllVars = ( n == 0 ) ;

        for ( int i = 0 ; i < n ; i++ )
        {
            Node c = node.jjtGetChild(i) ;
            if ( ! (c instanceof Q_Var) )
                throw new RDQL_InternalErrorException("Internal error: parser created '"+c.getClass().getName()+"' when Q_Var expected") ;
            Q_Var v = (Q_Var)c ;
            q.addResultVar(v.varName) ;
        }
    }

    // Like the above but for Graph objects, not Model objects. 
    
    private void extractTriplePatternsFP(Query q, Node node)
    {
        Q_TriplePatternClause tpc = (Q_TriplePatternClause)node ;
        List patternVars = q.getBoundVars() ;
        int n = tpc.jjtGetNumChildren() ;
        for ( int j = 0 ; j < n ; j++ )
        {
            Q_TriplePattern tp = (Q_TriplePattern)tpc.jjtGetChild(j) ;
            if ( tp.jjtGetNumChildren() != 3 )
                throw new RDQL_InternalErrorException("Triple pattern has "+tp.jjtGetNumChildren()+" children") ;

            com.hp.hpl.jena.graph.Node nodeSubj = convertToGraphNode(tp.jjtGetChild(0), q) ;
            com.hp.hpl.jena.graph.Node nodePred = convertToGraphNode(tp.jjtGetChild(1), q) ;
            com.hp.hpl.jena.graph.Node nodeObj  = convertToGraphNode(tp.jjtGetChild(2), q) ;
            q.addTriplePattern(nodeSubj, nodePred, nodeObj) ;
        }
        
        if ( selectAllVars )
        {
            for ( Iterator iter = patternVars.iterator() ; iter.hasNext() ; )
            {
                String varName = (String)iter.next() ;
                q.addResultVar(varName) ;
            }
        }
        
        
    }

    // This operation puts all the thing to graph node conversion code in
    // one place.
    
    
    static private com.hp.hpl.jena.graph.Node convertToGraphNode(Node n, Query q)
    {
        if ( n instanceof Var )
        {
            String varName = ((Var)n).getVarName() ;
            q.addBoundVar(varName) ;
            return com.hp.hpl.jena.graph.Node.createVariable(((Var)n).getVarName()) ;
        }
        if ( n instanceof ParsedLiteral)
        {
            ParsedLiteral v = (ParsedLiteral)n ;
            
            if ( v.isNode() )
                return v.getNode() ;

            if ( v.isURI() )
                return com.hp.hpl.jena.graph.Node.createURI(v.getURI()) ;
                
            if ( v.isString() )
                return com.hp.hpl.jena.graph.Node.createLiteral(v.getString(), null, null) ;
            if ( v.isBoolean())
                return com.hp.hpl.jena.graph.Node.createLiteral(v.asUnquotedString(), null,null) ;
            
//            
            if ( v.isInt() )
                return com.hp.hpl.jena.graph.Node.createLiteral(
                        v.asUnquotedString(), null, XSDDatatype.XSDinteger) ;
            if ( v.isDouble())
                return com.hp.hpl.jena.graph.Node.createLiteral(
                    v.asUnquotedString(), null, XSDDatatype.XSDdouble) ;

            String s = v.getString() ;
            System.err.println("BUG: "+s) ;
                
//                
//            
//            //if ( v.isNumber())
                
        }
        throw new RDQL_InternalErrorException("convertToGraphNode encountered strange type: "+n.getClass().getName()) ;
                                                                
    }

    private void extractConstraints(Query q, Node node)
    {
        Q_ConstraintClause qcc = (Q_ConstraintClause)node ;
        int n = qcc.jjtGetNumChildren() ;
        for ( int j = 0 ; j < n ; j++ )
        {
            Object obj = qcc.jjtGetChild(j) ;
            if ( ! ( obj instanceof Constraint ) )
                throw new RDQL_InternalErrorException("Parse node in AND clause isn't a Constraint") ;
            //q.addConstraint(new ConstraintExpr(expr)) ;
            q.addConstraint((Constraint)obj) ;
        }
    }

    private void extractPrefixes(Query q, Q_PrefixesClause qns)
    {
    	if ( qns == null )
    		return ;
    
        int n = qns.jjtGetNumChildren() ;
        for ( int j = 0 ; j < n ; j++ )
        {
            Q_PrefixDecl qnsd = (Q_PrefixDecl)qns.jjtGetChild(j) ;
            // They should appear in pairs: an identifier and a URI
            for ( int k = 0 ; k < qnsd.jjtGetNumChildren() ; k+=2 )
            {
                Q_Identifier id = (Q_Identifier)qnsd.jjtGetChild(k) ;
                //Object tmp = qnsd.jjtGetChild(k+1) ; // Temp debug
                Q_URI uri = (Q_URI)qnsd.jjtGetChild(k+1) ;
                query.setPrefix(id.toString(), uri.toString()) ;
            }
        }
    }
}

/*
 *  (c) Copyright 2001, 2002, 2003, 2004, 2005, 2006 Hewlett-Packard Development Company, LP
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
