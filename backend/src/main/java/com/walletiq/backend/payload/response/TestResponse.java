package com.walletiq.backend.payload.response;

public record TestResponse(
    String id,
    String name,
    String email
) {

  public static TestResponse of(String id, String name, String email) {
    return new TestResponse(id, name, email);
  }
}