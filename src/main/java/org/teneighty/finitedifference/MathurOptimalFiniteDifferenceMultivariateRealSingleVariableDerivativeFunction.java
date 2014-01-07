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

import org.teneighty.finitedifference.util.MachineEpsilon;


/**
 * 
 */
public class MathurOptimalFiniteDifferenceMultivariateRealSingleVariableDerivativeFunction
	extends AbstractSingleVariableFiniteDifferenceMultivariateDerivativeFunction
{
	
	/**
	 * The trial grid width.
	 */
	private final Double trialGridWidth;

	/**
	 * The condition error.
	 */
	private final double conditionError;

	/**
	 * The roundoff error.
	 */
	private final double roundoffError;
	
	/**
	 * Constructor.
	 * <p>
	 * This constructor will use an automatic trial grid width, and the
	 * {@linkplain MachineEpsilon#DOUBLE_VALUE machine epsilon} for the
	 * condition and roundoff errors.
	 * 
	 * @param function The function.
	 * @param index The parameter index.
	 * @param finiteDifference The finite difference.
	 */
	public MathurOptimalFiniteDifferenceMultivariateRealSingleVariableDerivativeFunction(
			final MultivariateRealFunction function,
			final int index,
			final FiniteDifference finiteDifference)
	{
		this(function, index, finiteDifference, null, MachineEpsilon.DOUBLE_VALUE,
				MachineEpsilon.DOUBLE_VALUE);
	}

	/**
	 * Constructor.
	 * <p>
	 * This constructor will use the {@linkplain MachineEpsilon#DOUBLE_VALUE
	 * machine epsilon} as the condition and roundoff errors
	 * 
	 * @param function The function.
	 * @param index The parameter index.
	 * @param finiteDifference The finite difference.
	 * @param trialGridWidth The trial grid width used to estimate the
	 *            truncation error.
	 */
	public MathurOptimalFiniteDifferenceMultivariateRealSingleVariableDerivativeFunction(
			final MultivariateRealFunction function,
			final int index,
			final FiniteDifference finiteDifference, 
			final Double trialGridWidth)
	{
		this(function, index, finiteDifference, new Double(trialGridWidth),
				MachineEpsilon.DOUBLE_VALUE, MachineEpsilon.DOUBLE_VALUE);
	}

	/**
	 * Constructor.
	 * 
	 * @param function The function.
	 * @param index The parameter index.
	 * @param finiteDifference The finite difference.
	 * @param trialGridWidth The trial grid width.
	 * @param conditionError The function condition error.
	 * @param roundoffError The function roundoff error.
	 */
	public MathurOptimalFiniteDifferenceMultivariateRealSingleVariableDerivativeFunction(
			final MultivariateRealFunction function,
			final int index,
			final FiniteDifference finiteDifference,
			final Double trialGridWidth, 
			final double conditionError,
			final double roundoffError)
	{
		super(function, index, finiteDifference);

		this.trialGridWidth = trialGridWidth;
		this.conditionError = conditionError;
		this.roundoffError = roundoffError;
	}

	/**
	 * @see org.teneighty.finitedifference.AbstractSingleVariableFiniteDifferenceMultivariateDerivativeFunction#getDerivative(org.teneighty.finitedifference.PartiallyEvaluatedMultivariateRealFunction)
	 */
	@Override
	protected UnivariateRealFunction getDerivative(
			final PartiallyEvaluatedMultivariateRealFunction partial)
	{
		UnivariateRealFunction derivative = new MathurOptimalFiniteDifferenceUnivariateRealDerivativeFunction(partial, finiteDifference, trialGridWidth, conditionError, roundoffError);
		
		return derivative;
	}


}
