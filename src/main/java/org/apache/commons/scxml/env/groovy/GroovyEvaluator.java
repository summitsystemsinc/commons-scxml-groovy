/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.scxml.env.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.Eval;
import groovy.util.GroovyScriptEngine;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.Evaluator;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.env.jexl.JexlContext;
import org.codehaus.groovy.jsr223.GroovyScriptEngineFactory;
import org.w3c.dom.Node;

/**
 * Evaluator implementation enabling use of JEXL expressions in
 * SCXML documents.
 *
 */
public class GroovyEvaluator implements Evaluator, Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Error message if evaluation context is not a JexlContext. */
    private static final String ERR_CTX_TYPE = "Error evaluating Groovy "
        + "expression, Context must be a org.apache.commons.groovy.GroovyContext";

    /** Pattern for recognizing the SCXML In() special predicate. */
    private static Pattern inFct = Pattern.compile("In\\(");
    /** Pattern for recognizing the Commons SCXML Data() builtin function. */
    private static Pattern dataFct = Pattern.compile("Data\\(");

    /** Constructor. */
    public GroovyEvaluator() {
        super();
        Eval.me("def language = 'Groovy';");
    }

    /**
     * Evaluate an expression.
     *
     * @param ctx variable context
     * @param expr expression
     * @return a result of the evaluation
     * @throws SCXMLExpressionException For a malformed expression
     * @see Evaluator#eval(Context, String)
     */
    public Object eval(final Context ctx, final String expr)
    throws SCXMLExpressionException {
        if (expr == null) {
            return null;
        }
        GroovyContext groovyCtx = null;
        if (ctx instanceof GroovyContext) {
            groovyCtx = (GroovyContext) ctx;
        } else {
            throw new SCXMLExpressionException(ERR_CTX_TYPE);
        }
        
        try {
            String evalExpr = inFct.matcher(expr).replaceAll("_builtin.isMember(_ALL_STATES, ");
            evalExpr = dataFct.matcher(evalExpr).
                replaceAll("_builtin.data(_ALL_NAMESPACES, ");
            @SuppressWarnings("rawtypes")
			Map values = groovyCtx.getVars();
            return Eval.me("params", values, evalExpr);
        } catch (Exception e) {
            throw new SCXMLExpressionException("eval('" + expr + "'):"
                + e.getMessage(), e);
        }
    }

    /**
     * @see Evaluator#evalCond(Context, String)
     */
    public Boolean evalCond(final Context ctx, final String expr)
    throws SCXMLExpressionException {
        if (expr == null) {
            return null;
        }
        if (!(ctx instanceof GroovyContext)) {
            throw new SCXMLExpressionException(ERR_CTX_TYPE);
        }
        try {
            Binding b = new Binding();
            
            String evalExpr = inFct.matcher(expr).replaceAll("_builtin.isMember(_ALL_STATES, ");
            evalExpr = dataFct.matcher(evalExpr).
                replaceAll("_builtin.data(_ALL_NAMESPACES, ");
            Map<String, Object> values = getEffectiveVars((GroovyContext)ctx);
            for(String key : values.keySet()){
                b.setVariable(key, values.get(key));
            }
            GroovyShell sh = new GroovyShell(b);
            return (Boolean)sh.evaluate(evalExpr);
        } catch (Exception e) {
            throw new SCXMLExpressionException("eval('" + expr + "'):"
                + e.getMessage(), e);
        }
    }

    /**
     * @see Evaluator#evalLocation(Context, String)
     */
    public Node evalLocation(final Context ctx, final String expr)
    throws SCXMLExpressionException {
        throw new UnsupportedOperationException("Not supported yet");
        /**
        if (expr == null) {
            return null;
        }
        GroovyContext jexlCtx = null;
        if (ctx instanceof GroovyContext) {
            jexlCtx = (GroovyContext) ctx;
        } else {
            throw new SCXMLExpressionException(ERR_CTX_TYPE);
        }
        Expression exp = null;
        try {
            String evalExpr = inFct.matcher(expr).
                replaceAll("_builtin.isMember(_ALL_STATES, ");
            evalExpr = dataFct.matcher(evalExpr).
                replaceFirst("_builtin.dataNode(_ALL_NAMESPACES, ");
            evalExpr = dataFct.matcher(evalExpr).
                replaceAll("_builtin.data(_ALL_NAMESPACES, ");
            exp = ExpressionFactory.createExpression(evalExpr);
            return (Node) exp.evaluate(getEffectiveContext(jexlCtx));
        } catch (Exception e) {
            throw new SCXMLExpressionException("eval('" + expr + "'):"
                + e.getMessage(), e);
        }
        */
    }

    /**
     * Create a new child context.
     *
     * @param parent parent context
     * @return new child context
     * @see Evaluator#newContext(Context)
     */
    public Context newContext(final Context parent) {
        return new GroovyContext(parent);
    }
    
    @SuppressWarnings("unchecked")
	private Map<String, Object> getEffectiveVars(final GroovyContext context){
    	Map<String, Object> retVal = new HashMap<String, Object>();
    	
    	if(context.getParent()!= null){
    		retVal.putAll(getEffectiveVars((GroovyContext)context.getParent()));
    	}
    	retVal.putAll(context.getVars());
    	
    	return retVal;
    }

}

