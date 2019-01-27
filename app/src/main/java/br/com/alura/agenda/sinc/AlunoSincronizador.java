package br.com.alura.agenda.sinc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.alura.agenda.ListaAlunosActivity;
import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.dto.AlunoSync;
import br.com.alura.agenda.event.AtualizaListaAlunoEvent;
import br.com.alura.agenda.modelo.Aluno;
import br.com.alura.agenda.preferences.AlunoPreferences;
import br.com.alura.agenda.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlunoSincronizador {
    private final Context context;
    private EventBus bus = EventBus.getDefault();
    private AlunoPreferences preferences;

    public AlunoSincronizador(Context context) {
        this.context = context;
        preferences = new AlunoPreferences(context);
    }

    public void buscaAlunosDoWebService(){
        if (preferences.temVersao()){
            buscaNovos();
        }else {
            buscaTodos();
        }
    }

    private void buscaNovos() {
        String versao = preferences.getVersao();
        Call<AlunoSync> call = new RetrofitInicializador().getAlunoService().novos(versao);
        call.enqueue(buscaAlunosCallback());
    }

    private void buscaTodos() {
        Call<AlunoSync> call = new RetrofitInicializador().getAlunoService().lista();

        call.enqueue(buscaAlunosCallback());
    }

    @NonNull
    private Callback<AlunoSync> buscaAlunosCallback() {
        return new Callback<AlunoSync>() {
            @Override
            public void onResponse(Call<AlunoSync> call, Response<AlunoSync> response) {
                //// O parâmetro call => define todas as informações da requisição
                //// O parâmetro response => obtém a resposta que o servidor retornou
                AlunoSync alunoSync = response.body();

                sincroniza(alunoSync);
//                context.carregaLista();
//                context.getSwipe().setRefreshing(false);

//                Log.i("versao", preferences.getVersao());

                bus.post(new AtualizaListaAlunoEvent());

                sincronizaAlunosInternos();
            }

            @Override
            public void onFailure(Call<AlunoSync> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());
//                context.getSwipe().setRefreshing(false);
                bus.post(new AtualizaListaAlunoEvent());
            }
        };
    }

    public void sincroniza(AlunoSync alunoSync) {
        String versao = alunoSync.getMomentoDaUltimaModificacao();

        Log.i("versao externa", versao);

        if (temVersaoNova(versao)){

            preferences.salvaVersao(versao);

            Log.i("versao atual", preferences.getVersao());

            List<Aluno> alunoList = alunoSync.getAlunoList();
            AlunoDAO dao = new AlunoDAO(context);
            dao.sincroniza(alunoList);
            dao.close();
        }
    }

    private boolean temVersaoNova(String versao) {
        if (!preferences.temVersao())
            return true;
//        "2019-01-27T20:21:16.492" - formato
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            Date dataExterna = format.parse(versao);
            String versaoInterna = preferences.getVersao();

            Log.i("versao interna", versaoInterna);
            
            Date dataInterna = format.parse(versaoInterna);

            return dataExterna.after(dataInterna);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void sincronizaAlunosInternos(){
        final AlunoDAO dao = new AlunoDAO(context);
        List<Aluno> listaNaoSincronizados = dao.listaNaoSincronizados();
        dao.close();

        Call<AlunoSync> call = new RetrofitInicializador().getAlunoService().atualiza(listaNaoSincronizados);
        call.enqueue(new Callback<AlunoSync>() {
            @Override
            public void onResponse(Call<AlunoSync> call, Response<AlunoSync> response) {
                AlunoSync alunoSync = response.body();
//                dao.sincroniza(alunoSync.getAlunoList());
//                dao.close();
                sincroniza(alunoSync);
            }

            @Override
            public void onFailure(Call<AlunoSync> call, Throwable t) {

            }
        });
    }

    public void deleta(final Aluno aluno) {
        Call<Void> call = new RetrofitInicializador().getAlunoService().deleta(aluno.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                AlunoDAO dao = new AlunoDAO(context);
                dao.deleta(aluno);
                dao.close();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(context,
//                        "Não foi possível remover o aluno", Toast.LENGTH_SHORT).show();
            }
        });
    }
}