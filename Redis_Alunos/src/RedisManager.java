import redis.clients.jedis.UnifiedJedis;
import java.net.URI;
import java.util.*;

public class RedisManager {

    private UnifiedJedis jedis;

    public RedisManager() {
        try {
            String endpoint = "redis-11740.c257.us-east-1-3.ec2.redns.redis-cloud.com";
            int port = 11740;
            String user = "default";
            String password = "XFybZQsVqLz4aigwPKHyVaKekNbwJYkD";

            URI redisUri = new URI("redis://" + user + ":" + password + "@" + endpoint + ":" + port);
            this.jedis = new UnifiedJedis(redisUri);

            jedis.ping(); // Testa conexão
            System.out.println(" Conectado ao Redis com sucesso!");
        } catch (Exception e) {
            throw new RuntimeException(" Erro ao conectar ao Redis: " + e.getMessage());
        }
    }

    public boolean inserirAluno(String matricula, String nome, String curso, int periodo, double nota) {
        Map<String, String> dados = new HashMap<>();
        dados.put("Nome", nome);
        dados.put("Curso", curso);
        dados.put("Periodo", String.valueOf(periodo));
        dados.put("Nota", String.valueOf(nota));

        jedis.hset("aluno:" + matricula, dados);
        System.out.println(" Aluno cadastrado com sucesso!");
        System.out.println(" Dados do aluno:");
        System.out.println("Matrícula: " + matricula);
        System.out.println("Nome: " + nome);
        System.out.println("Curso: " + curso);
        System.out.println("Período: " + periodo);
        System.out.println("Nota: " + nota);
        System.out.println("----------------------------");
        return true;
    }

    public List<Map<String, String>> listarAlunos() {
        List<Map<String, String>> lista = new ArrayList<>();
        Set<String> keys = jedis.keys("aluno:*");

        if (keys.isEmpty()) {
            System.out.println(" Nenhum aluno encontrado no sistema.");
            return lista;
        }

        System.out.println("\n=====  Lista de Alunos =====");
        for (String key : keys) {
            Map<String, String> dados = jedis.hgetAll(key);
            dados.put("Matricula", key.replace("aluno:", ""));
            lista.add(dados);

            System.out.println("Matrícula: " + dados.get("Matricula"));
            System.out.println("Nome: " + dados.get("Nome"));
            System.out.println("Curso: " + dados.get("Curso"));
            System.out.println("Período: " + dados.get("Periodo"));
            System.out.println("Nota: " + dados.get("Nota"));
            System.out.println("----------------------------");
        }

        return lista;
    }

    public Optional<Map<String, String>> buscarAluno(String matricula) {
        String key = "aluno:" + matricula;
        if (!jedis.exists(key)) {
            System.out.println(" Aluno com matrícula " + matricula + " não encontrado.");
            return Optional.empty();
        }

        Map<String, String> dados = jedis.hgetAll(key);
        dados.put("Matricula", matricula);

        System.out.println("\n Aluno encontrado:");
        System.out.println("Matrícula: " + matricula);
        System.out.println("Nome: " + dados.get("Nome"));
        System.out.println("Curso: " + dados.get("Curso"));
        System.out.println("Período: " + dados.get("Periodo"));
        System.out.println("Nota: " + dados.get("Nota"));
        System.out.println("----------------------------");

        return Optional.of(dados);
    }

    public boolean atualizarAluno(String matricula, String campo, String novoValor) {
        String key = "aluno:" + matricula;
        if (!jedis.exists(key)) {
            System.out.println(" Não foi possível atualizar. Aluno com matrícula " + matricula + " não existe.");
            return false;
        }

        jedis.hset(key, campo, novoValor);
        System.out.println(" Campo \"" + campo + "\" do aluno " + matricula + " atualizado para: " + novoValor);
        System.out.println("----------------------------");
        return true;
    }

    public boolean removerAluno(String matricula) {
        long resultado = jedis.del("aluno:" + matricula);
        if (resultado > 0) {
            System.out.println(" Aluno com matrícula " + matricula + " removido com sucesso!");
            System.out.println("----------------------------");
            return true;
        } else {
            System.out.println(" Nenhum aluno encontrado com a matrícula " + matricula + ".");
            return false;
        }
    }

    public void close() {
        if (jedis != null) {
            jedis.close();
            System.out.println(" Conexão com Redis encerrada.");
        }
    }
}
