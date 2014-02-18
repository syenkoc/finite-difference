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

import junit.framework.Assert;

import org.junit.Test;

import com.chupacadabra.finitedifference.FiniteDifference;
import com.chupacadabra.finitedifference.FiniteDifferenceType;
import com.chupacadabra.finitedifference.UnivariateFunction;
import com.chupacadabra.finitedifference.bandwidth.UnivariateBandwidth;
import com.chupacadabra.finitedifference.function.Cosine;
import com.chupacadabra.finitedifference.function.Exp;
import com.chupacadabra.finitedifference.function.Negate;
import com.chupacadabra.finitedifference.function.Sine;

/**
 * Base class for univariate real finite difference tests.
 */
public abstract class AbstractUnivariateFiniteDifferenceFunctionTest
{

	/**
	 * First-order sine.
	 */
	@Test
	public void firstOrderSin()
	{
		Sine sin = new Sine();
		Cosine cos = new Cosine();
		
		// since sin is a bounded, periodic function, we use absolute error.
		testCore(sin, FiniteDifference.FIVE_POINT_CENTRAL, cos, 0, 4 * Math.PI, 10000, null, 1e-10);
	}
	
	/**
	 * First order exponential.
	 */
	@Test
	public void firstOrderExp()
	{
		Exp exp = new Exp();
		testCore(exp, FiniteDifference.FIVE_POINT_CENTRAL, exp, 0, 100000, 1000);		
	}
	
	/**
	 * Second order exponential.
	 */
	@Test
	public void secondOrderExp()
	{
		Exp exp = new Exp();
		FiniteDifference finiteDifference = new FiniteDifference(FiniteDifferenceType.CENTRAL, 2, 4);		
		testCore(exp, finiteDifference, exp, 0, 100000, 1000);		
	}
	
	/**
	 * Second-order sine.
	 */
	@Test
	public void secondOrderSin()
	{
		Sine sin = new Sine();
		UnivariateFunction derivative = new Negate(new Sine());
		
		FiniteDifference finiteDifference = new FiniteDifference(FiniteDifferenceType.CENTRAL, 2, 4);
		testCore(sin, finiteDifference, derivative, 0, 4 * Math.PI, 10000, null, 1e-3);
	}
	
	/**
	 * Get a derivative for the specified function using the specified finite
	 * difference stencil.
	 * 
	 * @return The derivative function.
	 */
	protected abstract UnivariateBandwidth getBandwidth();

	/**
	 * Get the default relative error.
	 * 
	 * @return Default relative error.
	 */
	protected Double getDefaultRelativeError()
	{
		return 1e-7;
	}

	/**
	 * Get the default absolute error.
	 * 
	 * @return Default absolute error.
	 */
	protected Double getDefaultAbsoluteError()
	{
		return null;
	}

	/**
	 * Test core method, using default error conditions.
	 * 
	 * @param function The function.
	 * @param finiteDifference The finite difference.
	 * @param derivative Analytical derivative.
	 * @param lowerBound The lower bound.
	 * @param upperBound Upper bound.
	 * @param steps The number of steps.
	 */
	protected void testCore(final UnivariateFunction function,
			final FiniteDifference finiteDifference,
			final UnivariateFunction derivative, 
			final double lowerBound,
			final double upperBound, 
			final int steps)
	{
		testCore(function, finiteDifference, derivative, lowerBound, upperBound, steps, 
				getDefaultRelativeError(),
				getDefaultAbsoluteError());
	}

	/**
	 * Test core method.
	 * 
	 * @param function The function.
	 * @param finiteDifference The finite difference.
	 * @param derivative Analytical derivative.
	 * @param lowerBound The lower bound.
	 * @param upperBound Upper bound.
	 * @param steps The number of steps.
	 * @param relativeErrorThreshold The relative error limit, or
	 *            <code>null</code> for no limit.
	 * @param absoluteErrorThreshold The absolute error limit, or
	 *            <code>null</code> for no limit.
	 */
	protected void testCore(final UnivariateFunction function,
			final FiniteDifference finiteDifference,
			final UnivariateFunction derivative, 
			final double lowerBound,
			final double upperBound, 
			final int steps,
			final Double relativeErrorThreshold,
			final Double absoluteErrorThreshold)
	{
		UnivariateBandwidth bandwidthFunction = getBandwidth();
		UnivariateFunction finiteDifferenceDerivative = new UnivariateFiniteDifferenceDerivativeFunction(function, bandwidthFunction, finiteDifference);

		double step = (upperBound - lowerBound) / steps;
		for(double value = lowerBound; value <= upperBound; value += step)
		{
			double finiteValue = finiteDifferenceDerivative.value(value);
			double analyticalValue = derivative.value(value);

			if(relativeErrorThreshold != null)
			{
				double relativeError = getRelativeError(finiteValue, analyticalValue);

				if(relativeError > relativeErrorThreshold.doubleValue())
				{
					finiteValue = finiteDifferenceDerivative.value(value);
					
					String message = String.format("Relative error of %1$s (exceeded %2$s): %3$s / %4$s at %5$s",
									relativeError, 
									relativeErrorThreshold,
									finiteValue, 
									analyticalValue, 
									value);
					
					Assert.fail(message);
				}
			}
			
			if(absoluteErrorThreshold != null)
			{
				double absoluteError = getAbsoluteError(finiteValue, analyticalValue);
				
				if(absoluteError > absoluteErrorThreshold.doubleValue())
				{
					finiteValue = finiteDifferenceDerivative.value(value);
					
					String message = String.format("Absolute error of %1$s (exceeded %2$s): %3$s / %4$s at %5$s",
							absoluteError, 
							absoluteErrorThreshold,
							finiteValue, 
							analyticalValue, 
							value);
					
					Assert.fail(message);
				}
			}
		}
	}

	/**
	 * Get the absolute error between <code>x</code> and <code>y</code>.
	 * 
	 * @param x <code>x</code>
	 * @param y <code>y</code>
	 * @return Absolute error.
	 */
	private static double getAbsoluteError(final double x, final double y)
	{
		return Math.abs(x - y);
	}

	/**
	 * Get the relative error between <code>x</code> and <code>y</code>.
	 * 
	 * @param x <code>x</code>
	 * @param y <code>y</code>
	 * @return The relative error.
	 */
	private static double getRelativeError(final double x, final double y)
	{
		return Math.abs(x - y) / Math.max(Math.abs(x), Math.abs(y));
	}

}
