package com.mimiton.redroid.base.node;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;

public class V {
  private View view;
  public V () {}
  public V (View view) {
    setView(view);
  }

  public void setView (View view) {
    this.view = view;
  }

  private View sibling (String direction) {
    View result = null;

    if (view != null) {
      ViewParent viewParent = view.getParent();

      if (viewParent instanceof ViewGroup) {
        ViewGroup parent = (ViewGroup) viewParent;
        int index = parent.indexOfChild(view);
        switch (direction) {
          case "prev":
            if (index > 0) {
              result = parent.getChildAt(index - 1);
            }
            break;
          case "next":
            if (index < parent.getChildCount() - 1) {
              result = parent.getChildAt(index + 1);
            }
            break;
        }
      }
    }

    return result;
  }

  public View prev () {
    return sibling("prev");
  }

  public View next () {
    return sibling("next");
  }

  public View[] children () {
    if (view instanceof ViewGroup) {
      ViewGroup viewGroup = (ViewGroup) view;

      int childCount = viewGroup.getChildCount();
      View[] childs = new View[childCount];
      for (int i = 0; i < childCount; i++) {
        View currentChild = viewGroup.getChildAt(i);
        childs[i] = currentChild;
      }

      return childs;
    }
    else {
      return new View[] {};
    }
  }

  public View[] descendants () {
    if (view instanceof ViewGroup) {
      ViewGroup viewGroup = (ViewGroup) view;

      ArrayList<View> result = new ArrayList<>();
      View[] childs = new V(viewGroup).children();
      for (View child : childs) {
        result.add(child);

        if (child instanceof ViewGroup) {
          View[] descendants = new V(child).descendants();
          for (View descendant : descendants) {
            result.add(descendant);
          }
        }
      }
      View[] resultArray = new View[result.size()];
      result.toArray(resultArray);
      return resultArray;
    }
    else {
      return new View[] {};
    }
  }
}
