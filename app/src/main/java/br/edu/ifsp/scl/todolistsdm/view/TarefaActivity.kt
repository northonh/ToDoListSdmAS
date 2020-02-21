package br.edu.ifsp.scl.todolistsdm.view

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.room.Room
import br.edu.ifsp.scl.todolistsdm.R
import br.edu.ifsp.scl.todolistsdm.model.database.ToDoListDatabase
import br.edu.ifsp.scl.todolistsdm.model.entity.Tarefa
import kotlinx.android.synthetic.main.activity_tarefa.*
import kotlinx.android.synthetic.main.toolbar.*
import splitties.toast.toast

class TarefaActivity : AppCompatActivity() {
    private var tarefa: Tarefa? = null
    private lateinit var toDoListDatabase: ToDoListDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarefa)

        toolbarTb.title = getString(R.string.tarefa)
        setSupportActionBar(toolbarTb)

        /*
        Buscar referência com fonte de dados
         */
        toDoListDatabase = Room.databaseBuilder(
            this,
            ToDoListDatabase::class.java,
            ToDoListDatabase.Constantes.DB_NAME
        ).build()

        /* Edição ou Nova? */
        tarefa = intent.getParcelableExtra(MainActivity.Constantes.TAREFA_EXTRA)
        if (tarefa != null) {
            nomeTarefaEt.setText(tarefa?.nome)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detalhe_tarefa, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cancelarMi -> {
                /* Inserção ou Edição cancelada pelo usuário */
                toast("${if (tarefa == null) "Inserção" else "Edição"} cancelada.")
                finish()
            }
            R.id.salvarMi -> {
                /*
                Salva ou Atualiza Tarefa
                 */
                if (tarefa == null) {
                    tarefa = Tarefa(nome = nomeTarefaEt.text.toString())
                }
                else {
                    tarefa?.nome = nomeTarefaEt.text.toString()
                }

                /*
                Retorna tarefa para MainActivity
                 */
                SalvarAtualizarTarefaAT().execute(tarefa)
            }
        }
        return true
    }

    private inner class SalvarAtualizarTarefaAT: AsyncTask<Tarefa, Unit, Tarefa>(){
        override fun onPostExecute(tarefa: Tarefa?) {
            super.onPostExecute(tarefa)
            if (tarefa != null) {
                val intentRetorno = Intent()
                intentRetorno.putExtra(
                    MainActivity.Constantes.TAREFA_EXTRA,
                    tarefa
                )
                setResult(Activity.RESULT_OK, intentRetorno)
            }

            finish()
        }

        override fun doInBackground(vararg params: Tarefa?): Tarefa {
            val tarefaRetorno: Tarefa
            if (params[0]?.id == 0) {
                val id = toDoListDatabase.getTarefaDao().inserirTarefa(params[0]!!)
                tarefaRetorno = toDoListDatabase.getTarefaDao().recuperaTarefa(id.toInt())
            }
            else {
                toDoListDatabase.getTarefaDao().atualizarTarefa(params[0]!!)
                tarefaRetorno = params[0]!!
            }
            return tarefaRetorno
        }
    }
}
