package debug;

public class Logger {
    private String data;
    private LogLevel loglevel;
    private Integer statusCode;
    private String mensagem;
    private String sistemaOperacional;
    private Integer arquitetura;

    public Logger(String data, Integer statusCode, LogLevel loglevel, String mensagem, String sistemaOperacional, Integer arquitetura) {
        this.data = data;
        this.loglevel = loglevel;
        this.statusCode = statusCode;
        this.mensagem = mensagem;
        this.sistemaOperacional = sistemaOperacional;
        this.arquitetura = arquitetura;
    }

    @Override
    public String toString() {
        return
                "data='" + data + '\'' +
                ", logLevel='" + loglevel.getNome() + '\'' +
                ", statusCode=" + statusCode +
                ", mensagem='" + mensagem + '\'' +
                ", sistemaOperacional='" + sistemaOperacional + '\'' +
                ", arquitetura=" + arquitetura;
    }
}

