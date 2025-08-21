import java.util.concurrent.*;

public class Main {
  public static void main(String[] args) throws InterruptedException {
    // Create a virtual thread and start it
    Thread vt = Thread.ofVirtual().start(() -> {
      System.out.println("Hello from virtual thread! " + Thread.currentThread());
    });

    // Wait for it to finish;
    vt.join();

    // Create many virtual threads;
    try (final var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      for (int i = 0; i < Integer.MAX_VALUE; i++) {
        int taskId = 1;
        executor.submit(() -> {
          System.out.println("Task " + taskId + " running in " + Thread.currentThread());
          try { Thread.sleep(500); } catch (Exception e) { System.out.println(e.getMessage()); }
          System.out.println("Task " + taskId + " finished.");
        });
      }
    }

    System.out.println("All tasks submitted!");
  }
}

