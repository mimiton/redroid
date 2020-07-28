# Redroid —— Redefine Android
## 一个真正的 积木化、响应式、声明式 的安卓UI开发框架
## 努力改变安卓原生应用的开发体验和效率，愿望是能让大家从复杂恶心的胶水代码中解脱出来
### (初步版本，还在继续开发中，欢迎有兴趣的朋友来一起探讨，有兴趣加我wechat: mimiton)

#### 快速上手

1. 数据绑定，可计算的响应式属性
`Component.java`
```java
import com.mimiton.redroid.base.ViewModel;
import com.mimiton.redroid.flux.state.State;

pubic class Component extends ViewModel {
  // 普通属性
  private State<String> propsX = new State<String>("XXX");
  private State<String> propsY = new State<String>("YYY");
  // 计算属性
  private State<String> propsZ = new State<String>() {
    @Override
    protected String compute() {
      return propsX.get() + "和" + propsY.get(); // "XXX和YYY"
    }
  };

  public Component(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected int layoutId() {
    return R.layout.component; // 返回组件使用的layout声明
  }

  @Override
  protected void onMounted () {
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        propsX.set("属性X变啦！")
      }
    }, 1000);
  }
}
```
`R.layout.component`
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout
  xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:v="http://com.ritter.framework/directive" // 行为指令命名空间
  xmlns:bind="http://com.ritter.framework/databind" // 数据绑定命名空间
  xmlns:on="http://com.ritter.framework/eventbind" // 事件处理命名空间
  android:layout_width="match_parent"
  android:layout_height="match_parent">
  <com.mimiton.redroid.component.Text
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    bind:text="propsZ">
  <!-- 此处 propsZ 会指向组件里名叫propsZ的计算属性，并形成响应式的绑定，实际UI表现会随数据状态变化而自动变化 -->
  <!-- 文字初始会显示为"XXX和YYY"，随后1秒会变成"属性X变啦！和YYY" -->
  </com.mimiton.redroid.component.Text>
</FrameLayout>
```

2. 条件与循环

`Component.java`
```java
import com.mimiton.redroid.base.ViewModel;
import com.mimiton.redroid.flux.state.State;

pubic class Component extends ViewModel {
  private State<Boolean> showText = new State<>(false);
  private State<ArrayList<HashMap<String, String>>> dataList = new State<>();

  public Component(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected int layoutId() {
    return R.layout.component; // 返回组件使用的layout声明
  }

  @Override
  protected void onMounted () {
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        dataList.set(
          new ArrayList<HashMap<String, String>>() {{
            put("text", "第1个item");
            put("text", "第2个item");
            put("text", "第3个item");
          }}
        );
      }
    }, 1000);
  }
}
```
`R.layout.component`
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout
  xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:v="http://com.ritter.framework/directive" // 行为指令命名空间
  xmlns:bind="http://com.ritter.framework/databind" // 数据绑定命名空间
  xmlns:on="http://com.ritter.framework/eventbind" // 事件处理命名空间
  android:layout_width="match_parent"
  android:layout_height="match_parent">
  <!-- showText属性值为false，此处的Text组件不会被加载，当属性变为true时，它会自动加载显示，变为false时，又会自动移除隐藏 -->
  <com.mimiton.redroid.component.Text
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    v:if="showText">
  </com.mimiton.redroid.component.Text>
  <!-- 这里的Text组件会自动根据dataList属性里的列表数据复制出对应的视图 -->
  <com.mimiton.redroid.component.Text
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    v:for="dataList">
  </com.mimiton.redroid.component.Text>
</FrameLayout>
```

3. 组件嵌套插槽
  等我休息一下再写。。。