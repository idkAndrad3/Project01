package entities;

public enum StatusInscricao {
    ATIVA("ativa"),
    CANCELADA("cancelada"),
    PENDENTE("pendente");

    private final String sqlValue;

    StatusInscricao(String sqlValue) {
        this.sqlValue = sqlValue;
    }

    public String getSQLValue() {
        return sqlValue;
    }
}
