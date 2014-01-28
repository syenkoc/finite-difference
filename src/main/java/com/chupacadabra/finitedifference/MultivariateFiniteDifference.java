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


/**
 * A multivariate finite difference stencil descriptor.
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
 * <p>
 * This class computes the tensor product
 * <code>C(i<sub>1</sub>, ... ,  i<sub>n</sub>)(m<sub>1</sub>, ... , m<sub>n</sub>)</code>
 * and stores the results in row-major (multi-index/tenor) order.
 */
public class MultivariateFiniteDifference
{
	
	/**
	 * Finite difference descriptors.
	 */
	private final FiniteDifference[] finiteDifferences;
	
	/**
	 * Finite difference tensor in row-major order.
	 */
	private final double[] tensor;

	/**
	 * Constructor.
	 * 
	 * @param finiteDifferences Univariate differences.
	 */
	public MultivariateFiniteDifference(final FiniteDifference... finiteDifferences)
	{
		this.finiteDifferences = finiteDifferences;
		this.tensor = createCoefficientTensor();
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
				double[] coefficients = finiteDifferences[indexIndex].getCoefficients();
				tensorValue *= coefficients[index[indexIndex]]; 
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
	 * Get the finite differences.
	 * 
	 * @return The finite differences.
	 */
	public FiniteDifference[] getFiniteDifferences()
	{
		return finiteDifferences;
	}
	
	/**
	 * Get the finite difference coefficient tensor in row-major order.
	 * 
	 * @return The tensor.
	 */
	public double[] getCoefficients()
	{
		return tensor;
	}
	
}
