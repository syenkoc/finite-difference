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

import com.chupacadabra.finitedifference.util.DotProduct;


/**
 * Base class for univariate real finite difference function.
 */
public abstract class AbstractFiniteDifferenceUnivariateDerivativeFunction
	implements UnivariateFunction
{

	/**
	 * The function.
	 */
	protected final UnivariateFunction function;
	
	/**
	 * The finite difference.
	 */
	protected final FiniteDifference finiteDifference;
	
	/**
	 * The coefficients.
	 */
	protected final double[] coefficients;
	
	/**
	 * Constructor.
	 * 
	 * @param function The function. 
	 * @param finiteDifference The finite difference.
	 */
	protected AbstractFiniteDifferenceUnivariateDerivativeFunction(
			final UnivariateFunction function, 
			final FiniteDifference finiteDifference)
	{
		this.function = function;
		this.finiteDifference = finiteDifference;
		
		// grab the coefficients.
		coefficients = finiteDifference.getCoefficients();
	}
	
	/**
	 * @see com.chupacadabra.finitedifference.UnivariateFunction#value(double)
	 */
	public double value(final double x)
	{
		double gridWidth = getGridWidth(x);
		double derivative = getDerivative(x, gridWidth);
		
		return derivative;
	}
	
	/**
	 * Get the grid width at the specified point.
	 * 
	 * @param x The point.
	 * @return The width.
	 */
	protected abstract double getGridWidth(double x);
	
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
		
		double dotProduct = DotProduct.of(valueGrid, coefficients);
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
