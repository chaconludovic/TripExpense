package com.eldoraludo.tripexpense;

import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Projet;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class AccueilActivity extends Activity {
	private static final String ID_PROJET_COURANT = "id_projet_courant";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accueil);
		DatabaseHandler databaseHandler = new DatabaseHandler(this);
		// if (databaseHandler.getProjetsCount() != 0) {
		// Projet projetCourant = databaseHandler.trouverLeProjetCourant();
		// Intent intent = new Intent(this, SyntheseActivity.class);
		// intent.putExtra(ID_PROJET_COURANT, projetCourant.getId());
		// startActivity(intent);
		// } else {
		Intent intent = new Intent(this, GestionProjetActivity.class);
		startActivity(intent);
		// }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.accueil, menu);
		return true;
	}

}
