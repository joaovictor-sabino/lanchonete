package com.database.restaurante.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.text.Normalizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArmazenamentoImagemService {

    private final Path pasta;

    public ArmazenamentoImagemService(@Value("${app.uploads.dir}") String dir) throws IOException {
        this.pasta = Path.of(dir, "produtos").toAbsolutePath().normalize();
        Files.createDirectories(this.pasta);
    }

    public Path getPasta() { return pasta; }

    /** Salva a imagem e devolve o nome gerado. Lança IllegalArgumentException se for inválida. */
    public String salvar(MultipartFile arquivo, String nomeBase) {
        try {
            byte[] dados = arquivo.getBytes();
            String ext = detectarExtensao(dados);
            if (ext == null) {
                throw new IllegalArgumentException("Arquivo inválido. Envie uma imagem JPG, PNG ou WebP.");
            }
            String nome = slug(nomeBase) + "." + ext;
            Files.write(pasta.resolve(nome), dados);   // se já existir, substitui
            return nome;
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível salvar a imagem.", e);
        }
    }

    // "X-Burguer Especial" -> "x-burguer-especial"
    private String slug(String texto) {
        String s = Normalizer.normalize(texto == null ? "" : texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");
        return s.isEmpty() ? "produto" : s;
    }

    /** Apaga o arquivo (se existir). Nunca sai da pasta de uploads. */
    public void remover(String nome) {
        if (nome == null || nome.isBlank()) return;
        try {
            Path alvo = pasta.resolve(nome).normalize();
            if (alvo.startsWith(pasta)) {
                Files.deleteIfExists(alvo);
            }
        } catch (IOException | RuntimeException e) {
            // se não conseguir apagar, o arquivo só fica sobrando; não afeta o sistema
        }
    }

    // descobre o tipo pelos primeiros bytes, ignorando o nome/extensão enviados
    private String detectarExtensao(byte[] d) {
        if (d.length < 12) return null;
        if ((d[0] & 0xFF) == 0xFF && (d[1] & 0xFF) == 0xD8 && (d[2] & 0xFF) == 0xFF) return "jpg";
        if ((d[0] & 0xFF) == 0x89 && d[1] == 'P' && d[2] == 'N' && d[3] == 'G') return "png";
        if (d[0] == 'R' && d[1] == 'I' && d[2] == 'F' && d[3] == 'F'
                && d[8] == 'W' && d[9] == 'E' && d[10] == 'B' && d[11] == 'P') return "webp";
        return null;
    }
}