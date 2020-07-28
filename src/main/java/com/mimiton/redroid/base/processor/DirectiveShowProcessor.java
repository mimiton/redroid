package com.mimiton.redroid.base.processor;

import android.view.View;

import com.mimiton.redroid.base.ViewModel;
import com.mimiton.redroid.flux.state.Reactor;
import com.mimiton.redroid.flux.state.State;

public class DirectiveShowProcessor extends AttributeProcessor {
  public DirectiveShowProcessor (ViewModel contextViewModel, ViewModel targetViewModel) {
    super(contextViewModel, targetViewModel);
  }

  @Override
  public void process (String name, String value, String namespace) {
    State property = contextViewModel.getProperty(value);
    if (property != null) {
      property.link(new Reactor<Boolean>(targetViewModel.getContext()) {
        @Override
        public void onNotifiedChanges (Boolean newVal, Boolean oldVal) {
          if (newVal == true) {
            targetViewModel.setVisibility(View.VISIBLE);
          }
          else {
            targetViewModel.setVisibility(View.INVISIBLE);
          }
        }
      });
    }
    else {
      targetViewModel.setVisibility(View.INVISIBLE);
    }
  }
}
