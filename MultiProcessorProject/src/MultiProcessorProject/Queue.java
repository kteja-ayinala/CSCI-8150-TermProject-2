/**
 * 
 */
package MultiProcessorProject;

/**
 * @author krishnatejaayinala, sindhura
 *
 */

//Implementation from
//https://www.techiedelight.com/queue-implementation-in-java/
public class Queue {
	private int front; // front points to front element in the queue
	private int rear; // rear points to last element in the queue
	private int capacity; // maximum capacity of the queue
	private int count; // current size of the queue
	private Object arr[]; // array to store queue elements

	Queue() {
		arr = new Object[64];
		capacity = 64;
		front = 0;
		rear = -1;
		count = 0;
	}

	Object dequedObj = null;

	public Object dequeue() {

		// check for queue underflow
		if (isEmpty()) {
			System.out.println("UnderFlow");
		}
		dequedObj = arr[front];
		// System.out.println("Removing " + arr[front]);
		front = (front + 1);
		count--;
		return dequedObj;
	}

	public void enqueue(Object item) {
		// check for queue overflow
		if (isFull()) {
			// System.out.println("OverFlow\nProgram Terminated");
		}

		// System.out.println("Inserting " + item);

		rear = (rear + 1);
		arr[rear] = item;
		count++;
	}

	public int size() {
		return count;
	}

	public Boolean isEmpty() {
		return (size() == 0);
	}

	public Boolean isFull() {
		return (size() == capacity);
	}
}
