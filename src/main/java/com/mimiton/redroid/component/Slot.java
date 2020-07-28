package com.mimiton.redroid.component;

import android.content.Context;
import android.util.AttributeSet;

import com.mimiton.redroid.base.ViewModel;

public class Slot extends ViewModel {

  public Slot (Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public void onCreated () {
    ViewModel inflateContext = getInflateContext();
    if (inflateContext != null) {
      inflateContext.setSlot("default", this);
    }
  }
}
