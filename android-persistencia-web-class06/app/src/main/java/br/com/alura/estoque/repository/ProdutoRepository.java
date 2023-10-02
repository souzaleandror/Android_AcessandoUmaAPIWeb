package br.com.alura.estoque.repository;

import android.content.Context;

import java.util.List;

import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.database.EstoqueDatabase;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.retrofit.EstoqueRetrofit;
import br.com.alura.estoque.retrofit.callback.BaseCallback;
import br.com.alura.estoque.retrofit.callback.CallbackSemRetorno;
import br.com.alura.estoque.retrofit.service.ProdutoService;
import retrofit2.Call;

public class ProdutoRepository {
    private final ProdutoDAO dao;
    private ProdutoService service;

    public ProdutoRepository(Context context) {
        EstoqueDatabase db = EstoqueDatabase.getInstance(context);
        dao = db.getProdutoDAO();
        service = new EstoqueRetrofit().getProdutoService();
    }

    public void buscaProdutos(DadosCarregadosCallback<List<Produto>> callback) {
        buscaProdutosInternos(callback);
    }

    private void buscaProdutosInternos(DadosCarregadosCallback<List<Produto>> callback) {
        new BaseAsyncTask<>(dao::buscaTodos, resultado -> {
            //Notifica que o dado esta pronto
            callback.quandoSucesso(resultado);
            buscaProdutosNaAPI(callback);
        }).execute();
    }

    private void buscaProdutosNaAPI(DadosCarregadosCallback<List<Produto>> callback) {
        Call<List<Produto>> call = service.buscaTodos();

        call.enqueue(new BaseCallback<>(new BaseCallback.CallbackComRetorno<List<Produto>>() {
            @Override
            public void quandoSucesso(List<Produto> produtosNovos) {
                atualizaInterno(produtosNovos, callback);
            }

            @Override
            public void quandoFalha(String erro) {
                callback.quandoFalha(erro);
            }
        }));

//        call.enqueue(new Callback<List<Produto>>() {
//            @Override
//            @EverythingIsNonNull
//            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {
//                if(response.isSuccessful()) {
//                    List<Produto> produtosNovos = response.body();
//                    if(produtosNovos != null) {
//                        atualizaInterno(produtosNovos, callback);
//                    }
//                } else {
//                    callback.quandoFalha("Resposta nao sucedida");
//                }
//            }
//
//            @Override
//            @EverythingIsNonNull
//            public void onFailure(Call<List<Produto>> call, Throwable t) {
//                callback.quandoFalha("Falha de comunicacao: " + t.getMessage());
//            }
//        });

//        new BaseAsyncTask<>(() -> {
//            try {
//                Response<List<Produto>> resposta = call.execute();
//                List<Produto> produtosNovos = resposta.body();
//                dao.salvaProdutos(produtosNovos);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            return dao.buscaTodos();
//        }, listener::quandoCarregados).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void atualizaInterno(List<Produto> produtosNovos, DadosCarregadosCallback<List<Produto>> callback) {
        new BaseAsyncTask<>(() -> {
            dao.salvaProdutos(produtosNovos);
            return dao.buscaTodos();
        }, callback::quandoSucesso).execute();
    }

    public interface ProdutoCarregadosListener {
        void quandoCarregados(List<Produto> produtos);
    }

    public void salva(Produto produto, DadosCarregadosCallback<Produto> callback) {
        salvaNaApi(produto, callback);
    }

    private void salvaNaApi(Produto produto, DadosCarregadosCallback<Produto> callback) {
        Call<Produto> call = service.salva(produto);
        call.enqueue(new BaseCallback<>(new BaseCallback.CallbackComRetorno<Produto>() {
            @Override
            public void quandoSucesso(Produto produtoSalvo) {
                salvaInterno(produtoSalvo, callback);
            }

            @Override
            public void quandoFalha(String erro) {
                callback.quandoFalha(erro);
            }
        }));
    }

    public void edita(Produto produto, DadosCarregadosCallback<Produto> callback) {
        Call<Produto> call = service.edita(produto.getId(), produto);
        editaNaApi(produto, callback, call);
    }

    private void editaNaApi(Produto produto, DadosCarregadosCallback<Produto> callback, Call<Produto> call) {
        call.enqueue(new BaseCallback<>(new BaseCallback.CallbackComRetorno<Produto>() {
            @Override
            public void quandoSucesso(Produto resultado) {
                editaInterno(produto, callback);
            }

            @Override
            public void quandoFalha(String erro) {
                callback.quandoFalha(erro);
            }
        }));
    }

    private void editaInterno(Produto produto, DadosCarregadosCallback<Produto> callback) {
        new BaseAsyncTask<>(() -> {
            dao.atualiza(produto);
            return produto;
        }, callback::quandoSucesso)
                .execute();
    }

    public void salvaInterno(Produto produtoSalvo, DadosCarregadosCallback<Produto> callback) {
        new BaseAsyncTask<>(() -> {
            long id = dao.salva(produtoSalvo);
            return dao.buscaProduto(id);
        }, callback::quandoSucesso)
                .execute();
    }

    public void remove(Produto produto, DadosCarregadosCallback<Void> callback) {
        removeNaApi(produto, callback);
    }

    private void removeNaApi(Produto produto, DadosCarregadosCallback<Void> callback) {
        Call<Void> call = service.remove(produto.getId());
        call.enqueue(new CallbackSemRetorno(new CallbackSemRetorno.RespostaCallback() {
            @Override
            public void quandoSucesso() {
                removeInterno(produto, callback);
            }

            @Override
            public void quandoFalha(String erro) {
                callback.quandoFalha(erro);
            }
        }));
    }

    private void removeInterno(Produto produto, DadosCarregadosCallback<Void> callback) {
        new BaseAsyncTask<>(() -> {
            dao.remove(produto);
            return null;
        }, callback::quandoSucesso)
                .execute();
    }

    public interface DadosCarregadosCallback<T> {
        void quandoSucesso(T resultado);
        void quandoFalha(String erro);
    }
}
