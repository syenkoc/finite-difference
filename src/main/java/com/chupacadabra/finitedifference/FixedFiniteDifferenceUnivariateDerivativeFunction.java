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
 * Fixed width finite difference univariate real derivative function.
 */
public class FixedFiniteDifferenceUnivariateDerivativeFunction
	extends AbstractFiniteDifferenceUnivariateDerivativeFunction
{
	
	/**
	 * The grid width.
	 */
	private final double gridWidth;
	
	/**
	 * Constructor.
	 * 
	 * @param function The function.
	 * @param finiteDifference Finite grid width.
	 * @param gridWidth The grid width.
	 */
	public FixedFiniteDifferenceUnivariateDerivativeFunction(
			final UnivariateFunction function, 
			final FiniteDifference finiteDifference,
			final double gridWidth)
	{
		super(function, finiteDifference);
		
		this.gridWidth = gridWidth;
	}

	/**
	 * @see com.chupacadabra.finitedifference.AbstractFiniteDifferenceUnivariateDerivativeFunction#getGridWidth(double)
	 */
	@Override
	protected double getGridWidth(final double x)
	{		
		return gridWidth;
	}

}
