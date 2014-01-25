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

import com.chupacadabra.finitedifference.util.MachineEpsilon;
import com.chupacadabra.finitedifference.util.PowerOfTwo;

/**
 * Computes rule-of-thumb grid widths.
 * <p>
 * From Mathur's thesis, we have that the optimal step size is:<br>
 * <code>h<sub>opt</sub> = [(d/n)(1/C<sub>n</sub>)(&epsilon;|F<sub>&epsilon;</sub>| + &delta;|F<sub>&delta;</sub>|)]<sup>1/(n+d)</sup></code>
 * <p>
 * Where:
 * <table>
 * <tr>
 * <td align="right"><code>d</code></td>
 * <td><code>=</code></td>
 * <td>Derivative order</td>
 * </tr>
 * <tr>
 * <td align="right"><code>n</code></td>
 * <td><code>=</code></td>
 * <td>Error order</td>
 * </tr>
 * <tr>
 * <td align="right"><code>C<sub>n</sub></code></td>
 * <td><code>=</code></td>
 * <td>Truncation error</td>
 * </tr>
 * <tr>
 * <td align="right"><code>C<sub>n</sub></code></td>
 * <td><code>=</code></td>
 * <td>Truncation error</td>
 * </tr>
 * <tr>
 * <td align="right"><code>&epsilon;</code></td>
 * <td><code>=</code></td>
 * <td>Function condition error</td>
 * </tr>
 * <tr>
 * <td align="right"><code>F<sub>&epsilon;</sub></code></td>
 * <td><code>=</code></td>
 * <td>Condition error coefficient</td>
 * </tr>
 * <tr>
 * <td align="right"><code>&delta;</code></td>
 * <td><code>=</code></td>
 * <td>Function roundoff error</td>
 * </tr>
 * <tr>
 * <td align="right"><code>F<sub>&epsilon;</sub></code></td>
 * <td><code>=</code></td>
 * <td>Roundoff error coefficient</td>
 * </tr>
 * </table>
 * <p>
 * We derive a rule-of-thumb grid width by making the following simplifying
 * assumptions:
 * <ul>
 * <li>We use the simplified versions of the condition and roundoff
 * coefficients. In this case:
 * <ul>
 * <li>
 * <code>|F<sub>&epsilon;</sub>| = |f<sub>0</sub>| &Sigma; |FD<sub>i</sub>|</code>
 * </li>
 * <li><code>F<sub>&delta;</sub> = F<sub>&epsilon;</sub> / 2</code></li>
 * </ul>
 * </li>
 * <li>We assume <code>&epsilon; = &delta; = &mu;</code>, where
 * <code>&mu;</code> is the {@linkplain MachineEpsilon#DOUBLE_VALUE machine
 * epsilon}.</li>
 * </ul>
 * <p>
 * Inserting these assumptions into the formula above and re-arranging, we have:
 * <br>
 * <code>h<sub>opt</sub> = (3/2)(d/n)(&mu; &Sigma;)<sup>1/(n+d)</sup> (|f<sub>0</sub>|/|C<sub>n</sub>|)<sup>1/(n+d)</sup></code>
 * <p>
 * Thus, if we could estimate <code>|f<sub>0</sub>| / |C<sub>n</sub>|</code>, we
 * could have a reasonable grid wdith. Unfortunately, this involves knowing
 * something about the behavior of the higher-order derivatives of
 * <code>f</code>. So, we use another approach. We can regard
 * <code>(|f<sub>0</sub>| / |C<sub>n</sub>|)<sup>1/(n+d)</sup></code> as a kind
 * of "charateristic scale" over which <code>f</code> changes; in the absence of
 * any other information, we simply select this quantity to be
 * <code>|x<sub>0</sub>|</code> itself (except near 0).
 * <p>
 * In general, it is recommended that you use 
 * {@linkplain #getPowerOfTwoRuleOfThumbGridWidth(double, FiniteDifference) power-of-two}
 * grid widths, as then <code>x +/- h</code> will be computed exactly (i.e. 
 * without any representation error).  
 * <p>
 * This class is stateless and cannot be instantiated.
 */
public final class RuleOfThumb
{

	/**
	 * Get rule-of-thumb grid width.
	 * 
	 * @param x The point around which we want to take a derivative.
	 * @param finiteDifference The finite difference.
	 * @return The rule of the thumb grid width.
	 */
	public static double getRuleOfThumbGridWidth(final double x,
			final FiniteDifference finiteDifference)
	{
		double sum = 0;
		double[] cofficients = finiteDifference.getCoefficients();
		for(double c : cofficients)
		{
			sum += Math.abs(c);
		}		
		
		double mu = MachineEpsilon.DOUBLE_VALUE;
		double d = finiteDifference.getDerivativeOrder();
		double n = finiteDifference.getErrorOrder();		
		double power = 1d / (n + d);
		double arg = (3 * d * mu * sum) / (2 * n);
		
		// finally, we can compute the grid width.
		double gridWidth = Math.pow(arg, power) * Math.max(1d, Math.abs(x));		

		// make sure x and x + h differ by an exact representable number.
		double temp = x + gridWidth;
		nop(temp);
		gridWidth = temp - x;

		return gridWidth;
	}

	/**
	 * Do nothing!
	 * 
	 * @param x Ignored.
	 */
	private static void nop(final double x)
	{
	}

	/**
	 * Get next smallest power-of-two to the rule-of-thumb grid width.
	 * <p>
	 * There are many theoretical and practical advantages to using 
	 * power-of-two grid widths. Mostly, <code>x + h</code> can be
	 * computed without any loss of precision.
	 * 
	 * @param x The point around which we want to take a derivative.
	 * @param finiteDifference The finite difference.
	 * @return The rule of the thumb grid width.
	 */
	public static double getPowerOfTwoRuleOfThumbGridWidth(final double x,
			final FiniteDifference finiteDifference)
	{
		double gridWidth = getRuleOfThumbGridWidth(x, finiteDifference);				

		double powerOfTwoWidth = PowerOfTwo.nextLargestPowerOfTwo(gridWidth);
		
		return powerOfTwoWidth;
	}

	/**
	 * Constructor.
	 */
	private RuleOfThumb()
	{
	}

}
