package com.android.testable.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Activity3 : AppCompatActivity() {

    companion object {
        const val EXTRA_ITEM = "EXTRA_ITEM"

        fun newIntent(context: Context, item: Item) = Intent(context, Activity3::class.java).putExtra(EXTRA_ITEM, item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_3)
        val text = intent.getParcelableExtra<Item>(EXTRA_ITEM)?.text.orEmpty()
        findViewById<TextView>(R.id.activity_3_text_view).text = (application as MyApplication).stringProvider.provideYouClickedString(text)
    }
}
