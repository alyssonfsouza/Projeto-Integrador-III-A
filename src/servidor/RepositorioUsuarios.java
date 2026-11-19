package servidor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RepositorioUsuarios {
    private final Map<String, String> credenciaisHash = new HashMap<>();
    private final Map<String, Integer> tentativasFalhas = new HashMap<>();
    private static final int MAX_TENTATIVAS = 3;

    public RepositorioUsuarios(String caminhoCsv) {
        carregarUsuarios(caminhoCsv);
    }

    private void carregarUsuarios(String caminho) {
        File arquivo = new File(caminho);
        if (!arquivo.exists()) {
            System.err.println("[AVISO] Arquivo de usuarios nao encontrado: " + caminho);
            return;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#")) continue;
                String[] partes = linha.split(";");
                if (partes.length >= 2) {
                    credenciaisHash.put(partes[0].trim().toLowerCase(), partes[1].trim());
                }
            }
            System.out.println("[SEGURANCA] " + credenciaisHash.size() + " credenciais institucional(is) carregadas.");
        } catch (Exception e) {
            System.err.println("[ERRO USUARIOS] " + e.getMessage());
        }
    }

    public String autenticar(String usuario, String senhaPura, String ipCliente) {
        String chaveBloqueio = ipCliente + "_" + usuario.toLowerCase();
        int falhas = tentativasFalhas.getOrDefault(chaveBloqueio, 0);

        if (falhas >= MAX_TENTATIVAS) {
            return "BLOQUEADO: Excesso de tentativas falhas para este IP/Usuario. Acesso suspenso.";
        }

        String hashEsperado = credenciaisHash.get(usuario.toLowerCase());
        if (hashEsperado != null && hashEsperado.equals(SegurancaUtil.gerarHashSHA256(senhaPura))) {
            tentativasFalhas.remove(chaveBloqueio);
            return "OK";
        } else {
            falhas++;
            tentativasFalhas.put(chaveBloqueio, falhas);
            int restantes = MAX_TENTATIVAS - falhas;
            if (restantes <= 0) {
                return "BLOQUEADO: Limite de 3 tentativas atingido. Conexao bloqueada por seguranca.";
            }
            return "NEGADO: Usuario ou senha invalidos. Tentativas restantes: " + restantes;
        }
    }
}
