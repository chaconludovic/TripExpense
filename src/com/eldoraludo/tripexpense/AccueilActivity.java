package com.eldoraludo.tripexpense;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Projet;

public class AccueilActivity extends Activity {

	private DatabaseHandler databaseHandler;
	private Button allerALaPageDeSynthese;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accueil);
		databaseHandler = new DatabaseHandler(this);
		allerALaPageDeSynthese = (Button) findViewById(R.id.allerALaPageDeSynthese);
		if (databaseHandler.getProjetsCount() == 0) {
			allerALaPageDeSynthese.setVisibility(View.INVISIBLE);
		}

	}

	public void onClick(View view) {
		// If add button was clicked
		if (allerALaPageDeSynthese.isPressed()) {
			Intent intent = new Intent(this, SyntheseActivity.class);
			Projet projetCourant = databaseHandler.trouverLeProjetCourant();
			intent.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
					projetCourant.getId());
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.accueil, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.gestion_projet_menu:
			Intent pageGestionProjet = new Intent(getApplicationContext(),
					GestionProjetActivity.class);
			startActivity(pageGestionProjet);
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

}
