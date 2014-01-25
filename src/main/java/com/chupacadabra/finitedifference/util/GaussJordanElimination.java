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
package com.chupacadabra.finitedifference.util;

/**
 * Performs arbitrary-precision Gauss-Jordan elimination.
 * <p>
 * This class is stateless and cannot be instantiated.
 */
public final class GaussJordanElimination
{

	/**
	 * Solve the system <code><b>a</b>x = b</code> to arbitrary precision, using
	 * Guass-Jordon elimination.
	 * 
	 * @param a The matrix of coefficients.
	 * @param b The constant values.
	 * @return <code>x</code>
	 * @throws IllegalArgumentException If <code>a</code> is singular.
	 */
	public static BigRational[] solve(final BigRational[][] a, final BigRational[] b)
	{
		int n = b.length;

		for(int p = 0; p < n; p++)
		{
			// find pivot row and swap
			int max = p;
			for(int i = p + 1; i < n; i++)
			{
				if(a[i][p].abs().compareTo(a[max][p].abs()) > 0)
				{
					max = i;
				}
			}

			BigRational[] temp = a[p];
			a[p] = a[max];
			a[max] = temp;
			
			BigRational t = b[p];
			b[p] = b[max];
			b[max] = t;

			if(a[p][p].equals(BigRational.ZERO))
			{
				// singular matrix... Given that we are solving finite
				// difference system, this should probably never happen.
				throw new IllegalArgumentException("a is singular");
			}

			// pivot within a and b
			for(int i = p + 1; i < n; i++)
			{
				BigRational alpha = a[i][p].divide(a[p][p]);
				b[i] = b[i].subtract(alpha.multiply(b[p]));
								
				for(int j = p; j < n; j++)
				{
					a[i][j] = a[i][j].subtract(alpha.multiply(a[p][j]));
				}
			}
		}

		// perform back substitution to solve the system.
		BigRational[] x = new BigRational[n];
		for(int i = n - 1; i >= 0; i--)
		{
			BigRational sum = BigRational.ZERO;
			
			for(int j = i + 1; j < n; j++)
			{
				sum = sum.add(a[i][j].multiply(x[j]));
			}
			
			x[i] = (b[i].subtract(sum)).divide(a[i][i]);
		}
		
		return x;
	}

	/**
	 * Constructor.
	 */
	private GaussJordanElimination()
	{
	}

}
