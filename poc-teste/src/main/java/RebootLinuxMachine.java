import java.io.IOException;
public class RebootLinuxMachine {
    public static void main(String[] args) {
        // Intervalo de tempo em milissegundos antes de reiniciar (por exemplo, 5 minutos)
        long intervalo = 5 * 60 * 1000; // 5 minutos

        // Laço infinito para reiniciar a máquina em intervalos regulares
        while (true) {
            try {
                // Espera pelo intervalo especificado
                Thread.sleep(intervalo);

                // Reinicia a máquina executando o comando de reinicialização do sistema
                rebootLinux();
            } catch (InterruptedException e) {
               // e.printStackTrace();
            }
        }
    }

    // Método para reiniciar a máquina Linux
    private static void rebootLinux() {
        try {
            // Executa o comando de reinicialização do sistema usando o comando "shutdown"
            Process processo = Runtime.getRuntime().exec("sudo shutdown -r now");

            // Espera o término do processo
            processo.waitFor();

            // Verifica se o reinício foi bem-sucedido
            int exitCode = processo.exitValue();
            if (exitCode == 0) {
                System.out.println("Máquina reiniciada com sucesso.");
            } else {
                System.err.println("Falha ao reiniciar a máquina. Código de saída: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
           // e.printStackTrace();
        }
    }
}

