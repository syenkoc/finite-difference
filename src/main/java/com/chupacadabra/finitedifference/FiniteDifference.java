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

import java.io.Serializable;


/**
 * A finite difference descriptor.
 */
public final class FiniteDifference
	implements Serializable
{
	
	// a few canonical stencils.
	
	/**
	 * 0<sup>th</sup> order stencil.
	 * <p>
	 * Obviously useful in the context of multivariate functions...
	 */
	public static final FiniteDifference VALUE = new FiniteDifference(FiniteDifferenceType.CENTRAL, 0, 1);
	
	/**
	 * Two point forward 1<sup>st</sup> order stencil.
	 */
	public static final FiniteDifference TWO_POINT_FORWARD = new FiniteDifference(FiniteDifferenceType.FORWARD, 1, 1);
	
	/**
	 * Three point 1<sup>st</sup> order stencil.
	 */
	public static final FiniteDifference THREE_POINT_CENTRAL = new FiniteDifference(FiniteDifferenceType.CENTRAL, 1, 2);
	
	/**
	 * Five point 1<sup>st</sup> order stencil.
	 */
	public static final FiniteDifference FIVE_POINT_CENTRAL = new FiniteDifference(FiniteDifferenceType.CENTRAL, 1, 4);
	
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The difference type.
	 */
	private final FiniteDifferenceType finiteDifferenceType;
	
	/**
	 * Derivative order.
	 */
	private final int derivativeOrder;
	
	/**
	 * Error order.
	 */
	private final int errorOrder;
	
	/**
	 * Left index multiplier.
	 */
	private final int leftMultiplier;
	
	/**
	 * The right multiplier.
	 */
	private final int rightMultiplier;

	/**
	 * The length.
	 */
	private final int length;
	
	/**
	 * The coefficients.
	 */
	private final double[] coefficents;
	
	/**
	 * Constructor.
	 * 
	 * @param finiteDifferenceType The difference type.
	 * @param derivativeOrder The derivative order.
	 * @param errorOrder The error order.
	 */
	public FiniteDifference(
			final FiniteDifferenceType finiteDifferenceType,
			final int derivativeOrder, 
			final int errorOrder)
	{
		this.finiteDifferenceType = finiteDifferenceType;
		this.derivativeOrder = derivativeOrder;
		this.errorOrder = errorOrder;
		
		switch(finiteDifferenceType)
		{
			case BACKWARD:
				rightMultiplier = 0;
				leftMultiplier = errorOrder + derivativeOrder;
				length = leftMultiplier;
				break;
			case CENTRAL:
				rightMultiplier = ((derivativeOrder + errorOrder) / 2);
				leftMultiplier = -rightMultiplier;
				length = (rightMultiplier * 2) + 1;
				break;
			case FORWARD:
				rightMultiplier = errorOrder + derivativeOrder;
				leftMultiplier = 0;
				length = rightMultiplier;
				break;
			default:
				throw new IllegalArgumentException("finiteDifferenceType");
		}
		
		this.coefficents = FiniteDifferenceCoefficients.getCoefficients(this);
	}
	
	/**
	 * Get the coefficients.
	 * 
	 * @return The coefficients.
	 */
	public double[] getCoefficients()
	{
		return coefficents;
	}

	/**
	 * Get the finite difference type.
	 * 
	 * @return The type.
	 */
	public FiniteDifferenceType getFiniteDifferenceType()
	{
		return finiteDifferenceType;
	}
	
	/**
	 * Get the derivative order;
	 * 
	 * @return The derivative order
	 */
	public int getDerivativeOrder()
	{
		return derivativeOrder;
	}

	/**
	 * Get the error order.
	 * 
	 * @return The error order.
	 */
	public int getErrorOrder()
	{
		return errorOrder;
	}
	
	/**
	 * Get the left multiplier.
	 * 
	 * @return The left multiplier.
	 */
	public int getLeftMultiplier()
	{
		return leftMultiplier;
	}
	
	/**
	 * Get the right multiplier.
	 * 
	 * @return The right multiplier.
	 */
	public int getRightMultiplier()
	{
		return rightMultiplier;
	}
	
	/**
	 * Get the length.
	 * 
	 * @return The length.
	 */
	public int getLength()
	{
		return length;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("FiniteDifference [finiteDifferenceType=");
		builder.append(finiteDifferenceType);
		builder.append(", derivativeOrder=");
		builder.append(derivativeOrder);
		builder.append(", errorOrder=");
		builder.append(errorOrder);
		builder.append(", length=");
		builder.append(length);
		builder.append("]");
		
		return builder.toString();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hashCode = finiteDifferenceType.hashCode();
		hashCode ^= derivativeOrder;
		hashCode ^= errorOrder;
		
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
		
		if(!(obj instanceof FiniteDifference))
		{
			return false;
		}
		
		FiniteDifference other = (FiniteDifference)obj;
		return (finiteDifferenceType == other.finiteDifferenceType) &&
				(derivativeOrder == other.derivativeOrder) &&
				(errorOrder == other.derivativeOrder);
	}
	
}
