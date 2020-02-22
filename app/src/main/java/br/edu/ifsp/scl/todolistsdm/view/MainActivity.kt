package br.edu.ifsp.scl.todolistsdm.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.scl.todolistsdm.R
import br.edu.ifsp.scl.todolistsdm.adapter.ListaTarefasAdapter
import br.edu.ifsp.scl.todolistsdm.controller.MainActivityController
import br.edu.ifsp.scl.todolistsdm.model.entity.Tarefa
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.celula_lista_tarefas.view.*
import kotlinx.android.synthetic.main.conteudo_principal.*
import kotlinx.android.synthetic.main.toolbar.*
import splitties.alertdialog.alertDialog
import splitties.alertdialog.cancelButton
import splitties.alertdialog.message
import splitties.alertdialog.okButton
import splitties.toast.toast

class MainActivity : AppCompatActivity() {
    object Constantes {
        const val TAREFA_ACTIVITY_REQUEST_CODE = 0
        const val TAREFA_EXTRA = "TAREFA_EXTRA"
    }

    private lateinit var listaTarefasAdapter: ListaTarefasAdapter
    private lateinit var controller: MainActivityController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbarTb.title = getString(R.string.app_name)
        setSupportActionBar(toolbarTb)

        /* Adaptador do ListView */
        listaTarefasAdapter =
            ListaTarefasAdapter(
                this,
                mutableListOf()
            )
        conteudoLv.adapter = listaTarefasAdapter

        /* Menu de contexto */
        conteudoLv.setOnItemClickListener { _, view, position, _ ->
            val tarefaClicada = listaTarefasAdapter.getItem(position)
            tarefaClicada?.checado = if (tarefaClicada?.checado == 1) 0 else 1

            view.checadoTarefaCb.isChecked = tarefaClicada?.checado == 1

            /*
            Atualizar atributo checado na fonte de dados
             */
            tarefaClicada?.let {
                controller.alterarTarefa(it)
            }
        }

        registerForContextMenu(conteudoLv)

        /* Botão de ação flutuante para adicionar nova tarefa */
        novaTarefaFab.setOnClickListener {
            /* Abrir TarefaActivity para inserção */
            val tarefaIntent = Intent(this, TarefaActivity::class.java)
            startActivityForResult(tarefaIntent, Constantes.TAREFA_ACTIVITY_REQUEST_CODE)
        }

        /*
        Instanciar controller
         */
        controller = MainActivityController(this)

        /*
        Recuperar tarefas da fonte de dados e passar para o adaptador do ListView
         */
        controller.buscarTarefas()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menuInflater.inflate(R.menu.menu_context_main, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val infoMi = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val tarefaClicada = listaTarefasAdapter.getItem(infoMi.position)

        when (item.itemId) {
            R.id.editarTarefaMi -> {
                /* Abrir TarefaActivity para edição, enviar tarefa como parâmetro */
                val editarTarefaIntent = Intent(this, TarefaActivity::class.java)
                editarTarefaIntent.putExtra(Constantes.TAREFA_EXTRA, tarefaClicada)
                startActivityForResult(editarTarefaIntent, Constantes.TAREFA_ACTIVITY_REQUEST_CODE)
            }
            R.id.removerTarefaMi -> {
                alertDialog {
                    message = getString(R.string.excluir_tarefa)
                    okButton {
                        /*
                        Remover tarefa da fonte de dados
                         */
                        tarefaClicada?.let{
                            controller.apagarTarefa(it)
                        }
                    }
                    cancelButton {
                        /* Ação cancelada pelo usuário, nada necessário */
                    }
                }.show()
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.limparTudoMi) {
            alertDialog {
                message = getString(R.string.excluir_todas_tarefas)
                okButton {
                    /*
                    Remover TODAS as tarefas da fonte de dados
                     */
                    controller.apagarTarefas(*listaTarefasAdapter.getAll().toTypedArray())
                }
                cancelButton {
                    /* Ação cancelada pelo usuário, nada necessário */
                }
            }.show()
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constantes.TAREFA_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            /* Pegando tarefa retornada (se existe) pela TarefaActivity */
            val tarefaRetorno = data?.getParcelableExtra<Tarefa>(Constantes.TAREFA_EXTRA)

            if (tarefaRetorno != null) {
                /* Verificando se tarefa já existe no adaptador do ListView */
                val tarefaExistente = listaTarefasAdapter.getTarefaById(tarefaRetorno.id)
                if (tarefaExistente != null) {
                    /* Atualiza tarefa existente */
                    val indiceTarefaExistente = listaTarefasAdapter.getPosition(tarefaExistente)

                    /* Remover e inserir força atualização do ListView */
                    listaTarefasAdapter.remove(tarefaExistente)
                    listaTarefasAdapter.insert(tarefaRetorno, indiceTarefaExistente)
                }
                else {
                    /* Tarefa não existente, então adiciona */
                    listaTarefasAdapter.add(tarefaRetorno)
                }
            }
        }
    }

    fun setTarefas(listaTarefas: MutableList<Tarefa>) {
        listaTarefasAdapter.clear()
        listaTarefasAdapter.addAll(listaTarefas)
    }

    fun notificaTarefaApagada(tarefa: Tarefa) {
        toast(getString(R.string.tarefa_removida))
        /* Remover tarefa do adaptador do ListView */
        listaTarefasAdapter.remove(tarefa)
    }

    fun notificaTarefasApagadas() {
        /* Remover TODAS as tarefas do adaptador do ListView */
        listaTarefasAdapter.clear()
        toast(getString(R.string.tarefas_removidas))
    }
}
