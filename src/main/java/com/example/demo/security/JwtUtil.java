package com.example.demo.security;

import java.util.Date;
import java.util.Base64;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

	// Chave segura de 256 bits gerada automaticamente
	private static final String SECRET = "78qSH2fQCYpC2rfbmV8CtIoVP1iX9tDzJR8ClE5pcBE=";

	private static String gerarChaveSegura() {
		byte[] key = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
		return Base64.getEncoder().encodeToString(key);
	}

	public static String getSecret() {
		return SECRET;
	}

	public static String gerarToken(String usuario, Integer id, String matricula) {
		return Jwts.builder().setSubject(usuario).claim("id", id).claim("matricula", matricula).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora
				.signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET))).compact();
	}
}
