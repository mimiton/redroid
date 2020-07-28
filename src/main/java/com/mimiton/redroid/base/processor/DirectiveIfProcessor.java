package com.mimiton.redroid.base.processor;

import com.mimiton.redroid.base.ViewModel;
import com.mimiton.redroid.flux.state.Reactor;
import com.mimiton.redroid.flux.state.State;

public class DirectiveIfProcessor extends AttributeProcessor {
  public DirectiveIfProcessor (ViewModel contextViewModel, ViewModel targetViewModel) {
    super(contextViewModel, targetViewModel);
  }

  @Override
  public void process (String name, String value, String namespace) {
    State property = contextViewModel.getProperty(value);
    if (property != null) {
      property.link(new Reactor<Boolean>(targetViewModel.getContext()) {
        @Override
        public void onNotifiedChanges (Boolean newVal, Boolean oldVal) {
          if (newVal) {
            targetViewModel.mount();
          }
          else {
            targetViewModel.unmount();
          }
        }
      });
    }
  }
}
