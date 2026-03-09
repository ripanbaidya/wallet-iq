package com.walletiq.backend.dto.test;

public record TestResponse(
    String id,
    String name,
    String email
) {

  public static TestResponse of(String id, String name, String email) {
    return new TestResponse(id, name, email);
  }
}