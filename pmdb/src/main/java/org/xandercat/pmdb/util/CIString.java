package org.xandercat.pmdb.util;

/**
 * String container that is case insensitive for comparison operations.
 * 
 * @author Scott Arnold
 */
public class CIString implements Comparable<CIString> {

	private String value;

	public CIString(String value) {
		this.value = value;
	}
	
	@Override
	public int compareTo(CIString o) {
		if (this == o) {
			return 0;
		}
		if (o == null) {
			return 1;
		}
		if (value == null && o.value == null) {
			return 0;
		}
		if (value != null && o.value != null) {
			return value.compareToIgnoreCase(o.value);
		}
		return (value == null)? -1 : 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.toLowerCase().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CIString other = (CIString) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equalsIgnoreCase(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return value;
	}
}
