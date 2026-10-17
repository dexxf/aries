package com.dexxf.iris.auth.application.port.out;

public interface HashPort {
    String encode(CharSequence raw);
    boolean matches(CharSequence rawPassword, String encoded);
}
