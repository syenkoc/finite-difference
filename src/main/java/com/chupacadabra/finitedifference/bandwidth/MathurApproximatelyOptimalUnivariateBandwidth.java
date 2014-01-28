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
package com.chupacadabra.finitedifference.bandwidth;

import com.chupacadabra.finitedifference.FiniteDifference;
import com.chupacadabra.finitedifference.UnivariateFunction;
import com.chupacadabra.finitedifference.util.MachineEpsilon;


/**
 * Mathur approximately optimal univariate bandwidth strategy.
 */
public class MathurApproximatelyOptimalUnivariateBandwidth
	implements UnivariateBandwidth
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
	 */
	public MathurApproximatelyOptimalUnivariateBandwidth()
	{
		this(null, MachineEpsilon.DOUBLE_VALUE, MachineEpsilon.DOUBLE_VALUE);
	}

	/**
	 * Constructor.
	 * 
	 * @param trialGridWidth The trial grid width.
	 */
	public MathurApproximatelyOptimalUnivariateBandwidth(final double trialGridWidth)
	{
		this(trialGridWidth, MachineEpsilon.DOUBLE_VALUE, MachineEpsilon.DOUBLE_VALUE);
	}
	
	/**
	 * Full constructor.
	 * 
	 * @param trialGridWidth The trial grid width.
	 * @param conditionError The function condition error.
	 * @param roundoffError The function round-off error.
	 */
	public MathurApproximatelyOptimalUnivariateBandwidth(
			final double trialGridWidth,
			final double conditionError, 
			final double roundoffError)
	{
		this(new Double(trialGridWidth), conditionError, roundoffError);
	}

	/**
	 * Core constructor.
	 * 
	 * @param trialGridWidth The trial grid width.
	 * @param conditionError The condition error.
	 * @param roundoffError The round-off error.
	 */
	private MathurApproximatelyOptimalUnivariateBandwidth(
			final Double trialGridWidth,
			final double conditionError, 
			final double roundoffError)
	{
		this.trialGridWidth = trialGridWidth;
		this.conditionError = conditionError;
		this.roundoffError = roundoffError;
	}

	/**
	 * @see com.chupacadabra.finitedifference.bandwidth.UnivariateBandwidth#value(double, com.chupacadabra.finitedifference.FiniteDifference, com.chupacadabra.finitedifference.UnivariateFunction)
	 */
	public double value(final double x, 
			final FiniteDifference finiteDifference,
			final UnivariateFunction function)
	{
		// construct core Mathur bandwidth finder.
		MathurApproximatelyOptimal mathur = new MathurApproximatelyOptimal(
				trialGridWidth,
				conditionError,
				roundoffError, 
				x,
				finiteDifference, 
				function);
		
		double bandwidth = mathur.value();
		
		return bandwidth;		
	}


}
