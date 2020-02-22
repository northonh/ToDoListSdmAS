package br.edu.ifsp.scl.todolistsdm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import br.edu.ifsp.scl.todolistsdm.R
import br.edu.ifsp.scl.todolistsdm.model.entity.Tarefa

class ListaTarefasAdapter(contexto: Context, listaTarefas: MutableList<Tarefa>):
    ArrayAdapter<Tarefa>(contexto,
        R.layout.celula_lista_tarefas, listaTarefas) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.celula_lista_tarefas, parent, false)

            val viewHolder =
                CelulaListaTarefasViewHolder(
                    view.findViewById(R.id.nomeTarefaTv),
                    view.findViewById(R.id.checadoTarefaCb)
                )

            view.setTag(viewHolder)
        }
        val tarefa = getItem(position)

        (view?.tag as CelulaListaTarefasViewHolder).textView.setText(tarefa?.nome)
        (view?.tag as CelulaListaTarefasViewHolder).checkBox.isChecked = if (tarefa?.checado == 1) true else false

        return view
    }

    fun getTarefaById(id: Int): Tarefa? {
        for (indice in 0..count - 1) {
            val tarefa = getItem(indice)
            if (id == tarefa?.id){
                return tarefa
            }
        }
        return null
    }

    fun getAll(): MutableList<Tarefa> {
        val lista = mutableListOf<Tarefa>()
        for (indice in 0..count - 1) {
            getItem(indice)?.let{
                lista.add(it)
            }
        }
        return lista
    }

    data class CelulaListaTarefasViewHolder (
        val textView: TextView,
        val checkBox: CheckBox
    )
}