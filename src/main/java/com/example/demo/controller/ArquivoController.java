package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.MinioService;

@RestController
@RequestMapping("/arquivos")
public class ArquivoController {

	private final MinioService minioService;

	public ArquivoController(MinioService minioService) {
		this.minioService = minioService;
	}

	@PostMapping("/upload")
	public ResponseEntity<String> upload(MultipartFile file) throws Exception {
		String nome = minioService.uploadFile(file);
		return ResponseEntity.ok("Arquivo enviado: " + nome);
	}

	@GetMapping("/download/{nome}")
	public ResponseEntity<byte[]> download(@PathVariable String nome) throws Exception {
		byte[] bytes = minioService.downloadFile(nome);
		return ResponseEntity.ok(bytes);
	}
}
