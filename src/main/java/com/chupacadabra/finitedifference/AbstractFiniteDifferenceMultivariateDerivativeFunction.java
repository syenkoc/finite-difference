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
 * Base class for multivariate real finite difference derivative functions.
 * <p>
 * We construct a multivariate derivative structure by taking the tensor product
 * of univariate coefficients.
 * <p>
 * Let <code>F(x<sub>1</sub>, ... , x<sub>n</sub>) : <b>R<sup>n</sup></b> &rarr; <b>R</b></code>.
 * A straight-forward extension of univariate finite differences yields:
 * <pre>
 * &part;<sup>m<sub>1</sub></sup> ... &part;<sup>m<sub>n</sub></sup>
 * ------------------------ F(x<sub>1</sub>, ... , x<sub>n</sub>) = &Sigma;<sub>i<sub>1</sub></sub> ... &Sigma;<sub>i<sub>n</sub></sub> C(i<sub>1</sub>, ... ,  i<sub>n</sub>)(m<sub>1</sub>, ... , m<sub>n</sub>) F(x<sub>1</sub> + i<sub>1</sub>h<sub>1</sub>, ... , x<sub>n</sub> + i<sub>n</sub>h<sub>n</sub>) 
 * h<sub>1</sub><sup>m<sub>1</sub></sup> &part;x<sub>1</sub><sup>m<sub>1</sub></sup> ... h<sub>1</sub><sup>m<sub>1</sub></sup> &part;x<sub>n</sub><sup>m<sub>n</sub></sup>
 * </pre>
 * where <code>C(i<sub>1</sub>, ... ,  i<sub>n</sub>)(m<sub>1</sub>, ... , m<sub>n</sub>)</code>
 * is the tensor product of univariate finite difference coefficients.
 */
public abstract class AbstractFiniteDifferenceMultivariateDerivativeFunction
	implements MultivariateFunction
{

	/**
	 * The function.
	 */
	protected final MultivariateFunction function;

	/**
	 * The finite difference 
	 */
	protected final FiniteDifference[] finiteDifferences;
	
	/**
	 * The coefficients.
	 */
	protected final double[][] coefficients;

	/**
	 * The tensor product of the coefficients.
	 */
	protected final double[] coefficientTensor;

	/**
	 * Constructor.
	 * 
	 * @param function The function.
	 * @param finiteDifferences The finite difference descriptors.
	 */
	protected AbstractFiniteDifferenceMultivariateDerivativeFunction(
			final MultivariateFunction function,
			final FiniteDifference[] finiteDifferences)
	{
		// store the magic.
		this.function = function;
		this.finiteDifferences = finiteDifferences;
		
		// get all coefficients.
		this.coefficients = new double[finiteDifferences.length][];
		for(int index = 0; index < coefficients.length; index++)
		{
			coefficients[index] = finiteDifferences[index].getCoefficients();
		}
		
		
		this.coefficientTensor = createCoefficientTensor();
	}

	/**
	 * Create the coefficient tensor.
	 * 
	 * @return The tensor.
	 */
	private double[] createCoefficientTensor()
	{
		int size = 1;
		for(int index = 0; index < finiteDifferences.length; index++)
		{
			size *= finiteDifferences[index].getLength();
		}

		// store the tensor in row-major order.
		double[] tensor = new double[size];
		int[] index = new int[finiteDifferences.length];
		
		for(int tensorIndex = 0; tensorIndex < size; tensorIndex++)
		{
			double tensorValue = 1d;
			for(int indexIndex = 0; indexIndex < index.length; indexIndex++)
			{
				// compute tensor product.
				tensorValue *= coefficients[indexIndex][index[indexIndex]]; 
			}
			
			tensor[tensorIndex] = tensorValue;
			
			if((tensorIndex - 1) < size)
			{
				// advance the index
				int incrementIndex = 0;
				while(true)
				{
					index[incrementIndex] += 1;
					
					if(index[incrementIndex] == finiteDifferences[incrementIndex].getLength())
					{
						index[incrementIndex] = 0;
						incrementIndex += 1;
					}
					else
					{
						break;
					}
				}				
			}
		}
		
		return tensor;
	}
	
	/**
	 * Get the grid widths for the specified point.
	 * 
	 * @param x The point in question.
	 * @return The grid widths.
	 */
	protected abstract double[] getGridWidths(final double[] x);

	/**
	 * @see com.chupacadabra.finitedifference.MultivariateFunction#value(double[])
	 */
	public double value(final double... x)
	{
		double[] gridWidths = getGridWidths(x);
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
	protected double getDerivative(final double[] x, final double[] widths)
	{
		double[] valueTensor = getValueTensor(x, widths);
		
		// compute inner product.
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
		double[] valueTensor = new double[coefficientTensor.length];
		
		// compute base point.
		double[] base = new double[x.length];
		for(int index = 0; index < base.length; index++)
		{
			base[index] = x[index] + (gridWidths[index] * finiteDifferences[index].getLeftMultiplier());			
		}
				
		double[] input = base.clone();
		int[] index = new int[finiteDifferences.length];
		for(int tensorIndex = 0; tensorIndex < coefficientTensor.length; tensorIndex++)
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
