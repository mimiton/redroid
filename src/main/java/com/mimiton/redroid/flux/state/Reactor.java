package com.mimiton.redroid.flux.state;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.HashSet;

abstract public class Reactor<T> {
  private Reactor<T> parent;
  private HashSet<Reactor<T>> childs = new HashSet<>();


  public Reactor (Context context) {
    bindLifecycle(context);
  }

  private void bindLifecycle (Context context) {
    if (context instanceof LifecycleOwner) {
      ((LifecycleOwner)context)
        .getLifecycle()
        .addObserver(
          new LifecycleEventObserver() {
            @Override
            public void onStateChanged(
              @NonNull LifecycleOwner source,
              @NonNull Lifecycle.Event event
            ) {
              if (event == Lifecycle.Event.ON_DESTROY) {
                removeSelf();
              }
            }
          }
        );
    }
  }


  final protected void notifyChild () {
    notifyChild(null);
  }
  final protected void notifyChild (final Reactor specifiedChild) {
    if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
      if (specifiedChild == null) {
        for (Reactor child : childs) {
          child.onNotified();
        }
      }
      else {
        specifiedChild.onNotified();
      }
    }
    else {
      // 在主线程执行，避免可能在非主线程操作UI引起问题
      new Handler(Looper.getMainLooper())
        .post(new Runnable() {
          @Override
          public void run() {
            notifyChild(specifiedChild);
          }
        });
    }
  }

  protected void onNotified () {
    if (childs.size() > 0) {
      notifyChild();
    }
    else if (
        parent instanceof State
      &&
        !(this instanceof State)
      &&
        (this instanceof Reactor)
    ) {
      State<T> parentState = (State<T>) parent;
      onNotifiedChanges(parentState.get(), parentState.oldVal);
    }
  }

  abstract protected void onNotifiedChanges (T newVal, T oldVal);

  final public void addChild (Reactor child) {
    child.parent = this;
    childs.add(child);
  }

  final public void removeChild (Reactor child) {
    child.parent = null;
    childs.remove(child);
  }

  final public void removeSelf () {
    if (parent != null) {
      parent.removeChild(this);
    }
  }
}
