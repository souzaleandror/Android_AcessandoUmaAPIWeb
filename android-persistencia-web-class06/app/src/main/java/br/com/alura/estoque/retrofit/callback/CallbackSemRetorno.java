package br.com.alura.estoque.retrofit.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class CallbackSemRetorno implements Callback<Void> {

    private final RespostaCallback callback;

    public CallbackSemRetorno(RespostaCallback callback) {
        this.callback = callback;
    }

    @Override
    @EverythingIsNonNull
    public void onResponse(Call<Void> call, Response<Void> response) {
        if(response.isSuccessful()) {
            // notifica sucesso
            callback.quandoSucesso();
        } else {
            // notifica falha
            callback.quandoFalha("Resposta nao sucedida");
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call<Void> call, Throwable t) {
        // notifica falha
        callback.quandoFalha("Falha de comunicacao: " + t.getMessage());
    }
    public interface RespostaCallback {
        void quandoSucesso();
        void quandoFalha(String erro);
    }
}
