package com.mimiton.redroid.flux.state;

import java.util.Stack;

public class DepsCollectStack {
  static DepsCollectStack instance;

  private ThreadLocal<Stack<State>> stackThreadLocal = new ThreadLocal<>();

  public static DepsCollectStack getInstance() {
    if (instance == null) {
      instance = new DepsCollectStack();
    }

    if (instance.stackThreadLocal.get() == null) {
      instance.stackThreadLocal.set(new Stack<State>());
    }

    return instance;
  }

  public void push (State state) {
    Stack<State> stack = stackThreadLocal.get();
    stack.push(state);
  }

  public State pop () {
    return stackThreadLocal.get().pop();
  }

  public State peek () {
    Stack<State> stack = stackThreadLocal.get();
    return stack.empty() ? null : stack.peek();
  }
}
