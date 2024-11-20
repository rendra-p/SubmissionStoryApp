package com.example.mystoryapp.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystoryapp.R
import com.example.mystoryapp.data.Injection
import com.example.mystoryapp.data.remote.response.ListStoryItem
import com.example.mystoryapp.databinding.ActivityMainBinding
import com.example.mystoryapp.ui.detail.DetailActivity
import com.example.mystoryapp.ui.upload.UploadActivity
import com.example.mystoryapp.ui.welcome.TokenDataStore
import com.example.mystoryapp.ui.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var tokenDataStore: TokenDataStore

    private val uploadLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            storyViewModel.fetchStories()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenDataStore = TokenDataStore.getInstance(this)

        val viewModelFactory = Injection.provideViewModelFactory(this)
        storyViewModel = ViewModelProvider(this, viewModelFactory)[StoryViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
        setupAction()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                storyViewModel.fetchStories()
                true
            }
            R.id.logout -> {
                showAlertDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val alert = builder.create()
        builder
            .setTitle(getString(R.string.logout))
            .setMessage("Apakah anda yakin keluar?")
            .setPositiveButton("Lanjutkan") { _, _ ->
                lifecycleScope.launch {
                    tokenDataStore.clearToken()
                    startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
                    finish()
                }
            }
            .setNegativeButton("Batal") { _, _ ->
                alert.cancel()
            }
            .show()
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter()
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter
        }

        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(story: ListStoryItem) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_STORY, story)
                }
                startActivity(intent)
            }
        })
    }

    private fun observeViewModel() {
        storyViewModel.stories.observe(this) { stories ->
            storyAdapter.submitList(stories)
        }

        storyViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        storyViewModel.fetchStories()
    }

    private fun setupAction(){
        binding.floatingBtn.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            uploadLauncher.launch(intent)
        }
    }
}