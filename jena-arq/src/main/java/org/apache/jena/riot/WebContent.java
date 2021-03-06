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

package org.apache.jena.riot;

import org.apache.jena.atlas.lib.Lib ;
import org.apache.jena.atlas.web.ContentType ;


public class WebContent
{
    // Names for things.

    public static final String      contentTypeTurtle            = "text/turtle" ;
    public static final ContentType ctTurtle                     = ContentType.create(contentTypeTurtle) ;

    public static final String      contentTypeTurtleAlt1        = "application/turtle" ;
    public static final ContentType ctTurtleAlt1                 = ContentType.create(contentTypeTurtleAlt1) ;

    public static final String      contentTypeTurtleAlt2        = "application/x-turtle" ;
    public static final ContentType ctTurtleAlt2                 = ContentType.create(contentTypeTurtleAlt2) ;

    public static final String      contentTypeRDFXML            = "application/rdf+xml" ;
    public static final ContentType ctRDFXML                     = ContentType.create(contentTypeRDFXML) ;

    public static final String      contentTypeRDFJSON           = "application/rdf+json" ;
    public static final ContentType ctRDFJSON                    = ContentType.create(contentTypeRDFJSON) ;

    public static final String      contentTypeJSONLD            = "application/ld+json" ;
    public static final ContentType ctJSONLD                     = ContentType.create(contentTypeJSONLD) ;

    // MIME type for N-triple is text/plain (!!!)
    public static final String      contentTypeTextPlain         = "text/plain" ;
    public static final ContentType ctTextPlain                  = ContentType.create(contentTypeTextPlain) ;

    public static final String      contentTypeNTriples          = "application/n-triples" ;
    public static final ContentType ctNTriples                   = ContentType.create(contentTypeNTriples) ;

    public static final String      contentTypeNTriplesAlt       = contentTypeTextPlain ;
    public static final ContentType ctNTriplesAlt                = ContentType.create(contentTypeNTriplesAlt) ;

    public static final String      contentTypeXML               = "application/xml" ;
    public static final ContentType ctXML                        = ContentType.create(contentTypeXML) ;

    public static final String      contentTypeXMLAlt            = "text/xml" ;
    public static final ContentType ctXMLAlt                     = ContentType.create(contentTypeXMLAlt) ;

    public static final String      contentTypeTriG              = "text/trig" ;
    public static final ContentType ctTriG                       = ContentType.create(contentTypeTriG) ;

    public static final String      contentTypeNQuads            = "application/n-quads" ;
    public static final ContentType ctNQuads                     = ContentType.create(contentTypeNQuads) ;

    public static final String      contentTypeTriGAlt1          = "application/x-trig" ;
    public static final ContentType ctTriGAlt1                   = ContentType.create(contentTypeTriGAlt1) ;

    public static final String      contentTypeTriGAlt2          = "application/trig" ;
    public static final ContentType ctTriGAlt2                   = ContentType.create(contentTypeTriGAlt2) ;

    public static final String      contentTypeNQuadsAlt1        = "text/n-quads" ;
    public static final ContentType ctNQuadsAlt1                 = ContentType.create(contentTypeNQuadsAlt1) ;

    public static final String      contentTypeNQuadsAlt2        = "text/nquads" ;
    public static final ContentType ctNQuadsAlt2                 = ContentType.create(contentTypeNQuadsAlt2) ;

    public static final String      contentTypeTriX              = "application/trix+xml" ;
    public static final ContentType ctTriX                       = ContentType.create(contentTypeTriX) ;

    public static final String      contentTypeOctets            = "application/octet-stream" ;
    public static final ContentType ctOctets                     = ContentType.create(contentTypeOctets) ;

    public static final String      contentTypeMultipartMixed    = "multipart/mixed" ;
    public static final ContentType ctMultipartMixed             = ContentType.create(contentTypeMultipartMixed) ;

    public static final String      contentTypeMultipartFormData = "multipart/form-data" ;
    public static final ContentType ctMultipartFormData          = ContentType.create(contentTypeMultipartFormData) ;

    public static final String      contentTypeMultiAlt          = "multipart/alternative" ;
    public static final ContentType ctMultiAlt                   = ContentType.create(contentTypeMultiAlt) ;

    public static final String      contentTypeRdfJson           = "application/rdf+json" ;
    public static final ContentType ctRdfJson                    = ContentType.create(contentTypeRdfJson) ;

    public static final String      contentTypeN3                = "text/rdf+n3" ;
    public static final ContentType ctTypeN3                     = ContentType.create("text/rdf+n3") ;
    public static final String      contentTypeN3Alt1            = "application/n3" ;
    public static final ContentType ctN3Alt1                     = ContentType.create(contentTypeN3Alt1) ;

    public static final String      contentTypeN3Alt2            = "text/n3" ;
    public static final ContentType ctN3Alt2                     = ContentType.create(contentTypeN3Alt2) ;

    public static final String      contentTypeResultsXML        = "application/sparql-results+xml" ;
    public static final ContentType ctResultsXML                 = ContentType.create(contentTypeResultsXML) ;

    public static final String      contentTypeResultsJSON       = "application/sparql-results+json" ;
    public static final ContentType ctResultsJSON                = ContentType.create(contentTypeResultsJSON) ;

    public static final String      contentTypeJSON              = "application/json" ;
    public static final ContentType ctJSON                       = ContentType.create(contentTypeJSON) ;

    // Unofficial
    public static final String      contentTypeResultsBIO        = "application/sparql-results+bio" ;
    public static final ContentType ctResultsBIO                 = ContentType.create(contentTypeResultsBIO) ;

    public static final String      contentTypeSPARQLQuery       = "application/sparql-query" ;
    public static final ContentType ctSPARQLQuery                = ContentType.create(contentTypeSPARQLQuery) ;

    public static final String      contentTypeSPARQLUpdate      = "application/sparql-update" ;
    public static final ContentType ctSPARQLUpdate               = ContentType.create(contentTypeSPARQLUpdate) ;

    public static final String      contentTypeHTMLForm          = "application/x-www-form-urlencoded" ;
    public static final ContentType ctHTMLForm                   = ContentType.create(contentTypeHTMLForm) ;

    public static final String      contentTypeTextCSV           = "text/csv" ;
    public static final ContentType ctTextCSV                    = ContentType.create(contentTypeTextCSV) ;

    public static final String      contentTypeTextTSV           = "text/tab-separated-values" ;
    public static final ContentType ctTextTSV                    = ContentType.create(contentTypeTextTSV) ;

    public static final String      contentTypeSSE               = "text/sse" ;
    public static final ContentType ctSSE                        = ContentType.create(contentTypeSSE) ;

    public static final String      charsetUTF8                  = "utf-8" ;
    public static final String      charsetASCII                 = "ascii" ;

    // Names used in Jena for the parsers
    // See also Lang enum (preferred).
    public static final String langRDFXML           = "RDF/XML" ;
    public static final String langRDFXMLAbbrev     = "RDF/XML-ABBREV" ;
    public static final String langNTriple          = "N-TRIPLE" ;
    public static final String langNTriples         = "N-TRIPLES" ;
    public static final String langN3               = "N3" ;
    public static final String langTurtle           = "TURTLE" ;
    public static final String langTTL              = "TTL" ;
    public static final String langRdfJson			= "RDF/JSON" ;

    public static final String langNQuads           = "NQUADS" ;
    public static final String langTriG             = "TRIG" ;
    
    /** Java name for UTF-8 encoding */
    public static final String encodingUTF8         = "utf-8" ;
    
    /** Accept header part when looking for a graph */
    private static final String defaultGraphAccept          
        =  "text/turtle,application/n-triples;q=0.9,application/ld+json;q=0.8,application/rdf+xml;q=0.7" ;
    
    /** Accept header when looking for a graph */
    // Catches aplication/xml and application.json
    public static final String defaultGraphAcceptHeader     =  defaultGraphAccept+",*/*;q=0.5" ; 

    /** Accept header part when looking for a dataset */
    private static final String defaultDatasetAccept         
        =  "application/trig,application/n-quads;q=0.9,text/x-nquads;q=0.8,application/x-trig;q=0.7,application/ld+json;q=0.5" ;
    
    /** Accept header when looking for a dataset */
    public static final String defaultDatasetAcceptHeader   =  defaultDatasetAccept+",*/*;q=0.5" ;
    
    /** Accept header when looking for a graph or dataset */
    public static final String defaultRDFAcceptHeader       =  defaultGraphAccept+","+defaultDatasetAccept+",*/*;q=0.5" ;
    
    /** Return our "canonical" name for a Content Type.
     * This should be the standard one, no X-*
     */
    public static String contentTypeCanonical(String contentType)
    { 
        Lang lang = RDFLanguages.contentTypeToLang(contentType) ;
        if ( lang == null )
            return null ;
        return lang.getHeaderString() ;
        //return mapLangToContentType.get(lang) ;
    }

    /** Match content type (ignores charsets and other parameters) */ 
    public static boolean matchContentType(ContentType ct1, ContentType ct2)  {
        return matchContentType(ct1.getContentType(), ct2.getContentType()) ;
    }
    
    public static boolean matchContentType(String ct1, String ct2)  {
        return Lib.equalsIgnoreCase(ct1,  ct2) ;
    }

    public static boolean isHtmlForm(ContentType ct) {
        if ( ct == null )
            return false ;
        return contentTypeHTMLForm.equalsIgnoreCase(ct.getContentType()) ;
    }

    public static boolean isMultiPartForm(ContentType ct) {
        return contentTypeMultipartFormData.equalsIgnoreCase(ct.getContentType()) ;
    }

    /** @deprecated Use {@linkplain RDFLanguages#contentTypeToLang(String)}*/
    @Deprecated
    public static Lang contentTypeToLang(String contentType)
    {
        return RDFLanguages.contentTypeToLang(contentType) ;
    }

    /** @deprecated Use {@linkplain Lang#getHeaderString()} */
    @Deprecated
    public static String mapLangToContentType(Lang lang) { return lang.getHeaderString() ; }
    
    /** @deprecated Use {@linkplain RDFLanguages#getCharsetForContentType(String)} */
    @Deprecated
    public static String getCharsetForContentType(String contentType)
    {
        return RDFLanguages.getCharsetForContentType(contentType) ;
    }
}
