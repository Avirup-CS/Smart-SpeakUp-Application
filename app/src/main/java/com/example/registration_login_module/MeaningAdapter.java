package com.example.registration_login_module;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.example.registration_login_module.databinding.MeaningRecyclerRowBinding;
import java.util.List;

public class MeaningAdapter extends RecyclerView.Adapter<MeaningAdapter.MeaningViewHolder> {

    private List<Meaning> meaningList;

    public MeaningAdapter(List<Meaning> meaningList) {
        this.meaningList = meaningList;
    }

    public static class MeaningViewHolder extends RecyclerView.ViewHolder {

        private final MeaningRecyclerRowBinding binding;

        public MeaningViewHolder(MeaningRecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Meaning meaning) {
            // Setting the part of speech
            binding.partOfSpeechTextview.setText(meaning.getPartOfSpeech());

            // Setting the definitions using StringBuilder
            StringBuilder definitionsText = new StringBuilder();
            List<Meaning.Definition> definitions = meaning.getDefinitions();
            for (int i = 0; i < definitions.size(); i++) {
                Meaning.Definition definition = definitions.get(i);
                definitionsText.append((i + 1) + ". " + definition.getDefinition() + "\n\n");
            }
            binding.definitionsTextview.setText(definitionsText.toString());

            // Handle synonyms visibility
            if (meaning.getSynonyms().isEmpty()) {
                binding.synonymsTitleTextview.setVisibility(View.GONE);
                binding.synonymsTextview.setVisibility(View.GONE);
            } else {
                binding.synonymsTitleTextview.setVisibility(View.VISIBLE);
                binding.synonymsTextview.setVisibility(View.VISIBLE);
                binding.synonymsTextview.setText(String.join(", ", meaning.getSynonyms()));
            }

            // Handle antonyms visibility
            if (meaning.getAntonyms().isEmpty()) {
                binding.antonymsTitleTextview.setVisibility(View.GONE);
                binding.antonymsTextview.setVisibility(View.GONE);
            } else {
                binding.antonymsTitleTextview.setVisibility(View.VISIBLE);
                binding.antonymsTextview.setVisibility(View.VISIBLE);
                binding.antonymsTextview.setText(String.join(", ", meaning.getAntonyms()));
            }
        }
    }

    public void updateNewData(List<Meaning> newMeaningList) {
        meaningList = newMeaningList;
        notifyDataSetChanged();
    }

    @Override
    public MeaningViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MeaningRecyclerRowBinding binding = MeaningRecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MeaningViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return meaningList.size();
    }

    @Override
    public void onBindViewHolder(MeaningViewHolder holder, int position) {
        holder.bind(meaningList.get(position));
    }
}
