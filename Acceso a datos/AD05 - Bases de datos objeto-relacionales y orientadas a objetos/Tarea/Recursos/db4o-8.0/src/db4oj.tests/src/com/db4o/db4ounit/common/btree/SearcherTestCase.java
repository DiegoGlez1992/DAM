/* This file is part of the db4o object database http://www.db4o.com

Copyright (C) 2004 - 2011  Versant Corporation http://www.versant.com

db4o is free software; you can redistribute it and/or modify it under
the terms of version 3 of the GNU General Public License as published
by the Free Software Foundation.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program.  If not, see http://www.gnu.org/licenses/. */
package com.db4o.db4ounit.common.btree;

import com.db4o.internal.btree.*;

import db4ounit.*;


public class SearcherTestCase implements TestCase, TestLifeCycle{
    
    private Searcher _searcher;
    
    private final static int FIRST = 4;
    
    private final static int LAST = 11;
    
    private final static int[] EVEN_VALUES = new int[] {4, 7, 9, 11};
    
    private final static int[] ODD_VALUES = new int[] {4, 7, 8, 9, 11};

    private final static int[] NON_MATCHES = new int[] {3, 5, 6, 10, 12};

    private final static int[] MATCHES = new int[] {4, 7, 9, 11};
    
    private final static int BEFORE = FIRST - 1;
    
    private final static int BEYOND = LAST + 1;
    
    public void ttestPrintResults(){
        // not a test, but nice to visualize
        int[] evenValues = new int[] {4, 7, 9, 11};
        int[] searches = new int[]{3, 4, 5, 7, 10, 11, 12};
        for (int i = 0; i < searches.length; i++) {
            int res = search(evenValues, searches[i]);
            System.out.println(res);    
        }
    }
    
    public void testCursorEndsOnSmaller(){
        Assert.areEqual(0, search(EVEN_VALUES, 6));
        Assert.areEqual(0, search(ODD_VALUES, 6));
        Assert.areEqual(2, search(EVEN_VALUES, 10));
        Assert.areEqual(3, search(ODD_VALUES, 10));
    }
    
    public void testMatchEven(){
        assertMatch(EVEN_VALUES);
    }
    
    public void testMatchOdd(){
        assertMatch(ODD_VALUES);
    }
    
    public void testNoMatchEven(){
        assertNoMatch(EVEN_VALUES);
    }
    
    public void testNoMatchOdd(){
        assertNoMatch(ODD_VALUES);
    }
    
    public void testBeyondEven(){
        assertBeyond(EVEN_VALUES);
    }
    
    public void testBeyondOdd(){
        assertBeyond(ODD_VALUES);
    }
    
    public void testNotBeyondEven(){
        assertNotBeyond(EVEN_VALUES);
    }
    
    public void testNotBeyondOdd(){
        assertNotBeyond(ODD_VALUES);
    }

    public void testBeforeEven(){
        assertBefore(EVEN_VALUES);
    }
    
    public void testBeforeOdd(){
        assertBefore(ODD_VALUES);
    }
    
    public void testNotBeforeEven(){
        assertNotBefore(EVEN_VALUES);
    }
    
    public void testNotBeforeOdd(){
        assertNotBefore(ODD_VALUES);
    }
    
    public void testEmptySet(){
        _searcher = new Searcher(SearchTarget.ANY, 0);
        if(_searcher.incomplete()){
            Assert.fail();
        }
        Assert.areEqual(0, _searcher.cursor());
    }


    private void assertMatch(int[] values) {
        for (int i = 0; i < MATCHES.length; i++) {
            search(values, MATCHES[i]);
            Assert.isTrue(_searcher.foundMatch());
        }
    }

    private void assertNoMatch(int[] values) {
        for (int i = 0; i < NON_MATCHES.length; i++) {
            search(values, NON_MATCHES[i]);
            Assert.isFalse(_searcher.foundMatch());
        }
    }
    
    private void assertBeyond(int[] values) {
        int res = search(values, BEYOND);
        Assert.areEqual(values.length - 1, res);
        Assert.isTrue(_searcher.afterLast());
    }

    private void assertNotBeyond(int[] values) {
        int res = search(values, LAST);
        Assert.areEqual(values.length - 1, res);
        Assert.isFalse(_searcher.afterLast());
    }
    
    private void assertBefore(int[] values) {
        int res = search(values, BEFORE);
        Assert.areEqual(0, res);
        Assert.isTrue(_searcher.beforeFirst());
    }

    private void assertNotBefore(int[] values) {
        int res = search(values, FIRST);
        Assert.areEqual(0, res);
        Assert.isFalse(_searcher.beforeFirst());
    }

    
    
    
    private int search(int[] values, int value){
        
        _searcher = new Searcher(SearchTarget.ANY, values.length);
        
        while(_searcher.incomplete()){
            _searcher.resultIs( values[_searcher.cursor()] - value );
        }
        
        return _searcher.cursor();
    }

    public void setUp() throws Exception {
        _searcher = null;
    }

    public void tearDown() throws Exception {
        
    }

}
