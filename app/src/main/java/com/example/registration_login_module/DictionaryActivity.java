package com.example.registration_login_module;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.registration_login_module.databinding.ActivityDictionaryBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class DictionaryActivity extends AppCompatActivity {

    private ActivityDictionaryBinding binding;
    private MeaningAdapter adapter;

    Button backbtndictionary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_dictionary);

        binding = ActivityDictionaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the RecyclerView
        adapter = new MeaningAdapter(new java.util.ArrayList<>());
        binding.meaningRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.meaningRecyclerView.setAdapter(adapter);

        // Set up the Search Button click listener
        binding.searchBtn.setOnClickListener(v -> {
            String word = binding.searchInput.getText().toString().trim();
            if (!word.isEmpty()) {
                getMeaning(word);
            } else {
                Toast.makeText(DictionaryActivity.this, "Please enter a word", Toast.LENGTH_SHORT).show();
            }
        });

        backbtndictionary = findViewById(R.id.backBtnDictionary);
        backbtndictionary.setOnClickListener(v -> {
            Intent intent = new Intent(DictionaryActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    private void getMeaning(String word) {
        setInProgress(true);

        // Corrected callback type
        RetrofitInstance.dictionaryApi.getMeaning(word).enqueue(new Callback<List<WordResult>>() {
            @Override
            public void onResponse(Call<List<WordResult>> call, Response<List<WordResult>> response) {
                setInProgress(false);
                if (response.body() != null && !response.body().isEmpty()) {
                    // Update UI with the result
                    WordResult wordResult = response.body().get(0); // Get the first item from the list
                    setUI(wordResult);
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(Call<List<WordResult>> call, Throwable t) {
                setInProgress(false);
                showError();
            }
        });
    }

    private void setUI(WordResult response) {
        binding.wordTextview.setText(response.getWord());
        if (response.getPhonetic() != null) {
            binding.phoneticTextview.setText(response.getPhonetic());
        } else {
            binding.phoneticTextview.setText("Phonetic not available");
        }
        adapter.updateNewData(response.getMeanings());
    }

    private void showError() {
        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            binding.searchBtn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.searchBtn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }

    }


}
