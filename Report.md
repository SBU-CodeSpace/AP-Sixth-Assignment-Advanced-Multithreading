# Atomic Variables

```java  
import java.util.concurrent.atomic.AtomicInteger;
public class AtomicDemo {
  private static AtomicInteger atomicCounter = new AtomicInteger(0);
  private static int normalCounter = 0;
  public static void main(String[] args) throws InterruptedException {
    Runnable task = () -> {
      for (int i = 0; i < 1_000_000; i++) {
        atomicCounter.incrementAndGet();
        normalCounter++;
      }
    };

    Thread t1 = new Thread(task);
    Thread t2 = new Thread(task);
    t1.start();
    t2.start();
    t1.join();
    t2.join();

    System.out.println("Atomic Counter: " + atomicCounter);
    System.out.println("Normal Counter: " + normalCounter);
  }
}
  
```  

## **Questions:**

- What output do you get from the program? Why?

- What is the purpose of AtomicInteger in this code?

- What thread-safety guarantees does atomicCounter.incrementAndGet() provide?

- In which situations would using a lock be a better choice than an atomic variable?

- Besides AtomicInteger, what other data types are available in the java.util.concurrent.atomic package?

---  

## **Answers** : 

### Q1 : 

- Atomic Counter: 2000000
- Normal Counter: Less than 2000000

#### Why:

- `atomicCounter` uses `AtomicInteger`, which ensures atomic increments, resulting in exactly 2,000,000 (1M increments per thread).

- `normalCounter` is a regular `int`, and `normalCounter++` is not atomic, leading to race conditions where some increments are lost due to concurrent modifications.

### Q2 : 

`AtomicInteger` provides thread-safe, atomic operations to safely increment `atomicCounter` without locks, preventing race conditions in a multithreaded environment.

### Q3 : 

`incrementAndGet()` is atomic, ensuring that the read, increment, and write operations are performed as a single, indivisible unit.

It guarantees visibility (changes are immediately visible to all threads) and prevents race conditions.

### Q4 : 

- When operations involve multiple variables or complex logic that require mutual exclusion (e.g., updating two counters consistently).
- When fairness or explicit control over locking (e.g., ReentrantLock) is needed.

### Q5 : 

#### Other Data Types:

- `AtomicLong`: For atomic operations on `long` values.
- `AtomicBoolean`: For atomic operations on `boolean` values.
- `AtomicReference<V>`: For atomic operations on object references.
- `AtomicIntegerArray`, `AtomicLongArray`, `AtomicReferenceArray`: For arrays of atomic integers, longs, or references.

---

# Monte Carlo π Estimation Report

## Performance Comparison

- **Single-Threaded Version:**
    - Execution Time: ~1.4-2 seconds for 50,000,000 points.
    - Estimated π: ~3.1416 (accuracy depends on point count).

- **Multi-Threaded Version (4 threads):**
    - Execution Time: ~0.1-0.3 second for 50,000,000 points.
    - Estimated π: ~3.1416 (same accuracy as single-threaded).

one of the outputs :
```
Single threaded calculation started: 
Monte Carlo Pi Approximation (single thread): 3.14150792
Time taken (single threads): 1553 ms
Multi threaded calculation started: (your device has 32 logical threads)
Monte Carlo Pi Approximation (Multi-threaded): 3.14165904
Time taken (Multi-threaded): 161 ms
```
## Questions

### Was the multi-threaded implementation always faster than the single-threaded one?

No, the multi-threaded implementation is not always faster.

**Why not?**
- Small point counts (e.g., 10,000) incur thread pool setup overhead that outweighs processing time.
- Excessive threads beyond CPU cores (e.g., 16 threads on 4 cores) cause context switching overhead.
- Single-core systems lack parallelism, making multi-threading slower.
- Uneven point distribution (mitigated in this code) can lead to idle threads.

### If not, what factors are the cause and what can you do to mitigate these issues?

**Factors:**
- **Thread pool setup overhead:** Initializing `ExecutorService` is costly for small point counts.
- **Context switching:** Excessive threads beyond CPU cores increase switching overhead.
- **Hardware limitations:** Single-core systems lack parallelism.

**Mitigations:**
- Use `ExecutorService` to reuse threads, reducing creation overhead.
- Set thread count to match CPU cores (`availableProcessors()`).
- Fall back to single-threaded version for small point counts (<1M).