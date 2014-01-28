/*  
 * $Id$  
 *   
 * Copyright (c) 2012-2014 Fran Lattanzio  
 *   
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal  
 * in the Software without restriction, including without limitation the rights  
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
 * copies of the Software, and to permit persons to whom the Software is  
 * furnished to do so, subject to the following conditions:  
 *   
 * The above copyright notice and this permission notice shall be included in  
 * all copies or substantial portions of the Software.  
 *   
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  
 * SOFTWARE.  
 */ 
package com.chupacadabra.finitedifference.bandwidth;

import com.chupacadabra.finitedifference.FiniteDifference;
import com.chupacadabra.finitedifference.UnivariateFiniteDifferenceDerivativeFunction;
import com.chupacadabra.finitedifference.UnivariateFunction;
import com.chupacadabra.finitedifference.util.MachineEpsilon;
import com.chupacadabra.finitedifference.util.PowerOfTwo;


/**
 * Mathur-inspired approximately optimal bandwidth selector.
 */
public final class MathurApproximatelyOptimal
{
	
	/**
	 * The trial grid width.
	 */
	private final Double trialGridWidth;

	/**
	 * The condition error.
	 */
	private final double conditionError;

	/**
	 * The roundoff error.
	 */
	private final double roundoffError;
	
	/**
	 * The point.
	 */
	private final double x; 
	
	/**
	 * Adjusted function value.
	 */
	private final double functionValue;
	
	/**
	 * Adjusted function value.
	 */
	private final double value;
	
	/**
	 * The finite difference.
	 */
	private final FiniteDifference finiteDifference;
	
	/**
	 * The function.
	 */
	private final UnivariateFunction function;

	/**
	 * Constructor.
	 * 
	 * @param trialGridWidth
	 * @param conditionError
	 * @param roundoffError
	 * @param x
	 * @param finiteDifference
	 * @param function
	 */
	public MathurApproximatelyOptimal(
			final Double trialGridWidth, 
			final double conditionError,
			final double roundoffError, 
			final double x, 
			final FiniteDifference finiteDifference,
			final UnivariateFunction function)
	{
		this.trialGridWidth = trialGridWidth;
		this.conditionError = conditionError;
		this.roundoffError = roundoffError;
		this.x = x;
		this.finiteDifference = finiteDifference;
		this.function = function;
		
		functionValue = function.value(x);
		value = Math.max(MachineEpsilon.DOUBLE_VALUE, Math.abs(functionValue));

	}

	/**
	 * Get the bandwidth.
	 * 
	 * @return The value.
	 */
	public double value()
	{		
		// get error estimates.
		double fe = getSimplifiedConditionErrorCoefficient();
		double fd = getSimplifiedRoundoffErrorCoefficient();

		// and truncation error.
		double cn = getEstimatedTruncationError();
		
		if(cn == 0)
		{
			// well, it would appear that the value of the derivative doesn't
			// depend on the step size. This of course makes perfect sense in
			// some cases (e.g. the first derivative of a linear function), but
			// more importantly means we don't need to do anything complicated
			// to compute a step size.
			
			double h = RuleOfThumb.getPowerOfTwoRuleOfThumbBandwidth(x, finiteDifference);
			
			return h;
		}
		
		double d = finiteDifference.getDerivativeOrder();
		double n = finiteDifference.getErrorOrder();

		double power = 1d / (n + d);
		double arg = (d / n) * (1d / cn) * (conditionError * fe + roundoffError * fd);
		double hopt = Math.pow(arg, power);
		
		// finally, round up to nearest power of two!
		hopt = PowerOfTwo.nextLargestPowerOfTwo(hopt);
		
		return hopt;
	}

	/**
	 * Get the simplified condition error.
	 * 
	 * @return The simplified condition error.
	 */
	private double getSimplifiedConditionErrorCoefficient()
	{
		double[] coefficients = finiteDifference.getCoefficients();
		
		double sum = 0 ;
		for(double c : coefficients)
		{
			sum += Math.abs(c);
		}
		
		double conditionError = sum * value;
		
		return conditionError;
	}
	
	/**
	 * Get the simplified roundoff error.
	 * 
	 * @return The simplified round error.
	 */
	private double getSimplifiedRoundoffErrorCoefficient()
	{
		double conditionError = getSimplifiedConditionErrorCoefficient();
		double roundoffError = conditionError / 2;
		
		return roundoffError;		
	}
	
	/**
	 * Get the estimated truncation error.
	 * 
	 * @return The estimated truncation error.
	 */
	private double getEstimatedTruncationError()
	{
		double h2 = getTrialGridWidth();
		
		// ensure that h1 > h2.
		double h1 = h2 * 2;
		
		double fd1 = getDerivative(h1);
		double fd2 = getDerivative(h2);
		
		double diff = fd2 - fd1;
		double n = finiteDifference.getErrorOrder();
		double cn = diff / (Math.pow(h1, n) - Math.pow(h2, n));
		
		cn = Math.abs(cn);
		
		return cn;		
	}
	
	/**
	 * Get the derivative with the specified bandwidth.
	 * 
	 * @param bandwidth The bandwidth.
	 * @return The derivative value.
	 */
	private double getDerivative(final double bandwidth)
	{
		FixedUnivariateBandwidth bandwidthFunction = new FixedUnivariateBandwidth(bandwidth);
		UnivariateFiniteDifferenceDerivativeFunction derivativeFunction = new UnivariateFiniteDifferenceDerivativeFunction(function, bandwidthFunction, finiteDifference);
		double derivative = derivativeFunction.value(x);
		
		return derivative;		
	}
	
	

	/**
	 * Get the trial grid width, used to estimate the truncation error.
	 * 
	 * @return The trial grid width.
	 */
	private double getTrialGridWidth()
	{
		if(trialGridWidth != null)
		{
			// use user-supplied grid width.
			return trialGridWidth.doubleValue();
		}
		
		// use rule-of-thumb...
		double h = RuleOfThumb.getPowerOfTwoRuleOfThumbBandwidth(x, finiteDifference);		
		
		return h;
	}
		
	
	

}
