package com.android.testable.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NotesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
    }
}