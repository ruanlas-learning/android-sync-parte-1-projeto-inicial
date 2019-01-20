package br.com.alura.agenda.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import br.com.alura.agenda.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// FirebaseInstanceIdService is deprecated.
// Sua nova implementação está em AgendaMessagingService que extende de FirebaseMessagingService
public class AgendaInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("token firebase", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        enviaTokenParaServidor(refreshedToken);
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
}
