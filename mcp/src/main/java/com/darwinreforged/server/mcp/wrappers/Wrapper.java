package com.darwinreforged.server.mcp.wrappers;

public abstract class Wrapper<T> {

  T t;

  public T get() {
    return t;
  }

  protected void set(T t) {
    this.t = t;
  }
}
