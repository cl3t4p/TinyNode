package com.cl3t4p.TinyNode.model;

public enum CommandType {
  OK(0),
  ON(1),
  OFF(2),
  ON_TIMER(3),
  OFF_TIMER(4),
  ;

  public final int value;

  CommandType(int value) {
    this.value = value;
  }
}
