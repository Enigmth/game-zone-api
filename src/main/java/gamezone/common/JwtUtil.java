package gamezone.common;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public final class JwtUtil {

    private static final long ACCESS_TOKEN_TTL_MS = 24L * 60 * 60 * 1000;
    private static final String DEFAULT_SECRET = "dev-secret-changeme-must-be-at-least-32ch";

    private static final byte[] SECRET_BYTES;

    static {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.isBlank()) {
            secret = DEFAULT_SECRET;
        }
        SECRET_BYTES = secret.getBytes(StandardCharsets.UTF_8);
    }

    private JwtUtil() {
    }

    public static String generateAccessToken(String userId) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
            Date now = new Date();
            Date expiry = new Date(now.getTime() + ACCESS_TOKEN_TTL_MS);
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(userId)
                    .issueTime(now)
                    .expirationTime(expiry)
                    .build();
            SignedJWT jwt = new SignedJWT(header, claims);
            jwt.sign(new MACSigner(SECRET_BYTES));
            return jwt.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate access token", e);
        }
    }

    /**
     * Verifies the JWT signature and expiry, then returns the subject (userId).
     * Throws an exception if the token is invalid or expired.
     */
    public static String verifyAndGetUserId(String token) throws Exception {
        SignedJWT jwt = SignedJWT.parse(token);
        if (!jwt.verify(new MACVerifier(SECRET_BYTES))) {
            throw new Exception("Invalid JWT signature");
        }
        JWTClaimsSet claims = jwt.getJWTClaimsSet();
        if (claims.getExpirationTime() == null || claims.getExpirationTime().before(new Date())) {
            throw new Exception("JWT expired");
        }
        return claims.getSubject();
    }
}
