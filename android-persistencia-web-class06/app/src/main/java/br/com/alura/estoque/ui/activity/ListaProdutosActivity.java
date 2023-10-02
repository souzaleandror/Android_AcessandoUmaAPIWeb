package br.com.alura.estoque.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.alura.estoque.R;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.repository.ProdutoRepository;
import br.com.alura.estoque.ui.dialog.EditaProdutoDialog;
import br.com.alura.estoque.ui.dialog.SalvaProdutoDialog;
import br.com.alura.estoque.ui.recyclerview.adapter.ListaProdutosAdapter;
import retrofit2.Response;

public class ListaProdutosActivity extends AppCompatActivity {

    private static final String TITULO_APPBAR = "Lista de produtos";
    private static final String NAO_FOI_POSSIVEL_CARREGAR_OS_PRODUTOS_NOVOS = "Nao foi possivel carregar os produtos novos";
    private static final String NAO_FOI_POSSIVEL_REMOVER_OS_PRODUTOS = "Nao foi possivel remover os produtos";
    private static final String NAO_FOI_POSSIVEL_SALVAR_O_PRODUTO = "Nao foi possivel salvar o produto";
    private static final String NAO_FOI_POSSIVEL_EDITAR_O_PRODUTO = "Nao foi possivel editar o produto";
    private ListaProdutosAdapter adapter;
    private Response<List<Produto>> execute;
    private ProdutoRepository repository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produtos);
        setTitle(TITULO_APPBAR);

        configuraListaProdutos();
        configuraFabSalvaProduto();

        repository = new ProdutoRepository(this);
        buscaProdutos();
    }

    private void buscaProdutos() {
        repository.buscaProdutos(new ProdutoRepository.DadosCarregadosCallback<List<Produto>>() {
            @Override
            public void quandoSucesso(List<Produto> produtosNovos) {
                adapter.atualiza(produtosNovos);
            }

            @Override
            public void quandoFalha(String erro) {
                mostraErro(NAO_FOI_POSSIVEL_CARREGAR_OS_PRODUTOS_NOVOS, erro);
            }
        });
    }
    private void mostraErro(String message, String erro) {
        Toast.makeText(this, message + erro, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Error: " + erro, Toast.LENGTH_LONG).show();
    }

    private void configuraListaProdutos() {
        RecyclerView listaProdutos = findViewById(R.id.activity_lista_produtos_lista);
        adapter = new ListaProdutosAdapter(this, this::abreFormularioEditaProduto);
        listaProdutos.setAdapter(adapter);
        removeProduto();
    }

    private void removeProduto() {
        adapter.setOnItemClickRemoveContextMenuListener((posicao, produtoEscolhido) -> repository.remove(produtoEscolhido, new ProdutoRepository.DadosCarregadosCallback<Void>() {
            @Override
            public void quandoSucesso(Void resultado) {
                adapter.remove(posicao);
            }

            @Override
            public void quandoFalha(String erro) {
                mostraErro(NAO_FOI_POSSIVEL_REMOVER_OS_PRODUTOS, erro);
            }
        }));
    }

    private void configuraFabSalvaProduto() {
        FloatingActionButton fabAdicionaProduto = findViewById(R.id.activity_lista_produtos_fab_adiciona_produto);
        fabAdicionaProduto.setOnClickListener(v -> abreFormularioSalvaProduto());
    }

    private void abreFormularioSalvaProduto() {
        salvaProduto();
    }

    private void salvaProduto() {
        new SalvaProdutoDialog(this, produtoCriado -> repository.salva(produtoCriado, new ProdutoRepository.DadosCarregadosCallback<Produto>() {
            @Override
            public void quandoSucesso(Produto produtoSalvo) {
                adapter.adiciona(produtoSalvo);
            }

            @Override
            public void quandoFalha(String erro) {
                mostraErro(NAO_FOI_POSSIVEL_SALVAR_O_PRODUTO, erro);
            }
        })).mostra();
    }

    private void abreFormularioEditaProduto(int posicao, Produto produto) {
        editaProduto(posicao, produto);
    }

    private void editaProduto(int posicao, Produto produto) {
        new EditaProdutoDialog(this, produto,
                produtoCriado -> repository.edita(produtoCriado, new ProdutoRepository.DadosCarregadosCallback<Produto>() {
                    @Override
                    public void quandoSucesso(Produto produtoEditado) {
                        adapter.edita(posicao, produtoEditado);
                    }

                    @Override
                    public void quandoFalha(String erro) {
                        mostraErro(NAO_FOI_POSSIVEL_EDITAR_O_PRODUTO, erro);
                    }
                }))
                .mostra();
    }

}
