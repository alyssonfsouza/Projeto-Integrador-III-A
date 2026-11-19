package servidor;

import modelo.AulaHorario;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RepositorioHorarios {
    private final List<AulaHorario> aulas = new ArrayList<>();

    public RepositorioHorarios(String caminhoCsv) {
        carregarDados(caminhoCsv);
    }

    private void carregarDados(String caminho) {
        File arquivo = new File(caminho);
        if (!arquivo.exists()) {
            System.err.println("[AVISO] Arquivo CSV nao encontrado: " + caminho);
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#")) continue;

                String[] p = linha.split(";");
                if (p.length >= 6) {
                    aulas.add(new AulaHorario(
                        Integer.parseInt(p[0].trim()),
                        p[1].trim(),
                        p[2].trim(),
                        p[3].trim(),
                        p[4].trim(),
                        p[5].trim()
                    ));
                }
            }
            System.out.println("[REPOSITORIO] " + aulas.size() + " alocacoes horarias carregadas.");
        } catch (Exception e) {
            System.err.println("[ERRO CARGA CSV] " + e.getMessage());
        }
    }

    public List<AulaHorario> listarTodas() {
        return Collections.unmodifiableList(aulas);
    }

    public List<AulaHorario> buscarPorProfessor(String nome) {
        String termo = nome.toUpperCase();
        List<AulaHorario> res = new ArrayList<>();
        for (AulaHorario a : aulas) {
            if (a.getProfessor().toUpperCase().contains(termo)) res.add(a);
        }
        return res;
    }

    public List<AulaHorario> buscarPorTurma(String turma) {
        String termo = turma.toUpperCase();
        List<AulaHorario> res = new ArrayList<>();
        for (AulaHorario a : aulas) {
            if (a.getTurma().toUpperCase().contains(termo)) res.add(a);
        }
        return res;
    }

    public List<AulaHorario> buscarPorDia(String dia) {
        String termo = dia.toUpperCase();
        List<AulaHorario> res = new ArrayList<>();
        for (AulaHorario a : aulas) {
            if (a.getDiaSemana().toUpperCase().contains(termo)) res.add(a);
        }
        return res;
    }

    public AulaHorario buscarPorId(int id) {
        for (AulaHorario a : aulas) {
            if (a.getId() == id) return a;
        }
        return null;
    }
}
