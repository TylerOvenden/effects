// LinkedListVector Class
// Written by: Craig A. Lindley
// Last Update: 03/02/99

package craigl.utils;

import java.util.*;

/**
 * This is an implementation of a Vector for AbstractAudio devices. It is
 * different than a normal Vector in that each element can determine the
 * elements on both sides of itself via previous and next object references.
 * In this regard this is more a linked list class with Vector like methods
 * hence its name.
 */
public class LinkedListVector {

	/**
	 * Linked List Vector class constructor.
	 * Sets the content of the vector to empty and the count of elements
	 * to zero.
	 */
	public LinkedListVector() {
		
		head = null;
		tail = null;
		count = 0;
	}

	/**
	 * Returns the number of AbstractAudio elements on the list
	 *
	 * @return int containing the element count
	 */
	public int size() {
		
		return count;
	}

	/**
	 * Used to determine if list is empty
	 *
	 * @return boolean true if list is empty, false if not empty
	 */
	public boolean isEmpty() {
		
		return (count == 0);
	}

	/**
	 * Converts the list into a string for display
	 *
	 * @return String containing the AbstractAudio elements on the list. 
	 * The string can be empty if no elements exist. It will never be
	 * null.
	 */
	public synchronized String toString() {
		
		String retString = "";

		if (count == 0)
			return retString;

		AbstractAudio aa = head;
		while (aa != null) {
			retString += aa;
			aa = aa.next;
		}
		return retString;
	}

	/**
	 * Return an enumeration for the list
	 *
	 * @return Enumeration object for enumerating the elements on the list
	 */
	public synchronized Enumeration elements() {

		return new LLVEnumeration(head);
	}

	/**
	 * Insert an AbstractAudio device into the list at a specified
	 * index.
	 *
	 * @param AbstractAudio a is the device to add to the list
	 * @param int index is the position in the list the device should
	 * be inserted at.
	 */
	public synchronized void insertElementAt(AbstractAudio a, 
	                                         int index) 
		throws IndexOutOfBoundsException {

		if ((count == 0) || (index >= count))
			throw new IndexOutOfBoundsException();
		
		// index is within range
		AbstractAudio aa = head;
		while (index-- != 0)
			aa = aa.next;
		
		// insert before aa
		if (aa == head) {
			
			a.next = head;
			a.previous = null;
			aa.previous = a;
			head = a;

		}	else	{
			a.next = aa;
			a.previous = aa.previous;
			aa.previous.next = a;
			aa.previous = a;
		} 
	}

	/**
	 * Remove an AbstractAudio element from the list
	 *
	 * @param AbstractAudio a is the device to remove
	 *
	 * @return boolean true if the device existed in the list and false
	 * if device was not found
	 */
	public synchronized boolean removeElement(AbstractAudio a) {

		int index = indexOf(a);
		if (index == -1)
			return false;

		removeElementAt(index);
		return true;
	}

	/**
	 * Remove the AbstractAudio element at specified index from the list
	 *
	 * @param int index is the index of the device to remove
	 */
	public synchronized void removeElementAt(int index) 
		throws IndexOutOfBoundsException {

		if ((count == 0) || (index >= count))
			throw new IndexOutOfBoundsException();
		
		// index is within range
		AbstractAudio aa = head;
		while (index-- != 0)
			aa = aa.next;

		// aa is the object to remove
		if (aa == head) {
			
			AbstractAudio newHead = aa.next;
			aa.next = aa.previous = null;
			head = newHead;
			head.previous = null;
		
		}	else if (aa == tail) {
			AbstractAudio newTail = aa.previous;
			aa.next = aa.previous = null;
			tail = newTail;
			tail.next = null;
		}	else {
			AbstractAudio prevo = aa.previous;
			AbstractAudio nexto = aa.next;
			aa.next = aa.previous = null;
			prevo.next = nexto;
			nexto.previous = prevo;
		}
	}

	/**
	 * Find the last index of the specified AbstractAudio device
	 * in the list.
	 *
	 * @param AbstractAudio a is the device to find the last index of
	 *
	 * @return int containing the last index or -1 if the device was
	 * not found.
	 */
	public synchronized int lastIndexOf(AbstractAudio a) {

		int index = count - 1;

		AbstractAudio aa = tail;
		while (aa != null) {
			if (aa.equals(a))
				return index;
			index--;
			aa = aa.previous;
		}
		return -1;
	}

	/**
	 * Find the index of the specified AbstractAudio device
	 * in the list.
	 *
	 * @param AbstractAudio a is the device to find the index of
	 *
	 * @return int containing the index or -1 if the device was
	 * not found.
	 */
	public synchronized int indexOf(AbstractAudio a) {

		int index = 0;

		AbstractAudio aa = head;
		while (aa != null) {
			if (aa.equals(a))
				return index;
			index++;
			aa = aa.next;
		}
		return -1;
	}


	/**
	 * Find the index of the specified AbstractAudio device
	 * in the list starting with the specified index.
	 *
	 * @param AbstractAudio a is the device to find the index of
	 * @param int index is the index in the list to start the search
	 * from.
	 *
	 * @return int containing the index or -1 if the device was
	 * not found.
	 */
	public synchronized int indexOf(AbstractAudio a, int index) {

		if ((count == 0) || (index >= count))
			return -1;

		// index is within range
		int foundIndex = index;

		AbstractAudio aa = head;
		while (index-- != 0)
			aa = aa.next;

		while(aa != null) {
			if (aa.equals(a))
				return foundIndex;

			foundIndex++;
			aa = aa.next;
		}
		return -1;
	}

	/**
	 * Return the AbstractAudio device at the specified index in the list
	 *
	 * @param int index is the index of the element to return
	 *
	 * @return AbstractAudio device at the specified index
	 */
	public synchronized AbstractAudio elementAt(int index)
		throws IndexOutOfBoundsException {

		if ((count == 0) || (index >= count))
			throw new IndexOutOfBoundsException();

		AbstractAudio aa = head;
		while(index-- != 0) 
			aa = aa.next;

		return aa;
	}

	/**
	 * Determine if the specified AbstractAudio device exists in the
	 * list.
	 *
	 * @param AbstractAudio a is the device to look for
	 *
	 * @return boolean true if list contains element
	 */
	public synchronized boolean contains(AbstractAudio a) {

		if (count == 0)
			return false;

		AbstractAudio aa = head;
		while (aa != null) {
			if (aa.equals(a))
				return true;
			aa = aa.next;
		}
		return false;
	}

	/**
	 * Remove all elements from the list
	 */
	public synchronized void removeAllElements() {

		// Only have something to do if there are elements in list
		if (count != 0) {
			while (head != null) {
				AbstractAudio next = head.next;
				head.next = null;
				head.previous = null;
				head = next;
			}
			count = 0;
			head = tail = null;
		}
	}

	/**
	 * Return the first element of the list
	 *
	 * @return AbstractAudio device at front of list
	 */
	public synchronized AbstractAudio firstElement() 
		throws NoSuchElementException {
		
		if (head == null)
			throw new NoSuchElementException();

		return head;
	}

	/**
	 * Return the last element of the list
	 *
	 * @return AbstractAudio device at end of list
	 */
	public final synchronized AbstractAudio lastElement() {

		if (tail == null)
			throw new NoSuchElementException();

		return tail;
	}

	/**
	 * Add an AbstractAudio device to the list of devices
	 *
	 * @param AbstractAudio a is the device to add to the list
	 */
	public synchronized void addElement(AbstractAudio a) {

		a.previous = tail;
		if (tail != null)
			tail.next = a;
		
		tail = a;

		if (head == null)
			head = a;

		count++;
	}
	// Private class data
	private AbstractAudio head;
	private AbstractAudio tail;
	private int count;
}

/**
 * Enumeration class for LinkedListVector 
 */
class LLVEnumeration implements Enumeration {

	/**
	 * Class constructor
	 *
	 * @param AbstractAudio head is a reference to the head of
	 * of the list
	 */
	public LLVEnumeration(AbstractAudio head) {
		
		this.head = head;
	}

	/**
	 * See if list has more elements
	 *
	 * @return boolean true if list has more elements. False if
	 * it doesn't.
	 */
	public boolean hasMoreElements() {
		
		return (head != null);
	}

	/**
	 * Return next element on the list
	 *
	 * @return Object is a reference to the next object on the list
	 */
	public Object nextElement() {

		if (head == null)
			throw new NoSuchElementException();

		AbstractAudio aa = head;
		head = head.next;
		return aa;
	}

	// Private class data
	private AbstractAudio head;
}
		





	