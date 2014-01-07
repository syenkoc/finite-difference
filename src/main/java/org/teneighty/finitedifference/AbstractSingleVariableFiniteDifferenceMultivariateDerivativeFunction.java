/*  
 * $Id$  
 *   
 * Copyright (c) 2012-2013 Fran Lattanzio  
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
package org.teneighty.finitedifference;


/**
 * Base class for single-index multivariate derivative function.
 */
public abstract class AbstractSingleVariableFiniteDifferenceMultivariateDerivativeFunction
	implements MultivariateRealFunction
{
	
	/**
	 * The function.
	 */
	protected final MultivariateRealFunction function;
	
	/**
	 * The index.
	 */
	protected final int index;
	
	/**
	 * The finite difference.
	 */
	protected final FiniteDifference finiteDifference;

	/**
	 * Constructor.
	 * 
	 * @param function The function.
	 * @param index The index.
	 * @param finiteDifference The finite difference.
	 */
	protected AbstractSingleVariableFiniteDifferenceMultivariateDerivativeFunction(
			final MultivariateRealFunction function, 
			final int index,
			final FiniteDifference finiteDifference)
	{
		this.function = function;
		this.index = index;
		this.finiteDifference = finiteDifference;
	}

	/**
	 * @see org.teneighty.finitedifference.MultivariateRealFunction#value(double[])
	 */
	public double value(final double... x)
	{
		PartiallyEvaluatedMultivariateRealFunction partial = new PartiallyEvaluatedMultivariateRealFunction(function, x, index);
		UnivariateRealFunction derivative = getDerivative(partial);
		
		double xi = x[index];
		double value = derivative.value(xi);
		
		return value;
	}

	/**
	 * Get the derivative.
	 * 
	 * @param partial The partial function. 
	 * @return The derivative function.
	 */
	protected abstract UnivariateRealFunction getDerivative(PartiallyEvaluatedMultivariateRealFunction partial);	

}
