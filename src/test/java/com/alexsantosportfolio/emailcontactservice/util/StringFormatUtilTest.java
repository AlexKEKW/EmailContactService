package com.alexsantosportfolio.emailcontactservice.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StringFormatUtil — formatação camelCase → Title Case")
class StringFormatUtilTest {

    @Test
    @DisplayName("Deve converter camelCase para Title Case com espaços")
    void deveConverterCamelCaseParaTitleCase() {
        Map<String, Object> input = Map.of("comoNosConheceu", "Google");

        Map<String, Object> resultado = StringFormatUtil.formatarChavesCamelCaseParaHeader(input);

        assertThat(resultado).containsEntry("Como Nos Conheceu", "Google");
    }

    @Test
    @DisplayName("Deve manter chave já capitalizada inalterada")
    void deveManterChaveCapitalizada() {
        Map<String, Object> input = Map.of("Telefone", "11999999999");

        Map<String, Object> resultado = StringFormatUtil.formatarChavesCamelCaseParaHeader(input);

        assertThat(resultado).containsKey("Telefone");
        assertThat(resultado.get("Telefone")).isEqualTo("11999999999");
    }

    @Test
    @DisplayName("Deve retornar mapa vazio quando input é null")
    void deveRetornarMapaVazioQuandoNull() {
        Map<String, Object> resultado = StringFormatUtil.formatarChavesCamelCaseParaHeader(null);

        assertThat(resultado).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve retornar mapa vazio quando input é vazio")
    void deveRetornarMapaVazioQuandoVazio() {
        Map<String, Object> resultado = StringFormatUtil.formatarChavesCamelCaseParaHeader(Map.of());

        assertThat(resultado).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve preservar chave vazia sem alterar")
    void devePreservarChaveVazia() {
        Map<String, Object> input = new HashMap<>();
        input.put("", "valor");

        Map<String, Object> resultado = StringFormatUtil.formatarChavesCamelCaseParaHeader(input);

        assertThat(resultado).containsKey("");
    }

    @Test
    @DisplayName("Deve preservar chave null sem alterar")
    void devePreservarChaveNull() {
        Map<String, Object> input = new HashMap<>();
        input.put(null, "valor");

        Map<String, Object> resultado = StringFormatUtil.formatarChavesCamelCaseParaHeader(input);

        assertThat(resultado).containsKey(null);
    }

    @Test
    @DisplayName("Deve separar múltiplas maiúsculas consecutivas corretamente")
    void deveSepararMultiplasMaiusculasConsecutivas() {
        Map<String, Object> input = Map.of("urlHTTP", "https://example.com");

        Map<String, Object> resultado = StringFormatUtil.formatarChavesCamelCaseParaHeader(input);

        // "urlHTTP" → regex ([a-z])([A-Z]+) → "url HTTP" → capitalize → "Url HTTP"
        assertThat(resultado).containsKey("Url HTTP");
    }

    @Test
    @DisplayName("Deve converter múltiplas entradas corretamente")
    void deveConverterMultiplasEntradas() {
        Map<String, Object> input = Map.of(
                "nomeCompleto", "João",
                "dataNascimento", "01/01/2000",
                "cidadeOrigem", "São Paulo"
        );

        Map<String, Object> resultado = StringFormatUtil.formatarChavesCamelCaseParaHeader(input);

        assertThat(resultado).hasSize(3);
        assertThat(resultado).containsKeys("Nome Completo", "Data Nascimento", "Cidade Origem");
    }

    @Test
    @DisplayName("Deve preservar valores originais após formatação das chaves")
    void devePreservarValoresOriginais() {
        Map<String, Object> input = Map.of(
                "telefoneContato", "(11) 99999-9999",
                "valorOrcamento", 1500.50
        );

        Map<String, Object> resultado = StringFormatUtil.formatarChavesCamelCaseParaHeader(input);

        assertThat(resultado.get("Telefone Contato")).isEqualTo("(11) 99999-9999");
        assertThat(resultado.get("Valor Orcamento")).isEqualTo(1500.50);
    }
}
