import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.sistema.Sistema;
import debug.LogLevel;
import debug.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

public class Main {
    public static void main(String[] args) {
        Looca looca = new Looca();
        Sistema sistema = new Sistema();
        Scanner input = new Scanner(System.in);
        Conexao conexao = new Conexao();
        JdbcTemplate con = conexao.getConexaoMySql();
        SuporteConexao suporteConexao = new SuporteConexao();
        NotebookConexao notebookConexao = new NotebookConexao();

        ConexaoSQL conexaoSQL = new ConexaoSQL();


        System.out.println("\n                  ___           ___                                     ___           ___           ___     \n" +
                "                 /\\  \\         /\\  \\                                   /\\__\\         /\\__\\         /\\  \\    \n" +
                "      ___        \\:\\  \\       /::\\  \\       ___           ___         /:/ _/_       /:/  /         \\:\\  \\   \n" +
                "     /|  |        \\:\\  \\     /:/\\:\\__\\     /\\__\\         /\\__\\       /:/ /\\__\\     /:/  /           \\:\\  \\  \n"+
                "    |:|  |    ___  \\:\\  \\   /:/ /:/  /    /:/__/        /:/  /      /:/ /:/ _/_   /:/  /  ___   ___ /::\\  \\ \n"+
                "    |:|  |   /\\  \\  \\:\\__\\ /:/_/:/__/___ /::\\  \\       /:/__/      /:/_/:/ /\\__\\ /:/__/  /\\__\\ /\\  /:/\\:\\__\\\n"+
                "  __|:|__|   \\:\\  \\ /:/  / \\:\\/:::::/  / \\/\\:\\  \\__   /::\\  \\      \\:\\/:/ /:/  / \\:\\  \\ /:/  / \\:\\/:/  \\/__/\n"+
                " /::::\\  \\    \\:\\  /:/  /   \\::/~~/~~~~   ~~\\:\\/\\__\\ /:/\\:\\  \\      \\::/_/:/  /   \\:\\  /:/  /   \\::/__/     \n"+
                " ~~~~\\:\\  \\    \\:\\/:/  /     \\:\\~~\\          \\::/  / \\/__\\:\\  \\      \\:\\/:/  /     \\:\\/:/  /     \\:\\  \\     \n"+
                "      \\:\\__\\    \\::/  /       \\:\\__\\         /:/  /       \\:\\__\\      \\::/  /       \\::/  /       \\:\\__\\    \n"+
                "       \\/__/     \\/__/         \\/__/         \\/__/         \\/__/       \\/__/         \\/__/         \\/__/\n");
        System.out.println("\nSeja bem-vindo(a)\n");

        Boolean loginRealizado = false;
        Suporte suporte = new Suporte();

        String email;
        String senha;

        do {
            System.out.println("Insira seu email:");
            email = input.nextLine();
            // Variável onde será salvo o email do usuário

            System.out.println("Insira sua senha:");
            senha = input.nextLine();
            // Variável onde será salva a senha, contendo pelo menos:
            // 8 caractéres, 1 caractere especial, 1 número e uma letra maiúscula

            if (suporte.login(email, senha)) {
                loginRealizado = true;
            } else {
                // Se as credenciais estiverem incorretas, solicita ao usuário que tente novamente
                String data = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss.SSS").format(new Date());
                Integer statusCode = 401;
                String mensagem = "Erro ao logar, e-mail ou senha incorretos";
                String sistemaOperacional =  looca.getSistema().getSistemaOperacional();
                Integer arquitetura = looca.getSistema().getArquitetura();
                LogLevel logLevel = LogLevel.ERROR;

                Logger errorLogin = new Logger(data, statusCode, logLevel, mensagem, sistemaOperacional, arquitetura);
                String dataAtual = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                String nomeArquivoLog = String.format(".\\log-%s.txt", dataAtual);

                try (FileWriter writer = new FileWriter(nomeArquivoLog, true)) {
                    writer.write(errorLogin.toString());
                } catch (IOException e) {
                    System.out.println("Erro ao salvar log: " + e.getMessage());
                }

            }
        } while (!loginRealizado);
        // Loop do login, fazendo com que o usuário fique "preso" até inserir email e senha cadastrados

        System.out.println("""
                \nLogin realizado com sucesso!
                """);

        // Criação da empresa, para conseguirmos pegar dados de outros objetos
        Integer idEmpresa = suporte.buscarEmpresa(email, senha);
        Empresa empresa = suporteConexao.cadastrarEmpresa(idEmpresa);
        empresa.insertEmpresa();

        Boolean funcionarioVerificado = false;
        Funcionario funcionario = new Funcionario();

        String emailFuncionario;

        do {
            System.out.println("Insira o email do funcionário alocado a esta máquina:");
            emailFuncionario = input.nextLine();

            // Select que verifica se o email existe na empresa

                if (funcionario.funcionarioAlocado(emailFuncionario)){
                    funcionarioVerificado = true;
                }

        } while (!funcionarioVerificado);

        // Select que trará o número de série do notebook alocado ao usuário citado anteriormente:

        funcionario.cadastrarFuncionario(emailFuncionario);

        Integer idNotebook = funcionario.buscarNotebook(funcionario.getId(), empresa.getIdEmpresa());
        Notebook notebook = notebookConexao.cadastrarNotebook(idNotebook);
        notebook.insertNotebook();

        InfoNotebook infoNotebook = new InfoNotebook();
        Registro registro = new Registro();
        infoNotebook.capturarInformacoesNotebook(notebook.getIdNotebook(), empresa.getIdEmpresa());

        System.out.println("Gostaria de visualizar os processos da máquina? ('s' ou 'n')");
        String resposta = input.nextLine();
        if (resposta.equalsIgnoreCase("s")){
            registro.informarJanelas();
            System.out.println("Digite o nome do processo que gostaria de finalizar?");
            resposta = input.nextLine();
            registro.fecharJanelas(resposta);
        }

        System.out.println("""
                \nIremos iniciar o monitoramento
                \n""");

        ParametrosConexao parametrosConexao = new ParametrosConexao();
        Parametros parametros = parametrosConexao.capturarParametros(empresa.getIdEmpresa());
        parametros.insertParametros();

        Reboot.Desligar(infoNotebook.getSistemaOperacional());

        do {
            registro.capturarDados(notebook.getIdNotebook(), empresa.getIdEmpresa());
            parametros.alertar(funcionario.getNome(), notebook.getNumeroSerie(), registro.getUsoCpu(), registro.getUsoDisco(), registro.getUsoMemoriaRam(), registro.getId(), notebook.getIdNotebook());

            try {
                TimeUnit.SECONDS.sleep(parametros.getTempoSegCapturaDeDados());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } while (loginRealizado);

    }
}