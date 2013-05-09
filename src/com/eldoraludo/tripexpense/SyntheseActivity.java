package com.eldoraludo.tripexpense;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SyntheseActivity extends Activity {
	private Integer idProjet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_synthese);

		Intent intent = getIntent();
		idProjet = intent.getIntExtra(GestionProjetActivity.ID_PROJET_COURANT,
				0);
		Toast.makeText(getApplicationContext(),
				"Synthese du projet courant  " + idProjet, Toast.LENGTH_LONG)
				.show();
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.synthese, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.gestion_projet_menu:
			startActivity(new Intent(getApplicationContext(),
					GestionProjetActivity.class));
			return true;
		case R.id.gestion_participant_menu:
			Intent i = new Intent(getApplicationContext(),
					GestionParticipantActivity.class);
			i.putExtra(GestionProjetActivity.ID_PROJET_COURANT, idProjet);
			startActivity(i);
			return true;
		case R.id.gestion_depense_menu:
			Intent pageDepense = new Intent(getApplicationContext(),
					GestionDepenseActivity.class);
			pageDepense.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
					idProjet);
			startActivity(pageDepense);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
