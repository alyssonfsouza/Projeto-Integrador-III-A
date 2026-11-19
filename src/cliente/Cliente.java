package cliente;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Cliente {
    private static final String HOST = "127.0.0.1";
    private static final int PORTA = 5000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORTA);
             BufferedReader leitor = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter escritor = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             Scanner teclado = new Scanner(System.in)) {

            System.out.println("=========================================================");
            System.out.println("  COLÉGIO SANTO AGOSTINHO - CONSULTA DE GRADE HORÁRIA   ");
            System.out.println("=========================================================");

            boolean logado = false;
            while (!logado) {
                System.out.print("\nUsuario da Coordenacao: ");
                String user = teclado.nextLine().trim();
                System.out.print("Senha de Acesso: ");
                String pass = teclado.nextLine().trim();

                escritor.println("AUTH " + user + " " + pass);

                String respAuth = leitor.readLine();
                leitor.readLine(); // consome <<<FIM_MSG>>>

                if (respAuth != null && respAuth.startsWith("AUTH_OK")) {
                    System.out.println("\n[SUCESSO] " + respAuth.substring(8));
                    logado = true;
                } else {
                    System.out.println("\n[ACESSO NEGADO] " + (respAuth != null ? respAuth.substring(10) : "Erro de conexao."));
                    if (respAuth != null && respAuth.contains("BLOQUEADO")) {
                        return;
                    }
                }
            }

            System.out.println("\nComandos: LISTAR | BUSCAR <prof> | TURMA <cod> | DIA <dia> | DETALHAR <id> | AJUDA | SAIR");

            while (true) {
                System.out.print("> ");
                String comando = teclado.nextLine().trim();
                if (comando.isEmpty()) continue;

                escritor.println(comando);
                if (comando.equalsIgnoreCase("SAIR")) break;

                String linhaResposta;
                while ((linhaResposta = leitor.readLine()) != null) {
                    if (linhaResposta.equals("<<<FIM_MSG>>>")) break;
                    System.out.println(linhaResposta);
                }
            }

        } catch (IOException e) {
            System.err.println("Erro de rede: " + e.getMessage());
        }
    }
}
