package com.alexsantosportfolio.emailcontactservice.util;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilitário responsável por formatações de texto e strings na aplicação.
 */
public class StringFormatUtil {

    private StringFormatUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Converte chaves de propriedades vindas em padrão camelCase para 
     * o padrão Title Case separado por espaços. Útil para exibição limpa em emails e UIs.
     * 
     * <p>Exemplo de entrada: {@code comoNosConheceu}</p>
     * <p>Exemplo de saída: {@code Como Nos Conheceu}</p>
     * 
     * @param propriedadesOriginais Mapa contendo as chaves a serem formatadas
     * @return Novo mapa com as chaves formatadas (imutando a entrada)
     */
    public static Map<String, Object> formatarChavesCamelCaseParaHeader(Map<String, Object> propriedadesOriginais) {
        if (propriedadesOriginais == null || propriedadesOriginais.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Object> propriedadesFormatadas = new HashMap<>();

        for (Map.Entry<String, Object> entrada : propriedadesOriginais.entrySet()) {
            String chave = entrada.getKey();
            
            if (chave != null && !chave.trim().isEmpty()) {
                // regex que acha encontro de minúscula com maiúscula e insere um espaço ("$1 $2")
                String comEspacos = chave.replaceAll("([a-z])([A-Z]+)", "$1 $2");
                
                // Transforma a primeira letra (ou todo o conjunto) no formato natural capitalizado
                // Usando o utils do Spring fica "Como Nos Conheceu" em vez de "como Nos Conheceu"
                String tituloFormatado = StringUtils.capitalize(comEspacos);
                
                propriedadesFormatadas.put(tituloFormatado, entrada.getValue());
            } else {
                propriedadesFormatadas.put(chave, entrada.getValue());
            }
        }

        return propriedadesFormatadas;
    }
}
