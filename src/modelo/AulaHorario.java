package modelo;

public class AulaHorario {
    private int id;
    private String professor;
    private String disciplina;
    private String diaSemana;
    private String horario;
    private String turma;

    public AulaHorario(int id, String professor, String disciplina, String diaSemana, String horario, String turma) {
        this.id = id;
        this.professor = professor;
        this.disciplina = disciplina;
        this.diaSemana = diaSemana;
        this.horario = horario;
        this.turma = turma;
    }

    public int getId() { return id; }
    public String getProfessor() { return professor; }
    public String getDisciplina() { return disciplina; }
    public String getDiaSemana() { return diaSemana; }
    public String getHorario() { return horario; }
    public String getTurma() { return turma; }

    @Override
    public String toString() {
        return String.format("[%02d] %-13s | %-13s | %-5s | Turma: %-9s | %s",
                id, professor, diaSemana, horario, turma, disciplina);
    }
}
