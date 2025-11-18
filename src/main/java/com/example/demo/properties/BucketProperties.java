package com.example.demo.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bucket")
public class BucketProperties {

	private Interno interno = new Interno();
	private Visitante visitante = new Visitante();
	private Advogado advogado = new Advogado();

	public Interno getInterno() {
		return interno;
	}

	public Visitante getVisitante() {
		return visitante;
	}

	public Advogado getAdvogado() {
		return advogado;
	}

	// ---------------------- INTERNOS ----------------------

	public static class Interno {
		private Foto foto = new Foto();

		public Foto getFoto() {
			return foto;
		}

		public static class Foto {
			private String principal;
			private String sinais;
			private String endereco;
			private String perfil;
			private String historico;

			public String getPrincipal() {
				return principal;
			}

			public void setPrincipal(String principal) {
				this.principal = principal;
			}

			public String getSinais() {
				return sinais;
			}

			public void setSinais(String sinais) {
				this.sinais = sinais;
			}

			public String getEndereco() {
				return endereco;
			}

			public void setEndereco(String endereco) {
				this.endereco = endereco;
			}

			public String getPerfil() {
				return perfil;
			}

			public void setPerfil(String perfil) {
				this.perfil = perfil;
			}

			public String getHistorico() {
				return historico;
			}

			public void setHistorico(String historico) {
				this.historico = historico;
			}
		}
	}

	// ---------------------- VISITANTE ----------------------

	public static class Visitante {
		private Foto foto = new Foto();
		private Documentos documentos = new Documentos();

		public Foto getFoto() {
			return foto;
		}

		public Documentos getDocumentos() {
			return documentos;
		}

		public static class Foto {
			private String principal;

			public String getPrincipal() {
				return principal;
			}

			public void setPrincipal(String principal) {
				this.principal = principal;
			}
		}

		public static class Documentos {
			private String principal;

			public String getPrincipal() {
				return principal;
			}

			public void setPrincipal(String principal) {
				this.principal = principal;
			}
		}
	}

	// ---------------------- ADVOGADO ----------------------

	public static class Advogado {
		private Foto foto = new Foto();
		private Documentos documentos = new Documentos();

		public Foto getFoto() {
			return foto;
		}

		public Documentos getDocumentos() {
			return documentos;
		}

		public static class Foto {
			private String principal;

			public String getPrincipal() {
				return principal;
			}

			public void setPrincipal(String principal) {
				this.principal = principal;
			}
		}

		public static class Documentos {
			private String principal;

			public String getPrincipal() {
				return principal;
			}

			public void setPrincipal(String principal) {
				this.principal = principal;
			}
		}
	}
}
