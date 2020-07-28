package com.mimiton.redroid.base.attribute;

import android.util.AttributeSet;

import java.util.ArrayList;

public class AttributeList extends ArrayList<Attribute> {
  public static final String NAMESPACE_DIRECTIVE = "http://com.ritter.framework/directive";
  public static final String NAMESPACE_EVENTBIND = "http://com.ritter.framework/eventbind";
  public static final String NAMESPACE_DATABIND = "http://com.ritter.framework/databind";
  private String[] namespacesToDigest = {
    NAMESPACE_DIRECTIVE,
    NAMESPACE_EVENTBIND,
    NAMESPACE_DATABIND
  };

  public AttributeList () {}
  public AttributeList (AttributeSet attributeSet) {
    digestFromAttributeSet(attributeSet);
  }

  public void digestFromAttributeSet (AttributeSet attributeSet) {
    if (attributeSet == null) {
      return;
    }
    int count = attributeSet.getAttributeCount();
    for (int i = 0; i < count; i ++) {
      String name = attributeSet.getAttributeName(i);
      for (String namespace : namespacesToDigest) {
        String value = attributeSet.getAttributeValue(namespace, name);
        if (value != null) {
          add(new Attribute(name, value, namespace));
        }
      }
    }
  }
}
