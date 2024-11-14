package com.example.mystoryapp.ui.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.mystoryapp.R
import com.example.mystoryapp.data.remote.response.ListStoryItem
import com.example.mystoryapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_STORY = "extra_story"
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan data story dari intent
        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)

        // Menampilkan data story
        story?.let {
            binding.tvName.text = it.name
            binding.tvDescription.text = it.description

            // Memuat gambar menggunakan Glide
            Glide.with(this)
                .load(it.photoUrl)
                .into(binding.imgStory)
        }

        // Tambahkan tombol kembali di action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Story Detail"
    }

    // Menghandle tombol kembali di action bar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}