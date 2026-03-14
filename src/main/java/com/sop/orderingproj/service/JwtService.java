package com.sop.orderingproj.service;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    @Value("${jwt.expiration-time}")
    private Integer jwtExpiration;

    private SecretKey secretKey;

    private static String SECRET;

    JwtService() {
        KeyGenerator keyGen;
        try {
            keyGen = KeyGenerator.getInstance("HmacSHA256");
            keyGen.init(256); // 256 bits
            SECRET = Base64.getEncoder().encodeToString(keyGen.generateKey().getEncoded());
            System.out.println(SECRET); // COPY THIS OUTPUT
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(SECRET);
        this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, JWSAlgorithm.HS256.getName());
    }

    public String generateToken(UserDetails userDetails) {
        return buildToken(userDetails, jwtExpiration);
    }

    public String generateToken(UserDetails userDetails, long expiration) {
        return buildToken(userDetails, expiration);
    }

    private String buildToken(UserDetails userDetails, long expiration) {
        try {
            JWSSigner signer = new MACSigner(secretKey);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userDetails.getUsername())
                    .claim("roles", userDetails.getAuthorities()
                            .stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()))
                    .issueTime(Date.from(Instant.now()))
                    .expirationTime(Date.from(Instant.now().plus(jwtExpiration, TimeUnit.HOURS.toChronoUnit())))
                    .build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, JWTClaimsSet::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, JWTClaimsSet::getExpirationTime);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(Date.from(Instant.now()));
    }

    public <T> T extractClaim(String token, Function<JWTClaimsSet, T> claimsResolver) {
        final JWTClaimsSet claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private JWTClaimsSet extractAllClaims(String token) {
        try {

            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secretKey);
            System.out.println("❌ SIGNATURE VERIFICATION FAILED");
            System.out.println("Token Header Alg: " + signedJWT.getHeader().getAlgorithm());
            System.out.println("Server Key Alg: " + secretKey.getAlgorithm());
            System.out.println("Server Key Length: " + secretKey.getEncoded().length + " bytes");
            if (!signedJWT.verify(verifier)) {
                
                throw new RuntimeException("Invalid JWT signature");
            }

            return signedJWT.getJWTClaimsSet();
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException("Error parsing or verifying JWT token", e);
        }
    }

}
