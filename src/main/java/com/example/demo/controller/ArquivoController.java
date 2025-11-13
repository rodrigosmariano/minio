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
	
	@PostMapping("/upload-foto-principal")
	public ResponseEntity<String> uploadFotoPrincipal(MultipartFile file, String idInterno) throws Exception {
		System.out.println("ID Interno: " + idInterno);
		String nome = minioService.uploadFotoPrincipal(file, idInterno);
		System.out.println("Nome do arquivo enviado: " + nome);
		return ResponseEntity.ok("Arquivo enviado: " + nome);
	}

	@GetMapping("/download-foto-historico-interno/{nome}")
	public ResponseEntity<byte[]> downloadFotoHistoricoInterno(@PathVariable String nome) throws Exception {
		byte[] bytes = minioService.downloadFotoHistoricoInterno(nome);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG); // ou MediaType.IMAGE_PNG conforme o caso
		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}
	
	@GetMapping("/download-foto-principal-interno/{nome}")
	public ResponseEntity<byte[]> downloadFotoPrincipalInterno(@PathVariable String nome) throws Exception {
		byte[] bytes = minioService.downloadFotoPrincipalInterno(nome);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG); // ou MediaType.IMAGE_PNG conforme o caso
		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}

	@GetMapping("/listar-historico-fotos-interno/{idInterno}")
	public ResponseEntity<List<String>> listarHistoricoFotosInterno(@PathVariable Integer idInterno) throws Exception {
		System.out.println(idInterno);
		List<String> arquivos = minioService.listarArquivosHistoricoInterno(idInterno);
		return ResponseEntity.ok(arquivos);
	}

	@GetMapping("/listar-foto-principal-interno/{idInterno}")
	public ResponseEntity<String> listarFotoPrincipalInterno(@PathVariable Integer idInterno) throws Exception {
		System.out.println(idInterno);
		String fileName = minioService.listarFotoPrincipalInterno(idInterno);
		return ResponseEntity.ok(fileName);
	}
}
