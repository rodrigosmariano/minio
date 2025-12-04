package com.example.demo.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface InternoService {

	String uploadFile(MultipartFile file, String pasta) throws Exception;

	String uploadFotoSinalParticular(MultipartFile file, String idSinal) throws Exception;

	String uploadFotoHistoricoEndereco(MultipartFile file, String idHistoricoEndereco) throws Exception;

	String uploadFotoPrincipal(MultipartFile file, String idInterno, String rgi) throws Exception;

	byte[] downloadFotoHistoricoInterno(String fileName, String matricula) throws Exception;

	byte[] downloadFotoHistoricoEndereco(String fileName, String matricula) throws Exception;

	byte[] downloadFotoSinaisParticulares(String fileName, String matricula) throws Exception;

	byte[] downloadFotoPerfil(String fileName, String matricula) throws Exception;

	byte[] downloadFotoPrincipalInterno(String fileName, String matricula) throws Exception;

	String listarFotoPrincipalInterno(Integer idInterno) throws Exception;

	List<String> listarArquivosHistoricoInterno(Integer idInterno) throws Exception;

	byte[] downloadFotoPrincipalInternoForagidos(String fileName) throws Exception;

}
