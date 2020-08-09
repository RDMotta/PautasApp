package com.rdm.uds.pautas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.rdm.uds.pautas.app.FirebaseUtils
import com.rdm.uds.pautas.app.FirebaseUtils.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.rdm.uds.pautas.controller.PautasController
import kotlinx.android.synthetic.main.activity_drawer_layout.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main_layout.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var mRecyclerViewPautas: RecyclerView
    lateinit var mPautasDB: FirebaseFirestore
    lateinit var mSettingsPautasDB: FirebaseFirestoreSettings
    lateinit var mTokenUser: String
    lateinit var mPautasController: PautasController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_layout)
        setSupportActionBar(findViewById(R.id.toolbar))
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        mSettingsPautasDB = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        mPautasController = PautasController()
        setTitle(R.string.app_name)
        mRecyclerViewPautas = findViewById(R.id.recycler_pauta)
        mRecyclerViewPautas.layoutManager = LinearLayoutManager(this)
        setConfigurationDB()

        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            val intent = Intent(this, PautasActivity::class.java)
            startActivity(intent)
            finish()
        }
        callPautas()
    }

    protected fun setConfigurationDB() {
        val reference: FirebaseUtils = Reference.instance
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (user != null) mTokenUser =  user.uid

        mPautasDB = reference.firebaseFirestore
        mPautasDB.firestoreSettings = mSettingsPautasDB
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val user = Reference.firebaseAuth.currentUser
        user?.let {
            txtUserdesc.text = (getString(R.string.txt_conta))
            val email = user.email
            val name = user.displayName
            txtUserName.text = (name.toString())
            txtUserMail.text = (email.toString())
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logoutApp()
                return true;
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_pautas -> {
                callPautas()
            }
            R.id.nav_pautas_finalizadas -> {
                callPautasFinalizadas()
            }
            R.id.nav_logout -> {
                logoutApp()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun callPautas(){
        mRecyclerViewPautas.visibility = View.VISIBLE
        mPautasController.consultarPautas(mTokenUser, mPautasDB, this,  mRecyclerViewPautas)
    }
    fun callPautasFinalizadas(){
        mRecyclerViewPautas.visibility = View.VISIBLE
        mPautasController.consultarPautasFinalizadas(mTokenUser, mPautasDB, this,  mRecyclerViewPautas)
    }


    fun logoutApp(){
        FirebaseUtils.firebaseAuth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}