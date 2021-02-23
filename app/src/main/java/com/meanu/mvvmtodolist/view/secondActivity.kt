package com.meanu.mvvmtodolist.view

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.meanu.mvvmtodolist.R
import com.meanu.mvvmtodolist.model.TodoModel
import com.meanu.mvvmtodolist.view.adapter.TodoListAdapter
import com.meanu.mvvmtodolist.viewmodel.TodoViewModel
import kotlinx.android.synthetic.main.dialog_layout.view.*
import kotlinx.android.synthetic.main.item_todo.view.*
import kotlinx.android.synthetic.main.livedatarecyclerview.*
import java.util.*
import kotlin.collections.ArrayList

class secondActivity : AppCompatActivity() {

    private lateinit var mTodoListAdapter: TodoListAdapter
    private lateinit var mTodoViewModel: TodoViewModel
    private val mTodoItems: ArrayList<TodoModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            with(window) {
                requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
                // set an slide transition
                enterTransition = Slide(Gravity.LEFT)
                exitTransition = Slide(Gravity.RIGHT)
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.livedatarecyclerview)
        initRecyclerView()
        initAddButton()
        initViewModel()
        //observe 결합 코드는 oncreat 메소드내에 위치하는것이 바람직.
    }

    fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun initViewModel() {//ViewModel을 초기화 하는 방법은 일반 객체처럼 생성자로부터 호출하는 것이 아닌 Provider에 의해 초기화 되어야 한다
        mTodoViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(TodoViewModel::class.java)
        mTodoViewModel.getTodoList().observe(this, androidx.lifecycle.Observer{
            mTodoListAdapter.setTodoItems(it)
        })
    }

    private fun initAddButton() {
        btn_add_todo.setOnClickListener {
            openAddTodoDialog()
        }
    }

    private fun openAddTodoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, null)
        val dialog = AlertDialog.Builder(this)
                .setTitle("Add TODOLIST")
                .setView(dialogView)
                .setPositiveButton("CONFIRM",  {dialoginterface, i ->
                    val title =dialogView.et_todo_title.text.toString()
                    val description = dialogView.et_todo_desc.text.toString()
                    val createdDate = Date().time
//                    val todoModel  = TodoModel(null, title, description, createdDate)
                    //전달되면 ROOM에의해 삽입되면서 할당된다.
//                    mTodoViewModel.insertTodo(todoModel)
                })
                .setNegativeButton("CANCLE", null)
                .create()
        dialog.show()
    }

    private fun initRecyclerView() {
        mTodoListAdapter = TodoListAdapter()
        rl_todo_list.run{
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@secondActivity)
            adapter = mTodoListAdapter
        }

        mTodoListAdapter.setItemClickListener( object : TodoListAdapter.ItemClickListener{
            override fun onTodoItemClick(view: View, position: Int) {
                toast("itemClicked")
            }

            override fun onTodoItemLongClick(view: View, position: Int) {
                toast("itemLongClicked")
                mTodoViewModel.delTodo(view.tv_todo_title.text as String)

            }
        })
    }
}
