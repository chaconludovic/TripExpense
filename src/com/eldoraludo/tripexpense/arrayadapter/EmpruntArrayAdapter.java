package com.eldoraludo.tripexpense.arrayadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eldoraludo.tripexpense.R;
import com.eldoraludo.tripexpense.R.drawable;
import com.eldoraludo.tripexpense.R.id;
import com.eldoraludo.tripexpense.R.layout;
import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Depense;
import com.eldoraludo.tripexpense.entite.Emprunt;
import com.eldoraludo.tripexpense.entite.Participant;
import com.eldoraludo.tripexpense.entite.TypeDeDepense;
import com.eldoraludo.tripexpense.util.DateHelper;

import java.util.List;

public class EmpruntArrayAdapter extends ArrayAdapter<Emprunt> {
	private final Context context;
	private final List<Emprunt> values;

	public EmpruntArrayAdapter(Context context, List<Emprunt> values) {
		super(context, layout.ligne_emprunt, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DatabaseHandler databaseHandler = new DatabaseHandler(this.context);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View empruntLigneView = inflater.inflate(layout.ligne_emprunt,
				parent, false);
		TextView nomEmprunteurText = (TextView) empruntLigneView
				.findViewById(id.nomEmprunteur);
			TextView nomParticipantText = (TextView) empruntLigneView
				.findViewById(id.nomParticipant);
		TextView nomDeLEmpruntText = (TextView) empruntLigneView
				.findViewById(id.nomDeLEmprunt);
        TextView dateEmpruntText = (TextView) empruntLigneView
                .findViewById(R.id.dateEmprunt);
		TextView montantText = (TextView) empruntLigneView
				.findViewById(id.montantDeLEmprunt);
		Emprunt emprunt = values.get(position);
        Participant emprunteur = databaseHandler.trouverLeParticipant(emprunt
                .getEmprunteurId());
        Participant participant = databaseHandler.trouverLeParticipant(emprunt
                .getEmprunteurId());
        dateEmpruntText.setText(DateHelper.prettyDate(emprunt.getDateEmprunt()));
		nomDeLEmpruntText.setText(" " + emprunt.getNomEmprunt());
		montantText.setText(" " + emprunt.getMontant() + " euros");

		nomEmprunteurText.setText(emprunteur.getNom());
        nomParticipantText.setText(participant.getNom());
		// Change the icon for Windows and iPhone

		return empruntLigneView;
	}
}