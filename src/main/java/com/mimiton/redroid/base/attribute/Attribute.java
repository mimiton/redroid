package com.mimiton.redroid.base.attribute;

public class Attribute {
  public String namespace;
  public String name;
  public String value;

  public Attribute (String name, String value) {
    this.name = name;
    this.value = value;
  }
  public Attribute (String name, String value, String namespace) {
    this.name = name;
    this.value = value;
    this.namespace = namespace;
  }
}
