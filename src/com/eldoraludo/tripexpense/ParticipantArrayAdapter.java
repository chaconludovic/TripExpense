package com.eldoraludo.tripexpense;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eldoraludo.tripexpense.entite.Participant;
import com.eldoraludo.tripexpense.util.DateHelper;

public class ParticipantArrayAdapter extends ArrayAdapter<Participant> {
	private final Context context;
	private final List<Participant> values;

	public ParticipantArrayAdapter(Context context, List<Participant> values) {
		super(context, R.layout.ligne_participant, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View depenseLigneView = inflater.inflate(R.layout.ligne_participant,
				parent, false);
		TextView nomParticipantText = (TextView) depenseLigneView
				.findViewById(R.id.nomParticipant);
		TextView dateText = (TextView) depenseLigneView.findViewById(R.id.date);

		Participant participant = values.get(position);
		nomParticipantText.setText(participant.getNom());
		dateText.setText("Arrivé le "
				+ DateHelper.prettyDate(participant.getDateArrive())
				+ " et départ le "
				+ DateHelper.prettyDate(participant.getDateDepart()));

		return depenseLigneView;
	}
}