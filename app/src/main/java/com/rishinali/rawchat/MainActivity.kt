package com.rishinali.rawchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import com.google.firebase.FirebaseApp

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        navController = findNavController(R.id.main_nav_fragment)
        navGraph = navController.navInflater.inflate(R.navigation.nav_graph_main)
//        navGraph.startDestination = R.id.landingFragment
        navController.graph = navGraph
    }
}