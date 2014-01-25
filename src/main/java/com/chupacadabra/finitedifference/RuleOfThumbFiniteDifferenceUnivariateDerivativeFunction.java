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
 * Uses {@linkplain RuleOfThumb rule-of-thumb} grid widths.
 */
public class RuleOfThumbFiniteDifferenceUnivariateDerivativeFunction
	extends AbstractFiniteDifferenceUnivariateDerivativeFunction
{

	/**
	 * Should we use a power-of-two grid width.
	 */
	private final boolean usePowerOfTwo;
	
	/**
	 * Constructor.
	 * <p>
	 * We default to using a power-of-two grid width.
	 * 
	 * @param function The function.
	 * @param finiteDifference The finite difference.
	 */
	public RuleOfThumbFiniteDifferenceUnivariateDerivativeFunction(
			final UnivariateFunction function, 
			final FiniteDifference finiteDifference)
	{
		this(function, finiteDifference, true);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param function The function.
	 * @param finiteDifference The finite difference.
	 * @param usePowerOfTwo Should we use a power-of-two grid width.
	 */
	public RuleOfThumbFiniteDifferenceUnivariateDerivativeFunction(
			final UnivariateFunction function, 
			final FiniteDifference finiteDifference,
			final boolean usePowerOfTwo)
	{
		super(function, finiteDifference);

		this.usePowerOfTwo = usePowerOfTwo;
	}


	/**
	 * @see com.chupacadabra.finitedifference.AbstractFiniteDifferenceUnivariateDerivativeFunction#getGridWidth(double)
	 */
	@Override
	protected double getGridWidth(final double x)
	{
		double gridWidth = usePowerOfTwo ? 
				RuleOfThumb.getPowerOfTwoRuleOfThumbGridWidth(x, finiteDifference) :
				RuleOfThumb.getRuleOfThumbGridWidth(x, finiteDifference);
				
		return gridWidth;
	}

}
