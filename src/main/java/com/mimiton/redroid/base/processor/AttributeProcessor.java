package com.mimiton.redroid.base.processor;

import androidx.annotation.NonNull;

import com.mimiton.redroid.base.ViewModel;
import com.mimiton.redroid.base.attribute.Attribute;
import com.mimiton.redroid.base.attribute.AttributeList;

public class AttributeProcessor {
  private boolean hasDirectiveIf = false;
  protected ViewModel contextViewModel;
  protected ViewModel targetViewModel;

  public AttributeProcessor (@NonNull ViewModel contextViewModel, @NonNull ViewModel targetViewModel) {
    this.contextViewModel = contextViewModel;
    this.targetViewModel = targetViewModel;
  }

  protected void process (String name, String value, String namespace) {}

  public void processAll () {
    if (contextViewModel != null) {
      for (Attribute attribute : targetViewModel.attributesList) {
        String name = attribute.name;
        String value = attribute.value;
        String namespace = attribute.namespace;
        processOne(name, value, namespace);
      }
    }

    if (!hasDirectiveIf) {
      targetViewModel.mount();
    }
  }

  private void processOne (String name, String value, String namespace) {
    if (namespace == null) {
      return;
    }

    if (namespace.equals(AttributeList.NAMESPACE_DIRECTIVE)) {
      if (name.equals("if")) {
        hasDirectiveIf = true;
        new DirectiveIfProcessor(contextViewModel, targetViewModel).process(name, value, namespace);
      }
      else if (name.equals("for")) {
        new DirectiveForProcessor(contextViewModel, targetViewModel).process(name, value, namespace);
      }
    }
    else if (namespace.equals(AttributeList.NAMESPACE_EVENTBIND)) {
      if (
          name.equals("tap")
        ||name.equals("touchstart")
        ||name.equals("touchmove")
        ||name.equals("touchend")
      ) {
        new EventTouchProcessor(contextViewModel, targetViewModel).process(name, value, namespace);
      }

      new EventBindProcessor(contextViewModel, targetViewModel).process(name, value, namespace);
    }
    else if (namespace != null && namespace.equals(AttributeList.NAMESPACE_DATABIND) && value != null) {
      new DataBindProcessor(contextViewModel, targetViewModel).process(name, value, namespace);
    }
  }
}
