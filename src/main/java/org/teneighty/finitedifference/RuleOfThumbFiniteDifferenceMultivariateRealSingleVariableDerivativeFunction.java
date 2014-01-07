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
 * Single variable rule-of-thumb finite difference derivative function.
 */
public class RuleOfThumbFiniteDifferenceMultivariateRealSingleVariableDerivativeFunction
	extends AbstractSingleVariableFiniteDifferenceMultivariateDerivativeFunction
{
	
	/**
	 * Use of power-of-two grid width?
	 */
	private final boolean usePowerOfTwo;

	/**
	 * Constructor.
	 * 
	 * @param function The function.
	 * @param index The index.
 	 * @param finiteDifference The finite difference.
	 */
	public RuleOfThumbFiniteDifferenceMultivariateRealSingleVariableDerivativeFunction(
			final MultivariateRealFunction function, 
			final int index,
			final FiniteDifference finiteDifference)
	{
		this(function, index, finiteDifference, true);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param function
	 * @param index
	 * @param finiteDifference
	 * @param usePowerOfTwo
	 */
	public RuleOfThumbFiniteDifferenceMultivariateRealSingleVariableDerivativeFunction(
			final MultivariateRealFunction function, 
			final int index,
			final FiniteDifference finiteDifference, 
			final boolean usePowerOfTwo)
	{
		super(function, index, finiteDifference);
		
		this.usePowerOfTwo = usePowerOfTwo;
	}

	/**
	 * @see org.teneighty.finitedifference.AbstractSingleVariableFiniteDifferenceMultivariateDerivativeFunction#getDerivative(org.teneighty.finitedifference.PartiallyEvaluatedMultivariateRealFunction)
	 */
	@Override
	protected UnivariateRealFunction getDerivative(
			final PartiallyEvaluatedMultivariateRealFunction partial)
	{
		return new RuleOfThumbFiniteDifferenceUnivariateRealDerivativeFunction(partial, finiteDifference, usePowerOfTwo);
	}

}
