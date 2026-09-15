package br.com.adocao.api;

import java.util.Map;

public record ApiError(String mensagem, Map<String, String> campos) {
}
