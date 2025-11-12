package com.example.demo.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
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
