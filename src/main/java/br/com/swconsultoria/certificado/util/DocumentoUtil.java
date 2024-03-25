package br.com.swconsultoria.certificado.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentoUtil {
    DocumentoUtil(){}

    private static final String CPF_INDICATOR = "\u0001";
    private static final String CPF_TERMINATOR = "\u0017";
    private static final Pattern PATTERN_CPF = Pattern.compile("(?<!\\d)\\d{11}(?!\\d)");
    private static final int CPF_LENGTH = 11;
    private static final int CPF_OFFSET = 15;
    private static final Pattern PATTERN_CNPJ = Pattern.compile("\\d{14}");
    private static final String CNPJ_INDICATOR = "\u0006\u0005`L\u0001\u0003\u0003";
    private static final int CNPJ_OFFSET = 6;
    private static final int CNPJ_LENGTH = 25;


    public static Optional<String> getDocumentoFromCertificado(byte[] extensionValue) {
        String valor = new String(extensionValue);
        return Optional.ofNullable(
                processaCNPJ(valor)
                        .orElse(processaCPF(valor)
                                .orElse(null)));
    }

    private static Optional<String> processaCPF(String extensionValue) {
        int cpfStartIndex = extensionValue.indexOf(CPF_INDICATOR);
        if (cpfStartIndex != -1) {
            cpfStartIndex += CPF_OFFSET;
            int cpfEndIndex = extensionValue.indexOf(CPF_TERMINATOR, cpfStartIndex);
            if (cpfEndIndex != -1) {
                String cpf = extrairNumeros(extensionValue.substring(cpfStartIndex, cpfStartIndex + CPF_LENGTH));
                return validarDocumento(cpf);
            }
        }
        return Optional.empty();
    }

    private static Optional<String> processaCNPJ(String extensionValue) {
        int cnpjIndex = extensionValue.indexOf(CNPJ_INDICATOR);
        if (cnpjIndex != -1) {
            String cnpj = extrairNumeros(extensionValue.substring(cnpjIndex + CNPJ_OFFSET, cnpjIndex + CNPJ_LENGTH));
            return validarDocumento(cnpj);
        }
        return Optional.empty();
    }

    private static String extrairNumeros(String valor) {
        return valor.replaceAll("[^\\d]", "");
    }

    private static Optional<String> validarDocumento(String documento) {
        Matcher matcherCNPJ = PATTERN_CNPJ.matcher(documento);
        if (matcherCNPJ.find()) {
            return Optional.of(matcherCNPJ.group());
        }

        Matcher matcherCPF = PATTERN_CPF.matcher(documento);
        if (matcherCPF.find()) {
            return Optional.of(matcherCPF.group());
        }

        return Optional.empty();
    }
}
