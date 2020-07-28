package com.mimiton.redroid.flux.state;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

public class State<T> extends Reactor<T> {
  static final String TAG = "State";

  public T oldVal;
  private T value;
  private Boolean isDirty = true;
  private Boolean flagIsComputeState;

  public State () {
    this(null);
  }
  public State (T initialValue) {
    super(null);

    if (isComputeState()) {
      executeCompute();
    }
    else {
      set(initialValue);
    }
  }

  /**
   * 判断计算方法有没有被重写
   * @return
   */
  private final Boolean isComputeState () {
    if (flagIsComputeState != null) {
      return flagIsComputeState;
    }

    flagIsComputeState = false;
    try {
      Method methodOriginal = State.class.getDeclaredMethod("compute");
      Method methodCurrent = getClass().getDeclaredMethod("compute");
      flagIsComputeState = !methodCurrent.equals(methodOriginal);
    } catch (Exception e) {
      Log.w(TAG, e);
    }

    return flagIsComputeState;
  }

  public void link (Reactor reactor) {
    link(reactor, true);
  }
  public void link (Reactor reactor, boolean notifyImmediately) {
    addChild(reactor);

    if (notifyImmediately && get() != null) {
      notifyChild(reactor);
    }
  }

  /**
   * 绑定一个StateWatcher到当前State，同步当前State的数据到另一个State，形成绑定
   * @param context
   * @param state
   */
  public void bind (Context context, final State<T> state) {
    if (state == null) {
      Log.w(TAG, "Binding State with a Null State. Ignored!");
      return;
    }
    if (context == null) {
      Log.w(TAG, "Binding State with a Null Context. Please pass a Context for lifecycle recycling.");
      return;
    }
    if (isComputeState()) {
      Log.w(TAG, "Binding State on a compute State, Ignored!");
      return;
    }

    state.link(new Reactor<T>(context) {
      @Override
      protected void onNotifiedChanges(T newVal, T oldVal) {
        State.this.set(newVal);
      }
    });
  }

  /**
   * 读取数据
   * 并处理依赖收集栈里的State的观察过程
   * @return
   */
  public T get () {
    // 如果依赖收集栈存在至少一个State
    final State depsState = DepsCollectStack.getInstance().peek();
    if (depsState != null) {
      // 链接响应关系（静默模式，不触发初始通知）
      link(depsState, false);
    }

    // 如果是计算属性
    if (isComputeState() && isDirty) {
      executeCompute();
    }

    return value;
  }

  /**
   * 设置数据
   * @param value
   */
  public void set (T value) {
    if (isComputeState()) {
      Log.w(TAG, "Don't set value to a compute State! Ignored!");
      return;
    }

    T newVal = value;
    T oldVal = this.value;

    // 数据相同则跳过，不触发变更
    if (newVal == oldVal) {
      return;
    }

    this.oldVal = oldVal;
    this.value = newVal;
    notifyChild();
  }

  /**
   * 一个空的计算方法，需要在创建时被覆写来让它实际生效
   * @return
   */
  protected T compute () {
    return value;
  }

  /**
   * 进行计算过程
   * 此过程会收集计算过程中所依赖的State，并以Watcher身份对它们产生观察
   * 观察的处理过程请见 get() 方法
   */
  private void executeCompute () {
    DepsCollectStack.getInstance().push(this);
    value = compute();
    DepsCollectStack.getInstance().pop();
    isDirty = false;
  }

  @Override
  protected void onNotified () {
    if (isComputeState()) {
      isDirty = true;
      oldVal = value;
    }

    // 注意：此处必须在处理完计算属性的脏标记后调用super的方法
    //      否则会使计算属性get到前一个旧值
    super.onNotified();
  }

  @Override
  protected void onNotifiedChanges (T newVal, T oldVal) {

  }
}
