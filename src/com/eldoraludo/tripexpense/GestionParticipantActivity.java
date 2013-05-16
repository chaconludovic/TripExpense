package com.eldoraludo.tripexpense;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.eldoraludo.tripexpense.arrayadapter.ParticipantArrayAdapter;
import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Participant;
import com.google.common.base.Preconditions;

public class GestionParticipantActivity extends ListActivity {
	protected static final String ID_PARTICIPANT = "id_participant";
	private static final int REQUEST_AJOUTER_PARTICIPANT = 3;
	private Integer idProjet;
	private Button ajouterNouveauParticipantButton;
	private DatabaseHandler databaseHandler;
	private ListView lv;
	protected static final int CONTEXTMENU_DELETEITEM = 0;
	protected static final int CONTEXTMENU_MODIFYITEM = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gestion_participant);
		ajouterNouveauParticipantButton = (Button) findViewById(R.id.ajouterNouveauParticipantButton);
		databaseHandler = new DatabaseHandler(this);
		Intent intent = getIntent();
		idProjet = intent.getIntExtra(GestionProjetActivity.ID_PROJET_COURANT,
				-1);
		Preconditions.checkState(!idProjet.equals(-1),
				"L'id du projet doit �tre d�finit");

		List<Participant> values = databaseHandler.getAllParticipant(idProjet);
		// Binding resources Array to ListAdapter
		// this.setListAdapter(new ArrayAdapter<Participant>(this,
		// android.R.layout.simple_list_item_1, values));
		ParticipantArrayAdapter adapter = new ParticipantArrayAdapter(this,
				values);
		this.setListAdapter(adapter);
		lv = getListView();

		// listening to single list item on click
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Participant participantAModifier = (Participant) lv
						.getAdapter().getItem(position);
				Intent i = new Intent(getApplicationContext(),
						AjouterParticipantActivity.class);
				// sending data to new activity
				i.putExtra(ID_PARTICIPANT, participantAModifier.getId());
				i.putExtra(GestionProjetActivity.ID_PROJET_COURANT, idProjet);

				startActivity(i);
			}
		});
		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				menu.setHeaderTitle("Que voulez vous faire?");
				menu.add(0, CONTEXTMENU_MODIFYITEM, 0, "Modifier");
				menu.add(0, CONTEXTMENU_DELETEITEM, 0, "Supprimer");
			}

		});
		// Show the Up button in the action bar.
		setupActionBar();
	}

	@Override
	public boolean onContextItemSelected(MenuItem aItem) {

		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem
				.getMenuInfo();

		/* Switch on the ID of the item, to get what the user selected. */

		switch (aItem.getItemId()) {

		case CONTEXTMENU_DELETEITEM:
			ArrayAdapter<Participant> adapter = (ArrayAdapter<Participant>) getListAdapter();

			/* Get the selected item out of the Adapter by its position. */

			Participant participantASupprimer = (Participant) lv.getAdapter()
					.getItem(menuInfo.position);

			databaseHandler.deleteParticipant(participantASupprimer);
			/* Remove it from the list. */
			adapter.remove(participantASupprimer);
			adapter.notifyDataSetChanged();
			invalidateOptionsMenu();
			return true; /* true means: "we handled the event". */
		case CONTEXTMENU_MODIFYITEM:
			Participant projetAModifier = (Participant) lv.getAdapter()
					.getItem(menuInfo.position);
			Intent i = new Intent(getApplicationContext(),
					AjouterParticipantActivity.class);
			// sending data to new activity
			i.putExtra(ID_PARTICIPANT, projetAModifier.getId());
			i.putExtra(GestionProjetActivity.ID_PROJET_COURANT, idProjet);
			startActivityForResult(i, REQUEST_AJOUTER_PARTICIPANT);
			return true; /* true means: "we handled the event". */
		}

		return false;

	}

	public void onClick(View view) {
		// If add button was clicked
		if (ajouterNouveauParticipantButton.isPressed()) {
			Intent intent = new Intent(this, AjouterParticipantActivity.class);
			intent.putExtra(GestionProjetActivity.ID_PROJET_COURANT, idProjet);
			startActivityForResult(intent, REQUEST_AJOUTER_PARTICIPANT);
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
		getMenuInflater().inflate(R.menu.gestion_participant, menu);
		if (databaseHandler.getParticipantsCount(idProjet) == 0) {
			MenuItem item = menu.findItem(R.id.gestion_depense_menu);
			item.setVisible(false);
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_AJOUTER_PARTICIPANT) {
				List<Participant> values = databaseHandler
						.getAllParticipant(idProjet);
				ParticipantArrayAdapter adapter = new ParticipantArrayAdapter(
						this, values);
				this.setListAdapter(adapter);
				invalidateOptionsMenu();
			}
		}
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
					GestionDepenseActivity.class);
			pageDepense.putExtra(GestionProjetActivity.ID_PROJET_COURANT,
					idProjet);
			startActivity(pageDepense);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
