package br.com.alura.estoque.repository;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.retrofit.EstoqueRetrofit;
import br.com.alura.estoque.retrofit.service.ProdutoService;
import retrofit2.Call;
import retrofit2.Response;

public class ProdutoRepository {
    private final ProdutoDAO dao;
    public ProdutoRepository(ProdutoDAO dao) {
        this.dao = dao;

    }

    public void buscaProdutos(ProdutoCarregadosListener listener) {
        buscaProdutosInternos(listener);
    }

    private void buscaProdutosInternos(ProdutoCarregadosListener listener) {
        new BaseAsyncTask<>(dao::buscaTodos, resultado -> {
            //Notifica que o dado esta pronto
            listener.quandoCarregados(resultado);
            buscaProdutosNaAPI(listener);
        }).execute();
    }

    private void buscaProdutosNaAPI(ProdutoCarregadosListener listener) {
        ProdutoService service = new EstoqueRetrofit().getProdutoService();
        Call<List<Produto>> call = service.buscaTodos();
        new BaseAsyncTask<>(() -> {
            try {
                Response<List<Produto>> resposta = call.execute();
                List<Produto> produtosNovos = resposta.body();
                dao.salvaProdutos(produtosNovos);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return dao.buscaTodos();
        }, listener::quandoCarregados).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public interface ProdutoCarregadosListener {
        void quandoCarregados(List<Produto> produtos);
    }
}
