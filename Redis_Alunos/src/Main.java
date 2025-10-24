import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        RedisManager redisManager = new RedisManager();
        Scanner scanner = new Scanner(System.in);


        int opcao;
		do {
            System.out.println("\n===== MENU - GERENCIAMENTO DE ALUNOS =====");
            System.out.println("1️  Inserir aluno");
            System.out.println("2️  Listar alunos");
            System.out.println("3️  Buscar aluno");
            System.out.println("4️  Atualizar aluno");
            System.out.println("5️ Remover aluno");
            System.out.println("0️  Sair");
            System.out.print("Escolha uma opção: ");

            opcao = Integer.parseInt(scanner.nextLine());

            switch (opcao) {
                case 1:
                    System.out.print("Matrícula: ");
                    String matricula = scanner.nextLine();
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    System.out.print("Curso: ");
                    String curso = scanner.nextLine();
                    System.out.print("Período: ");
                    int periodo = Integer.parseInt(scanner.nextLine());
                    System.out.print("Nota: ");
                    double nota = Double.parseDouble(scanner.nextLine());

                    redisManager.inserirAluno(matricula, nome, curso, periodo, nota);
                    break;

                case 2:
                    redisManager.listarAlunos();
                    break;

                case 3:
                    System.out.print("Informe a matrícula: ");
                    String buscaMat = scanner.nextLine();
                    redisManager.buscarAluno(buscaMat);
                    break;

                case 4:
                    System.out.print("Matrícula: ");
                    String matAtualizar = scanner.nextLine();
                    System.out.print("Campo a atualizar (Nome, Curso, Período, Nota): ");
                    String campo = scanner.nextLine();
                    System.out.print("Novo valor: ");
                    String novoValor = scanner.nextLine();

                    redisManager.atualizarAluno(matAtualizar, campo, novoValor);
                    break;

                case 5:
                    System.out.print("Matrícula: ");
                    String matRemover = scanner.nextLine();
                    redisManager.removerAluno(matRemover);
                    break;

                case 0:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println(" Opção inválida!");
                    break;
            }

        } while (opcao != 0);

        redisManager.close();
        scanner.close();
    }
}
