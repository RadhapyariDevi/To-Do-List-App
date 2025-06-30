package com.radhapyari.todolist.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.radhapyari.todolist.databinding.FragmentAddToDoPopUpBinding
import com.radhapyari.todolist.utils.TodoData

class AddToDoPopUpFragment : DialogFragment() {

    private lateinit var binding: FragmentAddToDoPopUpBinding
    private lateinit var listener: DialogNextBtnClickListener
    private var todoData: TodoData? = null

    fun setListener(listener: DialogNextBtnClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "AddToDoPopUpFragment"

        @JvmStatic
        fun newInstance(taskId: String, task: String) = AddToDoPopUpFragment().apply {
            arguments = Bundle().apply {
                putString("taskId", taskId)
                putString("task", task)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddToDoPopUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            todoData = TodoData(
                arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString()
            )
            binding.todoEt.setText(todoData?.task)
        }
        registerEvents()
    }

    private fun registerEvents() {
        binding.todoNextBtn.setOnClickListener {
            val todotask = binding.todoEt.text.toString()

            if (todotask.isNotEmpty()) {
                if(todoData == null){
                    listener.onSaveTask(todotask, binding.todoEt)
                }else{
                    todoData?.task = todotask
                    listener.onUpdateTask(todoData!!, binding.todoEt)
                }

            } else {
                Toast.makeText(context, "Please Add some task", Toast.LENGTH_SHORT).show()
            }
        }

        binding.todoClose.setOnClickListener {
            dismiss()
        }

    }

    interface DialogNextBtnClickListener {
        fun onSaveTask(todo: String, todoEt: TextInputEditText)
        fun onUpdateTask(todoData: TodoData, todoEt: TextInputEditText)
    }


}