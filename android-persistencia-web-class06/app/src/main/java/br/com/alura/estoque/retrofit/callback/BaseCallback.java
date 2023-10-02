package br.com.alura.estoque.retrofit.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class BaseCallback <T> implements Callback<T> {

    private final CallbackComRetorno callback;

    public BaseCallback(CallbackComRetorno callback) {
        this.callback = callback;
    }

    @Override
    @EverythingIsNonNull
    public void onResponse(Call<T> call, Response<T> response) {
        if(response.isSuccessful()) {
            T resultado = response.body();
            if(resultado != null) {
                // Notifica que tem resposta com sucesso
                callback.quandoSucesso(resultado);
            }
        } else {
            // Notifica falha
            callback.quandoFalha("Resposta nao sucedida");
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call<T> call, Throwable t) {
            // Notifica falha
        callback.quandoFalha("Falha de comunicacao: " + t.getMessage());
    }

    public interface CallbackComRetorno<T> {
        void quandoSucesso(T resultado);
        void quandoFalha(String erro);
    }
}
