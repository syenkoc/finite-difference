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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * An arbitrary precision, rational number.
 * <p>
 * Instances of this class are immutable. The "operator" methods of this class
 * return new instances with the desired properties.
 */
public final class BigRational
	extends Number
	implements Comparable<BigRational>
{

	/**
	 * Zero.
	 */
	public static final BigRational ZERO = new BigRational(BigInteger.ZERO, BigInteger.ONE);

	/**
	 * One.
	 */
	public static final BigRational ONE = new BigRational(BigInteger.ONE, BigInteger.ONE);

	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Get the value of the specified number.
	 * 
	 * @param value The value.
	 * @return <code>value</code> as a BigRational.
	 */
	public static BigRational valueOf(final long value)
	{
		return valueOf(value, 1L);
	}
	
	/**
	 * Get the value of the specified number.
	 * 
	 * @param value The value.
	 * @return <code>value</code> as a BigRational.
	 */
	public static BigRational valueOf(final BigInteger value)
	{
		return valueOf(value, BigInteger.ONE);
	}

	/**
	 * Get the value of the specified rational.
	 * 
	 * @param numerator The numerator.
	 * @param denominator The denominator.
	 * @return <code>numerator / denominator</code>
	 */
	public static BigRational valueOf(
			final long numerator,
			final long denominator)
	{
		return valueOf(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
	}

	/**
	 * Get the value of the specified rational.
	 * 
	 * @param numerator The numerator.
	 * @param denominator The denominator.
	 * @return <code>numerator / denominator</code>
	 */
	public static BigRational valueOf(BigInteger numerator, BigInteger denominator)
	{
		if(denominator.equals(BigInteger.ZERO))
		{
			throw new IllegalArgumentException("division by zero");
		}

		// special case check.
		if(numerator.equals(BigInteger.ZERO))
		{
			return ZERO;
		}

		// extract any common factor.
		BigInteger gcd = numerator.gcd(denominator);
		numerator = numerator.divide(gcd);
		denominator = denominator.divide(gcd);

		// normalize so numerator will contain negative sign.
		boolean negative = (numerator.signum() * denominator.signum()) == -1;
		numerator = numerator.abs();
		denominator = denominator.abs();

		if(negative)
		{
			numerator = numerator.negate();
		}

		if(numerator.equals(BigInteger.ONE) && 
			denominator.equals(BigInteger.ONE))
		{
			return ONE;
		}

		return new BigRational(numerator, denominator);
	}

	/**
	 * The numerator.
	 */
	private final BigInteger numerator;

	/**
	 * The denomerator.
	 */
	private final BigInteger denominator;

	/**
	 * Constructor.
	 * 
	 * @param numerator Numerator.
	 * @param denominator The denominator.
	 */
	private BigRational(final BigInteger numerator, final BigInteger denominator)
	{
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	/**
	 * Get the numerator.
	 * 
	 * @return The numerator.
	 */
	public BigInteger getNumerator()
	{
		return numerator;
	}
	
	/**
	 * Get the denominator.
	 * 
	 * @return The denominator.
	 */
	public BigInteger getDenominator()
	{
		return denominator;
	}

	/**
	 * Is this rational an integer?
	 * 
	 * @return <code>true</code> if this is an integer; and <code>false</code>
	 *         otherwise.
	 */
	private boolean isInteger()
	{
		return denominator.equals(BigInteger.ONE);
	}

	/**
	 * Get the absolute value of this number.
	 * 
	 * @return <code>|this|</code>
	 */
	public BigRational abs()
	{
		return valueOf(numerator.abs(), denominator);
	}

	/**
	 * Get the negative of this value.
	 * 
	 * @return <code>-this</code>
	 */
	public BigRational negate()
	{
		BigInteger negative = numerator.negate();
		
		return valueOf(negative, denominator);
	}

	/**
	 * Get the inverse of this value.
	 * 
	 * @return <code>this<sup>-1</sup></code>
	 */
	public BigRational inverse()
	{
		return valueOf(denominator, numerator);
	}

	/**
	 * Add the specified value to this.
	 * 
	 * @param that The value to add.
	 * @return <code>this + that</code>
	 */
	public BigRational add(final BigRational that)
	{
		BigInteger left = numerator.multiply(that.denominator);
		BigInteger right = denominator.multiply(that.numerator);
		BigInteger top = left.add(right);

		BigInteger bottom = denominator.multiply(that.denominator);

		return valueOf(top, bottom);
	}

	/**
	 * Subtract the specified value from this.
	 * 
	 * @param that The value to subtract.
	 * @return <code>this - that</code>
	 */
	public BigRational subtract(final BigRational that)
	{
		return add(that.negate());
	}

	/**
	 * Multiply this by the specified value.
	 * 
	 * @param that The value by which to multiply.
	 * @return <code>this * that</code>
	 */
	public BigRational multiply(final BigRational that)
	{
		BigInteger top = numerator.multiply(that.numerator);
		BigInteger bottom = denominator.multiply(that.denominator);

		return valueOf(top, bottom);
	}

	/**
	 * Divide this by the specified value.
	 * 
	 * @param that The value by which to divide.
	 * @return <code>this / that</code>
	 */
	public BigRational divide(final BigRational that)
	{
		return multiply(that.inverse());
	}

	/**
	 * @see java.lang.Number#intValue()
	 */
	@Override
	public int intValue()
	{
		BigDecimal numeratorDecimal = new BigDecimal(numerator);
		BigDecimal denominatorDecimal = new BigDecimal(denominator);
		BigDecimal result = numeratorDecimal.divide(denominatorDecimal, 0, RoundingMode.HALF_EVEN);

		return result.intValue();
	}

	/**
	 * @see java.lang.Number#longValue()
	 */
	@Override
	public long longValue()
	{
		BigDecimal numeratorDecimal = new BigDecimal(numerator);
		BigDecimal denominatorDecimal = new BigDecimal(denominator);
		BigDecimal result = numeratorDecimal.divide(denominatorDecimal, 0, RoundingMode.HALF_EVEN);

		return result.longValue();
	}

	/**
	 * @see java.lang.Number#doubleValue()
	 */
	@Override
	public double doubleValue()
	{
		BigDecimal numeratorDecimal = new BigDecimal(numerator);
		BigDecimal denominatorDecimal = new BigDecimal(denominator);

		int scale = denominatorDecimal.precision() + 18;
		BigDecimal result = numeratorDecimal.divide(denominatorDecimal, scale, RoundingMode.HALF_EVEN);

		return result.doubleValue();
	}

	/**
	 * @see java.lang.Number#floatValue()
	 */
	@Override
	public float floatValue()
	{
		BigDecimal numeratorDecimal = new BigDecimal(numerator);
		BigDecimal denominatorDecimal = new BigDecimal(denominator);

		int scale = denominatorDecimal.precision() + 7;
		BigDecimal result = numeratorDecimal.divide(denominatorDecimal, scale, RoundingMode.HALF_EVEN);

		return result.floatValue();
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final BigRational that)
	{
		BigInteger left = numerator.multiply(that.denominator);
		BigInteger right = denominator.max(that.numerator);

		return left.compareTo(right);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hashCode = numerator.hashCode();
		hashCode ^= denominator.hashCode();

		return hashCode;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if(this == obj)
		{
			return true;
		}

		if(obj == null)
		{
			return false;
		}

		if(!(obj instanceof BigRational))
		{
			return false;
		}

		BigRational other = (BigRational)obj;
		return numerator.equals(other.numerator)
				&& denominator.equals(other.denominator);

	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		if(isInteger())
		{
			return numerator.toString();
		}

		return String.format("%1$s / %2$s", numerator, denominator);
	}

}
