package servidor;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Servidor {
    private static final int PORTA = 5000;
    private static final String CSV_HORARIOS = "dados/professores.csv";
    private static final String CSV_USUARIOS = "dados/usuarios.csv";

    public static void main(String[] args) {
        RepositorioHorarios repoHorarios = new RepositorioHorarios(CSV_HORARIOS);
        RepositorioUsuarios repoUsuarios = new RepositorioUsuarios(CSV_USUARIOS);
        ProcessadorComandos processador = new ProcessadorComandos(repoHorarios);

        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("Servidor CSA ativo na porta " + PORTA + " (Autenticacao SHA-256 exigida)...");

            while (true) {
                Socket cliente = serverSocket.accept();
                System.out.println("[+] Nova conexao recebida: " + cliente.getRemoteSocketAddress());
                new Thread(() -> atenderCliente(cliente, processador, repoUsuarios)).start();
            }
        } catch (IOException e) {
            System.err.println("Erro critico no servidor: " + e.getMessage());
        }
    }

    private static void atenderCliente(Socket socket, ProcessadorComandos proc, RepositorioUsuarios repoUsers) {
        String ipCliente = socket.getInetAddress().getHostAddress();
        String clientId = socket.getRemoteSocketAddress().toString();
        boolean sessaoAutenticada = false;
        String usuarioLogado = "ANONIMO";

        try (
            BufferedReader leitor = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter escritor = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)
        ) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                linha = linha.trim();
                if (linha.equalsIgnoreCase("SAIR")) {
                    escritor.println("OK Sessao encerrada.");
                    break;
                }

                if (linha.toUpperCase().startsWith("AUTH ")) {
                    String[] partes = linha.split("\s+");
                    if (partes.length == 3) {
                        String user = partes[1];
                        String pass = partes[2];
                        String statusAuth = repoUsers.autenticar(user, pass, ipCliente);

                        if (statusAuth.equals("OK")) {
                            sessaoAutenticada = true;
                            usuarioLogado = user.toUpperCase();
                            System.out.println("[AUTH SUCESSO] " + clientId + " autenticado como " + usuarioLogado);
                            escritor.println("AUTH_OK Bem-vindo, " + usuarioLogado + "!");
                        } else {
                            System.out.println("[ALERTA SEGURANCA] Falha de login de " + clientId + " (" + statusAuth + ")");
                            escritor.println("AUTH_ERRO " + statusAuth);
                        }
                    } else {
                        escritor.println("AUTH_ERRO Formato invalido. Use: AUTH <usuario> <senha>");
                    }
                    escritor.println("<<<FIM_MSG>>>");
                    continue;
                }

                System.out.println("[" + usuarioLogado + "@" + clientId + "] " + linha);
                String resposta = proc.processar(linha, sessaoAutenticada);
                escritor.println(resposta);
                escritor.println("<<<FIM_MSG>>>");
            }
        } catch (IOException e) {
            System.out.println("[-] Cliente desconectado: " + clientId);
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
