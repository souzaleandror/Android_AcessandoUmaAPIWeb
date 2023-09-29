package br.com.alura.estoque.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.alura.estoque.R;
import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.database.EstoqueDatabase;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.repository.ProdutoRepository;
import br.com.alura.estoque.retrofit.callback.BaseCallback;
import br.com.alura.estoque.ui.dialog.EditaProdutoDialog;
import br.com.alura.estoque.ui.dialog.SalvaProdutoDialog;
import br.com.alura.estoque.ui.recyclerview.adapter.ListaProdutosAdapter;
import retrofit2.Response;

public class ListaProdutosActivity extends AppCompatActivity {

    private static final String TITULO_APPBAR = "Lista de produtos";
    private ListaProdutosAdapter adapter;
    private ProdutoDAO dao;
    private Response<List<Produto>> execute;
    private ProdutoRepository repository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produtos);
        setTitle(TITULO_APPBAR);

        configuraListaProdutos();
        configuraFabSalvaProduto();

        EstoqueDatabase db = EstoqueDatabase.getInstance(this);
        dao = db.getProdutoDAO();

        repository = new ProdutoRepository(dao);
        repository.buscaProdutos(new ProdutoRepository.DadosCarregadosCallback<List<Produto>>() {
            @Override
            public void quandoSucesso(List<Produto> produtosNovos) {
                adapter.atualiza(produtosNovos);
            }

            @Override
            public void quandoFalha(String erro) {
                Toast.makeText(ListaProdutosActivity.this, "Nao foi possivel carregar os produtos novos" + erro, Toast.LENGTH_SHORT).show();
                Toast.makeText(ListaProdutosActivity.this, "Error: " + erro, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void configuraListaProdutos() {
        RecyclerView listaProdutos = findViewById(R.id.activity_lista_produtos_lista);
        adapter = new ListaProdutosAdapter(this, this::abreFormularioEditaProduto);
        listaProdutos.setAdapter(adapter);
        adapter.setOnItemClickRemoveContextMenuListener(this::remove);
    }

    private void remove(int posicao,
                        Produto produtoRemovido) {
        new BaseAsyncTask<>(() -> {
            dao.remove(produtoRemovido);
            return null;
        }, resultado -> adapter.remove(posicao))
                .execute();
    }

    private void configuraFabSalvaProduto() {
        FloatingActionButton fabAdicionaProduto = findViewById(R.id.activity_lista_produtos_fab_adiciona_produto);
        fabAdicionaProduto.setOnClickListener(v -> abreFormularioSalvaProduto());
    }

    private void abreFormularioSalvaProduto() {
        new SalvaProdutoDialog(this, produtoCriado -> repository.salva(produtoCriado, new ProdutoRepository.DadosCarregadosCallback<Produto>() {
            @Override
            public void quandoSucesso(Produto produtoSalvo) {
                adapter.adiciona(produtoSalvo);
            }

            @Override
            public void quandoFalha(String erro) {
                Toast.makeText(ListaProdutosActivity.this, "Nao foi possivel salvar o produto" + erro, Toast.LENGTH_SHORT).show();
                Toast.makeText(ListaProdutosActivity.this, "Error: " + erro, Toast.LENGTH_LONG).show();
            }
        })).mostra();
    }

    private void abreFormularioEditaProduto(int posicao, Produto produto) {
        new EditaProdutoDialog(this, produto,
                produtoCriado -> repository.edita(produtoCriado, new ProdutoRepository.DadosCarregadosCallback<Produto>() {
                    @Override
                    public void quandoSucesso(Produto produtoEditado) {
                        adapter.edita(posicao, produtoEditado);
                    }

                    @Override
                    public void quandoFalha(String erro) {
                        Toast.makeText(ListaProdutosActivity.this, "Nao foi possivel editar o produto" + erro, Toast.LENGTH_SHORT).show();
                        Toast.makeText(ListaProdutosActivity.this, "Error: " + erro, Toast.LENGTH_LONG).show();
                    }
                }))
                .mostra();
    }

}
