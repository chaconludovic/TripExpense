package com.eldoraludo.tripexpense.arrayadapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eldoraludo.tripexpense.R;
import com.eldoraludo.tripexpense.dto.SyntheseDTO;

public class SyntheseArrayAdapter extends ArrayAdapter<SyntheseDTO> {
	private final Context context;
	private final List<SyntheseDTO> values;

	public SyntheseArrayAdapter(Context context, List<SyntheseDTO> values) {
		super(context, R.layout.ligne_synthese, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View depenseLigneView = inflater.inflate(R.layout.ligne_synthese,
				parent, false);
		SyntheseDTO syntheseDTO = values.get(position);
		TextView syntheseText = (TextView) depenseLigneView
				.findViewById(R.id.syntheseText);
		syntheseText.setText(syntheseDTO.getParticipant().getNom() + " doit à "
				+ syntheseDTO.getDepenseur().getNom() + " la somme de "
				+ syntheseDTO.getMontant() + " euros");
		return depenseLigneView;
	}
}