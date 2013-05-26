package com.eldoraludo.tripexpense;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.eldoraludo.tripexpense.arrayadapter.SyntheseArrayAdapter;
import com.eldoraludo.tripexpense.calculateur.SyntheseCalculateur;
import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.dto.SyntheseDTO;
import com.eldoraludo.tripexpense.entite.Depense;
import com.eldoraludo.tripexpense.entite.Participant;

public class SyntheseActivity extends ListActivity {
	private Integer idProjet;
	private DatabaseHandler databaseHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_synthese);

		Intent intent = getIntent();
		idProjet = intent.getIntExtra(GestionProjetActivity.ID_PROJET_COURANT,
				0);
		databaseHandler = new DatabaseHandler(this);
		// this.setListAdapter(new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, new ArrayList<String>()));

		SyntheseArrayAdapter adapter = new SyntheseArrayAdapter(this,
				new ArrayList<SyntheseDTO>());
		this.setListAdapter(adapter);

		setupActionBar();
		ChargementDepensesEnArrierePlan chargementDepensesEnArrierePlan = new ChargementDepensesEnArrierePlan();
		chargementDepensesEnArrierePlan.execute();
	}

	public class ChargementDepensesEnArrierePlan extends
			AsyncTask<Void, String, List<SyntheseDTO>> {
		ProgressDialog progress;

		@Override
		protected void onPostExecute(List<SyntheseDTO> result) {
			progress.dismiss();
			// SyntheseActivity.this.setListAdapter(new ArrayAdapter<String>(
			// SyntheseActivity.this, android.R.layout.simple_list_item_1,
			// result));
			SyntheseArrayAdapter adapter = new SyntheseArrayAdapter(
					SyntheseActivity.this, result);
			SyntheseActivity.this.setListAdapter(adapter);
		}

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(SyntheseActivity.this, "",
					"Calcul des dépenses en cours", true);
		}

		@Override
		protected List<SyntheseDTO> doInBackground(Void... noParam) {
			return getListeDettes();
		}

		private List<SyntheseDTO> getListeDettes() {
			List<Depense> depenses = databaseHandler.getAllDepense(idProjet);
			List<Participant> participants = databaseHandler
					.getAllParticipant(idProjet);
			return new SyntheseCalculateur(depenses, participants).run();
		}
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
		if (databaseHandler.getParticipantsCount(idProjet) == 0) {
			MenuItem item = menu.findItem(R.id.gestion_depense_menu);
			item.setVisible(false);
		}
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
		Intent pageAccueil = new Intent(getApplicationContext(),
				AccueilActivity.class);
		startActivity(pageAccueil);
		super.finish();
		return true;
	}

}
