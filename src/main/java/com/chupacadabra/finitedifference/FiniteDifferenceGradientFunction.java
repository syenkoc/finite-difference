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

import com.chupacadabra.finitedifference.bandwidth.UnivariateBandwidth;


/**
 * Finite difference gradient function with pluggable bandwidth.
 */
public class FiniteDifferenceGradientFunction
	implements GradientFunction
{

	/**
	 * The function.
	 */
	private final MultivariateFunction function;
	
	/**
	 * The bandwidth functions.
	 */
	private final UnivariateBandwidth[] bandwidthFunctions;

	/**
	 * The finite difference.
	 */
	private final FiniteDifference[] finiteDifferences;
		
	/**
	 * Constructor.
	 * 
	 * @param function The function.
	 * @param bandwidthFunctions The bandwidth functions.
	 * @param finiteDifferences The finite differences.
	 */
	public FiniteDifferenceGradientFunction(
			final MultivariateFunction function,
			final UnivariateBandwidth[] bandwidthFunctions,
			final FiniteDifference[] finiteDifferences)
	{
		this.function = function;		
		this.bandwidthFunctions = bandwidthFunctions;
		this.finiteDifferences = finiteDifferences;
		
		for(FiniteDifference finiteDifference : finiteDifferences)
		{
			if(finiteDifference.getDerivativeOrder() != 1)
			{
				throw new IllegalArgumentException(finiteDifference.toString());
			}			
		}
	}

	/**
	 * @see com.chupacadabra.finitedifference.GradientFunction#value(double[])
	 */
	public double[] value(final double... x)
	{
		double[] gradient = new double[x.length];
		for(int index = 0; index < gradient.length; index++)
		{
			UnivariateFunction partial = new PartiallyEvaluatedMultivariateFunction(function, x, index);
			UnivariateFunction derivative = new UnivariateFiniteDifferenceDerivativeFunction(partial, bandwidthFunctions[index], finiteDifferences[index]);
					
			gradient[index] = derivative.value(x[index]);
		}
		
		return gradient;
	}
			
	/**
	 * Partially evaluated function.
	 */
	private static final class PartiallyEvaluatedMultivariateFunction
		implements UnivariateFunction
	{
		
		/**
		 * The function.
		 */
		private final MultivariateFunction function;
		
		/**
		 * The point.
		 */
		private final double[] x;
		
		/**
		 * The index.
		 */
		private final int index;

		/**
		 * Constructor.
		 * 
		 * @param function The function.
		 * @param x The vector.
		 * @param index The index.
		 */
		PartiallyEvaluatedMultivariateFunction(
				final MultivariateFunction function, 
				final double[] x, 
				final int index)
		{
			this.function = function;
			this.x = x;
			this.index = index;
		}

		/**
		 * @see com.chupacadabra.finitedifference.UnivariateFunction#value(double)
		 */
		public double value(final double at)
		{
			double[] input = x.clone();
			input[index] = at;
			
			double value = function.value(input);
			
			return value;
		}
				
	}

}
