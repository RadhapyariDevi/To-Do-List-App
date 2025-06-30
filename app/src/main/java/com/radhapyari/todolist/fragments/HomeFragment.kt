package com.radhapyari.todolist.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.radhapyari.todolist.databinding.FragmentHomeBinding
import com.radhapyari.todolist.utils.TodoAdapter
import com.radhapyari.todolist.utils.TodoData


class HomeFragment : Fragment(), AddToDoPopUpFragment.DialogNextBtnClickListener, TodoAdapter.ToDoAdapterClicksInterface {

    private lateinit var auth : FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding : FragmentHomeBinding
    private var popUpfragment : AddToDoPopUpFragment? = null
    private lateinit var adapter: TodoAdapter
    private lateinit var mList: MutableList<TodoData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()
        registerEvents()
    }

    private fun registerEvents(){
        binding.addBtnHome.setOnClickListener {
            if(popUpfragment != null){
                childFragmentManager.beginTransaction().remove(popUpfragment!!).commit()
            }
            popUpfragment = AddToDoPopUpFragment()
            popUpfragment!!.setListener(this)
            popUpfragment!!.show(
                childFragmentManager,
                AddToDoPopUpFragment.TAG
            )
        }
    }

    private fun init(view: View){
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference
            .child("Tasks").child(auth.currentUser?.uid.toString())
        binding.recyclerview.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = TodoAdapter(mList)
        adapter.setListener(this)
        binding.recyclerview.adapter = adapter
    }

    private fun getDataFromFirebase(){
        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for(taskSnapshot in snapshot.children){
                    val todoTask = taskSnapshot.key?.let{
                        TodoData(it, taskSnapshot.value.toString())
                    }
                    if( todoTask != null){
                        mList.add(todoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        } )
    }

    override fun onSaveTask(
        todo: String,
        todoEt: TextInputEditText
    ) {
        databaseRef.push().setValue(todo).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context, "Saved successfully",Toast.LENGTH_SHORT).show()
                todoEt.text = null
            }
            else{
                Toast.makeText(context,it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            popUpfragment!!.dismiss()
        }
    }

    override fun onUpdateTask(
        todoData: TodoData,
        todoEt: TextInputEditText
    ) {
        val map = HashMap<String, Any>()
        map[todoData.taskId] = todoData.task
        databaseRef.updateChildren(map).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context,"Updated Successfully", Toast.LENGTH_SHORT).show()

            }
            else{
                Toast.makeText(context,it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            todoEt.text = null
            popUpfragment!!.dismiss()
        }
    }

    override fun onDeleteTaskBtnClicked(todoData: TodoData) {
        databaseRef.child(todoData.taskId).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context,"Deleted Successfully", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context,it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditTaskBtnClicked(todoData: TodoData) {
        if(popUpfragment != null){
            childFragmentManager.beginTransaction().remove(popUpfragment!!).commit()
        }
        popUpfragment = AddToDoPopUpFragment.newInstance(todoData.taskId, todoData.task)
        popUpfragment!!.setListener(this)
        popUpfragment!!.show(childFragmentManager, AddToDoPopUpFragment.TAG)
    }


}