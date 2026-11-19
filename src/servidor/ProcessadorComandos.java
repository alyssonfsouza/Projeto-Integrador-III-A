package servidor;

import modelo.AulaHorario;
import java.util.List;

public class ProcessadorComandos {
    private final RepositorioHorarios repo;

    public ProcessadorComandos(RepositorioHorarios repo) {
        this.repo = repo;
    }

    public String processar(String comandoBruto, boolean autenticado) {
        if (comandoBruto == null || comandoBruto.trim().isEmpty()) {
            return "ERRO: Comando vazio.";
        }

        if (!autenticado) {
            return "403 FORBIDDEN: Acesso negado. Autentique-se primeiro usando: AUTH <usuario> <senha>";
        }

        String[] tokens = comandoBruto.trim().split("\s+", 2);
        String acao = tokens[0].toUpperCase();
        String param = tokens.length > 1 ? tokens[1].trim() : "";

        switch (acao) {
            case "LISTAR":
                return formatarLista(repo.listarTodas(), "GRADE COMPLETA - COLEGIO SANTO AGOSTINHO");

            case "BUSCAR":
                if (param.isEmpty()) return "ERRO: Informe o nome do professor. Ex: BUSCAR CARLOS";
                return formatarLista(repo.buscarPorProfessor(param), "HORARIOS DO DOCENTE: " + param.toUpperCase());

            case "TURMA":
                if (param.isEmpty()) return "ERRO: Informe a turma. Ex: TURMA 6º ANO A";
                return formatarLista(repo.buscarPorTurma(param), "GRADE DA TURMA: " + param.toUpperCase());

            case "DIA":
                if (param.isEmpty()) return "ERRO: Informe o dia da semana. Ex: DIA SEGUNDA";
                return formatarLista(repo.buscarPorDia(param), "DOCENTES ALOCADOS EM: " + param.toUpperCase());

            case "DETALHAR":
                if (param.isEmpty()) return "ERRO: Informe o ID. Ex: DETALHAR 1";
                try {
                    int id = Integer.parseInt(param);
                    AulaHorario a = repo.buscarPorId(id);
                    if (a == null) return "ERRO: Alocacao ID " + id + " nao encontrada.";
                    return "OK\n" +
                           "ID Alocacao: " + a.getId() + "\n" +
                           "Professor:   " + a.getProfessor() + "\n" +
                           "Disciplina:  " + a.getDisciplina() + "\n" +
                           "Dia/Horario: " + a.getDiaSemana() + " as " + a.getHorario() + "\n" +
                           "Turma:       " + a.getTurma();
                } catch (NumberFormatException e) {
                    return "ERRO: O ID deve ser numerico.";
                }

            case "AJUDA":
                return "OK\nComandos disponiveis (Sessao Autenticada):\n" +
                       "* LISTAR              - Exibe todas as alocacoes da grade\n" +
                       "* BUSCAR <professor>  - Filtra aulas por nome do docente\n" +
                       "* TURMA <codigo>      - Lista horarios e professores de uma turma (Ex: TURMA 6 A)\n" +
                       "* DIA <dia_semana>    - Lista professores dando aula no dia (Ex: DIA SEGUNDA)\n" +
                       "* DETALHAR <id>       - Exibe ficha detalhada da alocacao\n" +
                       "* SAIR                - Encerra a sessao";

            default:
                return "ERRO: Comando desconhecido. Digite AJUDA para ver as opcoes.";
        }
    }

    private String formatarLista(List<AulaHorario> lista, String titulo) {
        if (lista.isEmpty()) return "OK\nNenhum registro encontrado para este filtro.";
        StringBuilder sb = new StringBuilder("OK\n=== " + titulo + " ===\n");
        for (AulaHorario a : lista) {
            sb.append(a.toString()).append("\n");
        }
        return sb.toString().trim();
    }
}
