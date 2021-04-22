package com.plete.addrecord

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_update.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupListOfDataIntoRecyclerView()
        btnrecord.setOnClickListener {
            addRecord()
            setupListOfDataIntoRecyclerView()
            closeKeyboard()
        }

    }

    private fun addRecord(){
        val name = etName.text.toString()
        val email = etemail.text.toString()
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)

        if (!name.isEmpty() && !email.isEmpty()){
            val status = databaseHandler.addEmployee((EMPModel(0, name, email)))
            if (status > -1){
                Toast.makeText(this, "Record Saved", Toast.LENGTH_LONG).show()
                etName.text.clear()
                etemail.text.clear()
            }
        } else {
            Toast.makeText(this, "Nama or Email cannot be BLANK ", Toast.LENGTH_LONG).show()
        }
    }
    /*
    Method Untuk mendapatkan jumlah record
     */

    private fun getItemList():ArrayList<EMPModel>{
        val databaseHandler:DatabaseHandler = DatabaseHandler(this)
        val empList: ArrayList<EMPModel> = databaseHandler.viewEmployee()
        return empList
    }

    // Method untuk menampilkan empList ke recyclerView

    private fun setupListOfDataIntoRecyclerView(){

        if (getItemList().size > 0){
            rvItemList.visibility = View.VISIBLE
            tv_noRecordAvaible.visibility= View.GONE

            rvItemList.layoutManager = LinearLayoutManager(this)
            rvItemList.adapter = ItemAdapter(this, getItemList() )
        }else{
            rvItemList.visibility = View.GONE
            tv_noRecordAvaible.visibility = View.VISIBLE
        }
    }

    // Method untuk menampilka dialog konfirmasi delete

    fun deleteRecordAlertDialog(empModel: EMPModel){
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Delete Record")
        builder.setMessage("Are you Sure ? ")
        builder.setIcon(android.R.drawable.ic_menu_delete)

        //Menampilkan tombol Yes
        builder.setPositiveButton( "Yes" ) { dialog: DialogInterface?, which->
            val databaseHandler : DatabaseHandler = DatabaseHandler(this)
            val status = databaseHandler.deleteEmployee(EMPModel(empModel.id,"",""))

            if(status > -1){
                Toast.makeText(this,"Record deleted Successfully",Toast.LENGTH_LONG).show()
                setupListOfDataIntoRecyclerView()
        }
//            Toast.makeText(this,"Yes",Toast.LENGTH_LONG).show()
            dialog!!.dismiss()
        }
        //Menampilkan tombol No
        builder.setNegativeButton("No") {dialog: DialogInterface?, which ->
            Toast.makeText(this,"No",Toast.LENGTH_LONG).show()
            dialog!!.dismiss()
        }
        val alertDialog : AlertDialog = builder.create()
        // Memastikan Use menekan Tombol Yes atau No
        alertDialog.setCancelable(false)
        // Menampilkan Kotak Dialog
        alertDialog.show()
    }

    //method to show custom update dialog

    fun updateRecordDialog( empModel: EMPModel){
        val updateDialog = Dialog(this,R.style.Theme_Dialog)

        updateDialog.setCancelable(false)
        updateDialog.setContentView(R.layout.dialog_update)

        updateDialog.eUpdateName.setText(empModel.name)
        updateDialog.eUpdateEmail.setText(empModel.email)

        updateDialog.tvUpdate.setOnClickListener {
            val  name = updateDialog.eUpdateName.text.toString()
            val  email = updateDialog.eUpdateEmail.text.toString()

            val databaseHandler:DatabaseHandler = DatabaseHandler(this)

            if(!name.isEmpty() && !email.isEmpty()){
                val status = databaseHandler.updateEmployee(EMPModel(empModel.id,name, email))
                if (status > -1){
                    Toast.makeText(this,"Record update",Toast.LENGTH_LONG).show()
                    setupListOfDataIntoRecyclerView()
                    updateDialog.dismiss()
                    closeKeyboard()
                }
            }else{
                Toast.makeText(this,"name cannot be Blank",Toast.LENGTH_LONG).show()
            }
        }

        updateDialog.tvCancel.setOnClickListener {
            updateDialog.dismiss()
        }

        updateDialog.show()
    }




    //method to close keyboard

    private fun closeKeyboard(){
        val view = this.currentFocus
        if(view!= null){
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken,0)
        }
    }
}