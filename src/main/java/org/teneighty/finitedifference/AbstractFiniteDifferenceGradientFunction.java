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
 * Base class for gradient functions.
 */
public abstract class AbstractFiniteDifferenceGradientFunction
	implements GradientFunction
{

	/**
	 * The function.
	 */
	protected final MultivariateRealFunction function;
	
	/**
	 * The finite difference.
	 */
	protected final FiniteDifference finiteDifference;	
	
	/**
	 * Constructor.
	 * 
	 * @param function The function.
	 * @param finiteDifferenceType The finite difference type.
	 * @param errorOrder The error order.
	 */
	protected AbstractFiniteDifferenceGradientFunction(
			final MultivariateRealFunction function,
			final FiniteDifferenceType finiteDifferenceType,
			final int errorOrder)
	{
		this.function = function;		
		
		// create the finite difference.
		finiteDifference = new FiniteDifference(finiteDifferenceType, 1, errorOrder);
	}

	/**
	 * @see org.teneighty.finitedifference.GradientFunction#value(double[])
	 */
	public double[] value(final double... x)
	{
		double[] gradient = new double[x.length];
		for(int index = 0; index < gradient.length; index++)
		{
			UnivariateRealFunction partial = new PartiallyEvaluatedMultivariateRealFunction(function, x, index);
			UnivariateRealFunction derivative = getDerivative(partial);
			
			gradient[index] = derivative.value(x[index]);
		}
		
		return gradient;
	}
	
	/**
	 * Get the derivative of the specified partial function.
	 * 
	 * @param partial The partial function in question.
	 * @return The derivative.
	 */
	protected abstract UnivariateRealFunction getDerivative(UnivariateRealFunction partial);

}
