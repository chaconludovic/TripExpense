package com.eldoraludo.tripexpense;

import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Depense;
import com.eldoraludo.tripexpense.entite.Participant;
import com.eldoraludo.tripexpense.entite.Projet;
import com.eldoraludo.tripexpense.entite.TypeDeDepense;
import com.eldoraludo.tripexpense.util.DateHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.NavUtils;

public class AjouterProjetActivity extends Activity {
	private Button ajouterOuModifierProjetButton;
	private EditText nomProjetText;
	private DatabaseHandler databaseHandler;
	private Integer idProjet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ajouter_projet);
		Intent intent = getIntent();
		idProjet = intent.getIntExtra(GestionProjetActivity.ID_PROJET_COURANT,
				-1);
		databaseHandler = new DatabaseHandler(this);

		ajouterOuModifierProjetButton = (Button) findViewById(R.id.ajouterOuModifierProjetButton);
		nomProjetText = (EditText) findViewById(R.id.nomProjetText);
		if (idProjet != -1) {
			Projet projet = databaseHandler.trouverLeProjet(idProjet);
			nomProjetText.setText(projet.getNom());
		}
		// Show the Up button in the action bar.
		setupActionBar();
	}

	public void onClick(View view) {
		// If add button was clicked
		if (ajouterOuModifierProjetButton.isPressed()) {
			// Get entered text
			String nomProjetTextValue = nomProjetText.getText().toString();
			nomProjetText.setText("");

			if (nomProjetTextValue == null || nomProjetTextValue.isEmpty()) {
				return;
			}

			// Add text to the database
			databaseHandler.ajouterOuModifierProjet(Projet.newBuilder()
					.withId(idProjet == -1 ? null : idProjet)
					.withNom(nomProjetTextValue).withEstProjetCourant(true)
					.build());
			Intent i = new Intent(getApplicationContext(),
					GestionProjetActivity.class);
			startActivity(i);
		}

		// } else if (backButton.isPressed()) {
		// // When back button is pressed
		// // Create an intent
		// Intent intent = new Intent(this, MainActivity.class);
		// // Start activity
		// startActivity(intent);
		// // Finish this activity
		// this.finish();
		//
		// // Close the database
		// dao.close();
		// }

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
		getMenuInflater().inflate(R.menu.ajouter_projet, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
