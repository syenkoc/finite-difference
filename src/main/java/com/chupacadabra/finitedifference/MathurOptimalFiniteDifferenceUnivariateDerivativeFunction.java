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
package com.chupacadabra.finitedifference;

import com.chupacadabra.finitedifference.util.MachineEpsilon;
import com.chupacadabra.finitedifference.util.PowerOfTwo;

/**
 * This class uses the ideas from Mathur's thesis
 * "An Analytical Approach to Computing Step Sizes for Finite-Difference Derivatives"
 * to estimate an optimal grid width.
 * <p>
 * Note that this class does <i>not</i> implement the full AutoDX algorithm
 * described in the paper.
 */
public class MathurOptimalFiniteDifferenceUnivariateDerivativeFunction
	extends AbstractFiniteDifferenceUnivariateDerivativeFunction
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
	 * Constructor.
	 * <p>
	 * This constructor will use an automatic trial grid width, and the
	 * {@linkplain MachineEpsilon#DOUBLE_VALUE machine epsilon} for the
	 * condition and roundoff errors.
	 * 
	 * @param function The function.
	 * @param finiteDifference The finite difference.
	 */
	public MathurOptimalFiniteDifferenceUnivariateDerivativeFunction(
			final UnivariateFunction function,
			final FiniteDifference finiteDifference)
	{
		this(function, finiteDifference, null, MachineEpsilon.DOUBLE_VALUE,
				MachineEpsilon.DOUBLE_VALUE);
	}

	/**
	 * Constructor.
	 * <p>
	 * This constructor will use the {@linkplain MachineEpsilon#DOUBLE_VALUE
	 * machine epsilon} as the condition and roundoff errors
	 * 
	 * @param function The function.
	 * @param finiteDifference The finite difference.
	 * @param trialGridWidth The trial grid width used to estimate the
	 *            truncation error.
	 */
	public MathurOptimalFiniteDifferenceUnivariateDerivativeFunction(
			final UnivariateFunction function,
			final FiniteDifference finiteDifference, 
			final Double trialGridWidth)
	{
		this(function, finiteDifference, trialGridWidth,
				MachineEpsilon.DOUBLE_VALUE, MachineEpsilon.DOUBLE_VALUE);
	}

	/**
	 * Constructor.
	 * 
	 * @param function The function.
	 * @param finiteDifference The finite difference.
	 * @param trialGridWidth The trial grid width used to estimate the
	 *            truncation error; or <code>null</code> to use an automatic
	 *            bandwidth.
	 * @param conditionError The condition error.
	 * @param roundoffError The roundoff error.
	 */
	public MathurOptimalFiniteDifferenceUnivariateDerivativeFunction(
			final UnivariateFunction function,
			final FiniteDifference finiteDifference,
			final Double trialGridWidth,
			final double conditionError,
			final double roundoffError)
	{
		super(function, finiteDifference);

		this.trialGridWidth = trialGridWidth;
		this.conditionError = conditionError;
		this.roundoffError = roundoffError;
	}

	/**
	 * @see com.chupacadabra.finitedifference.AbstractFiniteDifferenceUnivariateDerivativeFunction#getGridWidth(double)
	 */
	@Override
	protected double getGridWidth(final double x)
	{
		double functionValue = function.value(x);
		double value = Math.max(MachineEpsilon.DOUBLE_VALUE, Math.abs(functionValue));
		
		// get error estimates.
		double fe = getSimplifiedConditionErrorCoefficient(value);
		double fd = getSimplifiedRoundoffErrorCoefficient(value);

		// and truncation error.
		double cn = getEstimatedTruncationError(x);
		
		if(cn == 0)
		{
			// well, it would appear that the value of the derivative doesn't
			// depend on the step size. This of course makes perfect sense in
			// some cases (e.g. the first derivative of a linear function), but
			// more importantly means we don't need to do anything complicated
			// to compute a step size.
			
			double h = RuleOfThumb.getPowerOfTwoRuleOfThumbGridWidth(x, finiteDifference);
			
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
	 * @param value The function value.
	 * @return The simplified condition error.
	 */
	private double getSimplifiedConditionErrorCoefficient(final double value)
	{
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
	 * @param value The value.
	 * @return The simplified round error.
	 */
	private double getSimplifiedRoundoffErrorCoefficient(final double value)
	{
		double conditionError = getSimplifiedConditionErrorCoefficient(value);
		double roundoffError = conditionError / 2;
		
		return roundoffError;		
	}
	
	/**
	 * Get the estimated truncation error at the specified point.
	 * 
	 * @param x The point.
	 * @return The estimated truncation error.
	 */
	private double getEstimatedTruncationError(final double x)
	{
		double h2 = getTrialGridWidth(x);
		
		// ensure that h1 > h2.
		double h1 = h2 * 2;
		
		double fd1 = getDerivative(x, h1);
		double fd2 = getDerivative(x, h2);
		
		double diff = fd2 - fd1;
		double n = finiteDifference.getErrorOrder();
		double cn = diff / (Math.pow(h1, n) - Math.pow(h2, n));
		
		cn = Math.abs(cn);
		
		return cn;		
	}

	/**
	 * Get the trial grid width, used to estimate the truncation error.
	 * 
	 * @param x The point.
	 * @return The trial grid width.
	 */
	private double getTrialGridWidth(final double x)
	{
		if(trialGridWidth != null)
		{
			// use user-supplied grid width.
			return trialGridWidth.doubleValue();
		}
		
		// use rule-of-thumb...
		double h = RuleOfThumb.getPowerOfTwoRuleOfThumbGridWidth(x, finiteDifference);		
		
		return h;
	}

}
