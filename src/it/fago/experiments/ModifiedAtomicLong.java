package it.fago.experiments;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

/**
 * 
 * @author Stefano Fago
 * 
 */
public class ModifiedAtomicLong extends Number {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4499743040705573372L;

	// ===================================================================
	//
	private static boolean VMSupportsCS8;
	//
	private static final sun.misc.Unsafe unsafe = getUnsafe();
	//
	private static final long valueOffset;
	//
	private volatile long value;
	//
	private long p1, p2, p3, p4, p5, p6, p7;
	//
	// ===================================================================

	static {
		VMSupportsCS8();
		try {
			valueOffset = unsafe.objectFieldOffset(AtomicLong.class
					.getDeclaredField("value"));
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	// ===================================================================

	public ModifiedAtomicLong() {
		value = 0L;
	}

	public ModifiedAtomicLong(long initValue) {
		value = initValue;
	}

	@SuppressWarnings("unused")
	private long sumPaddingToPreventOptimisation() {
		return p1 + p2 + p3 + p4 + p5 + p6 + p7;
	}

	/**
	 * Gets the current value.
	 * 
	 * @return the current value
	 */
	public final long get() {
		return value;
	}

	/**
	 * Sets to the given value.
	 * 
	 * @param newValue
	 *            the new value
	 */
	public final void set(long newValue) {
		value = newValue;
	}

	/**
	 * Eventually sets to the given value.
	 * 
	 * @param newValue
	 *            the new value
	 * @since 1.6
	 */
	public final void lazySet(long newValue) {
		unsafe.putOrderedLong(this, valueOffset, newValue);
	}

	/**
	 * Atomically sets to the given value and returns the old value.
	 * 
	 * @param newValue
	 *            the new value
	 * @return the previous value
	 */
	public final long getAndSet(long newValue) {
		while (true) {
			long current = get();
			if (compareAndSet(current, newValue)) {
				return current;
			} else {
				backoff();
			}
		}
	}

	/**
	 * Atomically sets the value to the given updated value if the current value
	 * {@code ==} the expected value.
	 * 
	 * @param expect
	 *            the expected value
	 * @param update
	 *            the new value
	 * @return true if successful. False return indicates that the actual value
	 *         was not equal to the expected value.
	 */
	public final boolean compareAndSet(long expect, long update) {
		return unsafe.compareAndSwapLong(this, valueOffset, expect, update);
	}

	/**
	 * Atomically sets the value to the given updated value if the current value
	 * {@code ==} the expected value.
	 * 
	 * <p>
	 * May <a href="package-summary.html#Spurious">fail spuriously</a> and does
	 * not provide ordering guarantees, so is only rarely an appropriate
	 * alternative to {@code compareAndSet}.
	 * 
	 * @param expect
	 *            the expected value
	 * @param update
	 *            the new value
	 * @return true if successful.
	 */
	public final boolean weakCompareAndSet(long expect, long update) {
		return unsafe.compareAndSwapLong(this, valueOffset, expect, update);
	}

	/**
	 * Atomically increments by one the current value.
	 * 
	 * @return the previous value
	 */
	public final long getAndIncrement() {
		while (true) {
			long current = get();
			long next = current + 1;
			if (compareAndSet(current, next)) {
				return current;
			} else {
				backoff();
			}
		}
	}

	/**
	 * Atomically decrements by one the current value.
	 * 
	 * @return the previous value
	 */
	public final long getAndDecrement() {
		while (true) {
			long current = get();
			long next = current - 1;
			if (compareAndSet(current, next)) {
				return current;
			} else {
				backoff();
			}
		}
	}

	/**
	 * Atomically adds the given value to the current value.
	 * 
	 * @param delta
	 *            the value to add
	 * @return the previous value
	 */
	public final long getAndAdd(long delta) {
		while (true) {
			long current = get();
			long next = current + delta;
			if (compareAndSet(current, next)) {
				return current;
			} else {
				backoff();
			}
		}
	}

	/**
	 * Atomically increments by one the current value.
	 * 
	 * @return the updated value
	 */
	public final long incrementAndGet() {
		for (;;) {
			long current = get();
			long next = current + 1;
			if (compareAndSet(current, next)) {
				return next;
			} else {
				backoff();
			}
		}
	}

	/**
	 * Atomically decrements by one the current value.
	 * 
	 * @return the updated value
	 */
	public final long decrementAndGet() {
		for (;;) {
			long current = get();
			long next = current - 1;
			if (compareAndSet(current, next)) {
				return next;
			} else {
				backoff();
			}
		}
	}

	/**
	 * Atomically adds the given value to the current value.
	 * 
	 * @param delta
	 *            the value to add
	 * @return the updated value
	 */
	public final long addAndGet(long delta) {
		for (;;) {
			long current = get();
			long next = current + delta;
			if (compareAndSet(current, next)) {
				return next;
			} else {
				backoff();
			}
		}
	}

	/**
	 * Returns the String representation of the current value.
	 * 
	 * @return the String representation of the current value.
	 */
	public String toString() {
		return Long.toString(get());
	}

	public int intValue() {
		return (int) get();
	}

	public long longValue() {
		return get();
	}

	public float floatValue() {
		return (float) get();
	}

	public double doubleValue() {
		return (double) get();
	}

	// ======================================================
	//
	//
	//
	// ======================================================

	private static final void VMSupportsCS8() {
		try {
			Method m = AtomicLong.class.getDeclaredMethod("VMSupportsCS8",
					(Class[]) null);
			m.setAccessible(true);
			VMSupportsCS8 = (Boolean) m.invoke(null, null);
		} catch (Exception e) {
			VMSupportsCS8 = false;
		}
	}

	private static final sun.misc.Unsafe getUnsafe() {
		try {
			return sun.misc.Unsafe.getUnsafe();
		} catch (SecurityException tryReflectionInstead) {
		}
		try {
			return java.security.AccessController
					.doPrivileged(new java.security.PrivilegedExceptionAction<sun.misc.Unsafe>() {
						public sun.misc.Unsafe run() throws Exception {
							Class<sun.misc.Unsafe> k = sun.misc.Unsafe.class;
							for (java.lang.reflect.Field f : k
									.getDeclaredFields()) {
								f.setAccessible(true);
								Object x = f.get(null);
								if (k.isInstance(x))
									return k.cast(x);
							}
							throw new NoSuchFieldError("the Unsafe");
						}
					});
		} catch (java.security.PrivilegedActionException e) {
			throw new RuntimeException("Could not initialize intrinsics",
					e.getCause());
		}
	}

	private static final void backoff() {
		LockSupport.parkNanos(1L);
	}

}// END
