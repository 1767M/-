import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class HtmlParser {
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();


    public static void main(String[] args) throws IOException, InterruptedException {
        // формат текущий даты
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String data1 = df.format(new Date());
        //электронный адрес нац. банка
        String http = "https://www.nationalbank.kz/rss/get_rates.cfm?fdate=";
        String adress = http + data1;
        System.out.println(adress);

        //Http  запрос
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(adress))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String ss =response.body();

        // Парсинг c jsoup
        Document doc = Jsoup.parse(ss, "", Parser.xmlParser());

        //Массив
        ArrayList<CurrencyRate> Tenge = new ArrayList<CurrencyRate>();
        int i = 0;

        for (Element e : doc.select("item")) {

           i = i + 1;


           String getCode = e.getElementsByTag("title").text();
           String str = e.getElementsByTag("description").text();
           double Value = Double.parseDouble(str);

           CurrencyRate Valuta = new  CurrencyRate();
           Valuta.currencyCode = "Валюта: "+getCode +"\nКурс = "+ Value;


           Tenge.add(Valuta);

        }

        System.out.println(Tenge.get(3).currencyCode );


    }

        public static HttpRequest.BodyPublisher ofFormData (Map < Object, Object > data){
            var builder = new StringBuilder();
            for (Map.Entry<Object, Object> entry : data.entrySet()) {
                if (builder.length() > 0) {
                    builder.append("&");
                }
                builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
                builder.append("=");
                builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
            }
            return HttpRequest.BodyPublishers.ofString(builder.toString());
        }
    }




