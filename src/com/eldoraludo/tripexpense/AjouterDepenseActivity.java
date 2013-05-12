package com.eldoraludo.tripexpense;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
	private static final int ERROR_DIALOG_DATE_INCOHERENTE = 45464;

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
			Participant participant = databaseHandler
					.trouverLeParticipant(depense.getParticipantId());
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

			Date dateDebut = DateHelper.convertirIntsToDate(jourDebutDepense,
					moisDebutDepense, anneeDebutDepense);
			Date dateFin = DateHelper.convertirIntsToDate(jourFinDepense,
					moisFinDepense, anneeFinDepense);
			if (dateDebut.after(dateFin)) {
				showDialog(ERROR_DIALOG_DATE_INCOHERENTE);
				return;
			}
			this.ajouterDepense(nomDepenseTextValue, montantDepenseTextValue,
					participantSelectionne.getId(), dateDebut, dateFin);
			Intent i = new Intent();
			setResult(RESULT_OK, i);
			super.finish();
		}
	}

	private void ajouterDepense(String nomDepenseTextValue,
			String montantDepenseTextValue, Integer participantId,
			Date dateDebut, Date dateFin) {
		// Add text to the database
		databaseHandler.ajouterOuModifierDepense(Depense.newBuilder()
				.withId(idDepense == -1 ? null : idDepense)
				.withNomDepense(nomDepenseTextValue)
				.withMontant(Double.valueOf(montantDepenseTextValue))
				.withDateDebut(dateDebut).withDateFin(dateFin)
				.withParticipantId(participantId)
				.withTypeDeDepense(TypeDeDepense.COURSE).withProjetId(idProjet)
				.build());
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
		case ERROR_DIALOG_DATE_INCOHERENTE:
			AlertDialog create = getDialogError("La date de début doit être avant la date de fin");
			return create;
		}
		return null;
	}

	private AlertDialog getDialogError(String errorMessage) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				AjouterDepenseActivity.this);
		builder.setTitle("Une erreur est arrivé");
		builder.setIcon(R.drawable.ic_action_error);
		// Add the buttons
		builder.setNeutralButton(errorMessage,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					}
				});

		AlertDialog create = builder.create();
		return create;
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

}
