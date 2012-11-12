/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.commons.scxml.env.groovy;

import groovy.util.Eval;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.Evaluator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.w3c.dom.Node;

/**
 *
 * @author Justin Smith <justin.smith@summitsystemsinc.com>
 */
public class GroovyEvaluatorTest {
    
    Context context;
    Evaluator evaluator;
    
    public GroovyEvaluatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        evaluator = new GroovyEvaluator();
        context = new GroovyContext();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of eval method, of class GroovyEvaluator.
     */
    @Test
    public void testEval() throws Exception {
        System.out.println("eval");
        
        Map vars = new HashMap();
        vars.put("world", "world");
        context.set("world", "world");
        String expr = "\"Hello ${params.world}\"";        
        
        Object expResult = Eval.me("params",vars,expr);
        Object result = evaluator.eval(context, expr);
        assertEquals(expResult, result);
    }

    /**
     * Test of evalCond method, of class GroovyEvaluator.
     */
    @Test
    public void testEvalCond() throws Exception {
        System.out.println("evalCond");
        
        String expr = "1+1 == 2";        
        
        Object expResult = true;
        Object result = evaluator.eval(context, expr);
        assertEquals(expResult, result);
    }

    /**
     * Test of evalLocation method, of class GroovyEvaluator.
     */
    @Test
    @Ignore("Still not sure what this is.")
    public void testEvalLocation() throws Exception {
        System.out.println("evalLocation");
        Context ctx = null;
        String expr = "";
        GroovyEvaluator instance = new GroovyEvaluator();
        Node expResult = null;
        Node result = instance.evalLocation(ctx, expr);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of newContext method, of class GroovyEvaluator.
     */
    @Test
    @Ignore(value="The equals methods in SCXML don't work as expected, ignoring this test.")
    public void testNewContext() {
        System.out.println("newContext");
        Context parent = new GroovyContext();
        GroovyEvaluator instance = new GroovyEvaluator();
        Context expResult = new GroovyContext(parent);
        Context result = instance.newContext(parent);
        assertEquals(expResult, result);
    }
}
