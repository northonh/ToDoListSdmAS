package br.edu.ifsp.scl.todolistsdm.controller

import androidx.room.Room
import br.edu.ifsp.scl.todolistsdm.model.dao.TarefaDao
import br.edu.ifsp.scl.todolistsdm.model.database.ToDoListDatabase
import br.edu.ifsp.scl.todolistsdm.model.entity.Tarefa
import br.edu.ifsp.scl.todolistsdm.view.TarefaActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TarefaActivityController(private val view: TarefaActivity) {

    private val model: TarefaDao = Room.databaseBuilder(
        view,
        ToDoListDatabase::class.java,
        ToDoListDatabase.Constantes.DB_NAME
    ).build().getTarefaDao()

    fun salvarTarefa(tarefa: Tarefa) {
        GlobalScope.launch {
            val id = model.inserirTarefa(tarefa)
            val tarefaRetorno = model.recuperaTarefa(id.toInt())

            view.setRetorno(tarefaRetorno)
        }
    }

    fun alterarTarefa(tarefa: Tarefa) {
        GlobalScope.launch {
            model.atualizarTarefa(tarefa)

            view.setRetorno(tarefa)
        }
    }
}