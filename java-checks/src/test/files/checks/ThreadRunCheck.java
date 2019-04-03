import java.io.Serializable;

class A {
  public static void main(String[] args) {
    Runnable runnable = null;

    Thread myThread = new Thread(runnable);
    myThread.run(); // Noncompliant {{Call the method Thread.start() to execute the content of the run() method in a dedicated thread.}}

    Thread myThread2 = new Thread(runnable);
    myThread2.start();

    run();
    A a = new A();
    a.run();

    B b = new B();
    b.run(); // Noncompliant

    C c = new C();
    c.run(); // Noncompliant

    D d = new D();
    d.run(); // Noncompliant

    E e = new E();
    e.run();

    F f = new F();
    f.run();

    runnable.run(); // Noncompliant
  }

  public static void run() {
  }

  static class B extends Thread {
  }

  static class C implements Runnable {

    @Override
    public void run() {
    }

  }

  static class D extends C {
    @Override
    public void run() {
      C c = new C();
      c.run(); // Noncompliant
      super.run(); // Noncompliant
    }
  }

  static class E implements Serializable {

    public void run() {
    }

  }

  static class F extends E {
  }
}
