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
 * A multivariate function evaluated curried to 
 */
public class PartiallyEvaluatedMultivariateRealFunction
	implements UnivariateRealFunction
{

	/**
	 * The function.
	 */
	private final MultivariateRealFunction multivariateRealFunction;
	
	/**
	 * The point.
	 */
	private final double[] point;
	
	/**
	 * The index.
	 */
	private final int index;
			
	/**
	 * Constructor.
	 * 
	 * @param multivariateRealFunction The function.
	 * @param point The point.
	 * @param index The index.
	 */
	public PartiallyEvaluatedMultivariateRealFunction(
			final MultivariateRealFunction multivariateRealFunction, 
			final double[] point,
			final int index)
	{
		this.multivariateRealFunction = multivariateRealFunction;
		this.point = point;
		this.index = index;
	}

	/**
	 * @see org.teneighty.finitedifference.UnivariateRealFunction#value(double)
	 */
	public double value(final double x)
	{
		double[] overriden = point.clone();
		overriden[index] = x;
		
		double value = multivariateRealFunction.value(overriden);
		
		return value;
	}

}
