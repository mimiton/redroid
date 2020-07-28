package com.mimiton.redroid.base.processor;

import android.view.MotionEvent;
import android.view.View;

import com.mimiton.redroid.base.ViewModel;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class EventTouchProcessor extends AttributeProcessor {
  public EventTouchProcessor(ViewModel contextViewModel, ViewModel targetViewModel) {
    super(contextViewModel, targetViewModel);
  }

  @Override
  public void process (String name, final String value, String namespace) {
    targetViewModel.setOnTouchListener(new View.OnTouchListener() {
      private long touchStartTime = 0;
      private int touchStartX = 0;
      private int touchStartY = 0;
      private Timeout timeout = new Timeout();

      @Override
      public boolean onTouch (View view, MotionEvent motionEvent) {
        int actionType = motionEvent.getAction();
        long currentTime = new Date().getTime();
        long timeOffset = currentTime - touchStartTime;
        int currentTouchX = (int) motionEvent.getRawX();
        int currentTouchY = (int) motionEvent.getRawY();
        int moveOffsetX = Math.abs(currentTouchX - touchStartX);
        int moveOffsetY = Math.abs(currentTouchY - touchStartY);

        switch (actionType) {
          case MotionEvent.ACTION_DOWN:
            timeout.setTimeout(new Runnable() {
              @Override
              public void run() {
                targetViewModel.emitEvent("longtouch");
              }
            }, 500);
            targetViewModel.emitEvent("touchdown");
            touchStartTime = currentTime;
            touchStartX = currentTouchX;
            touchStartY = currentTouchY;
            break;
          case MotionEvent.ACTION_MOVE:
            if (moveOffsetX > 5 || moveOffsetY > 5) {
              timeout.clear();
            }
            targetViewModel.emitEvent("touchmove");
            break;
          case MotionEvent.ACTION_CANCEL:
          case MotionEvent.ACTION_UP:
            timeout.clear();
            targetViewModel.emitEvent("touchup");
            if (moveOffsetX < 5 && moveOffsetY < 5 && timeOffset < 300) {
              targetViewModel.emitEvent("tap");
            }
            break;
        }
        return actionType == MotionEvent.ACTION_DOWN;
      }
    });
  }

  private class Timeout {
    private TimerTask task;

    public void setTimeout (final Runnable runnable, int time) {
      task = new TimerTask() {
        @Override
        public void run () {
          runnable.run();
        }
      };
      new Timer().schedule(task, time);
    }

    public void clear () {
      if (task != null) {
        task.cancel();
        task = null;
      }
    }
  }
}
