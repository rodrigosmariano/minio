package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/teste")
public class TesteController {

	@GetMapping
	public ResponseEntity<?> downloadFotoSinaisParticulares(@AuthenticationPrincipal Jwt jwt) throws Exception {
		String usuario = jwt.getSubject(); 
		String id = jwt.getClaimAsString("id"); 
		String matricula = jwt.getClaimAsString("matricula");
		System.out.println("Usuario: " + usuario);
		System.out.println("ID: " + id);
		System.out.println("Matricula: " + matricula);
		return ResponseEntity.ok("ok");
	}
}
