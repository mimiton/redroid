package com.mimiton.redroid.base.processor;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;

import com.mimiton.redroid.base.ViewModel;
import com.mimiton.redroid.flux.state.Reactor;
import com.mimiton.redroid.flux.state.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DirectiveForProcessor extends AttributeProcessor {
  static final String TAG = "DirectiveForProcessor";

  private ViewGroup parentView;

  public DirectiveForProcessor (@NonNull ViewModel contextViewModel, @NonNull ViewModel targetViewModel) {
    super(contextViewModel, targetViewModel);

    backupParentView();
  }

  @Override
  public void process (String name, String value, String namespace) {
    State property = contextViewModel.getProperty(value);
    if (property == null) {
      return;
    }

    property.link(new Reactor<ArrayList>(targetViewModel.getContext()) {
      @Override
      public void onNotifiedChanges (ArrayList newVal, ArrayList oldVal) {
        backupParentView();
        if (parentView == null) {
          return;
        }

        ensureDuplication(newVal.size());
        passThroughProperties(newVal);
      }
    });
  }

  private void backupParentView () {
    if (parentView != null) {
      return;
    }

    ViewParent viewParent = targetViewModel.getParent();
    if (viewParent instanceof ViewGroup) {
      parentView = (ViewGroup) viewParent;
      parentView.removeView(targetViewModel);
    }
  }

  private ViewModel duplicateViewModel () {
    ViewModel viewModel = null;
    try {
      viewModel = targetViewModel.getClass()
        .getDeclaredConstructor(Context.class, AttributeSet.class)
        .newInstance(targetViewModel.getContext(), targetViewModel.getAttributeSetBackup());
      viewModel.onFinishInflate();
    } catch (Exception e) {
      Log.w(TAG, e);
    }

    return viewModel;
  }

  private void ensureDuplication (int size) {
    int childCount = parentView.getChildCount();
    int addCount = size > childCount ? (size - childCount) : 0;
    int removeCount = size < childCount ? (childCount - size) : 0;

    while (addCount > 0) {
      ViewModel viewModel = duplicateViewModel();
      if (viewModel != null) {
        parentView.addView(viewModel);
      }
      addCount --;
    }

    if (removeCount > 0) {
      parentView.removeViews(size - 1, removeCount);
    }
  }

  private void passThroughProperties (ArrayList listData) {
    for (int index = 0; index < listData.size(); index ++) {
      Object item = listData.get(index);
      if (!(item instanceof HashMap)) {
        continue;
      }
      View child = parentView.getChildAt(index);
      if (!(child instanceof ViewModel)) {
        continue;
      }

      HashMap<Object, Object> mapData = (HashMap) item;
      ViewModel viewModel = (ViewModel) child;

      // 遍历map
      Iterator iterator = mapData.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry entry = (Map.Entry) iterator.next();
        Object key = entry.getKey();
        Object val = entry.getValue();
        if (!(key instanceof String && val instanceof String)) {
          continue;
        }

        viewModel.bindProperty((String) key, new State<>((String) val));
      }
    }
  }
}
