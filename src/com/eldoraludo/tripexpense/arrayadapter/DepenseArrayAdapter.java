package com.eldoraludo.tripexpense.arrayadapter;

import java.util.List;

import com.eldoraludo.tripexpense.R;
import com.eldoraludo.tripexpense.R.drawable;
import com.eldoraludo.tripexpense.R.id;
import com.eldoraludo.tripexpense.R.layout;
import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Depense;
import com.eldoraludo.tripexpense.entite.Participant;
import com.eldoraludo.tripexpense.entite.TypeDeDepense;
import com.eldoraludo.tripexpense.util.DateHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DepenseArrayAdapter extends ArrayAdapter<Depense> {
	private final Context context;
	private final List<Depense> values;

	public DepenseArrayAdapter(Context context, List<Depense> values) {
		super(context, R.layout.ligne_depense, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DatabaseHandler databaseHandler = new DatabaseHandler(this.context);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View depenseLigneView = inflater.inflate(R.layout.ligne_depense,
				parent, false);
		TextView nomParticipantText = (TextView) depenseLigneView
				.findViewById(R.id.nomParticipant);
		TextView nomDeLaDepenseText = (TextView) depenseLigneView
				.findViewById(R.id.nomDeLaDepense);
		TextView montantText = (TextView) depenseLigneView
				.findViewById(R.id.montantDeLaDepense);
		TextView dateDebutText = (TextView) depenseLigneView
				.findViewById(R.id.dateDebut);
		TextView dateFinText = (TextView) depenseLigneView
				.findViewById(R.id.dateFin);
		ImageView imageView = (ImageView) depenseLigneView
				.findViewById(R.id.imageDepense);
		Depense depense = values.get(position);
		Participant participant = databaseHandler.trouverLeParticipant(depense
				.getParticipantId());
		nomDeLaDepenseText.setText(" " + depense.getNomDepense());
		montantText.setText(" " + depense.getMontant() + " euros");
		dateDebutText.setText(" du "
				+ DateHelper.prettyDate(depense.getDateDebut()));
		dateFinText.setText(" au "
				+ DateHelper.prettyDate(depense.getDateFin()));
		nomParticipantText.setText(participant.getNom());
		// Change the icon for Windows and iPhone
		TypeDeDepense s = depense.getTypeDeDepense();
		if (TypeDeDepense.COURSE.equals(s)) {
			imageView.setImageResource(R.drawable.ic_depense_course);
		} else {
			imageView.setImageResource(R.drawable.ic_depense_essence);
		}

		return depenseLigneView;
	}
}