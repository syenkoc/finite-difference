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
package org.teneighty.finitedifference.util;

/**
 * Utility methods for getting doubles that are exact powers of two.
 */
public final class PowerOfTwo
{

	/**
	 * Mantissa mask - contains 12 1s shifted 52 places to mask out mantissa of
	 * IEEE double.
	 * <p>
	 * ("For any MC in any 52 states, I get psycho killer, Norman Bates!")
	 */
	private static final long MASK = 0xFFFL << 52;

	/**
	 * Find the next largest value that is a power of two.
	 * 
	 * @param value The value.
	 * @return The closest larger number that is a power of two.
	 */
	public static double nextLargestPowerOfTwo(final double value)
	{
		long bits = Double.doubleToLongBits(value);

		if((bits & ~MASK) == 0)
		{
			// already a power of two!
			return value;
		}

		double powerOfTwo = Double.longBitsToDouble(bits & MASK) * 2;

		return powerOfTwo;
	}

	/**
	 * Constructor.
	 */
	private PowerOfTwo()
	{
	}

}
