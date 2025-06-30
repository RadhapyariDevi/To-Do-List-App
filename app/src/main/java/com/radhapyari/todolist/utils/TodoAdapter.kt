package com.radhapyari.todolist.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.radhapyari.todolist.databinding.EachToDoBinding

class TodoAdapter(private val list: MutableList<TodoData>) : RecyclerView.Adapter<TodoAdapter.ToDoViewHolder>(){


    private var listener: ToDoAdapterClicksInterface? = null
    fun setListener(listener: ToDoAdapterClicksInterface){
        this.listener = listener
    }
    inner class ToDoViewHolder(val binding: EachToDoBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ToDoViewHolder {
        val binding = EachToDoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ToDoViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ToDoViewHolder,
        position: Int
    ) {

        with(holder){
            with(list[position]){
                binding.todoTask.text = this.task

                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteTaskBtnClicked(this)
                }

                binding.editTask.setOnClickListener {
                    listener?.onEditTaskBtnClicked(this)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ToDoAdapterClicksInterface{
        fun onDeleteTaskBtnClicked(todoData: TodoData)
        fun onEditTaskBtnClicked(todoData: TodoData)
    }

}