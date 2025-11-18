package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.InternoService;

@RestController
@RequestMapping("/interno")
public class InternoController {

	@Autowired
	private InternoService internoService;

	@PostMapping("/upload")
	public ResponseEntity<String> upload(@RequestParam MultipartFile file, String pasta) throws Exception {
		return ResponseEntity.ok("Arquivo enviado: " + internoService.uploadFile(file, pasta));
	}

	@PostMapping("/upload-foto-principal")
	public ResponseEntity<String> uploadFotoPrincipal(@RequestParam MultipartFile file, String idInterno, String rgi)
			throws Exception {
		return ResponseEntity.ok("Arquivo enviado: " + internoService.uploadFotoPrincipal(file, idInterno, rgi));
	}

	@PostMapping("/upload-foto-sinais-particulares")
	public ResponseEntity<String> uploadFotoSinaisParticulares(@RequestParam MultipartFile file, String idSinal)
			throws Exception {
		return ResponseEntity.ok("Arquivo enviado: " + internoService.uploadFotoSinalParticular(file, idSinal));
	}

	@PostMapping("/upload-foto-historico-endereco")
	public ResponseEntity<String> uploadFotoHistoricoEndereco(@RequestParam MultipartFile file,
			String idHistoricoEndereco) throws Exception {
		return ResponseEntity
				.ok("Arquivo enviado: " + internoService.uploadFotoHistoricoEndereco(file, idHistoricoEndereco));
	}

	@GetMapping("/download-foto-sinais-particulares")
	public ResponseEntity<byte[]> downloadFotoSinaisParticulares(String fileName, String matricula) throws Exception {
		return buildImageResponse(internoService.downloadFotoSinaisParticulares(fileName, matricula));
	}

	@GetMapping("/download-foto-perfil")
	public ResponseEntity<byte[]> downloadFotoPerfil(String fileName, String matricula) throws Exception {
		return buildImageResponse(internoService.downloadFotoPerfil(fileName, matricula));
	}

	@GetMapping("/download-foto-historico-interno")
	public ResponseEntity<byte[]> downloadFotoHistoricoInterno(String fileName, String matricula) throws Exception {
		return buildImageResponse(internoService.downloadFotoHistoricoInterno(fileName, matricula));
	}

	@GetMapping("/download-foto-historico-endereco")
	public ResponseEntity<byte[]> downloadFotoHistoricoEndereco(String fileName, String matricula) throws Exception {
		return buildImageResponse(internoService.downloadFotoHistoricoEndereco(fileName, matricula));
	}

	@GetMapping("/download-foto-principal-interno")
	public ResponseEntity<byte[]> downloadFotoPrincipalInterno(String fileName, String matricula) throws Exception {
		return buildImageResponse(internoService.downloadFotoPrincipalInterno(fileName, matricula));
	}

	@GetMapping("/listar-historico-fotos-interno/{idInterno}")
	public ResponseEntity<List<String>> listarHistoricoFotosInterno(@PathVariable Integer idInterno) throws Exception {
		return ResponseEntity.ok(internoService.listarArquivosHistoricoInterno(idInterno));
	}

	@GetMapping("/listar-foto-principal-interno/{idInterno}")
	public ResponseEntity<String> listarFotoPrincipalInterno(@PathVariable Integer idInterno) throws Exception {
		return ResponseEntity.ok(internoService.listarFotoPrincipalInterno(idInterno));
	}

	private ResponseEntity<byte[]> buildImageResponse(byte[] bytes) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}
}
