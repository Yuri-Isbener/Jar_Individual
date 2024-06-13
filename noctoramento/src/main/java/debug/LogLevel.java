package debug;

public enum LogLevel {
    TRACE("trace", 0, ""),
    DEBUG("debug", 1, ""),
    INFO("info", 2, ""),
    WARN("warn", 3, ""),
    ERROR("error", 4, ""),
    FATAL("fatal", 5, "");

    private final String nome;
    private final int level;

    private String local;

    LogLevel(String nome, int level, String local) {
        this.nome = nome;
        this.level = level;
        this.local = local;
    }

    public String getNome() {
        return nome;
    }

    public int getLevel() {
        return level;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }
}
