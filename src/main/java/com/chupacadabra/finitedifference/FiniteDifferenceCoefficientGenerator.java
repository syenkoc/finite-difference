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

import com.chupacadabra.finitedifference.util.BigRational;
import com.chupacadabra.finitedifference.util.GaussJordanElimination;

/**
 * Finite difference coefficient generator.
 * <p>
 * The basic idea here is to, given the derivative and error order, solve a set
 * of simultaneous equations, derived from repeated Taylor expansions, to find
 * the coefficients for the desired derivative approximation.
 * <p>
 * In general, you probably want to use the
 * {@linkplain FiniteDifferenceCoefficients coefficient cache} rather than this
 * class directly.
 */
public final class FiniteDifferenceCoefficientGenerator
{

	/**
	 * The finite difference.
	 */
	private final FiniteDifference finiteDifference;

	/**
	 * Constructor.
	 * 
	 * @param finiteDifference
	 */
	public FiniteDifferenceCoefficientGenerator(
			final FiniteDifference finiteDifference)
	{
		this.finiteDifference = finiteDifference;
	}

	/**
	 * Get the coefficients.
	 * 
	 * @return The coefficients.
	 */
	public double[] getCoefficients()
	{
		BigRational[][] a = getCoefficientMatrix();
		BigRational[] b = getConstantVector();
		BigRational[] x = GaussJordanElimination.solve(a, b);

		int factorial = factorial(finiteDifference.getDerivativeOrder());
		BigRational divisor = BigRational.valueOf(factorial, 1);

		for(int index = 0; index < x.length; index++)
		{
			x[index] = x[index].multiply(divisor);
		}

		double[] values = new double[x.length];
		for(int index = 0; index < x.length; index++)
		{
			values[index] = x[index].doubleValue();
		}

		return values;
	}

	/**
	 * Generate the coefficient matrix.
	 * 
	 * @return The coefficient matrix.
	 */
	private BigRational[][] getCoefficientMatrix()
	{
		int size = finiteDifference.getLength();
		BigRational[][] matrix = new BigRational[size][size];

		for(int row = 0; row < size; row += 1)
		{
			for(int col = 0, multiplier = finiteDifference.getLeftMultiplier(); col < size; col += 1, multiplier += 1)
			{
				if(row == 0)
				{
					matrix[row][col] = BigRational.ONE;
				}
				else
				{
					BigRational value = BigRational.valueOf(multiplier, 1);
					matrix[row][col] = matrix[(row - 1)][col].multiply(value);
				}
			}
		}

		return matrix;
	}

	/**
	 * Get the constant vector.
	 * 
	 * @return The constant vector.
	 */
	private BigRational[] getConstantVector()
	{
		int size = finiteDifference.getLength();
		BigRational[] b = new BigRational[size];

		for(int index = 0; index < b.length; index += 1)
		{
			BigRational value = (index == finiteDifference.getDerivativeOrder()) ? BigRational.ONE
					: BigRational.ZERO;
			b[index] = value;
		}

		return b;
	}

	/**
	 * Compute the factorial of the specified value.
	 * 
	 * @param n The value.
	 * @return <code>n!</code>
	 */
	private static int factorial(final int n)
	{
		int value = 1;
		for(int index = 1; index <= n; index++)
		{
			value *= index;
		}

		return value;
	}

}
