package com.example.instagramclone.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.Adapters.UserAdapter
import com.example.instagramclone.R
import com.example.instagramclone.Models.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment : Fragment() {
    private var recycler:RecyclerView?=null
    private var userAdapter:UserAdapter?=null
    private var mUser:MutableList<UserModel>?=null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Заполняем наш RecyclerView
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        recycler = view.findViewById(R.id.recyclerViewSearch)
        recycler?.setHasFixedSize(true)
        recycler?.layoutManager = LinearLayoutManager(context)
        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it,mUser as ArrayList<UserModel>,true) }
        recycler?.adapter = userAdapter

        // Обрабатываем нажатие в реальном времени
        view.theSearchEditText.addTextChangedListener(object:TextWatcher{

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (view.theSearchEditText.text.toString() == ""){

                }
                else {

                    catchAUser()
                    searchUser(p0.toString())
                }
            }
            override fun afterTextChanged(p0: Editable?) {}


        })
        return view
    }

    private fun searchUser(input:String) {
        val query = FirebaseDatabase.getInstance().reference.child("users")
            .orderByChild("name")
            .startAt(input)
            //endAt(input + "\uf8ff")
        query.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                    mUser?.clear()
                    for (snaps in snapshot.children){
                        val user = snaps.getValue(UserModel::class.java)
                        if (user!=null){
                            mUser?.add(user)
                        }
                    }
                    userAdapter?.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun catchAUser() {
        val userRef = FirebaseDatabase.getInstance().reference.child("users")
        userRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
            if (view?.theSearchEditText?.text.toString()==""){
                mUser?.clear()
                for (snaps in snapshot.children){
                    val user = snaps.getValue(UserModel::class.java)
                    if (user!=null){
                        mUser?.add(user)
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}