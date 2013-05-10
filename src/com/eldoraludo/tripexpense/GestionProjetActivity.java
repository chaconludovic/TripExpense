package com.eldoraludo.tripexpense;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.eldoraludo.tripexpense.database.DatabaseHandler;

import entite.Projet;

public class GestionProjetActivity extends ListActivity {
	public static final String ID_PROJET_COURANT = "idProjetCourant";
	protected static final int CONTEXTMENU_DELETEITEM = 0;
	private EditText nouveauProjetText;
	private Button ajouterNouveauProjetButton;
	private DatabaseHandler databaseHandler;
	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gestion_projet);

		databaseHandler = new DatabaseHandler(this);

		nouveauProjetText = (EditText) findViewById(R.id.nouveauProjetText);
		ajouterNouveauProjetButton = (Button) findViewById(R.id.ajouterNouveauProjetButton);

		
		List<Projet> values = databaseHandler.getAllProjet();
		// Binding resources Array to ListAdapter
		this.setListAdapter(new ArrayAdapter<Projet>(this,
				android.R.layout.simple_list_item_1, values));

		lv = getListView();

		// listening to single list item on click
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Projet projetACharger = (Projet) lv.getAdapter().getItem(
						position);
				Intent i = new Intent(getApplicationContext(),
						SyntheseActivity.class);
				// sending data to new activity
				i.putExtra(ID_PROJET_COURANT, projetACharger.getId());
				startActivity(i);
			}
		});
		// lv.setOnItemLongClickListener(new OnItemLongClickListener() {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// // TODO Auto-generated method stub
		// return false;
		// }
		//
		// });

		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				menu.setHeaderTitle("ContextMenu");
				menu.add(0, CONTEXTMENU_DELETEITEM, 0, "Delete this favorite!");
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
			ArrayAdapter<Projet> adapter = (ArrayAdapter<Projet>) getListAdapter();

			/* Get the selected item out of the Adapter by its position. */

			Projet projetASupprimer = (Projet) lv.getAdapter().getItem(
					menuInfo.position);

			databaseHandler.deleteProjet(projetASupprimer);
			/* Remove it from the list. */
			adapter.remove(projetASupprimer);
			adapter.notifyDataSetChanged();
			return true; /* true means: "we handled the event". */

		}

		return false;

	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	public void onClick(View view) {
		// If add button was clicked
		if (ajouterNouveauProjetButton.isPressed()) {
			ArrayAdapter<Projet> adapter = (ArrayAdapter<Projet>) getListAdapter();
			// Get entered text
			String projetTextValue = nouveauProjetText.getText().toString();
			nouveauProjetText.setText("");

			if (projetTextValue == null || projetTextValue.isEmpty()) {
				return;
			}

			// Add text to the database
			Projet projetCreer = databaseHandler.ajouterOuModifierProjet(Projet
					.newBuilder().withNom(projetTextValue)
					.withEstProjetCourant(true).build());

			// Display success information
			Toast.makeText(getApplicationContext(),
					"Nouveau projet ajout� " + nouveauProjetText,
					Toast.LENGTH_LONG).show();
			adapter.add(projetCreer);
			adapter.notifyDataSetChanged();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gestion_projet, menu);
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
