package com.rastreamento.correios.utils;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Gustavo Bussolotti
 */
public class StringUtils {

    public static final DecimalFormat DF_VALOR = new DecimalFormat("0.00");
    public static final DecimalFormat DF_QTDE = new DecimalFormat("0.000");

    public static boolean isNullOrEmpty(String str){
        return str == null || str.trim().isEmpty();
    }

    public static String mascaraCep(String cep) {
        if (cep == null) {
            cep = "";
        }
        cep = cep.replaceAll("[^0-9]", "");
        switch (cep.length()) {
            case 8:
                return cep.substring(0, 5) + "-" + cep.substring(5, 8);
            default:
                return "";
        }
    }


    public boolean validarEmail(String str) {
        String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pat = Pattern.compile(regex);
        Matcher mat;
        mat = pat.matcher(str);
        return mat.find();
    }

    public static String desformatarDadosSOAP(String texto) {
        return texto.replace("&amp;", "&").replace("&gt;", ">").replace("&lt;", "<");
    }

    public static synchronized String trocarAcentuacao(String acentuada) {
        if (!Normalizer.isNormalized(acentuada, Normalizer.Form.NFD)) {
            acentuada = Normalizer.normalize(acentuada, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        }
        return acentuada;
    }

    public static String limitadorString(String str, int qtdeChars) {
        if (str == null) {
            str = "";
        }
        str = str.trim();
        str = str.equalsIgnoreCase("null") ? "" : str;
        if (str.length() > qtdeChars) {
            return str.substring(0, qtdeChars - 1);
        }

        return str;
    }

    public static String stringBD(String str) {
        return str != null ? "'" + str.replace("'", "\\'") + "'" : "''";
    }

    public static String stringNotNull(String str) {
        if (str == null) {
            str = "";
        }
        str = str.trim();
        return str.equalsIgnoreCase("null") ? "" : str;
    }


    public static String stringBDConsulta(String str) {
        return str != null ? str.replace("'", "\\'") : "";
    }



    public static String unaccent(String src) {
        return Normalizer.normalize(src, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

}


