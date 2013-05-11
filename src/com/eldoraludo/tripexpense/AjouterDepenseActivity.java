package com.eldoraludo.tripexpense;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Depense;
import com.eldoraludo.tripexpense.entite.Participant;
import com.eldoraludo.tripexpense.entite.TypeDeDepense;
import com.eldoraludo.tripexpense.util.DateHelper;
import com.google.common.base.Preconditions;


public class AjouterDepenseActivity extends Activity {
	private Integer idDepense;
	private Integer idProjet;
	private Button ajouterOuModifierDepenseButton;
	private EditText nomDepenseText;
	private EditText montantText;
	private TextView tvDisplayDateDebutDepense;
	private Button btnChangeDateDebutDepense;
	private TextView tvDisplayDateFinDepense;
	private Button btnChangeDateFinDepense;
	private Spinner listeParticipant;

	private int anneeDebutDepense;
	private int moisDebutDepense;
	private int jourDebutDepense;
	private int anneeFinDepense;
	private int moisFinDepense;
	private int jourFinDepense;
	private DatabaseHandler databaseHandler;
	static final int DATE_DIALOG_ID_DEBUT_DEPENSE = 999;
	static final int DATE_DIALOG_ID_FIN_DEPENSE = 1999;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ajouter_depense);

		Intent intent = getIntent();
		idDepense = intent.getIntExtra(GestionDepenseActivity.ID_DEPENSE, -1);
		idProjet = intent.getIntExtra(GestionProjetActivity.ID_PROJET_COURANT,
				-1);
		Preconditions.checkState(!idProjet.equals(-1),
				"L'id du projet doit être définit");
		Toast.makeText(getApplicationContext(),
				"Ajouter modifier dépense  " + idDepense, Toast.LENGTH_LONG)
				.show();
		databaseHandler = new DatabaseHandler(this);
		nomDepenseText = (EditText) findViewById(R.id.nomDepenseText);
		montantText = (EditText) findViewById(R.id.montantText);
		ajouterOuModifierDepenseButton = (Button) findViewById(R.id.ajouterOuModifierDepenseButton);

		listeParticipant = (Spinner) findViewById(R.id.listeParticipant);
		List<Participant> list = databaseHandler.getAllParticipant(idProjet);

		ArrayAdapter<Participant> dataAdapter = new ArrayAdapter<Participant>(
				this, android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		listeParticipant.setAdapter(dataAdapter);

		if (idDepense != -1) {
			Depense depense = databaseHandler.trouverLaDepense(idDepense);
			Preconditions.checkNotNull(depense,
					"La dépense n'a pas été trouvée");
			nomDepenseText.setText(depense.getNomDepense());
			montantText.setText(String.valueOf(depense.getMontant()));
			anneeDebutDepense = DateHelper.recupererDepuisDate(
					depense.getDateDebut(), Calendar.YEAR);
			moisDebutDepense = DateHelper.recupererDepuisDate(
					depense.getDateDebut(), Calendar.MONTH);
			jourDebutDepense = DateHelper.recupererDepuisDate(
					depense.getDateDebut(), Calendar.DAY_OF_MONTH);

			anneeFinDepense = DateHelper.recupererDepuisDate(
					depense.getDateFin(), Calendar.YEAR);
			moisFinDepense = DateHelper.recupererDepuisDate(
					depense.getDateFin(), Calendar.MONTH);
			jourFinDepense = DateHelper.recupererDepuisDate(
					depense.getDateFin(), Calendar.DAY_OF_MONTH);
			Participant participant = databaseHandler.trouverLeParticipant(depense.getParticipantId());
			int pos = list.indexOf(participant);
			listeParticipant.setSelection(pos);
		} else {
			final Calendar c = Calendar.getInstance();
			anneeDebutDepense = c.get(Calendar.YEAR);
			moisDebutDepense = c.get(Calendar.MONTH);
			jourDebutDepense = c.get(Calendar.DAY_OF_MONTH);
			anneeFinDepense = c.get(Calendar.YEAR);
			moisFinDepense = c.get(Calendar.MONTH);
			jourFinDepense = c.get(Calendar.DAY_OF_MONTH) + 1;
		}

		setCurrentDateOnView();
		addListenerOnButton();
		// Show the Up button in the action bar.
		setupActionBar();
	}

	public void onClick(View view) {
		// If add button was clicked
		if (ajouterOuModifierDepenseButton.isPressed()) {
			// Get entered text
			String nomDepenseTextValue = nomDepenseText.getText().toString();
			nomDepenseText.setText("");
			String montantDepenseTextValue = montantText.getText().toString();
			montantText.setText("");
			Participant participantSelectionne = (Participant) listeParticipant
					.getSelectedItem();
			if (nomDepenseTextValue == null || nomDepenseTextValue.isEmpty()
					|| montantDepenseTextValue == null
					|| montantDepenseTextValue.isEmpty()) {
				return;
			}

			// Add text to the database
			databaseHandler.ajouterOuModifierDepense(Depense
					.newBuilder()
					.withId(idDepense == -1 ? null : idDepense)
					.withNomDepense(nomDepenseTextValue)
					.withMontant(Double.valueOf(montantDepenseTextValue))
					.withDateDebut(
							DateHelper.convertirIntsToDate(jourDebutDepense,
									moisDebutDepense, anneeDebutDepense))
					.withDateFin(
							DateHelper.convertirIntsToDate(jourFinDepense,
									moisFinDepense, anneeFinDepense))
					.withParticipantId(participantSelectionne.getId())
					.withTypeDeDepense(TypeDeDepense.COURSE)
					.withProjetId(idProjet).build());
			Intent i = new Intent(getApplicationContext(),
					GestionDepenseActivity.class);
			i.putExtra(GestionProjetActivity.ID_PROJET_COURANT, idProjet);
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

	// display current date
	public void setCurrentDateOnView() {
		tvDisplayDateDebutDepense = (TextView) findViewById(R.id.afficherDateDebutDepense);
		tvDisplayDateFinDepense = (TextView) findViewById(R.id.afficherDateFinDepense);
		// set current date into textview
		tvDisplayDateDebutDepense.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(jourDebutDepense).append("/")
				.append(moisDebutDepense + 1).append("/")
				.append(anneeDebutDepense).append(" "));
		tvDisplayDateFinDepense.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(jourFinDepense).append("/").append(moisFinDepense + 1)
				.append("/").append(anneeFinDepense).append(" "));
	}

	public void addListenerOnButton() {

		btnChangeDateDebutDepense = (Button) findViewById(R.id.btnChangeDateDebutDepense);

		btnChangeDateDebutDepense.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showDialog(DATE_DIALOG_ID_DEBUT_DEPENSE);

			}

		});
		btnChangeDateFinDepense = (Button) findViewById(R.id.btnChangeDateFinDepense);

		btnChangeDateFinDepense.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showDialog(DATE_DIALOG_ID_FIN_DEPENSE);

			}

		});

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID_DEBUT_DEPENSE:
			// set date picker as current date
			return new DatePickerDialog(this, datePickerListenerDebutDepense,
					anneeDebutDepense, moisDebutDepense, jourDebutDepense);
		case DATE_DIALOG_ID_FIN_DEPENSE:
			// set date picker as current date
			return new DatePickerDialog(this, datePickerListenerFinDepense,
					anneeFinDepense, moisFinDepense, jourFinDepense);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListenerDebutDepense = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			anneeDebutDepense = selectedYear;
			moisDebutDepense = selectedMonth;
			jourDebutDepense = selectedDay;

			// set selected date into textview
			tvDisplayDateDebutDepense.setText(new StringBuilder()
					.append(jourDebutDepense).append("/")
					.append(moisDebutDepense + 1).append("/")
					.append(anneeDebutDepense).append(" "));

		}
	};
	private DatePickerDialog.OnDateSetListener datePickerListenerFinDepense = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			anneeFinDepense = selectedYear;
			moisFinDepense = selectedMonth;
			jourFinDepense = selectedDay;

			// set selected date into textview
			tvDisplayDateFinDepense.setText(new StringBuilder()
					.append(jourFinDepense).append("/")
					.append(moisFinDepense + 1).append("/")
					.append(anneeFinDepense).append(" "));

		}
	};

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ajouter_depense, menu);
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
