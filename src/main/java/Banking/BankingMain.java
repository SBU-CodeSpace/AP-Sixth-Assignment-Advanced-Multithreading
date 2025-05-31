package Banking;

import java.util.ArrayList;
import java.util.List;

public class BankingMain {

    public List<BankAccount> calculate() throws InterruptedException {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new BankAccount(1,20000));
        accounts.add(new BankAccount(2,20000));
        accounts.add(new BankAccount(3,20000));
        accounts.add(new BankAccount(4,20000));

        Thread[] threads = new Thread[4];
        for(int i = 1; i <= 4; i++){
            String fileName = i + ".txt";
            threads[i - 1] = new Thread(new TransactionProcessor(accounts.get(i - 1), fileName, accounts) , ("Acc-" + i));
        }

        for(Thread thread : threads){
            thread.start();
        }

        for(Thread thread : threads){
            thread.join();
        }


        return accounts;
    }
    public static void main(String[] args) throws InterruptedException {
        BankingMain main = new BankingMain();
        List<BankAccount> accounts = main.calculate();
        for(BankAccount account : accounts){
            System.out.println("Final balance of Account Number "  + account.getId() + " : " + account.getBalance());
        }
    }
}
