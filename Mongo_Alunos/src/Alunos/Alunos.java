package Alunos;

import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;

import org.bson.Document;

import java.util.Scanner;

public class Alunos {

    
    private static MongoCollection<Document> cadastros;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        try (MongoClient mongoClient = MongoClients.create("mongodb+srv://nicolasalexandrinoo_db_user:2911@cluster0.c7lk9o2.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0")) {

            // Selecionar o banco de dados
            MongoDatabase database = mongoClient.getDatabase("Alunos");

            // Seleciona a coleção de Alunos
            cadastros = database.getCollection("Cadastros");

            int opcao;

            //  Loop do menu
            do {
                System.out.println("\n===== MENU =====");
                System.out.println("1. Cadastrar Aluno");
                System.out.println("2. Listar todos os Alunos");
                System.out.println("3. Buscar Aluno por nome");
                System.out.println("4. Atualizar Aluno");
                System.out.println("5. Remover Aluno");
                System.out.println("6. Listar Alunos por nota mínima");
                System.out.println("7. Exibir total de Alunos");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");

                // Lê o número digitado e trata possíveis erros
                try {
                    opcao = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Opção inválida! Digite um número.");
                    opcao = -1;
                }

                // Escolhe a operação correspondente
                switch (opcao) {
                    case 1 -> cadastrarAluno();
                    case 2 -> listarAlunos();
                    case 3 -> buscarPorNome();
                    case 4 -> atualizarAluno();
                    case 5 -> removerAluno();
                    case 6 -> listarPorNota();
                    case 7 -> exibirTotal();
                    case 0 -> System.out.println("Saindo... Até logo!");
                    default -> System.out.println("Opção inválida. Tente novamente.");
                }

            } while (opcao != 0); // repete até o usuário digitar 0

        } catch (Exception e) {
            System.err.println("❌ Erro ao conectar ao MongoDB: " + e.getMessage());
        }
    }
//Cadastra um novo aluno no banco.
private static void cadastrarAluno() {
System.out.println("\n=== Cadastro de Aluno ===");

// Coleta os dados do usuário
System.out.print("Nome: ");
String nome = scanner.nextLine();

System.out.print("Matrícula: ");
int matricula = Integer.parseInt(scanner.nextLine());

System.out.print("Período: ");
int periodo = Integer.parseInt(scanner.nextLine());

System.out.print("Curso: ");
String curso = scanner.nextLine();

System.out.print("Nota: ");
double nota = Double.parseDouble(scanner.nextLine());

// Cria um documento (objeto JSON) para representar o Aluno
Document aluno = new Document("Nome", nome)
    .append("Matrícula", matricula)
    .append("Curso", curso)
    .append("Período", periodo)
    .append("Nota", nota);

// Insere o documento na coleção
cadastros.insertOne(aluno);

System.out.println("Aluno cadastrado com sucesso!");
}

/**
* Lista todos os Alunos cadastrados no banco.
*/
private static void listarAlunos() {
System.out.println("\n=== Lista de Alunos ===");

// Percorre todos os documentos da coleção
for (Document doc : cadastros.find()) {
exibirAluno(doc);
}
}

/**
* Busca um Anluno pelo nome (ou parte dele).
*/
private static void buscarPorNome() {
System.out.print("\nDigite o nome do Aluno: ");
String termo = scanner.nextLine();

// Busca utilizando expressão regular (case-insensitive)
FindIterable<Document> resultados = cadastros.find(
    new Document("Nome", new Document("$regex", termo).append("$options", "i"))
);

boolean encontrado = false;
for (Document doc : resultados) {
exibirAluno(doc);
encontrado = true;
}

if (!encontrado) System.out.println("Nenhum Aluno encontrado com esse nome.");
}

/**
* Atualiza os dados de um Aluno já cadastrado.
*/
private static void atualizarAluno() {
System.out.print("\nDigite o nome do Aluno que deseja atualizar: ");
String nome = scanner.nextLine();

// Cria um filtro para encontrar o Aluno (regex para ignorar maiúsculas/minúsculas)
Document filtro = new Document("Nome", new Document("$regex", nome).append("$options", "i"));
Document aluno = cadastros.find(filtro).first();

// Verifica se o Aluno existe
if (aluno == null) {
System.out.println("Aluno não encontrado!");
return;
}

// Exibe os dados atuais
exibirAluno(aluno);

// Permite editar apenas o campo desejado
System.out.print("\nNovo Nome (ou pressione Enter para manter): ");
String novoNome = scanner.nextLine();
if (!novoNome.isEmpty()) aluno.put("Nome", novoNome);

System.out.print("Nova Matrícula (ou pressione Enter para manter): ");
String novaMatricula = scanner.nextLine();
if (!novaMatricula.isEmpty()) aluno.put("Matrícula", Integer.parseInt(novaMatricula));

System.out.print("Novo Curso (ou pressione Enter para manter): ");
String novoCurso = scanner.nextLine();
if (!novoCurso.isEmpty()) aluno.put("Curso", novoCurso);

System.out.print("Novo Período (ou pressione Enter para manter): ");
String novoPeriodo = scanner.nextLine();
if (!novoPeriodo.isEmpty()) aluno.put("Período", novoPeriodo);

System.out.print("Nova Nota (ou pressione Enter para manter): ");
String novaNota = scanner.nextLine();
if (!novaNota.isEmpty()) aluno.put("Nota", Double.parseDouble(novaNota));

// Atualiza o documento no banco
cadastros.replaceOne(filtro, aluno);
System.out.println("Aluno atualizado com sucesso!");
}

/**
* Remove um Aluno pelo Nome.
*/
private static void removerAluno() {
System.out.print("\nDigite o nome do Aluno que deseja remover: ");
String nome = scanner.nextLine();

// Cria o filtro de busca
Document filtro = new Document("Nome", new Document("$regex", nome).append("$options", "i"));

// Remove o primeiro documento encontrado
DeleteResult resultado = cadastros.deleteOne(filtro);

if (resultado.getDeletedCount() > 0) {
System.out.println("Aluno removido com sucesso!");
} else {
System.out.println("Nenhum Aluno encontrado com esse nome.");
}
}

/**
* Lista apenas os Alunos com nota superior a um valor informado.
*/
private static void listarPorNota() {
System.out.print("\nExibir alunos com nota acima de: ");
double notaMinima = Double.parseDouble(scanner.nextLine());

// Busca alunos com nota maior que o valor informado
FindIterable<Document> resultados = cadastros.find(new Document("Nota", new Document("$gt", notaMinima)));

boolean encontrado = false;
for (Document doc : resultados) {
exibirAluno(doc);
encontrado = true;
}

if (!encontrado) System.out.println("Nenhum aluno encontrado com nota acima de " + notaMinima);
}

/**
* Exibe a quantidade total de alunos cadastrados.
*/
private static void exibirTotal() {
long total = cadastros.countDocuments();
System.out.println("\n Total de alunos cadastrados: " + total);
}

/**
* Exibe os detalhes de um aluno formatado.
*/
private static void exibirAluno(Document aluno) {
    System.out.println("--------------------------");
    System.out.println(" Nome: " + aluno.getString("Nome"));

    Object matriculaObj = aluno.get("Matrícula");
    Object periodoObj = aluno.get("Período");
    Object notaObj = aluno.get("Nota");

    int matricula = matriculaObj instanceof Number ? ((Number) matriculaObj).intValue() : Integer.parseInt(matriculaObj.toString());
    int periodo = periodoObj instanceof Number ? ((Number) periodoObj).intValue() : Integer.parseInt(periodoObj.toString());
    double nota = notaObj instanceof Number ? ((Number) notaObj).doubleValue() : Double.parseDouble(notaObj.toString());

    System.out.println(" Matrícula: " + matricula);
    System.out.println(" Curso: " + aluno.getString("Curso"));
    System.out.println(" Período: " + periodo);
    System.out.println(" Nota: " + nota);
}

}
