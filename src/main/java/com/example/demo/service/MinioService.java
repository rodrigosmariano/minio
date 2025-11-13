package com.example.demo.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.messages.Item;

@Service
public class MinioService {

	@Autowired
	private MinioClient minioClient;

	public String uploadFile(MultipartFile file, String pasta) throws Exception {
		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

		try (InputStream inputStream = file.getInputStream()) {
			minioClient.putObject(PutObjectArgs.builder().bucket(pasta).object(fileName)
					.stream(inputStream, file.getSize(), -1).contentType(file.getContentType()).build());
		}

		return fileName;
	}

	public String uploadFotoPrincipal(MultipartFile file, String idInterno) throws Exception {

	    String fileName = idInterno + ".jpg";
	    String bucketAtual = "fotos-internos";
	    String bucketHistorico = "historico-fotos-internos";

	    // 1. Verificar se existe a foto atual
	    boolean existe = false;
	    try {
	        minioClient.statObject(
	                StatObjectArgs.builder()
	                        .bucket(bucketAtual)
	                        .object(fileName)
	                        .build()
	        );
	        existe = true;
	    } catch (Exception e) {
	        existe = false; // não existe, ignorar erro
	    }

	    // 2. Se existe → mover para histórico com nome renomeado
	    if (existe) {

	        // Nome novo no histórico
	        String novoNomeHistorico = idInterno + "_" + UUID.randomUUID() + ".jpg";

	        // COPIAR para o histórico com NOME NOVO
	        minioClient.copyObject(
	                CopyObjectArgs.builder()
	                        .bucket(bucketHistorico)
	                        .object(novoNomeHistorico)
	                        .source(
	                                CopySource.builder()
	                                        .bucket(bucketAtual)
	                                        .object(fileName)
	                                        .build()
	                        )
	                        .build()
	        );

	        // DELETAR do bucket atual
	        minioClient.removeObject(
	                RemoveObjectArgs.builder()
	                        .bucket(bucketAtual)
	                        .object(fileName)
	                        .build()
	        );
	    }

	    // 3. Salvar o novo arquivo no bucket fotos-internos
	    try (InputStream inputStream = file.getInputStream()) {
	        minioClient.putObject(
	                PutObjectArgs.builder()
	                        .bucket(bucketAtual)
	                        .object(fileName)
	                        .contentType(file.getContentType())
	                        .stream(inputStream, file.getSize(), -1)
	                        .build()
	        );
	    }

	    return fileName;
	}

	public byte[] downloadFotoHistoricoInterno(String fileName) throws Exception {
		try (GetObjectResponse response = minioClient
				.getObject(GetObjectArgs.builder().bucket("historico-fotos-internos").object(fileName).build())) {
			return response.readAllBytes();
		} catch (Exception e) {
			return null;
		}
	}

	public byte[] downloadFotoPrincipalInterno(String fileName) throws Exception {
		try (GetObjectResponse response = minioClient
				.getObject(GetObjectArgs.builder().bucket("fotos-internos").object(fileName).build())) {
			return response.readAllBytes();
		} catch (Exception e) {
			return null;
		}
	}

	public String listarFotoPrincipalInterno(Integer idInterno) throws Exception {
		List<String> nomesArquivos = new ArrayList<>();

		Iterable<Result<Item>> resultados = minioClient.listObjects(
				ListObjectsArgs.builder().bucket("fotos-internos").prefix(idInterno + ".").recursive(false).build());

		for (Result<Item> resultado : resultados) {
			Item item = resultado.get();
			nomesArquivos.add(item.objectName());
			System.out.println(item.objectName());
		}

		return nomesArquivos.isEmpty() ? null : nomesArquivos.get(0);
	}

	public List<String> listarArquivosHistoricoInterno(Integer idInterno) throws Exception {
		List<String> nomesArquivos = new ArrayList<>();

		Iterable<Result<Item>> resultados = minioClient.listObjects(ListObjectsArgs.builder()
				.bucket("historico-fotos-internos").prefix(idInterno + "_").recursive(false).build());

		for (Result<Item> resultado : resultados) {
			Item item = resultado.get();
			nomesArquivos.add(item.objectName());
			System.out.println(item.objectName());
		}

		return nomesArquivos;
	}

}
