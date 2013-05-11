package com.eldoraludo.tripexpense;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Depense;
import com.google.common.base.Preconditions;


public class GestionDepenseActivity extends ListActivity {
	protected static final String ID_DEPENSE = "id_depense";
	private Integer idProjet;
	private Button ajouterNouveauDepenseButton;
	private DatabaseHandler databaseHandler;
	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gestion_depense);
		ajouterNouveauDepenseButton = (Button) findViewById(R.id.ajouterNouveauDepenseButton);
		databaseHandler = new DatabaseHandler(this);
		Intent intent = getIntent();
		idProjet = intent.getIntExtra(GestionProjetActivity.ID_PROJET_COURANT,
				-1);
		Preconditions.checkState(!idProjet.equals(-1),
				"L'id du projet doit être définit");
		Toast.makeText(getApplicationContext(),
				"Gestion dépense du projet courant  " + idProjet,
				Toast.LENGTH_LONG).show();

		List<Depense> values = databaseHandler.getAllDepense(idProjet);
		// Binding resources Array to ListAdapter
		this.setListAdapter(new ArrayAdapter<Depense>(this,
				android.R.layout.simple_list_item_1, values));

		lv = getListView();

		// listening to single list item on click
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Depense depenseAModifier = (Depense) lv.getAdapter().getItem(
						position);
				Intent i = new Intent(getApplicationContext(),
						AjouterDepenseActivity.class);
				// sending data to new activity
				i.putExtra(ID_DEPENSE, depenseAModifier.getId());
				i.putExtra(GestionProjetActivity.ID_PROJET_COURANT, idProjet);

				startActivity(i);
			}
		});

		// Show the Up button in the action bar.
		setupActionBar();
	}

	public void onClick(View view) {
		// If add button was clicked
		if (ajouterNouveauDepenseButton.isPressed()) {
			Intent intent = new Intent(this, AjouterDepenseActivity.class);
			intent.putExtra(GestionProjetActivity.ID_PROJET_COURANT, idProjet);
			startActivity(intent);
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
		getMenuInflater().inflate(R.menu.gestion_depense, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.synthese_menu:
			Intent pageSynthese = new Intent(getApplicationContext(),
					SyntheseActivity.class);
			pageSynthese.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
					idProjet);
			startActivity(pageSynthese);
			return true;
		case R.id.gestion_depense_menu:
			Intent pageDepense = new Intent(getApplicationContext(),
					GestionParticipantActivity.class);
			pageDepense.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
					idProjet);
			startActivity(pageDepense);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
