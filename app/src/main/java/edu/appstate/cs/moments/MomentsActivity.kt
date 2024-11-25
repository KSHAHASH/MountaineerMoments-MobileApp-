package edu.appstate.cs.moments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MomentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moments)
        setSupportActionBar(findViewById(R.id.app_toolbar))
    }
}