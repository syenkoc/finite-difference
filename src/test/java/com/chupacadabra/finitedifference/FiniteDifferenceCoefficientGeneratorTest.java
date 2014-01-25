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

import org.junit.Assert;
import org.junit.Test;

import com.chupacadabra.finitedifference.FiniteDifference;
import com.chupacadabra.finitedifference.FiniteDifferenceCoefficientGenerator;
import com.chupacadabra.finitedifference.FiniteDifferenceType;
import com.chupacadabra.finitedifference.util.MachineEpsilon;


/**
 * Finite difference coefficient generator test.
 */
public final class FiniteDifferenceCoefficientGeneratorTest
{
	
	/**
	 * The sauce for double comparison.
	 */
	private static final double DELTA = MachineEpsilon.DOUBLE_VALUE * 2;

	/**
	 * Value.
	 */
	//@Test
	public void value()
	{
		testCore(FiniteDifference.VALUE, 1);
	}
	
	/**
	 * Three point stencil finite difference.
	 */
	//@Test
	public void threePointCentral()
	{
		testCore(FiniteDifference.THREE_POINT_CENTRAL, -0.5, 0, 0.5);
	}
	
	@Test
	public void thirdOrderCentral()
	{
		FiniteDifference finiteDifference = new FiniteDifference(FiniteDifferenceType.CENTRAL, 2, 4);
		testCore(finiteDifference, -0.5, 0, 0.5);
	}

	
	/**
	 * Core tester.
	 *  
	 * @param finiteDifference The finite difference.
	 * @param expected The coefficients.
	 */
	private void testCore(final FiniteDifference finiteDifference, final double... expected)
	{
		FiniteDifferenceCoefficientGenerator generator = new FiniteDifferenceCoefficientGenerator(finiteDifference);
		double[] generated = generator.getCoefficients();
		
		Assert.assertArrayEquals(expected, generated, DELTA);
	}
	
}
