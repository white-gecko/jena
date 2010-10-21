/*
 * (c) 2010 Talis Information Ltd
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.sparql.algebra;

import java.util.List ;

import com.hp.hpl.jena.sparql.algebra.op.* ;

/** Wrap another tranform and pass on the transform operation */
public class TransformWrapper implements Transform
{
    protected final Transform transform ;
    
    public TransformWrapper(Transform transform)
    {
        this.transform = transform ;
    }
    
    public Op transform(OpTable opTable)                    { return transform.transform(opTable) ; }
    public Op transform(OpBGP opBGP)                        { return transform.transform(opBGP) ; }
    public Op transform(OpTriple opTriple)                  { return transform.transform(opTriple) ; }
    public Op transform(OpPath opPath)                      { return transform.transform(opPath) ; } 

    public Op transform(OpProcedure opProc, Op subOp)       { return transform.transform(opProc, subOp) ; }
    public Op transform(OpPropFunc opPropFunc, Op subOp)    { return transform.transform(opPropFunc, subOp) ; }

    public Op transform(OpDatasetNames dsNames)             { return transform.transform(dsNames) ; }
    public Op transform(OpQuadPattern quadPattern)          { return transform.transform(quadPattern) ; }
    
    public Op transform(OpFilter opFilter, Op subOp)        { return transform.transform(opFilter, subOp) ; }
    public Op transform(OpGraph opGraph, Op subOp)          { return transform.transform(opGraph, subOp) ; } 
    public Op transform(OpService opService, Op subOp)      { return transform.transform(opService, subOp) ; } 

    public Op transform(OpAssign opAssign, Op subOp)        { return transform.transform(opAssign, subOp) ; }
    public Op transform(OpExtend opExtend, Op subOp)        { return transform.transform(opExtend, subOp) ; }
    
    public Op transform(OpJoin opJoin, Op left, Op right)           { return transform.transform(opJoin, left, right) ; }
    public Op transform(OpLeftJoin opLeftJoin, Op left, Op right)   { return transform.transform(opLeftJoin, left, right) ; }
    public Op transform(OpDiff opDiff, Op left, Op right)           { return transform.transform(opDiff, left, right) ; }
    public Op transform(OpMinus opMinus, Op left, Op right)         { return transform.transform(opMinus, left, right) ; }
    public Op transform(OpUnion opUnion, Op left, Op right)         { return transform.transform(opUnion, left, right) ; }
    public Op transform(OpConditional opCond, Op left, Op right)    { return transform.transform(opCond, left, right) ; } 
    
    public Op transform(OpSequence opSequence, List<Op> elts)       { return transform.transform(opSequence, elts) ; }
    public Op transform(OpDisjunction opDisjunction, List<Op> elts) { return transform.transform(opDisjunction, elts) ; }

    public Op transform(OpExt opExt)                        { return transform.transform(opExt) ; }
    public Op transform(OpNull opNull)                      { return transform.transform(opNull) ; }
    public Op transform(OpLabel opLabel, Op subOp)          { return transform.transform(opLabel, subOp) ; }
    
    public Op transform(OpList opList, Op subOp)            { return transform.transform(opList, subOp) ; }
    public Op transform(OpOrder opOrder, Op subOp)          { return transform.transform(opOrder, subOp) ; }
    public Op transform(OpProject opProject, Op subOp)      { return transform.transform(opProject, subOp) ; }
    public Op transform(OpDistinct opDistinct, Op subOp)    { return transform.transform(opDistinct, subOp) ; }
    public Op transform(OpReduced opReduced, Op subOp)      { return transform.transform(opReduced, subOp) ; }
    public Op transform(OpSlice opSlice, Op subOp)          { return transform.transform(opSlice, subOp) ; }
    public Op transform(OpGroup opGroup, Op subOp)          { return transform.transform(opGroup, subOp) ; }
}

/*
 * (c) 2010 Talis Information Ltd
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