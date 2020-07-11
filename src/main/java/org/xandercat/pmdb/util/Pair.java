package org.xandercat.pmdb.util;

/**
 * Container class for a pair of related values.
 * 
 * @author Scott Arnold
 *
 * @param <T> value type
 */
public class Pair<T> {

	private T first;
	private T second;
	
	public Pair() {
	}
	public Pair(T first, T second) {
		this.first = first;
		this.second = second;
	}
	public T getFirst() {
		return first;
	}
	public void setFirst(T first) {
		this.first = first;
	}
	public T getSecond() {
		return second;
	}
	public void setSecond(T second) {
		this.second = second;
	}
}
