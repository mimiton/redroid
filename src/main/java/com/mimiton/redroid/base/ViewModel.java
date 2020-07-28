package com.mimiton.redroid.base;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mimiton.redroid.base.attribute.AttributeList;
import com.mimiton.redroid.base.processor.AttributeProcessor;
import com.mimiton.redroid.component.Slot;
import com.mimiton.redroid.flux.state.State;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

abstract public class ViewModel extends FrameLayout {
  static final String TAG = "ViewModel";

  static public final Stack<ViewModel> inflateContextStack = new Stack<>();


  private AttributeSet attributeSetBackup;
  public AttributeList attributesList = new AttributeList();

  private ViewGroup shadowView;
  private HashMap<String, Slot> slotsMap = new HashMap<>();
  private HashMap<String, ArrayList<EventHandler>> eventHandlersMap = new HashMap<>();

  public ViewModel (Context context, AttributeSet attrs) {
    super(context, attrs);
    digestAttributes(attrs);
  }

  protected int layoutId () {
    return 0;
  }
  protected void onCreated () {}
  protected void onMounted () {}

  @Override
  final public void onFinishInflate () {
    super.onFinishInflate();
    init();
  }

  protected ViewModel getInflateContext () {
    return inflateContextStack.empty() ? null : inflateContextStack.peek();
  }

  private void digestAttributes (AttributeSet attributeSet) {
    attributeSetBackup = attributeSet;
    attributesList.digestFromAttributeSet(attributeSet);
  }

  public AttributeSet getAttributeSetBackup () {
    return attributeSetBackup;
  }

  private void init () {
    onCreated();
    inflateShadow();
    new AttributeProcessor(getInflateContext(), this).processAll();
  }

  public void mount () {
    // Todo: 需要进一步扩展具名插槽的能力
    Slot slot = slotsMap.get("default");

    if (slot != null) {
      ArrayList<View> originalChilds = new ArrayList<View>();
      // 先摘下所有子view
      int childCount = getChildCount();
      for (int i = 0; i < childCount; i ++) {
        View currentChild = getChildAt(i);
        originalChilds.add(currentChild);
      }
      removeAllViews();

      ViewGroup parent = (ViewGroup) slot.getParent();
      if (parent != null) {
        int index = parent.indexOfChild(slot);
        parent.removeView(slot);
        for (View view : originalChilds) {
          parent.addView(view, index);
          index ++;
        }
      }
    }

    setVisibility(VISIBLE);
    if (shadowView != null) {
      removeView(shadowView);
      addView(shadowView);
    }

    onMounted();
  }

  public void unmount () {
    setVisibility(GONE);
    removeView(shadowView);
  }

  private void inflateShadow () {
    int layoutId = this.layoutId();
    if (layoutId == 0) {
      return;
    }

    inflateContextStack.push(this);
    // 渲染组件的局部view树
    LayoutInflater mInflater = LayoutInflater.from(getContext());
    shadowView = (ViewGroup) mInflater.inflate(layoutId, this, false);
    inflateContextStack.pop();
  }

  public void setSlot (String key, Slot slot) {
    slotsMap.put(key, slot);
  }

  public State getProperty (String fieldName) {
    if (fieldName == null) {
      return null;
    }

    Field field = null;
    try {
      field = getClass().getDeclaredField(fieldName);
    } catch (NoSuchFieldException e) {
      Log.w(TAG, e);
    }
    if (field == null) {
      return null;
    }

    State propertyState = null;

    field.setAccessible(true);
    try {
      Object fieldValue = field.get(this);
      if (fieldValue instanceof State) {
        propertyState = (State) fieldValue;
      }
    } catch (IllegalAccessException e) {
      Log.w(TAG, e);
    }

    return propertyState;
  }

  public void bindProperty (String fieldName, State srcPropertyState) {
    if (fieldName == null || srcPropertyState == null) {
      return;
    }

    final State selfPropertyState = getProperty(fieldName);
    if (selfPropertyState != null) {
      selfPropertyState.bind(getContext(), srcPropertyState);
    }
  }

  public void invokeMethod (String methodName, Object ...params) {
    if (methodName == null) {
      return;
    }

    Class[] paramsClasses = new Class[params.length];
    for (int i = 0, j = params.length; i < j; i++) {
      paramsClasses[i] = params[i].getClass();
    }

    Method method = null;
    try {
      method = getClass().getDeclaredMethod(methodName, paramsClasses);
    } catch (NoSuchMethodException e) {
      Log.w(TAG, e);
    }
    if (method == null) {
      return;
    }

    method.setAccessible(true);
    try {
      method.invoke(this, params);
    } catch (Exception e) {
      Log.w(TAG, e);
    }
  }

  public void emitEvent (String eventName) {
    emitEvent(eventName, null);
  }
  public void emitEvent (String eventName, Object eventObject) {
    ArrayList<EventHandler> existList = eventHandlersMap.get(eventName);
    if (existList != null) {
      for (EventHandler handler : existList) {
        handler.handle(eventObject);
      }
    }
  }
  public void onEvent (String eventName, EventHandler handler) {
    ArrayList<EventHandler> existList = eventHandlersMap.get(eventName);
    if (existList == null) {
      existList = new ArrayList<EventHandler>();
      eventHandlersMap.put(eventName, existList);
    }

    existList.add(handler);
  }

  public interface EventHandler {
    public void handle(Object eventObject);
  }
}
