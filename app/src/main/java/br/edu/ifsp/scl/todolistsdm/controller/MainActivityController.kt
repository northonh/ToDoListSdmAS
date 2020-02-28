package br.edu.ifsp.scl.todolistsdm.controller

import androidx.room.Room
import br.edu.ifsp.scl.todolistsdm.model.dao.TarefaDao
import br.edu.ifsp.scl.todolistsdm.model.database.ToDoListDatabase
import br.edu.ifsp.scl.todolistsdm.model.entity.Tarefa
import br.edu.ifsp.scl.todolistsdm.view.MainActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/* TarefaController conhece a MainActivity */
class MainActivityController(private val view: MainActivity) {

    /* Também conhece o modelo, mas por injeção de dependência, isso não é um problema */
    private val model: TarefaDao = Room.databaseBuilder(
        view,
        ToDoListDatabase::class.java,
        ToDoListDatabase.Constantes.DB_NAME
    ).build().getTarefaDao()

    fun buscarTarefas() {
        GlobalScope.launch {
            val listaTarefas = model.recuperarTarefas().toMutableList()
            view.setTarefas(listaTarefas)
        }
    }

    fun apagarTarefa(tarefa: Tarefa){
        GlobalScope.launch {
            model.removerTarefa(tarefa)
            view.runOnUiThread {
                view.notificaTarefaApagada(tarefa)
            }
        }
    }

    fun apagarTarefas(vararg tarefa: Tarefa){
        GlobalScope.launch {
            model.removerTarefas(*tarefa)
            view.runOnUiThread {
                view.notificaTarefasApagadas()
            }
        }
    }

    fun alterarTarefa(tarefa: Tarefa) {
        GlobalScope.launch { model.atualizarTarefa(tarefa) }
    }
}