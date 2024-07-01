package com.sherrif.mediclab

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.gson.GsonBuilder
import com.sherrif.mediclab.adapters.LabAdapter
import com.sherrif.mediclab.constants.Constants
import com.sherrif.mediclab.helpers.ApiHelper
import com.sherrif.mediclab.helpers.PrefsHelper
import com.sherrif.mediclab.helpers.SQLiteCartHelper
import com.sherrif.mediclab.models.Lab
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    
    
    //Global declarations they can be accessed allover this class
    //declare variables as lateinit means that they are guaranteed to be initialize in future
    lateinit var recyclerview: RecyclerView
    lateinit var adapter: LabAdapter
    lateinit var progressBar: ProgressBar
    lateinit var itemList: List<Lab>
    lateinit var swiper: SwipeRefreshLayout

    //function to udate
    fun update() {
//        fetch the textview and the three buttons
        val user = findViewById<MaterialTextView>(R.id.user)
        val signin = findViewById<MaterialButton>(R.id.signin)
        val signout = findViewById<MaterialButton>(R.id.signout)
        val profile = findViewById<MaterialButton>(R.id.profile)


        signin.visibility = View.GONE
        signout.visibility = View.GONE
        profile.visibility = View.GONE

        //check do we have token
        val token = PrefsHelper.getPrefs(applicationContext, "access_token")
        //check if token is empty
        if (token.isEmpty()) {
            /// not logged in ,
            user.text = "Not Logged In"
            signin.visibility = View.VISIBLE
            signin.setOnClickListener {
                startActivity(Intent(applicationContext,LoginActivity::class.java))
            }

        }else{
//            else, logged in
            profile.visibility = View.VISIBLE

            profile.setOnClickListener {
                //intent to go to profile activity
                startActivity(Intent(applicationContext,MemberProfile::class.java))

            }

            signout.visibility = View.VISIBLE


            //get surname from shared prefs
            val surname = PrefsHelper.getPrefs(applicationContext,"surname")
            user.text ="Welcome $surname"

// sign out user
            signout.setOnClickListener{
                PrefsHelper.clearPrefs(applicationContext)
                startActivity(intent)
                finishAffinity()

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        update()


//        fetch recyclerview
        recyclerview = findViewById(R.id.recyclerview)
        progressBar = findViewById(R.id.progress)
//        set recyclerview and layoutadapter


        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerview.layoutManager = layoutManager
        recyclerview.setHasFixedSize(true)

// we link our adapter
        adapter = LabAdapter(applicationContext)
        recyclerview.adapter = adapter

        fetchData()
        //find the swiper
        swiper = findViewById(R.id.swiperefresh)
        swiper.setOnRefreshListener {
            fetchData() // fetch data again

        }

    }//end of oncreate
    //fuction  to fetch labs from api and bind them in recycler view

    fun fetchData() {
        //we  need api
        val api = Constants.BASE_URL + "/laboratories"
        val helper = ApiHelper(applicationContext)
        helper.get(api, object : ApiHelper.CallBack {
            override fun onSuccess(result: JSONArray?) {
//                 take the above results t the adapter
//convert above results from json array to List<Lab>
                val labjson = GsonBuilder().create()
                itemList = labjson.fromJson(result.toString(), Array<Lab>::class.java).toList()
                // finally our adapter has the data
                adapter.setListItems(itemList)

                //for the sake of recycling and looping items,recyclin
                recyclerview.adapter = adapter
                progressBar.visibility = View.GONE
                //set refreshing to false when /DDAta /records are loaded
                swiper.isRefreshing = false
//                search
                val labsearch = findViewById<EditText>(R.id.search)
                labsearch.addTextChangedListener(object:TextWatcher{
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }//end of before

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        filter(p0.toString())

                    }//end of text change

                    override fun afterTextChanged(p0: Editable?) {

                    }//end of text changed
                })
            }// end of onsuccess

            override fun onSuccess(result: JSONObject?) {

            }//end of on success

            override fun onFailure(result: String?) {
                Toast.makeText(applicationContext, "Error" + result.toString(), Toast.LENGTH_SHORT)
                    .show()
                progressBar.visibility = View.GONE
            }
        })
    }//the end of fetch data

    //    fun to filter data
// prvate function
    private fun filter(text: String) {
        // create a new array list to filter our data
        val filteredList: ArrayList<Lab> = ArrayList()
        //run a for loop to compare elements
        for (item in itemList) {
            //check if entered stinng matched with any item of our reccycler view
            if (item.lab_name.lowercase().contains(text.lowercase())) {
                // if the item is matchhed we are;
                // adding  it to our filtered list
                filteredList.add(item)
            }// end of if statement
        }//end of for loop
        if (filteredList.isEmpty()) {
            //if no item is Aadded in filterd list
            //we are displaying a toast message as no data  found
//            Toast.makeText(applicationContext, "No Data found ", Toast.LENGTH_SHORT).show()
            adapter.filterList((filteredList))
        } else {
            //atleast we are passing filtered data
            adapter.filterList(filteredList)

        }



    }// end of filter data
    //  we need to make the shopping cart icon in the options menu clickable, and link my cart activity in main activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // we want to access our main.xml
        menuInflater.inflate(R.menu.main, menu)

        //find my cart item
        val item: MenuItem = menu!!.findItem(R.id.mycart)
// load the design
        item.setActionView(R.layout.design)
        //get the custom actionview and we assign it to the action view variable
        val actionView : View? = item.actionView
        //access the image and textview
        val image = actionView?.findViewById<ImageView>(R.id.image)
        val badge = actionView?.findViewById<TextView>(R.id.badge)
//         if image is clicked link to my cart activity
        image?.setOnClickListener{
            val intent = Intent(applicationContext, MyCart::class.java)
            startActivity(intent)

        }// end of on click listener
        //load the number of items i the cart
        val helper = SQLiteCartHelper(applicationContext)
        badge?.text ="" + helper.getNumberOfItems()




        return super.onCreateOptionsMenu(menu)
    }



}