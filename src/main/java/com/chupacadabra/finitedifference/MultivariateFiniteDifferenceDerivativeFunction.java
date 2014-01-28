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

import com.chupacadabra.finitedifference.bandwidth.MultivariateBandwidth;
import com.chupacadabra.finitedifference.util.DotProduct;

/**
 * Multivariate finite difference derivative function with a pluggable bandwidth
 * strategy.
 */
public class MultivariateFiniteDifferenceDerivativeFunction
	implements MultivariateFunction
{

	/**
	 * The function.
	 */
	private final MultivariateFunction function;

	/**
	 * The bandwidth function.
	 */
	private final MultivariateBandwidth bandwidthFunction;
	
	/**
	 * The finite difference 
	 */
	private final MultivariateFiniteDifference finiteDifference;
	
	/**
	 * The individual finite differences.
	 */
	private final FiniteDifference[] finiteDifferences;
	
	/**
	 * The (multivariate) tensor stencil in row-major order.
	 */
	private final double[] tensor;
	
	/**
	 * Constructor.
	 * 
	 * @param function The function.
	 * @param bandwidthFunction The bandwidth function.
	 * @param finiteDifference The finite difference.
	 */
	public MultivariateFiniteDifferenceDerivativeFunction(
			final MultivariateFunction function,
			final MultivariateBandwidth bandwidthFunction,
			final MultivariateFiniteDifference finiteDifference)
	{
		this.function = function;
		this.bandwidthFunction = bandwidthFunction;
		this.finiteDifference = finiteDifference;
		
		// grab/cache some stuff we need from the multivariate stencil
		// descriptor.
		finiteDifferences = finiteDifference.getFiniteDifferences();
		tensor = finiteDifference.getCoefficients();
	}


	/**
	 * @see com.chupacadabra.finitedifference.MultivariateFunction#value(double[])
	 */
	public double value(final double... x)
	{
		double[] gridWidths = bandwidthFunction.value(x, finiteDifference, function);
		double derivative = getDerivative(x, gridWidths);
		
		return derivative;
	}
	
	/**
	 * Get the derivative at the specified point, given the specified widths. 
	 * 
	 * @param x The point.
	 * @param widths The grid widths.
	 * @return The derivative.
	 */
	private double getDerivative(final double[] x, final double[] widths)
	{
		double[] valueTensor = getValueTensor(x, widths);
		
		// compute inner product.
		double[] coefficientTensor = finiteDifference.getCoefficients();
		double innerProduct = DotProduct.of(coefficientTensor, valueTensor);
		double derivative = innerProduct;
		
		for(int index = 0; index < finiteDifferences.length; index++)
		{
			derivative /= Math.pow(widths[index], finiteDifferences[index].getDerivativeOrder());
		}
		
		return derivative;		
	}

	/**
	 * Get a tensor, in row-major order, of function values. 
	 *  
	 * @param x The point.
	 * @param gridWidths The grid widths.
	 * @return Value tensor.
	 */
	private double[] getValueTensor(final double[] x, final double[] gridWidths)
	{
		double[] valueTensor = new double[tensor.length];
		
		FiniteDifference[] finiteDifferences = finiteDifference.getFiniteDifferences();
		
		// compute base point.
		double[] base = new double[x.length];
		for(int index = 0; index < base.length; index++)
		{
			base[index] = x[index] + (gridWidths[index] * finiteDifferences[index].getLeftMultiplier());			
		}
				
		double[] input = base.clone();
		int[] index = new int[finiteDifferences.length];
		for(int tensorIndex = 0; tensorIndex < tensor.length; tensorIndex++)
		{
			double value = function.value(input);
			valueTensor[tensorIndex] = value;
			
			if((tensorIndex - 1) < valueTensor.length)
			{
				int incrementIndex = 0;
				while(true)
				{
					index[incrementIndex] += 1;
					valueTensor[incrementIndex] += gridWidths[incrementIndex];
					
					if(index[incrementIndex] == finiteDifferences[incrementIndex].getLength())
					{
						index[incrementIndex] = 0;
						valueTensor[incrementIndex] = base[incrementIndex];
						incrementIndex += 1;
					}
					else
					{
						break;
					}
				}				
			}			
		}
		
		return valueTensor;
	}

}
