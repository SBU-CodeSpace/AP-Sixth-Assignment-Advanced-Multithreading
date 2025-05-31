package Banking;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    private final int id;
    private int balance;
    private final Lock lock = new ReentrantLock();

    public BankAccount(int id, int initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }

    public int getId(){
        return  id;
    }
    public int getBalance() {
        return balance; // No need for a lock, as access to the balance is safe in other methods
    }

    public Lock getLock() {
        return lock;
    }

    public void deposit(int amount) {
        lock.lock();
        try {
            balance += amount;
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(int amount) {
        lock.lock();
        try {
            balance -= amount;
        } finally {
            lock.unlock();
        }
    }

    public void transfer(BankAccount target, int amount) {
        // Locking order by id to avoid deadlock
        Lock firstLock = this.id < target.id ? this.lock : target.lock;
        Lock secondLock = this.id < target.id ? target.lock : this.lock;

        firstLock.lock();
        try {
            secondLock.lock();
            try {
                this.balance -= amount; // Deduction from the first account
                target.balance += amount; // Deposit to destination account
            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
    }

}
