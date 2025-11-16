package com.example.demo.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;

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
import net.coobird.thumbnailator.Thumbnails;

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

	public String uploadFotoSinalParticular(MultipartFile file, String idSinal) throws Exception {
		String fileName = idSinal + ".jpg";

		try (InputStream inputStream = file.getInputStream()) {
			minioClient.putObject(PutObjectArgs.builder().bucket("sinais-particulares").object(fileName)
					.stream(inputStream, file.getSize(), -1).contentType(file.getContentType()).build());
		}
		return fileName;
	}

	public String uploadFotoHistoricoEndereco(MultipartFile file, String idHistoricoEndereco) throws Exception {
		String fileName = idHistoricoEndereco + ".jpg";

		try (InputStream inputStream = file.getInputStream()) {
			minioClient.putObject(PutObjectArgs.builder().bucket("historico-endereco-interno").object(fileName)
					.stream(inputStream, file.getSize(), -1).contentType(file.getContentType()).build());
		}
		return fileName;
	}

	public String uploadFotoPrincipal(MultipartFile file, String idInterno, String rgi) throws Exception {

		String fileName = idInterno + ".jpg";
		String bucketAtual = "fotos-internos";
		String bucketHistorico = "historico-fotos-internos";

		// 0. Converte MultipartFile → byte[]
		byte[] bytesOriginais = file.getBytes();

		// 1. Redimensionar para máximo de 800px
		byte[] resizedBytes = criaFoto(bytesOriginais, 800);

		// 2. Aplicar tarja
		byte[] finalBytes = colocarTarja(resizedBytes, rgi);

		// 3. Verificar se existe foto atual
		boolean existe = false;
		try {
			minioClient.statObject(StatObjectArgs.builder().bucket(bucketAtual).object(fileName).build());
			existe = true;
		} catch (Exception ignore) {
		}

		// 4. Se existe, mover para histórico com nome aleatório
		if (existe) {
			String novoNomeHistorico = idInterno + "_" + new SimpleDateFormat("ddMMyyyHHmmss").format(new Date())
					+ ".jpg";

			minioClient.copyObject(CopyObjectArgs.builder().bucket(bucketHistorico).object(novoNomeHistorico)
					.source(CopySource.builder().bucket(bucketAtual).object(fileName).build()).build());

			minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketAtual).object(fileName).build());
		}

		// 5. Fazer upload da foto final (JPG com tarja)
		try (InputStream inputStream = new ByteArrayInputStream(finalBytes)) {
			minioClient.putObject(PutObjectArgs.builder().bucket(bucketAtual).object(fileName).contentType("image/jpeg")
					.stream(inputStream, finalBytes.length, -1).build());
		}

		return fileName;
	}

	public byte[] downloadFotoHistoricoInterno(String fileName, String matricula) throws Exception {
		return addWatermarkRepeated(getFile(fileName, "historico-fotos-internos"), matricula);
	}
	
	public byte[] downloadFotoHistoricoEndereco(String fileName, String matricula) throws Exception {
		return addWatermarkRepeated(getFile(fileName, "historico-endereco-interno"), matricula);
	}

	public byte[] downloadFotoSinaisParticulares(String fileName, String matricula) throws Exception {
		return addWatermarkRepeated(getFile(fileName, "sinais-particulares"), matricula);
	}

	public byte[] downloadFotoPerfil(String fileName, String matricula) throws Exception {
		return addWatermarkRepeated(getFile(fileName, "foto-perfil"), matricula);
	}

	public byte[] downloadFotoPrincipalInterno(String fileName, String matricula) throws Exception {
		return addWatermarkRepeated(getFile(fileName, "fotos-internos"), matricula);
	}

	public static String decrypt(String encrypted) {
		try {
			String KEY = "1234567890123456";
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY.getBytes(), "AES"));
			byte[] decoded = Base64.getUrlDecoder().decode(encrypted);
			return new String(cipher.doFinal(decoded));
		} catch (Exception e) {
			return null;
		}
	}

	private byte[] addWatermarkRepeated(byte[] originalBytes, String text) throws IOException {
		text = decrypt(text);

		if (originalBytes == null || text == null || text.isEmpty()) {
			return null;
		}

		BufferedImage original = ImageIO.read(new ByteArrayInputStream(originalBytes));

		int width = original.getWidth();
		int height = original.getHeight();

		BufferedImage watermarked = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = watermarked.createGraphics();
		g2d.drawImage(original, 0, 0, null);

		// 🟦 1) Fonte proporcional ao tamanho da imagem
		// - imagens pequenas → fonte menor
		// - imagens grandes → fonte maior
		int fontSize = Math.max(12, Math.min(width, height) / 20); // variação inteligente
		g2d.setFont(new Font("Arial", Font.BOLD, fontSize));

		// Cor com transparência
		g2d.setColor(new Color(0, 0, 0, 35));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		FontMetrics fm = g2d.getFontMetrics();
		int textWidth = fm.stringWidth(text);
		int textHeight = fm.getHeight();

		// 🟦 2) Rotação diagonal
		g2d.rotate(Math.toRadians(-35), width / 2.0, height / 2.0);

		// 🟦 3) Espaçamento proporcional
		// pequenas imagens → espaçamento pequeno
		// grandes imagens → espaçamento maior
		int horizontalSpacing = textWidth + (width / 15);
		int verticalSpacing = textHeight + (height / 15);

		// 🟦 Ajuste mínimo para evitar espaços exagerados
		horizontalSpacing = Math.max(textWidth + 20, horizontalSpacing);
		verticalSpacing = Math.max(textHeight + 20, verticalSpacing);

		// 🟦 4) Preenche toda a imagem com o texto repetido
		for (int y = -height; y < height * 2; y += verticalSpacing) {
			for (int x = -width; x < width * 2; x += horizontalSpacing) {
				g2d.drawString(text, x + y, y);
			}
		}

		g2d.dispose();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(watermarked, "jpg", baos);
		return baos.toByteArray();
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

	private byte[] getFile(String fileName, String bucket) throws Exception {
		try (GetObjectResponse response = minioClient
				.getObject(GetObjectArgs.builder().bucket(bucket).object(fileName).build())) {
			return response.readAllBytes();
		} catch (Exception e) {
			return null;
		}
	}

	private byte[] criaFoto(byte[] fotoEmBytes, int tamanhoMax) throws IOException {

		BufferedImage image = ImageIO.read(new ByteArrayInputStream(fotoEmBytes));

		// Se já for menor, não precisa redimensionar
		if (image.getWidth() <= tamanhoMax && image.getHeight() <= tamanhoMax) {
			return fotoEmBytes;
		}

		// Redimensionar usando Thumbnailator (mantém proporção)
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		Thumbnails.of(image).size(tamanhoMax, tamanhoMax).outputFormat("jpg").outputQuality(1.0).toOutputStream(baos);

		return baos.toByteArray();
	}

	public byte[] colocarTarja(byte[] fotoBytes, String rgi) {
		try {
			BufferedImage pngImage = ImageIO.read(new ByteArrayInputStream(fotoBytes));

			BufferedImage newImage = new BufferedImage(pngImage.getWidth(), pngImage.getHeight(),
					BufferedImage.TYPE_INT_RGB);

			Graphics2D g2d = newImage.createGraphics();

			// fundo preto
			g2d.drawImage(pngImage, 0, 0, Color.BLACK, null);

			// TARJA
			int tarjaAltura = (int) (newImage.getHeight() * 0.07);
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, newImage.getHeight() - tarjaAltura, newImage.getWidth(), tarjaAltura);
			g2d.setColor(Color.YELLOW);

			// Texto
			int fonte = (int) (newImage.getHeight() * 0.045);
			g2d.setFont(new Font("Arial", Font.BOLD, fonte));

			String texto = "PPDF  Pront. " + rgi + " " + new SimpleDateFormat("dd/MM/yyyy").format(new Date());
			int x = (int) (newImage.getHeight() * 0.10);
			int y = newImage.getHeight() - (tarjaAltura / 2) + 8;

			g2d.drawString(texto, x, y);

			// LOGO
//			BufferedImage logo = ImageIO.read(new File("c:/imagens_servidores/BrasaoPoliciaPenalPequeno.png"));
			byte[] bytes = getFile("BrasaoPoliciaPenalPequeno.png", "sistema");
			BufferedImage logo = ImageIO.read(new ByteArrayInputStream(bytes));

			int logoWidth = (int) (newImage.getHeight() * 0.145);
			int logoHeight = (int) (newImage.getHeight() * 0.15);

			Image logoRedimensionado = logo.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
			g2d.drawImage(logoRedimensionado, x + g2d.getFontMetrics().stringWidth(texto) + 9,
					newImage.getHeight() - logoHeight, null);

			g2d.dispose();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(newImage, "jpg", baos);
			return baos.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao adicionar tarja à foto.");
		}
	}

}
