package com.mimiton.redroid.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.mimiton.redroid.base.ViewModel;
import com.mimiton.redroid.flux.state.Reactor;
import com.mimiton.redroid.flux.state.State;

public class Text extends ViewModel {
  private State<String> text = new State<>("");
  private TextView textView;

  public Text (Context context, AttributeSet attrs) {
    super(context, attrs);
    textView = new TextView(context, attrs);
    addView(textView);
  }

  @Override
  protected void onCreated () {
    text.link(new Reactor<String>(getContext()) {
      @Override
      public void onNotifiedChanges (String newVal, String oldVal) {
        textView.setText(newVal);
      }
    });
  }
}
