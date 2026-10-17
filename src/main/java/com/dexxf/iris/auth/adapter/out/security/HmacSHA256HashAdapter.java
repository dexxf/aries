package com.dexxf.iris.auth.adapter.out.security;

import com.dexxf.iris.auth.application.port.out.HashPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Service
public class HmacSHA256HashAdapter implements HashPort {

    private final SecretKeySpec keySpec;

    public HmacSHA256HashAdapter(@Value("${security.hash.hmac.sha-secret}") String secret) {
        this.keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Override
    public String encode(CharSequence raw) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);

            byte[] hash = mac.doFinal(
                    raw.toString().getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to compute HMAC-SHA256",
                    e
            );
        }
    }

    @Override
    public boolean matches(CharSequence raw, String encoded) {
        byte[] actual = encode(raw)
                .getBytes(StandardCharsets.UTF_8);

        byte[] expected = encoded
                .getBytes(StandardCharsets.UTF_8);

        return MessageDigest.isEqual(actual, expected);
    }
}
