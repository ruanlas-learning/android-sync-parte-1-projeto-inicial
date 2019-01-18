package br.com.alura.agenda.web;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by renan on 20/01/16.
 */
public class WebClient {
    public String post(String json) {
        String endereco = "https://www.caelum.com.br/mobile";
        return realizaConexao(json, endereco);
    }

//    configuração https
///    É importante ressaltar que essa é a configuração mais objetiva e não recomendada pela
///      equipe de desenvolvedores do Android. Considerando que no curso não vamos lidar com um ambiente
///      de produção, não tem nenhum problema.
//
///    Porém, se tiver que lidar com um ambiente em produção, a abordagem esperada é configurar
///      subdomínio específicos que podem acessar HTTP caso precise testar
////
////    https://developer.android.com/training/articles/security-config
////    https://blog.alura.com.br/qual-e-diferenca-entre-http-e-https/

////    https://developer.android.com/about/versions/pie/android-9.0-changes-all?hl=pt-br

    public void insere(String json) {
//        String endereco = "http://localhost:8080/api/aluno";
        String endereco = "http://192.168.1.34:8080/api/aluno";
        realizaConexao(json, endereco);
    }

    @Nullable
    private String realizaConexao(String json, String endereco) {
        try {
            URL url = new URL(endereco);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            connection.setDoOutput(true);

            PrintStream output = new PrintStream(connection.getOutputStream());
            output.println(json);

            connection.connect();

            Scanner scanner = new Scanner(connection.getInputStream());
            String resposta = scanner.next();
            return resposta;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
