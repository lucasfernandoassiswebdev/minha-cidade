package br.com.eddydata.minhacidade.util;

import com.sun.mail.util.MailSSLSocketFactory;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.activation.DataHandler;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.imageio.stream.FileImageOutputStream;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CaptureEvent;
import org.primefaces.model.CroppedImage;
import org.w3c.dom.Document;

public class Util {

    private final static String FORMATO_DTBR = "dd/MM/yyyy";
    private final static String FORMATO_HORA = "HH:mm";
    private static final String PATTERN_URL = "\\b(https?|ftp|file):/[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static final Pattern PATTERN_EMAIL = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    /**
     * arredondamento segundo normas da ABNT
     *
     * @param valor
     * @param casasDecimais
     * @return
     */
    public static Double arredondarValor(Double valor, Integer casasDecimais) {
        valor = (valor == null ? 0.0 : valor);
        casasDecimais = (casasDecimais == null ? 2 : casasDecimais);
        String valor_aux = "0.";
        for (int i = 0; i < casasDecimais; i++) {
            valor_aux += "0";
        }
        valor_aux += "5";

        double valorAux = Double.parseDouble(valor_aux);
        int digito = ((int) (valor * Math.pow(10, casasDecimais + 1))) % 10;

        if (digito >= 5) {
            return truncarValor(valor + valorAux, casasDecimais);
        } else {
            return truncarValor(valor, casasDecimais);
        }
    }

    public static boolean isNumerico(String val) {
        if (val == null || val.isEmpty()) {
            return false;
        }
        for (int i = 0; i < val.length(); i++) {
            if (val.charAt(i) < '0' || val.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    public static String formatarDecimal(String formato, Object valor) {
        DecimalFormat format = new DecimalFormat();
        format.applyPattern(formato);
        return format.format(valor);
    }

    /**
     * Elimina sem arredondamentos a quantidade desejada de casas decimais.
     *
     * @param val valor a truncar.
     * @param tamanhoFracionario numero de casas fracionarias do valor a
     * eliminar casas.
     *
     * @return double com as casas decimais eliminadas.
     */
    public static double truncarValor(double val, int tamanhoFracionario) {
        double fator = Math.pow(10, tamanhoFracionario);
        long valInt = (long) (val * fator);

        return valInt / fator;
    }

    public static boolean validarURL(String url) {
        Pattern p = Pattern.compile(PATTERN_URL);
        Matcher matcher = p.matcher(url);
        return matcher.matches();
    }

    public static void sleep(long segundos) {
        try {
            TimeUnit.SECONDS.sleep(segundos);
        } catch (InterruptedException ex) {
            System.out.println("Não foi possível usar funcao Sleep\n" + ex.getMessage());
        }
    }

    static synchronized public boolean validarCNPJ(String str_cnpj) {
        try {
            int soma = 0, dig;
            str_cnpj = Texto.removerPontos(str_cnpj);
            String cnpj_calc = str_cnpj.substring(0, 12);

            if (str_cnpj.length() != 14) {
                return false;
            }

            char[] chr_cnpj = str_cnpj.toCharArray();

            /*
             * Primeira parte
             */
            for (int i = 0; i < 4; i++) {
                if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                    soma += (chr_cnpj[i] - 48) * (6 - (i + 1));
                }
            }

            for (int i = 0; i < 8; i++) {
                if (chr_cnpj[i + 4] - 48 >= 0 && chr_cnpj[i + 4] - 48 <= 9) {
                    soma += (chr_cnpj[i + 4] - 48) * (10 - (i + 1));
                }
            }

            dig = 11 - (soma % 11);

            cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);

            /*
             * Segunda parte
             */
            soma = 0;
            for (int i = 0; i < 5; i++) {
                if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9) {
                    soma += (chr_cnpj[i] - 48) * (7 - (i + 1));
                }
            }

            for (int i = 0; i < 8; i++) {
                if (chr_cnpj[i + 5] - 48 >= 0 && chr_cnpj[i + 5] - 48 <= 9) {
                    soma += (chr_cnpj[i + 5] - 48) * (10 - (i + 1));
                }
            }

            dig = 11 - (soma % 11);
            cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);

            return str_cnpj.equals(cnpj_calc);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean validarCPF(String s_aux) {
        s_aux = Texto.removerPontos(s_aux);
        if (s_aux.length() == 11) {
            try {
                if (Long.parseLong(s_aux) <= 0) {
                    throw new Exception("Numero de CPF invalido");
                }

                double primeiraSoma = 0.0, segundaSoma = 0.0, primeiroDigito, segundoDigito, valorTemp;

                for (int i = 0; i < 10; i++) {
                    valorTemp = Double.parseDouble("" + s_aux.charAt(i));
                    primeiraSoma += (i < 9 ? valorTemp * (10 - i) : 0);
                    segundaSoma += valorTemp * (11 - i);
                }

                primeiroDigito = (primeiraSoma % 11 < 2 ? 0 : 11 - (primeiraSoma % 11));
                segundoDigito = (segundaSoma % 11 < 2 ? 0 : 11 - (segundaSoma % 11));

                return Integer.parseInt("" + s_aux.charAt(9)) == primeiroDigito
                        && Integer.parseInt("" + s_aux.charAt(10)) == segundoDigito;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public synchronized static String formatarCNPJ(long cnpj) {
        String value = String.format("%016d", cnpj);

        value = value.substring(value.length() - 15, value.length() - 12) + "."
                + value.substring(value.length() - 12, value.length() - 9) + "." + value.substring(value.length() - 9, value.length() - 6) + "/"
                + value.substring(value.length() - 6, value.length() - 2) + "-" + value.substring(value.length() - 2, value.length());

        return value;
    }

    public static String extrairStr(Object val) {
        if (val == null) {
            return "";
        } else {
            return String.valueOf(val);
        }
    }

    public static int extrairInt(Object val) {
        String out = extrairStr(val);
        if (out.length() == 0) {
            return 0;
        } else {
            return Integer.parseInt(val.toString());
        }
    }

    public static double extrairDouble(Object val) {
        if (val == null) {
            return 0.00;
        } else if (val.getClass() == BigDecimal.class) {
            return ((Number) val).doubleValue();
        } else if (val.getClass() == double.class) {
            return ((Double) val);
        } else {
            String out = extrairStr(val);
            if (out.length() == 0) {
                return 0.0;
            } else {
                return Double.parseDouble(val.toString());
            }
        }
    }

    public static String converteBrFloat(Object val) {
        if (val == null || val.toString().length() == 0) {
            val = "0";
        }
        if (isFloat(val) == false) {
//            erro("Valor fracion\u00e1rio inv\u00e1lido!", (Exception) null);
            return null;
        } else {
            double val_;
            val_ = Double.parseDouble(val.toString());
            DecimalFormat format = new DecimalFormat();
            format.applyPattern("#,##0.00##");
            String out = format.format(val_);
            return out;
        }
    }

    public static boolean isFloat(Object valor) {
        if (valor == null || valor.toString().length() == 0) {
            return true;
        }
        try {
            Double.parseDouble(valor.toString());
            return true;
        } catch (NumberFormatException er1) {
            return false;
        }
    }

    public static BufferedImage redimencionarImagem(BufferedImage imagem, int altura, int largura) {
        int h = imagem.getHeight();
        int w = imagem.getWidth();
        double proporcao;

        if (h > altura && w > largura) {
            if (h > w) {
                proporcao = altura / (double) h;
                h = altura;
                w = new Double(proporcao * w).intValue();
            } else {
                proporcao = largura / (double) w;
                h = new Double(proporcao * h).intValue();
                w = largura;
            }
        } else if (h > altura && w < largura) {
            proporcao = altura / (double) h;
            h = altura;
            w = new Double(proporcao * w).intValue();
        } else if (h < altura && w > largura) {
            proporcao = largura / (double) w;
            h = new Double(proporcao * h).intValue();
            w = largura;
        }

        BufferedImage imagemRedimensionada = new BufferedImage(w, h, imagem.getType());
        Graphics2D g = imagemRedimensionada.createGraphics();
        g.drawImage(imagem, 0, 0, w, h, null);
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.dispose();

        return imagemRedimensionada;
    }

    /**
     * Método para obter através do geocode do google a latitude e longitude do
     * endereço
     *
     * @param endereco LOGRADOURO, BAIRRO E CIDADE
     * @return 0 = LAT, 1 = LONG
     * @throws Exception
     */
    public static String[] obterLatitudeLongitude(String endereco) throws Exception {
        if (endereco == null) {
            return null;
        }
        endereco = endereco.trim().toUpperCase();
        if (!endereco.contains("BRASIL")) {
            endereco += " BRASIL";
        }
        String api = "http://maps.googleapis.com/maps/api/geocode/xml?address=" + URLEncoder.encode(endereco, "UTF-8") + "&sensor=true";
        URL url = new URL(api);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.connect();
        int responseCode = httpConnection.getResponseCode();
        if (responseCode == 200) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(httpConnection.getInputStream());
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            javax.xml.xpath.XPathExpression expr = xpath.compile("/GeocodeResponse/status");
            String status = (String) expr.evaluate(document, XPathConstants.STRING);
            if (status.equals("OK")) {
                expr = xpath.compile("//geometry/location/lat");
                String latitude = (String) expr.evaluate(document, XPathConstants.STRING);
                expr = xpath.compile("//geometry/location/lng");
                String longitude = (String) expr.evaluate(document, XPathConstants.STRING);
                return new String[]{latitude, longitude};
            } else {
                return null;
            }
        }
        return null;
    }

    public static String gerarQuery(Object entidade, String ordenacao) {
        if (entidade == null) {
            return "";
        }
        String query = "select x from " + entidade.getClass().getSimpleName() + " x ";
        try {
            query += gerarCriteria(entidade);
        } catch (SecurityException | IllegalArgumentException ex) {
            System.out.println("Não foi possivel converter a entidade em criteria\n" + ex.getMessage());
            return "";
        }
        if (ordenacao != null && !ordenacao.trim().isEmpty()) {
            query += " order by x." + ordenacao;
        }

        return query;
    }

    public static String gerarCriteria(Object entidade) {
        if (entidade == null) {
            return "";
        }
        String criteria = "";
        try {
            Field[] fields = entidade.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object valor = field.get(entidade);
                if (valor == null || valor.equals("") || valor.equals("(__) ____-_____") || valor.equals("___.___.___-__") || valor.equals("__.___.___-_")) {
                    continue;
                }
                if (field.getName().equals("serialVersionUID") || field.getName().startsWith("_")) {
                    continue;
                }
                String cond = (criteria.contains("WHERE") ? "\nAND " : "\nWHERE ");
                if (field.getType().getEnumConstants() != null) {
                    cond += "cast(x." + field.getName() + " as int) ";
                    cond += " = ";
                    cond += ((Enum) valor).ordinal();
                } else {
                    switch (field.getType().getSimpleName()) {
                        case "String":
                            cond += "function('rem_acento', UPPER(x." + field.getName() + "))";
                            cond += " like ";
                            cond += Texto.quotarStr("%" + Texto.removerAcentos(valor.toString().trim().toUpperCase()) + "%");
                            break;
                        case "Date":
                            cond += "cast(cast(x." + field.getName() + " as date) as varchar)";
                            cond += " = ";
                            cond += Texto.quotarStr(Data.formatarData(valor, "yyyy-MM-dd"));
                            break;
                        case "Cliente":
                        case "Produto":
                            cond += gerarCriteria(valor, field.getName());
                            break;
                        default:
                            cond += "x." + field.getName();
                            cond += " = ";
                            cond += valor;
                            break;
                    }
                }
                if (cond.endsWith("WHERE ")) {
                    cond = cond.substring(0, cond.lastIndexOf("WHERE "));
                } else if (cond.endsWith("AND ")) {
                    cond = cond.substring(0, cond.lastIndexOf("AND "));
                }
                criteria += cond;
            }
            return criteria;
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            System.out.println("Não foi possivel converter a entidade em criteria\n" + ex.getMessage());
            return "";
        }
    }

    public static String gerarCriteria(Object entidade, String aliasJoin) {
        if (entidade == null) {
            return "";
        }
        if (aliasJoin == null) {
            return gerarCriteria(entidade);
        }
        String criteria = "";
        try {
            Field[] fields = entidade.getClass().getDeclaredFields();
            fields:
            for (Field field : fields) {
                field.setAccessible(true);
                Object valor = field.get(entidade);
                if (valor == null || valor.equals("") || valor.equals("(__) ____-_____") || valor.equals("___.___.___-__") || valor.equals("__.___.___-_")) {
                    continue;
                }
                if (field.getName().equals("serialVersionUID") || field.getName().startsWith("_")) {
                    continue;
                }
                if (field.getType().getEnumConstants() != null) {
                    criteria += "cast(x." + aliasJoin + "." + field.getName() + " as int) ";
                    criteria += " = ";
                    criteria += ((Enum) valor).ordinal();
                    break;
                } else {
                    switch (field.getType().getSimpleName()) {
                        case "String":
                            criteria += "function('rem_acento', UPPER(x." + aliasJoin + "." + field.getName() + "))";
                            criteria += " like ";
                            criteria += Texto.quotarStr("%" + Texto.removerAcentos(valor.toString().trim().toUpperCase()) + "%");
                            break fields;
                        case "Date":
                            criteria += "x." + aliasJoin + "." + field.getName();
                            criteria += " = ";
                            criteria += Texto.quotarStr(Data.formatarData(valor, "yyyy-MM-dd"));
                            break fields;
                        default:
                            criteria += "x." + aliasJoin + "." + field.getName();
                            criteria += " = ";
                            criteria += valor;
                            break fields;
                    }
                }
            }
            return criteria;
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            System.out.println("Não foi possivel converter a entidade em criteria\n" + ex.getMessage());
            return "";
        }
    }

    public static class Data {

        public static boolean isDate(Object data) {
            try {
                formatarDataBrParaDataSQL(data.toString());
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public static Date parseSqlDateJson(Object data_) {
            try {
                if (data_ == null) {
                    return null;
                }
                if (data_.getClass() == String.class) { // se for uma data em string, deve ser convertida
                    if (isDate(data_) == false) {
                        return null;
                    } else { // se a data nao for string, deve estar em formato nativo
                        data_ = data_.toString().replace(" UTC", "");
                        return new SimpleDateFormat("yyyy-MM-dd").parse(data_.toString());
                    }
                } else {
                    String formato = "yyyy-MM-dd";
                    SimpleDateFormat f = new SimpleDateFormat(formato);
                    return f.parse(data_.toString());
                }
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        }

        public static String formatarHora(Object data) {
            SimpleDateFormat f = new SimpleDateFormat(FORMATO_HORA);
            if (data != null) {
                if (data.getClass() == String.class) {
                    Date d = java.sql.Date.valueOf(data.toString());
                    return f.format(d);
                }
                return f.format(data);
            } else {
                return null;
            }
        }

        public static java.util.Date formatarDataBR(String val) {
            SimpleDateFormat format = new SimpleDateFormat(FORMATO_DTBR);
            try {
                return format.parse(val);
            } catch (ParseException ex) {
                System.out.println("Não foi possível formatar valor\n" + ex.getMessage());
            }
            return null;
        }

        public static java.util.Date formatar(String val, String formato) {
            SimpleDateFormat format = new SimpleDateFormat(formato);
            try {
                return format.parse(val);
            } catch (ParseException ex) {
                System.out.println("Não foi possível formatar valor\n" + ex.getMessage());
            }
            return null;
        }

        public static java.sql.Date formatarDataBrParaDataSQL(String val) {
            SimpleDateFormat format = new SimpleDateFormat(FORMATO_DTBR);
            try {
                return new java.sql.Date(format.parse(val).getTime());
            } catch (ParseException ex) {
                System.out.println("Não foi possível formatar valor\n" + ex.getMessage());
            }
            return null;
        }

        public static String formatarDataBR(Object data) {
            SimpleDateFormat f = new SimpleDateFormat(FORMATO_DTBR);
            if (data != null) {
                if (data.getClass() == String.class) {
                    Date d = java.sql.Date.valueOf(data.toString());
                    return f.format(d);
                }
                return f.format(data);
            } else {
                return null;
            }
        }

        public static String formatarData(Object data, String formato) {
            String formato_dt = formato;
            SimpleDateFormat f = new SimpleDateFormat(formato_dt);
            if (data != null) {
                if (data.getClass() == String.class) {
                    Date d = java.sql.Date.valueOf(data.toString());
                    return f.format(d);
                }
                return f.format(data);
            } else {
                return null;
            }
        }

        public static java.util.Date extrairData(Object val) {
            if (val == null) {
                java.util.Date date = new java.util.Date();
                date.setTime(0);
                return date;
            }
            if (val.getClass() == java.sql.Date.class) {
                java.sql.Date d = (java.sql.Date) val;
                java.util.Date out = new java.util.Date();
                out.setTime(d.getTime());
                return out;
            } else {
                return Data.formatar(val.toString(), "yyyy-MM-dd HH:mm");
            }
        }

        public static String obterNomeMes(int mes) {
            switch (mes) {
                case 0:
                    return "ABERTURA EXERCICIO";
                case 1:
                    return "JANEIRO";
                case 2:
                    return "FEVEREIRO";
                case 3:
                    return "MAR\u00c7O";
                case 4:
                    return "ABRIL";
                case 5:
                    return "MAIO";
                case 6:
                    return "JUNHO";
                case 7:
                    return "JULHO";
                case 8:
                    return "AGOSTO";
                case 9:
                    return "SETEMBRO";
                case 10:
                    return "OUTUBRO";
                case 11:
                    return "NOVEMBRO";
                case 12:
                    return "DEZEMBRO";
                default:
                    return "?";
            }
        }

        /**
         * Retorna o numero do mes da data.
         *
         * @param data data a extrair mes
         * @return numero do mes
         */
        public static int extrairMes(java.util.Date data) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(data);
            return c.get(GregorianCalendar.MONTH) + 1;
        }

        public static int extrairDia(java.util.Date data) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(data);
            return c.get(GregorianCalendar.DAY_OF_MONTH);
        }

        public static int extrairAno(java.util.Date data) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(data);
            return c.get(GregorianCalendar.YEAR);
        }

        public static Date adicionarDia(Date hoje, int quant) {

            Calendar agora = Calendar.getInstance();
            agora.setTime(hoje);

            agora.add(Calendar.DAY_OF_MONTH, quant);

            return agora.getTime();
        }

        public static Date adicionarMes(Date hoje, int quant) {

            Calendar agora = Calendar.getInstance();
            agora.setTime(hoje);

            agora.add(Calendar.MONTH, quant);

            return agora.getTime();
        }

        public static Date adicionarAno(Date hoje, int quant) {

            Calendar agora = Calendar.getInstance();
            agora.setTime(hoje);

            agora.add(Calendar.DAY_OF_MONTH, quant);

            return agora.getTime();
        }

        public static String obterDataExtenso(Date data, boolean mostrar_diasemana) {
            String diaf = null;
            String mesf = null;
            String retorno = null;

            if (data == null) {
                return retorno;
            }

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(data);
            int semana = calendar.get(Calendar.DAY_OF_WEEK);
            int mes = calendar.get(Calendar.MONTH) + 1;
            int dia = calendar.get(Calendar.DAY_OF_MONTH);
            int ano = calendar.get(Calendar.YEAR);

            // semana
            switch (semana) {
                case 1:
                    diaf = "Domingo";
                    break;
                case 2:
                    diaf = "Segunda";
                    break;
                case 3:
                    diaf = "Terça";
                    break;
                case 4:
                    diaf = "Quarta";
                    break;
                case 5:
                    diaf = "Quinta";
                    break;
                case 6:
                    diaf = "Sexta";
                    break;
                case 7:
                    diaf = "Sábado";
                    break;
            }
            // mês
            switch (mes) {
                case 1:
                    mesf = "Janeiro";
                    break;
                case 2:
                    mesf = "Fevereiro";
                    break;
                case 3:
                    mesf = "Março";
                    break;
                case 4:
                    mesf = "Abril";
                    break;
                case 5:
                    mesf = "Maio";
                    break;
                case 6:
                    mesf = "Junho";
                    break;
                case 7:
                    mesf = "Julho";
                    break;
                case 8:
                    mesf = "Agosto";
                    break;
                case 9:
                    mesf = "Setembro";
                    break;
                case 10:
                    mesf = "Outubro";
                    break;
                case 11:
                    mesf = "Novembro";
                    break;
                case 12:
                    mesf = "Dezembro";
                    break;
            }
            if (mostrar_diasemana) {
                retorno = diaf + ", " + dia + " de " + mesf + " de " + ano;
            } else {
                retorno = dia + " de " + mesf + " de " + ano;
            }
            return retorno;
        }

        public static String obterHora() {

            // cria um StringBuilder
            StringBuilder sb = new StringBuilder();

            // cria um GregorianCalendar que vai conter a hora atual
            GregorianCalendar d = new GregorianCalendar();

            // anexa do StringBuilder os dados da hora
            sb.append(Texto.strZero(d.get(GregorianCalendar.HOUR_OF_DAY), 2));
            sb.append(":");
            sb.append(Texto.strZero(d.get(GregorianCalendar.MINUTE), 2));
            sb.append(":");
            sb.append(Texto.strZero(d.get(GregorianCalendar.SECOND), 2));

            // retorna a String do StringBuilder
            return sb.toString();
        }

        /**
         * Calcula a diferença de duas datas em dias <br> <b>Importante:</b>
         * Quando realiza a diferença em dias entre duas datas, este método
         * considera as horas restantes e as converte em fração de dias.
         *
         * @param dataInicial
         * @param dataFinal
         * @return quantidade de dias existentes entre a dataInicial e
         * dataFinal.
         */
        public static double diferencaEmDias(Date dataInicial, Date dataFinal) {
            double result;
            long diferenca = dataFinal.getTime() - dataInicial.getTime();
            double diferencaEmDias = (diferenca / 1000) / 60 / 60 / 24; //resultado é diferença entre as datas em dias
            long horasRestantes = (diferenca / 1000) / 60 / 60 % 24; //calcula as horas restantes
            result = diferencaEmDias + (horasRestantes / 24d); //transforma as horas restantes em fração de dias

            return truncarValor(result, 0);
        }

        /**
         * Calcula a diferença de duas datas em horas <br> <b>Importante:</b>
         * Quando realiza a diferença em horas entre duas datas, este método
         * considera os minutos restantes e os converte em fração de horas.
         *
         * @param dataInicial
         * @param dataFinal
         * @return quantidade de horas existentes entre a dataInicial e
         * dataFinal.
         */
        public static double diferencaEmHoras(Date dataInicial, Date dataFinal) {
            double result;
            long diferenca = dataFinal.getTime() - dataInicial.getTime();
            long diferencaEmHoras = (diferenca / 1000) / 60 / 60;
            long minutosRestantes = (diferenca / 1000) / 60 % 60;
            double horasRestantes = minutosRestantes / 60d;
            result = diferencaEmHoras + (horasRestantes);

            return result;
        }

        /**
         * Calcula a diferença de duas datas em minutos <br> <b>Importante:</b>
         * Quando realiza a diferença em minutos entre duas datas, este método
         * considera os segundos restantes e os converte em fração de minutos.
         *
         * @param dataInicial
         * @param dataFinal
         * @return quantidade de minutos existentes entre a dataInicial e
         * dataFinal.
         */
        public static double diferencaEmMinutos(Date dataInicial, Date dataFinal) {
            double result;
            long diferenca = dataFinal.getTime() - dataInicial.getTime();
            double diferencaEmMinutos = (diferenca / 1000) / 60; //resultado é diferença entre as datas em minutos
            long segundosRestantes = (diferenca / 1000) % 60; //calcula os segundos restantes
            result = diferencaEmMinutos + (segundosRestantes / 60d); //transforma os segundos restantes em minutos

            return result;
        }

        public static int diferencaEmMeses(Date data1, Date data2) {
            if (data1 == null || data2 == null) {
                return 0;
            }

            if (Data.extrairAno(data1) == Data.extrairAno(data2)) {
                return Data.extrairMes(data2) - Data.extrairMes(data1) + 1;
            } else {
                int retorno = 0;

                retorno += (Data.extrairAno(data2) - Data.extrairAno(data1) - 1) * 12;
                retorno += 12 - Data.extrairMes(data1);
                retorno += Data.extrairMes(data2);

                return retorno;
            }
        }

        public static int obterUltimoDiaMes(int mes, int ano) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(new Date(ano, mes - 1, 1));

            int dia = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            return dia;
        }

        public static int obterIdade(Date data_nascimento) {
            int anoAtual = Data.extrairAno(new Date());
            int anoNascimento = Data.extrairAno(data_nascimento);
            int idade = anoAtual - anoNascimento;
            return idade;
        }

        public static Date obterPrimeiroDiaSemana(Date data) {
            Calendar agora = Calendar.getInstance();
            agora.setTime(data);

            int dia = agora.get(Calendar.DAY_OF_WEEK);
            int dif_dom = 0;
            if (dia != 1) {
                dif_dom = ((dia - 1) * -1);
            }

            agora.add(Calendar.DAY_OF_MONTH, dif_dom);

            return agora.getTime();
        }

        public static Date obterUltimoDiaSemana(Date data) {
            Calendar agora = Calendar.getInstance();
            agora.setTime(data);

            int dia = agora.get(Calendar.DAY_OF_WEEK);
            int dif_sab = 0;
            if (dia != 7) {
                dif_sab = 7 - dia;
            }

            agora.add(Calendar.DAY_OF_MONTH, dif_sab);

            return agora.getTime();
        }

        public static String formatarDataJSON(String data) {
            StringBuilder dateBuilder = new StringBuilder();
            dateBuilder = dateBuilder.append(data.substring(8, 10)).append("/")
                    .append(data.substring(5, 7)).append("/")
                    .append(data.substring(0, 4));
            return dateBuilder.toString();
        }

        public static String formatarDataJSON(Object data) {
            if (data == null) {
                return null;
            }
            if (data.getClass() == String.class) { // se for uma data em string, deve ser convertida
                if (isDate(data) == false) {
                    return null;
                } else { // se a data nao for string, deve estar em formato nativo
                    SimpleDateFormat f = new SimpleDateFormat(FORMATO_DTBR);
                    try {
                        return extrairStr(new java.sql.Date(f.parse((String) data).getTime()));
                    } catch (ParseException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            } else {
                String formato = "yyyy-MM-dd";
                SimpleDateFormat f = new SimpleDateFormat(formato);
                return f.format(data);
            }
        }

    }

    public static class Texto {

        public static String alinharDireita(Object obj, int tamanho) {
            String out = "";
            String obj_ = extrairStr(obj);
            int fim = tamanho - 1;
            int j = obj_.length();
            for (int i = fim; i >= 0; i--) {
                j--;
                if (j < 0) {
                    out = ' ' + out;
                } else {
                    out = obj_.charAt(j) + out;
                }
            }
            return out;
        }

        public static String removerAcentos(String str) {
            if (str != null) {
                str = Normalizer.normalize(str, Normalizer.Form.NFD);
                str = str.replaceAll("[^\\p{ASCII}]", "");
                return str;
            } else {
                return "";
            }
        }

        public static String removerPontos(String str) {
            if (str != null) {
                str = str.trim();
                str = str.replaceAll("[^0-9]", "");
                return str;
            } else {
                return "";
            }
        }

        public static String alinharEsquerda(Object obj, int tamanho) {
            String out = "";
            String obj_ = extrairStr(obj);

            for (int i = 0; i < tamanho; i++) {
                if (i < obj_.length()) {
                    out += obj_.charAt(i);
                } else {
                    out += ' ';
                }
            }
            return out;
        }

        public static String strZero(Object obj, int tamanho) {
            StringBuffer sb = new StringBuffer(tamanho);
            String strValue = extrairStr(obj);
            strValue = Texto.desmascarar(".", strValue);
            int diff = tamanho - strValue.length();
            if (diff < 0) {
                return new String(sb);
            }
            while (diff > 0) {
                sb.append('0');
                diff--;
            }
            sb.append(strValue);

            return new String(sb);
        }

        public static String mascarar(String mask, String value) {
            if (value == null) {
                return "";
            }
            StringBuffer sb = new StringBuffer(value.length() + 4);
            int j = 0;
            for (int i = 0; i < mask.length(); i++) {
                if (j >= value.length()) {
                    break;
                } else if (mask.charAt(i) == '0' || mask.charAt(i) == '#') {
                    sb.append(value.charAt(j));
                    j++;
                } else if (mask.charAt(i) == '*') {
                    sb.append(value.substring(j, value.length()));
                    return new String(sb);
                } else {
                    sb.append(mask.charAt(i));
                }
            }
            return new String(sb);
        }

        public static String desmascarar(String mascara, String valor) {
            if (valor == null) {
                return null;
            }
            HashMap hm = new HashMap();
            for (int j = 0; j
                    < mascara.length(); j++) {
                if ((mascara.charAt(j) == '0' || mascara.charAt(j) == '#') == false) {
                    hm.put(mascara.charAt(j), null);
                }

            }

            StringBuffer sb = new StringBuffer(valor);
            for (int j = 0; j
                    < sb.length(); j++) {
                if (hm.containsKey(sb.charAt(j))) {
                    sb.delete(j, j + 1);
                    j--;

                }
            }
            return new String(sb);
        }

        public static String quotarStr(Object str) {
            String s = str == null ? "" : str.toString();
            StringBuffer sb = new StringBuffer(s.length() + 8);
            sb.append(s);
            for (int i = 0; i < sb.length(); i++) {
                if (sb.charAt(i) == '\'') {
                    sb.insert(i++, '\'');
                }
            }
            sb.insert(0, '\'');
            sb.append('\'');
            return new String(sb);
        }

        public static String capitularizar(String value) {
            String result = "";
            String[] nomes = value.split(" ");

            for (String palavra : nomes) {
                result += palavra.substring(0, 1).toUpperCase() + palavra.substring(1).toLowerCase() + " ";
            }
            return result.trim();
        }

        public static String abreviarString(String str, Integer n) {
            if (str == null || n == null) {
                return "";
            }
            return (str.length() < n ? str : str.substring(0, n));
        }
    }

    public static class Email {

        public static class Configuracao {

            private String emailSolicitacao;
            private String emailHostname;
            private String emailPorta;
            private String emailUsuario;
            private String emailSenha;
            private String emailStarttls;
            private Boolean emailAutenticado;

            public Configuracao(String emailSolicitacao, String emailHostname, String emailPorta, String emailUsuario, String emailSenha, String emailStarttls, Boolean emailAutenticado) {
                this.emailSolicitacao = emailSolicitacao;
                this.emailHostname = emailHostname;
                this.emailPorta = emailPorta;
                this.emailUsuario = emailUsuario;
                this.emailSenha = emailSenha;
                this.emailStarttls = emailStarttls;
                this.emailAutenticado = emailAutenticado;
            }

            public String getEmailSolicitacao() {
                return emailSolicitacao;
            }

            public void setEmailSolicitacao(String emailSolicitacao) {
                this.emailSolicitacao = emailSolicitacao;
            }

            public String getEmailHostname() {
                return emailHostname;
            }

            public void setEmailHostname(String emailHostname) {
                this.emailHostname = emailHostname;
            }

            public String getEmailPorta() {
                return emailPorta;
            }

            public void setEmailPorta(String emailPorta) {
                this.emailPorta = emailPorta;
            }

            public String getEmailUsuario() {
                return emailUsuario;
            }

            public void setEmailUsuario(String emailUsuario) {
                this.emailUsuario = emailUsuario;
            }

            public String getEmailSenha() {
                return emailSenha;
            }

            public void setEmailSenha(String emailSenha) {
                this.emailSenha = emailSenha;
            }

            public String getEmailStarttls() {
                return emailStarttls;
            }

            public void setEmailStarttls(String emailStarttls) {
                this.emailStarttls = emailStarttls;
            }

            public Boolean getEmailAutenticado() {
                return emailAutenticado;
            }

            public void setEmailAutenticado(Boolean emailAutenticado) {
                this.emailAutenticado = emailAutenticado;
            }

        }

        public static boolean validarEmail(String email) {
            if (email != null && !email.isEmpty()) {
                Matcher matcher = PATTERN_EMAIL.matcher(email);
                return (matcher.matches());
            } else {
                return false;
            }
        }

        public static void dispararEmail(Configuracao p, String assunto, String mensagem, String destino)
                throws EddyServerException, Exception {
            dispararEmail(p, assunto, mensagem, destino, null, null, null, false);
        }

        public static void dispararEmail(Configuracao p, String assunto, String mensagem, String destino, boolean debug)
                throws EddyServerException, Exception {
            dispararEmail(p, assunto, mensagem, destino, null, null, null, debug);
        }

        public static void dispararEmail(Configuracao p, String assunto, String mensagem, String destino, byte[] anexo, String nomeArquivo, String tipoArquivo)
                throws EddyServerException, Exception {
            dispararEmail(p, assunto, mensagem, destino, anexo, nomeArquivo, tipoArquivo, false);
        }

        public static void dispararEmail(final Configuracao p, String assunto, String mensagem, String destino, byte[] anexo, String nomeArquivo, String tipoArquivo, boolean debug)
                throws EddyServerException, Exception {
            if (p == null
                    || p.getEmailUsuario() == null || "".equals(p.getEmailUsuario())
                    || p.getEmailSenha() == null || "".equals(p.getEmailSenha())
                    || p.getEmailHostname() == null || "".equals(p.getEmailHostname())
                    || p.getEmailSolicitacao() == null || "".equals(p.getEmailSolicitacao())
                    || p.getEmailPorta() == null) {
                throw new EddyServerException("Parâmetro de e-mail não configurado corretamente");
            }
            if (!validarEmail(p.getEmailSolicitacao())) {
                throw new EddyServerException("E-mail de origem inválido");
            }
            if (!validarEmail(destino)) {
                throw new EddyServerException("E-mail de destino inválido");
            }

            String saida_debug = "";
            Properties properties = new Properties();
            properties.put("mail.smtp.host", p.getEmailHostname());
            properties.put("mail.smtp.port", p.getEmailPorta());
            properties.put("mail.smtp.auth", (p.getEmailAutenticado() ? "true" : "false"));
            properties.put("mail.smtp.starttls.enable", ("3".equals(p.getEmailStarttls()) ? "true" : "false"));

            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            properties.put("mail.smtp.socketFactory.port", p.getEmailPorta());
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.socketFactory.fallback", "true");
            properties.put("mail.smtp.ssl.socketFactory", sf);
            if (debug) {
                saida_debug += "\nPropriedades: " + properties.toString();
            }

            Authenticator auth = new Authenticator() {
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(p.getEmailUsuario(), p.getEmailSenha());
                }
            };
            if (debug) {
                saida_debug += "\nAutenticação: usuario(" + p.getEmailUsuario() + ") senha(" + p.getEmailSenha() + ")";
            }
            Session session = Session.getInstance(properties, auth);

            MimeMessage msg = new MimeMessage(session);
            Multipart mp = new MimeMultipart();

            if (anexo != null) {
                MimeBodyPart mbp = new MimeBodyPart();
                ByteArrayDataSource data;
                data = new ByteArrayDataSource(new ByteArrayInputStream(anexo), tipoArquivo + "; charset=utf-8");
                mbp.setDataHandler(new DataHandler(data));
                mbp.setFileName(nomeArquivo);

                mp.addBodyPart(mbp);

                if (debug) {
                    saida_debug += "\nAnexo: " + nomeArquivo + " - " + data.toString();
                }
            }

            MimeBodyPart mbp2 = new MimeBodyPart();
            mbp2.setContent(new String(mensagem.getBytes("utf-8"), "utf-8"), "text/html; charset=utf-8");
            mp.addBodyPart(mbp2);

            msg.setFrom(new InternetAddress(p.getEmailSolicitacao()));
            if (debug) {
                saida_debug += "\nORIGEM: " + p.getEmailSolicitacao();
            }

            InternetAddress[] addressTo = new InternetAddress[1];
            addressTo[0] = new InternetAddress(destino);
            if (debug) {
                saida_debug += "\nDESTINO: " + destino;
            }

            msg.setRecipients(Message.RecipientType.TO, addressTo);
            msg.setSentDate(new Date());
            msg.setSubject(new String(assunto.getBytes("utf-8"), "utf-8"), "utf-8");
            msg.setContent(mp);

            if (debug) {
                saida_debug += "\nAssunto: " + new String(assunto.getBytes("utf-8"), "utf-8");
            }

            if (debug) {
                System.out.println(saida_debug);
            }

            try {
                Transport.send(msg);
                if (debug) {
                    System.out.println("E-mail disparado com sucesso para o destino " + destino);
                }
            } catch (Exception ex) {
                throw new Exception("Não foi possível disparar email: \n" + ex.getMessage());
            }
        }
    }

    public static class Web {

        public static void executarJavascript(String script) {
            RequestContext.getCurrentInstance().execute(script);
        }

        public static void criarObjetoDeSessao(Object obj, String nome) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
            session.setAttribute(nome, obj);
        }

        public static Object pegarObjetoDaSessao(String nomeSessao) {
            HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            return sessao.getAttribute(nomeSessao);
        }

        public static void redirecionarPagina(String pagina) {
            String url = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(url + "/" + pagina);
            } catch (IOException ex) {
                System.out.println("Não foi possível redirecionar para a página " + pagina + "\n" + ex.getMessage());
            }
        }

        public static void atualizarObjeto(String id) {
            RequestContext.getCurrentInstance().update(id);
        }

        public static String obterCaminhoImagem(String nome_arquivo, CroppedImage imagem) throws Exception {
            return obterCaminhoImagem(nome_arquivo, (imagem == null ? null : imagem.getBytes()));
        }

        public static String obterCaminhoImagem(String nome_arquivo, CaptureEvent imagem) throws Exception {
            return obterCaminhoImagem(nome_arquivo, (imagem == null ? null : imagem.getData()));
        }

        public static String obterCaminhoImagem(String nome_arquivo, byte[] imagem) throws Exception {
            String caminhoImagem = "";
            nome_arquivo = (nome_arquivo == null || nome_arquivo.trim().isEmpty() ? "imagem.png" : nome_arquivo);

            if (imagem == null) {
                caminhoImagem = "resources/img/sem_foto.png";
            } else {
                try {
                    FacesContext context = FacesContext.getCurrentInstance();
                    ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
                    String imageUsers = servletContext.getRealPath("/resources/img");
                    File dirImageUsers = new File(imageUsers);
                    if (!dirImageUsers.exists()) {
                        dirImageUsers.createNewFile();
                    }

                    try (FileImageOutputStream imageOutput = new FileImageOutputStream(new File(dirImageUsers, nome_arquivo))) {
                        imageOutput.write(imagem, 0, imagem.length);
                        imageOutput.flush();
                    }

                    caminhoImagem = "resources/img/" + nome_arquivo;
                } catch (IOException ex) {
                    throw new Exception(ex.getMessage());
                }
            }
            return caminhoImagem;
        }

        public static Flash flashScope() {
            return (FacesContext.getCurrentInstance().getExternalContext().getFlash());
        }

        public static void adicionarCookie(String name, String value, int expiry) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
            Cookie cookie = null;

            Cookie[] userCookies = request.getCookies();
            if (userCookies != null && userCookies.length > 0) {
                for (Cookie userCookie : userCookies) {
                    if (userCookie.getName().equals(name)) {
                        cookie = userCookie;
                        break;
                    }
                }
            }

            if (cookie != null) {
                cookie.setValue(value);
            } else {
                cookie = new Cookie(name, value);
                cookie.setPath(request.getContextPath());
            }

            cookie.setMaxAge(expiry);

            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.addCookie(cookie);
        }

        public static Cookie obterCookie(String name) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
            Cookie cookie;

            Cookie[] userCookies = request.getCookies();
            if (userCookies != null && userCookies.length > 0) {
                for (Cookie userCookie : userCookies) {
                    if (userCookie.getName().equals(name)) {
                        cookie = userCookie;
                        return cookie;
                    }
                }
            }
            return null;
        }

        public static void destruirCookie(String nome) {
            adicionarCookie(nome, null, 0);
        }

        public boolean paginaAtual(String nomeXhtml) {
            if (nomeXhtml == null) {
                return false;
            }
            ServletRequest request = ((HttpServletRequest) (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            return httpRequest.getRequestURI().replace(".xhtml", "").endsWith(nomeXhtml);
        }
    }
}
