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
 * Fixed-width multivariate finite difference function.
 */
public class FixedFiniteDifferenceMultivariateRealDerivativeFunction
	extends AbstractFiniteDifferenceMultivariateRealDerivativeFunction
{
	
	/**
	 * The grid widths.
	 */
	private final double[] gridWidths;

	/**
	 * Constructor.
	 * 
	 * @param function The function.
	 * @param finiteDifferences The finite differences.
	 * @param gridWidths The grid widths.
	 */
	public FixedFiniteDifferenceMultivariateRealDerivativeFunction(
			final MultivariateRealFunction function,
			final FiniteDifference[] finiteDifferences,
			final double[] gridWidths)
	{
		super(function, finiteDifferences);
		
		this.gridWidths = gridWidths;
	}

	/**
	 * @see org.teneighty.finitedifference.AbstractFiniteDifferenceMultivariateRealDerivativeFunction#getGridWidths(double[])
	 */
	@Override
	protected double[] getGridWidths(final double[] x)
	{
		return gridWidths;
	}
	
}
