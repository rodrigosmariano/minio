package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
	public ResponseEntity<String> upload(MultipartFile file, String pasta) throws Exception {
		String nome = minioService.uploadFile(file, pasta);
		return ResponseEntity.ok("Arquivo enviado: " + nome);
	}

	@GetMapping("/download/{pasta}/{nome}")
	public ResponseEntity<byte[]> download(@PathVariable String pasta, @PathVariable String nome) throws Exception {
		byte[] bytes = minioService.downloadFile(pasta, nome);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG); // ou MediaType.IMAGE_PNG conforme o caso
		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}

	@GetMapping("/listar/{pasta}/{prefixo}")
	public ResponseEntity<List<String>> listarArquivosPorPrefixo(@PathVariable String pasta,
			@PathVariable String prefixo) throws Exception {
		System.out.println(pasta + "/" + prefixo);
		List<String> arquivos = minioService.listarArquivosPorPrefixo(prefixo, pasta);
		return ResponseEntity.ok(arquivos);
	}
}
