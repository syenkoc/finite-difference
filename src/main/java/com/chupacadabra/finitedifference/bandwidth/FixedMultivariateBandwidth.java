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

import com.chupacadabra.finitedifference.MultivariateFiniteDifference;
import com.chupacadabra.finitedifference.MultivariateFunction;


/**
 * Fixed width multivariate bandwidth strategy.
 */
public class FixedMultivariateBandwidth
	implements MultivariateBandwidth
{
	
	/**
	 * The bandwidths.
	 */
	private final double[] bandwidths;

	/**
	 * @param bandwidths
	 */
	public FixedMultivariateBandwidth(final double[] bandwidths)
	{
		this.bandwidths = bandwidths;
	}

	/**
	 * @see com.chupacadabra.finitedifference.bandwidth.MultivariateBandwidth#value(double[], com.chupacadabra.finitedifference.MultivariateFiniteDifference, com.chupacadabra.finitedifference.MultivariateFunction)
	 */
	@Override
	public double[] value(final double[] x,
			final MultivariateFiniteDifference finiteDifference,
			final MultivariateFunction function)
	{
		return bandwidths;
	}
	
}
