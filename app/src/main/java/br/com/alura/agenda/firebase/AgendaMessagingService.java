package br.com.alura.agenda.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import br.com.alura.agenda.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgendaMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        Log.d("token firebase", "Refreshed token: " + token);
        enviaTokenParaServidor(token);
    }

    private void enviaTokenParaServidor(final String token) {
// token recebido        fanRBoIggOM:APA91bF-md0DpmdQRhdBNu-Wwg6XC45A2MfkqPfG2OlE_A2AwLXhFT4PJq6y1KXcItZFd8_xZ_izuY8SVkqGBQqpVys-0wUEdQEIFXzy15ykbGBzcja1FMdvNZKeLAtwyiO0sTDmSdl8
        Call<Void> call = new RetrofitInicializador().getDispositivoService().enviaToken(token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i("token enviado", token);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("token falhou", t.getMessage());
            }
        });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> mensagem = remoteMessage.getData();
        Log.i("mensagem recebida", String.valueOf(mensagem));
    }
}
