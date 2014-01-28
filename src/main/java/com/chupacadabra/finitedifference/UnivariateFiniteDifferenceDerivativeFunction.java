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
import com.chupacadabra.finitedifference.util.DotProduct;


/**
 * Univariate finite difference derivative function.
 */
public class UnivariateFiniteDifferenceDerivativeFunction
	implements UnivariateFunction
{

	/**
	 * The function.
	 */
	private final UnivariateFunction function;
	
	/**
	 * The bandwidth function.
	 */
	private final UnivariateBandwidth bandwidthFunction;
	
	/**
	 * The finite difference.
	 */
	private final FiniteDifference finiteDifference;
			
	/**
	 * Constructor.
	 * 
	 * @param function The function.
	 * @param bandwidthFunction The bandwidth function.
	 * @param finiteDifference The finite difference.
	 */
	public UnivariateFiniteDifferenceDerivativeFunction(
			final UnivariateFunction function,
			final UnivariateBandwidth bandwidthFunction,
			final FiniteDifference finiteDifference)
	{
		this.function = function;
		this.bandwidthFunction = bandwidthFunction;
		this.finiteDifference = finiteDifference;
	}

	/**
	 * @see com.chupacadabra.finitedifference.UnivariateFunction#value(double)
	 */
	public double value(final double x)
	{
		double gridWidth = bandwidthFunction.value(x, finiteDifference, function);
		double derivative = getDerivative(x, gridWidth);
		
		return derivative;
	}
		
	/**
	 * Compute the derivative at the specified point, using the specified grid width. 
	 * 
	 * @param x The point. 
	 * @param gridWidth The grid width.
	 * @return The derivative.
	 */
	protected double getDerivative(final double x, final double gridWidth)
	{
		double[] valueGrid = getValueGrid(x, gridWidth);
		
		double dotProduct = DotProduct.of(valueGrid, finiteDifference.getCoefficients());
		double derivative = dotProduct / Math.pow(gridWidth, finiteDifference.getDerivativeOrder());
		
		return derivative;		
	}

	/**
	 * Get a grid of function values given the specified stencil, centered at
	 * the specified value.
	 * 
	 * @param x The value.
	 * @param gridWidth The grid width.
	 * @return Vector of function values.
	 */
	private double[] getValueGrid(final double x, final double gridWidth)
	{
		double[] values = new double[finiteDifference.getLength()];
				
		for(int index = 0, multiplier = finiteDifference.getLeftMultiplier(); index < values.length; index += 1, multiplier += 1)
		{
			// don't keep a "running" input value - it can allow a non-trivial
			// amount of error to accumulate!
			double inputValue = x + (gridWidth * multiplier);
			values[index] = function.value(inputValue);
			
			inputValue += gridWidth;
		}
		
		return values;
	}

}
