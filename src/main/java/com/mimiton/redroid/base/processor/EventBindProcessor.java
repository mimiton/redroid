package com.mimiton.redroid.base.processor;

import androidx.annotation.NonNull;

import com.mimiton.redroid.base.ViewModel;

public class EventBindProcessor extends AttributeProcessor {
  public EventBindProcessor (@NonNull ViewModel contextViewModel, @NonNull ViewModel targetViewModel) {
    super(contextViewModel, targetViewModel);
  }

  @Override
  public void process (String name, final String value, String namespace) {
    targetViewModel.onEvent(name, new ViewModel.EventHandler() {
      @Override
      public void handle (Object eventObject) {
        contextViewModel.invokeMethod(value);
      }
    });
  }
}
