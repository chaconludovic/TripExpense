package com.eldoraludo.tripexpense;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.eldoraludo.tripexpense.database.DatabaseHandler;
import com.eldoraludo.tripexpense.entite.Participant;
import com.eldoraludo.tripexpense.util.DateHelper;
import com.google.common.base.Preconditions;

public class AjouterParticipantActivity extends Activity {
	private Integer idParticipant;
	private Integer idProjet;
	private Button ajouterOuModifierParticipantButton;
	private EditText nomParticipantText;
	private TextView tvDisplayDateArrive;
	private Button btnChangeDateArrive;
	private TextView tvDisplayDateDepart;
	private Button btnChangeDateDepart;

	private int anneeArrive;
	private int moisArrive;
	private int jourArrive;
	private int anneeDepart;
	private int moisDepart;
	private int jourDepart;
	private DatabaseHandler databaseHandler;
	static final int DATE_DIALOG_ID_ARRIVE = 999;
	static final int DATE_DIALOG_ID_DEPART = 1999;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ajouter_participant);

		Intent intent = getIntent();
		idParticipant = intent.getIntExtra(
				GestionParticipantActivity.ID_PARTICIPANT, -1);
		idProjet = intent.getIntExtra(GestionProjetActivity.ID_PROJET_COURANT,
				-1);
		Preconditions.checkState(!idProjet.equals(-1),
				"L'id du projet doit être définit");
		databaseHandler = new DatabaseHandler(this);
		nomParticipantText = (EditText) findViewById(R.id.nomParticipantText);
		ajouterOuModifierParticipantButton = (Button) findViewById(R.id.ajouterOuModifierParticipantButton);

		if (idParticipant != -1) {
			Participant participant = databaseHandler
					.trouverLeParticipant(idParticipant);
			Preconditions.checkNotNull(participant,
					"Le participant n'a pas été trouvé");
			nomParticipantText.setText(participant.getNom());
			anneeArrive = participant.getDateArrive().getYear();
			moisArrive = participant.getDateArrive().getMonthOfYear();
			jourArrive = participant.getDateArrive().getDayOfMonth();

			anneeDepart = participant.getDateDepart().getYear();
			moisDepart = participant.getDateDepart().getMonthOfYear();
			jourDepart = participant.getDateDepart().getDayOfMonth();
		} else {
			final Calendar c = Calendar.getInstance();
			anneeArrive = c.get(Calendar.YEAR);
			moisArrive = c.get(Calendar.MONTH);
			jourArrive = c.get(Calendar.DAY_OF_MONTH);
			anneeDepart = c.get(Calendar.YEAR);
			moisDepart = c.get(Calendar.MONTH);
			jourDepart = c.get(Calendar.DAY_OF_MONTH) + 1;
		}

		setCurrentDateOnView();
		addListenerOnButton();

	}

	public void onClick(View view) {
		// If add button was clicked
		if (ajouterOuModifierParticipantButton.isPressed()) {
			// Get entered text
			String participantTextValue = nomParticipantText.getText()
					.toString();
			nomParticipantText.setText("");

			if (participantTextValue == null || participantTextValue.isEmpty()) {
				return;
			}

			// Add text to the database
			databaseHandler.ajouterOuModifierParticipant(Participant
					.newBuilder()
					.withId(idParticipant == -1 ? null : idParticipant)
					.withNom(participantTextValue)
					.withDateArrive(
							DateHelper.convertirIntsToDate(jourArrive,
									moisArrive, anneeArrive))
					.withDateDepart(
							DateHelper.convertirIntsToDate(jourDepart,
									moisDepart, anneeDepart))
					.withProjetId(idProjet).build());
			Intent i = new Intent();
			setResult(RESULT_OK, i);
			super.finish();
		}

	}

	// display current date
	public void setCurrentDateOnView() {
		tvDisplayDateArrive = (TextView) findViewById(R.id.afficherDateArrive);
		tvDisplayDateDepart = (TextView) findViewById(R.id.afficherDateDepart);
		// set current date into textview
		tvDisplayDateArrive.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(jourArrive).append("/").append(moisArrive + 1)
				.append("/").append(anneeArrive).append(" "));
		tvDisplayDateDepart.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(jourDepart).append("/").append(moisDepart + 1)
				.append("/").append(anneeDepart).append(" "));
	}

	public void addListenerOnButton() {

		btnChangeDateArrive = (Button) findViewById(R.id.btnChangeDateArrive);

		btnChangeDateArrive.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showDialog(DATE_DIALOG_ID_ARRIVE);

			}

		});
		btnChangeDateDepart = (Button) findViewById(R.id.btnChangeDateDepart);

		btnChangeDateDepart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showDialog(DATE_DIALOG_ID_DEPART);

			}

		});

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID_ARRIVE:
			// set date picker as current date
			return new DatePickerDialog(this, datePickerListenerArrive,
					anneeArrive, moisArrive, jourArrive);
		case DATE_DIALOG_ID_DEPART:
			// set date picker as current date
			return new DatePickerDialog(this, datePickerListenerDepart,
					anneeDepart, moisDepart, jourDepart);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListenerArrive = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			anneeArrive = selectedYear;
			moisArrive = selectedMonth;
			jourArrive = selectedDay;

			// set selected date into textview
			tvDisplayDateArrive.setText(new StringBuilder().append(jourArrive)
					.append("/").append(moisArrive + 1).append("/")
					.append(anneeArrive).append(" "));

		}
	};
	private DatePickerDialog.OnDateSetListener datePickerListenerDepart = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			anneeDepart = selectedYear;
			moisDepart = selectedMonth;
			jourDepart = selectedDay;

			// set selected date into textview
			tvDisplayDateDepart.setText(new StringBuilder().append(jourDepart)
					.append("/").append(moisDepart + 1).append("/")
					.append(anneeDepart).append(" "));

		}
	};

}
