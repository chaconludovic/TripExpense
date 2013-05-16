package com.eldoraludo.tripexpense;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.eldoraludo.tripexpense.arrayadapter.ProjetArrayAdapter;
import com.eldoraludo.tripexpense.arrayadapter.SyntheseArrayAdapter;
import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.dto.SyntheseDTO;
import com.eldoraludo.tripexpense.entite.Projet;

public class GestionProjetActivity extends ListActivity {
	public static final String ID_PROJET_COURANT = "idProjetCourant";
	protected static final int CONTEXTMENU_DELETEITEM = 0;
	protected static final int CONTEXTMENU_MODIFYITEM = 1;
	private static final int REQUEST_AJOUTER_PROJET = 1;

	// private EditText nouveauProjetText;
	private Button ajouterNouveauProjetButton;
	private DatabaseHandler databaseHandler;
	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gestion_projet);

		databaseHandler = new DatabaseHandler(this);

		// nouveauProjetText = (EditText) findViewById(R.id.nouveauProjetText);
		ajouterNouveauProjetButton = (Button) findViewById(R.id.ajouterNouveauProjetButton);

		List<Projet> values = databaseHandler.getAllProjet();
		// Binding resources Array to ListAdapter
		// this.setListAdapter(new ArrayAdapter<Projet>(this,
		// android.R.layout.simple_list_item_1, values));
		ProjetArrayAdapter adapter = new ProjetArrayAdapter(this, values);
		this.setListAdapter(adapter);
		lv = getListView();

		// listening to single list item on click
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Projet projetACharger = (Projet) lv.getAdapter().getItem(
						position);
				// mise � projet courant
				for (Projet projet : databaseHandler.getAllProjet()) {
					if (projet.estCourant()) {
						projet.annuleEtatCourant();
						databaseHandler.ajouterOuModifierProjet(projet);
					}
				}
				projetACharger.definirEnTantQueCourant();
				databaseHandler.ajouterOuModifierProjet(projetACharger);
				Intent i = new Intent(getApplicationContext(),
						SyntheseActivity.class);
				// sending data to new activity
				i.putExtra(ID_PROJET_COURANT, projetACharger.getId());
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

	}

	@Override
	public boolean onContextItemSelected(MenuItem aItem) {

		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) aItem
				.getMenuInfo();

		/* Switch on the ID of the item, to get what the user selected. */

		switch (aItem.getItemId()) {

		case CONTEXTMENU_DELETEITEM:
			ArrayAdapter<Projet> adapter = (ArrayAdapter<Projet>) getListAdapter();

			/* Get the selected item out of the Adapter by its position. */

			Projet projetASupprimer = (Projet) lv.getAdapter().getItem(
					menuInfo.position);

			databaseHandler.deleteProjet(projetASupprimer);
			/* Remove it from the list. */
			adapter.remove(projetASupprimer);
			adapter.notifyDataSetChanged();
			return true; /* true means: "we handled the event". */
		case CONTEXTMENU_MODIFYITEM:
			Projet projetAModifier = (Projet) lv.getAdapter().getItem(
					menuInfo.position);
			Intent i = new Intent(getApplicationContext(),
					AjouterProjetActivity.class);
			// sending data to new activity
			i.putExtra(ID_PROJET_COURANT, projetAModifier.getId());
			startActivityForResult(i, REQUEST_AJOUTER_PROJET);
			return true; /* true means: "we handled the event". */
		}

		return false;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_AJOUTER_PROJET) {
				// List<Projet> values = databaseHandler.getAllProjet();
				// // Binding resources Array to ListAdapter
				// this.setListAdapter(new ArrayAdapter<Projet>(this,
				// android.R.layout.simple_list_item_1, values));
				List<Projet> values = databaseHandler.getAllProjet();
				// Binding resources Array to ListAdapter
				// this.setListAdapter(new ArrayAdapter<Projet>(this,
				// android.R.layout.simple_list_item_1, values));
				ProjetArrayAdapter adapter = new ProjetArrayAdapter(this,
						values);
				this.setListAdapter(adapter);
			}
		}
	}

	public void onClick(View view) {
		// If add button was clicked
		if (ajouterNouveauProjetButton.isPressed()) {
			Intent intent = new Intent(this, AjouterProjetActivity.class);
			startActivityForResult(intent, REQUEST_AJOUTER_PROJET);
		}
	}

}
