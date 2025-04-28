package me.modelo.exceptions;

public class ExportacaoFalhouException extends RuntimeException {
    public ExportacaoFalhouException(String mensagem) {
        super(mensagem);
    }
}